package app.ui;

import app.config.AppSettings;
import app.config.SettingsManager;
import app.game.AdvancedGameController;
import app.game.GameState;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public final class AdvancedTicTacToeFrame extends JFrame implements AdvancedGameController.Listener {
    private final AppSettings settings;
    private final SettingsManager settingsManager;
    private final AdvancedGameController controller;
    private final SoundService soundService;
    private JPanel rootPanel;
    private JPanel topPanel;
    private JLabel statusLabel;
    private JLabel timerLabel;
    private BoardGridPanel boardPanel;
    private AnimatedCellButton[][] cellButtons;
    private char[][] previousBoard;
    private Timer winLineTimer;
    private ConfettiPanel confettiPanel;
    private IconButton undoBtn;
    private IconButton redoBtn;
    private IconButton newGameBtn;
    private IconButton settingsBtn;

    public AdvancedTicTacToeFrame(AppSettings settings, SettingsManager settingsManager,
            AdvancedGameController controller) {
        this.settings = settings;
        this.settingsManager = settingsManager;
        this.controller = controller;
        this.soundService = new SoundService(settings);
        setTitle("Tic Tac Toe - Advanced");
        setUndecorated(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(760, 840);
        setLocationRelativeTo(null);
        initializeUi();
        soundService.startMusicLoop();
        controller.addListener(this);
        controller.startNewGame();
    }

    @Override
    public void onStateChanged(GameState state) {
        SwingUtilities.invokeLater(() -> {
            rebuildBoardIfNeeded();
            updateBoardCells(state);
            updateStatus(state);
            if (undoBtn != null) undoBtn.setEnabled(controller.canUndo());
            if (redoBtn != null) redoBtn.setEnabled(controller.canRedo());
            if (!state.isGameOver() && confettiPanel != null && confettiPanel.isVisible()) {
                confettiPanel.stop();
            }
            if (state.isGameOver()) {
                if (state.getWinner() != null) {
                    soundService.playWin();
                    confettiPanel.start();
                    playWinningAnimation(state);
                } else {
                    soundService.playDraw();
                }
            } else {
                boolean isEmpty = true;
                char[][] board = state.getBoard();
                for (int r = 0; r < state.getBoardSize(); r++) {
                    for (int c = 0; c < state.getBoardSize(); c++) {
                        if (board[r][c] != 'E') {
                            isEmpty = false;
                            break;
                        }
                    }
                    if (!isEmpty)
                        break;
                }
                if (isEmpty) {
                    controller.pauseTimer();
                }
            }
        });
    }

    @Override
    public void onTimerTick(int secondsLeft, char player) {
        SwingUtilities.invokeLater(() -> timerLabel.setText("Timer: " + secondsLeft + "s (" + player + ")"));
    }

    @Override
    public void onInvalidMove() {
        SwingUtilities.invokeLater(() -> {
            soundService.playInvalid();
            JOptionPane.showMessageDialog(this, "Invalid move.", "Warning", JOptionPane.WARNING_MESSAGE);
        });
    }

    private WindowHeaderPanel headerPanel;

    private void initializeUi() {
        confettiPanel = new ConfettiPanel(() -> confettiPanel.setVisible(false));
        setGlassPane(confettiPanel);

        setLayout(new BorderLayout(0, 0));
        headerPanel = new WindowHeaderPanel(this);
        add(headerPanel, BorderLayout.NORTH);

        rootPanel = new JPanel(new BorderLayout(12, 12));
        rootPanel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        add(rootPanel, BorderLayout.CENTER);

        statusLabel = new JLabel("Ready", SwingConstants.CENTER);
        statusLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 24));
        timerLabel = new JLabel("Timer: --", SwingConstants.CENTER);
        timerLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 18));
        
        JPanel centerInfoPanel = new JPanel(new GridLayout(2, 1, 4, 4));
        centerInfoPanel.setOpaque(false);
        centerInfoPanel.add(statusLabel);
        centerInfoPanel.add(timerLabel);

        undoBtn = new IconButton(IconButton.IconType.UNDO, "Undo");
        undoBtn.addActionListener(event -> controller.undo());
        redoBtn = new IconButton(IconButton.IconType.REDO, "Redo");
        redoBtn.addActionListener(event -> controller.redo());
        newGameBtn = new IconButton(IconButton.IconType.REFRESH, "New Game");
        newGameBtn.addActionListener(event -> controller.startNewGame());
        settingsBtn = new IconButton(IconButton.IconType.GEAR, "Settings");
        settingsBtn.addActionListener(event -> openSettingsDialog());

        JPanel leftControls = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 8, 0));
        leftControls.setOpaque(false);
        leftControls.add(undoBtn);
        leftControls.add(redoBtn);

        JPanel rightControls = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 8, 0));
        rightControls.setOpaque(false);
        rightControls.add(newGameBtn);
        rightControls.add(settingsBtn);

        topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        topPanel.add(leftControls, BorderLayout.WEST);
        topPanel.add(centerInfoPanel, BorderLayout.CENTER);
        topPanel.add(rightControls, BorderLayout.EAST);
        rootPanel.add(topPanel, BorderLayout.NORTH);

        boardPanel = new BoardGridPanel();
        boardPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        rootPanel.add(boardPanel, BorderLayout.CENTER);

        applyTheme();
        rebuildBoardIfNeeded();
    }

    private void rebuildBoardIfNeeded() {
        int size = settings.getBoardSize();
        if (cellButtons != null && cellButtons.length == size) {
            return;
        }
        boardPanel.getGridPanel().removeAll();
        boardPanel.getGridPanel().setLayout(new GridLayout(size, size, 8, 8));
        cellButtons = new AnimatedCellButton[size][size];
        int fontSize = size == 3 ? 54 : (size == 4 ? 44 : 36);
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                final int selectedRow = row;
                final int selectedCol = col;
                AnimatedCellButton button = new AnimatedCellButton(fontSize);
                button.addActionListener(event -> {
                    boolean moved = controller.playHumanMove(selectedRow, selectedCol);
                    if (moved) {
                        soundService.playMove();
                    }
                });
                button.setBorder(
                        BorderFactory.createLineBorder(ThemePalette.forMode(settings.getThemeMode()).getGridLine(), 2));
                cellButtons[row][col] = button;
                boardPanel.getGridPanel().add(button);
            }
        }
        if (confettiPanel != null) {
            confettiPanel.stop();
        }
        boardPanel.clearWinningLine();
        boardPanel.getGridPanel().revalidate();
        boardPanel.getGridPanel().repaint();
        previousBoard = new char[size][size];
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                previousBoard[row][col] = 'E';
            }
        }
        applyTheme();
    }

    private void updateBoardCells(GameState state) {
        Set<String> winning = new HashSet<>();
        for (int[] cell : state.getWinningCells()) {
            winning.add(cell[0] + ":" + cell[1]);
        }
        if (!state.isGameOver() || state.getWinningCells().isEmpty()) {
            boardPanel.clearWinningLine();
        }
        ThemePalette palette = ThemePalette.forMode(settings.getThemeMode());
        char currentPlayer = state.getCurrentPlayer();
        Color previewColor = currentPlayer == 'X' ? palette.getXColor() : palette.getOColor();
        for (int row = 0; row < state.getBoardSize(); row++) {
            for (int col = 0; col < state.getBoardSize(); col++) {
                AnimatedCellButton button = cellButtons[row][col];
                char value = state.getBoard()[row][col];
                button.setCellValue(value);
                button.applyPalette(palette);
                button.setEnabled(!state.isGameOver() && value == 'E');
                button.setBackground(palette.getCellDefault());
                button.setForeground(
                        value == 'X' ? palette.getXColor() : (value == 'O' ? palette.getOColor() : palette.getText()));
                button.setBorder(BorderFactory.createLineBorder(palette.getGridLine(), 2));
                button.setHoverPreviewChar(!state.isGameOver() ? currentPlayer : 'E', previewColor);
                if (previousBoard != null) {
                    previousBoard[row][col] = value;
                }
            }
        }
    }

    private void updateStatus(GameState state) {
        timerLabel.setVisible(settings.isTimerEnabled());
        if (state.isGameOver()) {
            if (state.getWinner() == null) {
                statusLabel.setText("Draw");
            } else {
                statusLabel.setText("Winner: " + state.getWinner());
            }
        } else {
            ThemePalette palette = ThemePalette.forMode(settings.getThemeMode());
            String colorHex;
            if (state.getCurrentPlayer() == 'X') {
                Color c = palette.getXColor();
                colorHex = String.format("#%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue());
            } else {
                Color c = palette.getOColor();
                colorHex = String.format("#%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue());
            }
            statusLabel.setText("<html>Current Turn: <font color='" + colorHex + "'>" + state.getCurrentPlayer()
                    + "</font></html>");
        }
    }

    private void openSettingsDialog() {
        controller.pauseTimer();
        SettingsDialog dialog = new SettingsDialog(this, settings, requiresRebuild -> {
            settingsManager.save(settings);
            applyTheme();
            if (requiresRebuild) {
                controller.startNewGame();
                rebuildBoardIfNeeded();
            } else {
                controller.restartTimer();
            }
            soundService.stopMusicLoop();
            soundService.startMusicLoop();
        });
        dialog.setVisible(true);
        controller.resumeTimer();
    }

    private void applyTheme() {
        ThemePalette palette = ThemePalette.forMode(settings.getThemeMode());
        animateThemeTransition(palette);
    }

    private void playWinningAnimation(GameState state) {
        if (!state.isGameOver() || state.getWinningCells().isEmpty()) {
            return;
        }
        if (winLineTimer != null && winLineTimer.isRunning()) {
            winLineTimer.stop();
        }
        ThemePalette palette = ThemePalette.forMode(settings.getThemeMode());
        List<int[]> cells = state.getWinningCells();
        int[] start = cells.get(0);
        int[] end = cells.get(cells.size() - 1);
        winLineTimer = new Timer(16, null);
        final float[] progress = { 0.0f };
        winLineTimer.addActionListener(event -> {
            progress[0] = Math.min(1.0f, progress[0] + (16.0f / 300.0f));
            float eased = 1.0f - (float) Math.pow(1.0f - progress[0], 3);
            boardPanel.setWinningLine(start, end, eased, palette.getWinLineColor());
            if (progress[0] >= 1.0f) {
                winLineTimer.stop();
            }
        });
        winLineTimer.start();
    }

    private void animateThemeTransition(ThemePalette target) {
        Color from = getContentPane().getBackground() == null ? target.getBackground()
                : getContentPane().getBackground();
        Timer timer = new Timer(16, null);
        final int[] step = { 0 };
        timer.addActionListener(event -> {
            step[0]++;
            float t = Math.min(1.0f, step[0] / 12.0f);
            Color mixed = mix(from, target.getBackground(), t);
            getContentPane().setBackground(mixed);
            headerPanel.applyPalette(target);
            rootPanel.setBackground(mixed);
            topPanel.setBackground(target.getBoard());
            boardPanel.setBackground(target.getBoard());
            statusLabel.setForeground(target.getText());
            timerLabel.setForeground(target.getAccent());
            
            undoBtn.applyPalette(target);
            redoBtn.applyPalette(target);
            newGameBtn.applyPalette(target);
            settingsBtn.applyPalette(target);
            
            if (!controller.getState().isGameOver()) {
                Color c = controller.getState().getCurrentPlayer() == 'X' ? target.getXColor() : target.getOColor();
                String colorHex = String.format("#%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue());
                statusLabel.setText("<html>Current Turn: <font color='" + colorHex + "'>"
                        + controller.getState().getCurrentPlayer() + "</font></html>");
            }
            if (cellButtons != null) {
                for (AnimatedCellButton[] row : cellButtons) {
                    for (AnimatedCellButton cell : row) {
                        cell.applyPalette(target);
                        cell.setBorder(BorderFactory.createLineBorder(target.getGridLine(), 2));
                    }
                }
            }
            if (t >= 1.0f) {
                timer.stop();
            }
        });
        timer.start();
    }

    private static Color mix(Color a, Color b, float t) {
        int r = (int) (a.getRed() + (b.getRed() - a.getRed()) * t);
        int g = (int) (a.getGreen() + (b.getGreen() - a.getGreen()) * t);
        int bl = (int) (a.getBlue() + (b.getBlue() - a.getBlue()) * t);
        return new Color(r, g, bl);
    }

    @Override
    public void dispose() {
        soundService.stopMusicLoop();
        controller.stopTimer();
        if (winLineTimer != null) {
            winLineTimer.stop();
        }
        super.dispose();
    }
}
