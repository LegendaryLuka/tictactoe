package app.ui;

import app.config.AiDifficulty;
import app.config.AppSettings;
import app.config.GameMode;
import app.config.ThemeMode;
import app.config.TimerExpiryAction;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.Timer;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.plaf.basic.BasicSliderUI;

public final class SettingsDialog extends JDialog {
    public interface OnSettingsChanged {
        void onChanged(boolean requiresRebuild);
    }

    private final JComboBox<GameMode> gameModeBox;
    private final JComboBox<AiDifficulty> difficultyBox;
    private final JComboBox<Integer> boardSizeBox;
    private final JComboBox<Integer> winConditionBox;
    private final MacToggleSwitch timerEnabledBox;
    private final JSlider timerSecondsSlider;
    private final JComboBox<TimerExpiryAction> timerExpiryBox;
    private final MacToggleSwitch soundEnabledBox;
    private final MacToggleSwitch musicEnabledBox;
    private final JSlider volumeSlider;
    private final JComboBox<ThemeMode> themeBox;
    private final JPanel aiSection;
    private final JPanel timerSection;
    private final JPanel audioSection;
    private final JPanel winConditionRow;
    private final JPanel volumeRow;
    private final JPanel timerSecondsRow;
    private final JPanel timerExpiryRow;
    private final OnSettingsChanged onSettingsChanged;
    private final AppSettings settings;
    private boolean isRebuildingWinConditions = false;
    private WindowHeaderPanel headerPanel;
    private JPanel contentPanel;
    private JPanel form;

    public SettingsDialog(Frame owner, AppSettings settings, OnSettingsChanged onSettingsChanged) {
        super(owner, "Settings", true);
        this.onSettingsChanged = onSettingsChanged;
        this.settings = settings;
        setSize(480, 600);
        setLocationRelativeTo(owner);
        setUndecorated(true);
        setLayout(new BorderLayout(0, 0));
        headerPanel = new WindowHeaderPanel(this);
        add(headerPanel, BorderLayout.NORTH);

        contentPanel = new JPanel(new BorderLayout(8, 8));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        add(contentPanel, BorderLayout.CENTER);

        form = new JPanel();
        form.setOpaque(false);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        gameModeBox = new JComboBox<>(GameMode.values());
        difficultyBox = new JComboBox<>(AiDifficulty.values());
        boardSizeBox = new JComboBox<>(new Integer[] { 3, 4 });
        winConditionBox = new JComboBox<>();
        timerEnabledBox = new MacToggleSwitch();
        timerSecondsSlider = new JSlider(5, 30, settings.getTimerSeconds());
        timerSecondsSlider.setMajorTickSpacing(5);
        timerSecondsSlider.setPaintTicks(true);
        timerSecondsSlider.setPaintLabels(true);
        timerSecondsSlider.setSnapToTicks(true);
        timerExpiryBox = new JComboBox<>(TimerExpiryAction.values());
        soundEnabledBox = new MacToggleSwitch();
        musicEnabledBox = new MacToggleSwitch();
        volumeSlider = new JSlider(0, 100, settings.getVolumePercent());
        themeBox = new JComboBox<>(ThemeMode.values());

        setupComboBox(gameModeBox);
        setupComboBox(difficultyBox);
        setupComboBox(boardSizeBox);
        setupComboBox(winConditionBox);
        setupComboBox(timerExpiryBox);
        setupComboBox(themeBox);
        setupSlider(volumeSlider);
        setupSlider(timerSecondsSlider);

        JPanel gameSection = sectionPanel();
        addRow(gameSection, "Game Mode", gameModeBox);
        addRow(gameSection, "Board Size", boardSizeBox);
        winConditionRow = addRow(gameSection, "Win Condition", winConditionBox);
        form.add(sectionWrapper("Game Settings", gameSection));
        form.add(javax.swing.Box.createVerticalStrut(16));

        aiSection = sectionPanel();
        addRow(aiSection, "AI Difficulty", difficultyBox);
        form.add(sectionWrapper("AI Settings", aiSection));
        form.add(javax.swing.Box.createVerticalStrut(16));

        timerSection = sectionPanel();
        addRow(timerSection, "Timer Enabled", timerEnabledBox);
        timerSecondsRow = addRow(timerSection, "Timer Seconds", timerSecondsSlider);
        timerExpiryRow = addRow(timerSection, "On Time Expiry", timerExpiryBox);
        form.add(sectionWrapper("Timer Settings", timerSection));
        form.add(javax.swing.Box.createVerticalStrut(16));

        audioSection = sectionPanel();
        addRow(audioSection, "Sound Enabled", soundEnabledBox);
        addRow(audioSection, "Music Enabled", musicEnabledBox);
        volumeRow = addRow(audioSection, "Volume", volumeSlider);
        form.add(sectionWrapper("Audio Settings", audioSection));
        form.add(javax.swing.Box.createVerticalStrut(16));

        JPanel appearanceSection = sectionPanel();
        addRow(appearanceSection, "Theme", themeBox);
        form.add(sectionWrapper("Appearance", appearanceSection));

        applyFromSettings(settings);
        gameModeBox.addActionListener(event -> {
            refreshVisibility();
            notifyLiveChange();
        });
        boardSizeBox.addActionListener(event -> {
            rebuildWinConditionOptions();
            refreshVisibility();
            notifyLiveChange();
        });
        winConditionBox.addActionListener(event -> {
            if (!isRebuildingWinConditions)
                notifyLiveChange();
        });
        difficultyBox.addActionListener(event -> notifyLiveChange());
        timerEnabledBox.addActionListener(event -> {
            refreshVisibility();
            notifyLiveChange();
        });
        timerSecondsSlider.addChangeListener(event -> notifyLiveChange());
        timerExpiryBox.addActionListener(event -> notifyLiveChange());
        soundEnabledBox.addActionListener(event -> {
            refreshVisibility();
            notifyLiveChange();
        });
        musicEnabledBox.addActionListener(event -> notifyLiveChange());
        volumeSlider.addChangeListener(event -> notifyLiveChange());
        themeBox.addActionListener(event -> {
            notifyLiveChange();
            applyThemePreview();
        });
        rebuildWinConditionOptions();
        refreshVisibility();

        RoundedButton closeButton = new RoundedButton("Close");
        closeButton.addActionListener(event -> dispose());
        JPanel buttons = new JPanel(new GridLayout(1, 1, 8, 8));
        buttons.setBorder(BorderFactory.createEmptyBorder(0, 12, 12, 12));
        buttons.setOpaque(false);
        buttons.add(closeButton);

        JScrollPane scrollPane = new JScrollPane(form);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(12);
        scrollPane.getVerticalScrollBar().setUI(new OverlayScrollBarUI());
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        contentPanel.add(buttons, BorderLayout.SOUTH);

        applyThemePreview();
    }

    public boolean applyTo(AppSettings settings) {
        Integer boardSize = (Integer) boardSizeBox.getSelectedItem();
        Integer winCondition = (Integer) winConditionBox.getSelectedItem();
        if (boardSize == null)
            boardSize = 3;
        if (winCondition == null)
            winCondition = boardSize;
        if (winCondition > boardSize) {
            JOptionPane.showMessageDialog(this, "Win condition cannot exceed board size.");
            return false;
        }
        settings.setGameMode((GameMode) gameModeBox.getSelectedItem());
        settings.setAiDifficulty((AiDifficulty) difficultyBox.getSelectedItem());
        settings.setBoardSize(boardSize);
        settings.setWinCondition(winCondition);
        settings.setTimerEnabled(timerEnabledBox.isSelected());
        settings.setTimerSeconds(timerSecondsSlider.getValue());
        settings.setTimerExpiryAction((TimerExpiryAction) timerExpiryBox.getSelectedItem());
        settings.setSoundEnabled(soundEnabledBox.isSelected());
        settings.setMusicEnabled(musicEnabledBox.isSelected());
        settings.setVolumePercent(volumeSlider.getValue());
        settings.setThemeMode((ThemeMode) themeBox.getSelectedItem());
        return true;
    }

    private void applyFromSettings(AppSettings settings) {
        gameModeBox.setSelectedItem(settings.getGameMode());
        difficultyBox.setSelectedItem(settings.getAiDifficulty());
        boardSizeBox.setSelectedItem(settings.getBoardSize());
        timerEnabledBox.setSelected(settings.isTimerEnabled());
        timerSecondsSlider.setValue(settings.getTimerSeconds());
        timerExpiryBox.setSelectedItem(settings.getTimerExpiryAction());
        soundEnabledBox.setSelected(settings.isSoundEnabled());
        musicEnabledBox.setSelected(settings.isMusicEnabled());
        volumeSlider.setValue(settings.getVolumePercent());
        themeBox.setSelectedItem(settings.getThemeMode());
    }

    private void rebuildWinConditionOptions() {
        isRebuildingWinConditions = true;
        try {
            int boardSize = (Integer) boardSizeBox.getSelectedItem();
            Integer selected = (Integer) winConditionBox.getSelectedItem();
            winConditionBox.removeAllItems();
            for (int value = 3; value <= boardSize; value++) {
                winConditionBox.addItem(value);
            }
            if (selected != null && selected <= boardSize) {
                winConditionBox.setSelectedItem(selected);
            } else {
                winConditionBox.setSelectedItem(boardSize);
            }
        } finally {
            isRebuildingWinConditions = false;
        }
    }

    private static JPanel sectionWrapper(String title, JPanel sectionContent) {
        JPanel wrapper = new JPanel(new BorderLayout(0, 6));
        wrapper.setOpaque(false);
        JLabel titleLabel = new JLabel("  " + title);
        titleLabel.setFont(new java.awt.Font(java.awt.Font.SANS_SERIF, java.awt.Font.BOLD, 13));
        titleLabel.putClientProperty("isSectionTitle", true);
        wrapper.add(titleLabel, BorderLayout.NORTH);
        wrapper.add(sectionContent, BorderLayout.CENTER);
        return wrapper;
    }

    private static JPanel sectionPanel() {
        JPanel section = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.dispose();
            }
        };
        section.setOpaque(false);
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        return section;
    }

    private static JPanel addRow(JPanel panel, String label, java.awt.Component component) {
        JPanel row = new JPanel(new BorderLayout(8, 8));
        row.setOpaque(false);
        row.add(new JLabel(label), BorderLayout.WEST);
        JPanel eastPanel = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 0, 0));
        eastPanel.setOpaque(false);
        eastPanel.add(component);
        row.add(eastPanel, BorderLayout.EAST);
        row.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));

        // Add a subtle bottom separator? No, macOS settings have separators but maybe
        // we can skip them
        // to keep it simple, or draw them in paintComponent.
        panel.add(row);
        return row;
    }

    private void refreshVisibility() {
        boolean aiMode = gameModeBox.getSelectedItem() == GameMode.HUMAN_VS_AI;
        aiSection.getParent().setVisible(aiMode);

        boolean boardCustom = ((Integer) boardSizeBox.getSelectedItem()) > 3;
        winConditionRow.setVisible(boardCustom);

        boolean timerEnabled = timerEnabledBox.isSelected();
        timerSecondsRow.setVisible(timerEnabled);
        timerExpiryRow.setVisible(timerEnabled);

        boolean soundEnabled = soundEnabledBox.isSelected();
        volumeRow.setVisible(soundEnabled);

        form.revalidate();
        repaint();
    }

    private void notifyLiveChange() {
        int oldSize = settings.getBoardSize();
        int oldWin = settings.getWinCondition();
        GameMode oldMode = settings.getGameMode();
        Integer newSize = (Integer) boardSizeBox.getSelectedItem();
        Integer newWin = (Integer) winConditionBox.getSelectedItem();
        if (newSize == null)
            newSize = oldSize;
        if (newWin == null)
            newWin = oldWin;
        boolean requiresRebuild = newSize != oldSize ||
                newWin != oldWin ||
                ((GameMode) gameModeBox.getSelectedItem()) != oldMode;

        if (!applyTo(settings)) {
            return;
        }
        onSettingsChanged.onChanged(requiresRebuild);
    }

    private void applyThemePreview() {
        ThemeMode selected = (ThemeMode) themeBox.getSelectedItem();
        ThemePalette palette = ThemePalette.forMode(selected);
        getContentPane().setBackground(palette.getBackground());
        contentPanel.setBackground(palette.getBackground());
        if (headerPanel != null)
            headerPanel.applyPalette(palette);
        paintRecursively(this, palette);
    }

    private static void paintRecursively(Component component, ThemePalette palette) {
        if (component instanceof JLabel) {
            JLabel lbl = (JLabel) component;
            if (Boolean.TRUE.equals(lbl.getClientProperty("isSectionTitle"))) {
                lbl.setForeground(new Color(130, 130, 130));
            } else {
                lbl.setForeground(palette.getText());
            }
        } else if (component instanceof JPanel) {
            if (!component.isOpaque()) {
                component.setBackground(palette.getBoard());
            }
            component.setForeground(palette.getText());
        } else if (component instanceof MacToggleSwitch || component instanceof JSlider) {
            component.setBackground(palette.getBoard());
            component.setForeground(palette.getAccent());
        } else if (component instanceof JComboBox) {
            Color bg = palette.getBackground().equals(new Color(0x1C1C1E)) ? new Color(0x3A3A3C) : palette.getBoard();
            component.setBackground(bg);
            component.setForeground(palette.getText());
            javax.swing.ListCellRenderer<?> renderer = ((JComboBox<?>) component).getRenderer();
            if (renderer instanceof ThemeAwareComboBoxRenderer) {
                ((ThemeAwareComboBoxRenderer) renderer).setPalette(palette);
            }
        } else if (component instanceof RoundedButton) {
            ((RoundedButton) component).applyPalette(palette);
        }
        if (component instanceof java.awt.Container) {
            for (Component child : ((java.awt.Container) component).getComponents()) {
                paintRecursively(child, palette);
            }
        }
    }

    private static void setupComboBox(JComboBox<?> box) {
        box.setUI(new BasicComboBoxUI() {
            @Override
            protected JButton createArrowButton() {
                JButton btn = new JButton() {
                    @Override
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(box.getForeground());
                        int cx = getWidth() / 2;
                        int cy = getHeight() / 2;
                        g2.setStroke(new java.awt.BasicStroke(1.5f, java.awt.BasicStroke.CAP_ROUND,
                                java.awt.BasicStroke.JOIN_ROUND));
                        g2.drawLine(cx - 3, cy - 2, cx, cy - 5);
                        g2.drawLine(cx, cy - 5, cx + 3, cy - 2);
                        g2.drawLine(cx - 3, cy + 2, cx, cy + 5);
                        g2.drawLine(cx, cy + 5, cx + 3, cy + 2);
                        g2.dispose();
                    }
                };
                btn.setBorderPainted(false);
                btn.setContentAreaFilled(false);
                btn.setFocusPainted(false);
                btn.setPreferredSize(new Dimension(20, 20));
                return btn;
            }

            @Override
            public void paintCurrentValueBackground(Graphics g, java.awt.Rectangle bounds, boolean hasFocus) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(box.getBackground());
                g2.fillRoundRect(bounds.x, bounds.y, bounds.width, bounds.height, 12, 12);
                g2.dispose();
            }
        });
        box.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        box.setOpaque(false);
        box.setPreferredSize(new Dimension(160, 26));
        box.setRenderer(new ThemeAwareComboBoxRenderer());
    }

    private static void setupSlider(JSlider slider) {
        slider.setUI(new BasicSliderUI(slider) {
            @Override
            public void paintThumb(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillOval(thumbRect.x, thumbRect.y + 4, thumbRect.width, thumbRect.height - 8);
                g2.setColor(new Color(0, 0, 0, 50));
                g2.drawOval(thumbRect.x, thumbRect.y + 4, thumbRect.width, thumbRect.height - 8);
                g2.dispose();
            }

            @Override
            public void paintTrack(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int cy = trackRect.y + trackRect.height / 2;
                int h = 6;
                g2.setColor(new Color(0x48484A));
                g2.fillRoundRect(trackRect.x, cy - h / 2, trackRect.width, h, h, h);
                g2.setColor(slider.getForeground());
                int filledW = thumbRect.x + thumbRect.width / 2 - trackRect.x;
                g2.fillRoundRect(trackRect.x, cy - h / 2, filledW, h, h, h);
                g2.dispose();
            }

            @Override
            public void paintTicks(Graphics g) {
                if (!slider.getPaintTicks())
                    return;
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(130, 130, 130)); // Tick color
                int tickSpacing = slider.getMajorTickSpacing();
                if (tickSpacing > 0) {
                    int min = slider.getMinimum();
                    int max = slider.getMaximum();
                    for (int val = min; val <= max; val += tickSpacing) {
                        int x = xPositionForValue(val);
                        int y = trackRect.y + trackRect.height + 4; // Position below track
                        g2.fillOval(x - 2, y, 4, 4);
                    }
                }
                g2.dispose();
            }

            @Override
            public void paintLabels(Graphics g) {
                if (!slider.getPaintLabels())
                    return;
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(130, 130, 130)); // macOS secondary text color
                g2.setFont(new java.awt.Font(java.awt.Font.SANS_SERIF, java.awt.Font.PLAIN, 10));
                java.awt.FontMetrics fm = g2.getFontMetrics();

                int tickSpacing = slider.getMajorTickSpacing();
                if (tickSpacing > 0) {
                    int min = slider.getMinimum();
                    int max = slider.getMaximum();
                    for (int val = min; val <= max; val += tickSpacing) {
                        int x = xPositionForValue(val);
                        int y = trackRect.y + trackRect.height + 16;
                        String text = String.valueOf(val);
                        int textWidth = fm.stringWidth(text);
                        g2.drawString(text, x - textWidth / 2, y);
                    }
                }
                g2.dispose();
            }
        });
        slider.setOpaque(false);
        if (slider.getPaintLabels()) {
            slider.setPreferredSize(new Dimension(160, 42));
        } else if (slider.getPaintTicks()) {
            slider.setPreferredSize(new Dimension(160, 32));
        } else {
            slider.setPreferredSize(new Dimension(160, 24));
        }
    }

    private static class OverlayScrollBarUI extends BasicScrollBarUI {
        private float alpha = 0.0f;
        private Timer fadeOutTimer;
        private Timer fadeAnimTimer;

        public OverlayScrollBarUI() {
            fadeOutTimer = new Timer(1000, e -> {
                if (fadeAnimTimer != null && fadeAnimTimer.isRunning()) fadeAnimTimer.stop();
                fadeAnimTimer = new Timer(16, ev -> {
                    alpha = Math.max(0.0f, alpha - 0.05f);
                    if (scrollbar != null) scrollbar.repaint();
                    if (alpha <= 0.0f) {
                        fadeAnimTimer.stop();
                    }
                });
                fadeAnimTimer.start();
            });
            fadeOutTimer.setRepeats(false);
        }

        @Override
        public void installUI(javax.swing.JComponent c) {
            super.installUI(c);
            scrollbar.setOpaque(false);
            scrollbar.addAdjustmentListener(e -> {
                alpha = 1.0f;
                scrollbar.repaint();
                if (fadeAnimTimer != null) fadeAnimTimer.stop();
                fadeOutTimer.restart();
            });
            scrollbar.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    alpha = 1.0f;
                    scrollbar.repaint();
                    fadeOutTimer.stop();
                    if (fadeAnimTimer != null) fadeAnimTimer.stop();
                }
                @Override
                public void mouseExited(java.awt.event.MouseEvent e) {
                    fadeOutTimer.restart();
                }
            });
        }

        @Override
        protected JButton createDecreaseButton(int orientation) {
            return createZeroButton();
        }

        @Override
        protected JButton createIncreaseButton(int orientation) {
            return createZeroButton();
        }

        private JButton createZeroButton() {
            JButton button = new JButton();
            button.setPreferredSize(new Dimension(0, 0));
            button.setMinimumSize(new Dimension(0, 0));
            button.setMaximumSize(new Dimension(0, 0));
            return button;
        }

        @Override
        protected void paintTrack(Graphics g, javax.swing.JComponent c, java.awt.Rectangle trackBounds) {}

        @Override
        protected void paintThumb(Graphics g, javax.swing.JComponent c, java.awt.Rectangle thumbBounds) {
            if (thumbBounds.isEmpty() || !scrollbar.isEnabled() || alpha <= 0.0f) {
                return;
            }
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(130, 130, 130, (int) (140 * alpha)));
            int margin = 2;
            g2.fillRoundRect(
                thumbBounds.x + margin, 
                thumbBounds.y, 
                thumbBounds.width - margin * 2, 
                thumbBounds.height, 
                thumbBounds.width - margin * 2, 
                thumbBounds.width - margin * 2
            );
            g2.dispose();
        }

        @Override
        public Dimension getPreferredSize(javax.swing.JComponent c) {
            return new Dimension(10, super.getPreferredSize(c).height);
        }
    }

    private static class MacToggleSwitch extends JCheckBox {
        public MacToggleSwitch() {
            setOpaque(false);
            setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
            setPreferredSize(new Dimension(42, 24));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int h = 24;
            int w = 42;
            int x = getWidth() - w;
            int y = (getHeight() - h) / 2;

            if (isSelected()) {
                g2.setColor(new Color(0x34C759));
            } else {
                g2.setColor(new Color(0x48484A));
            }
            g2.fillRoundRect(x, y, w, h, h, h);

            g2.setColor(Color.WHITE);
            int thumbSize = h - 4;
            int thumbX = isSelected() ? x + w - thumbSize - 2 : x + 2;
            g2.fillOval(thumbX, y + 2, thumbSize, thumbSize);
            g2.dispose();
        }
    }

    private static class ThemeAwareComboBoxRenderer extends javax.swing.plaf.basic.BasicComboBoxRenderer {
        private ThemePalette palette;

        public void setPalette(ThemePalette palette) {
            this.palette = palette;
        }

        @Override
        public Component getListCellRendererComponent(javax.swing.JList list, Object value, int index,
                boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (palette != null) {
                setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
                if (isSelected) {
                    setBackground(palette.getAccent());
                    setForeground(Color.WHITE);
                } else {
                    setBackground(palette.getBoard());
                    setForeground(palette.getText());
                }
            }
            return this;
        }
    }
}
