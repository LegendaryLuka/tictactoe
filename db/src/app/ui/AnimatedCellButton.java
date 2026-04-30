package app.ui;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JButton;
import javax.swing.Timer;

public final class AnimatedCellButton extends JButton {
    private float symbolScale = 1.0f;
    private float symbolAlpha = 1.0f;
    private int baseFontSize = 48;
    private char value = 'E';
    private String displayText = "";
    private Color defaultBackground = Color.WHITE;
    private Color hoverBackground = new Color(0xE5E7EB);
    private boolean isHovered = false;
    private char hoverPreviewChar = 'E';
    private Color hoverPreviewColor = Color.GRAY;

    public AnimatedCellButton(int fontSize) {
        this.baseFontSize = fontSize;
        setFocusPainted(false);
        setContentAreaFilled(true);
        setOpaque(true);
        setBorderPainted(true);
        setFont(new Font(Font.SANS_SERIF, Font.BOLD, fontSize));
        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                isHovered = true;
                if (value == 'E' && isEnabled()) {
                    setBackground(hoverBackground);
                }
                repaint();
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                isHovered = false;
                setBackground(defaultBackground);
                repaint();
            }
        });
    }

    public void setHoverPreviewChar(char c, Color color) {
        this.hoverPreviewChar = c;
        this.hoverPreviewColor = color;
    }

    public void applyPalette(ThemePalette palette) {
        this.defaultBackground = palette.getCellDefault();
        this.hoverBackground = palette.getCellHover();
        setBackground(defaultBackground);
    }

    public void setCellValue(char newValue) {
        if (value == 'E' && newValue != 'E') {
            symbolScale = 0.8f;
            symbolAlpha = 0.0f;
            animatePlacement();
        }
        value = newValue;
        displayText = newValue == 'E' ? "" : String.valueOf(newValue);
        if (value != 'E') {
            setBackground(defaultBackground);
        }
    }

    public char getCellValue() {
        return value;
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        Graphics2D g2 = (Graphics2D) graphics.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        super.paintComponent(g2);
        String text = displayText;
        if (!text.isEmpty()) {
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, symbolAlpha));
            int size = Math.max(12, (int) (baseFontSize * symbolScale));
            g2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, size));
            java.awt.FontMetrics fm = g2.getFontMetrics();
            int x = (getWidth() - fm.stringWidth(text)) / 2;
            int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
            g2.setColor(getForeground());
            g2.drawString(text, x, y);
        } else if (isHovered && isEnabled() && value == 'E' && hoverPreviewChar != 'E') {
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
            g2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, baseFontSize));
            java.awt.FontMetrics fm = g2.getFontMetrics();
            String previewText = String.valueOf(hoverPreviewChar);
            int x = (getWidth() - fm.stringWidth(previewText)) / 2;
            int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
            g2.setColor(hoverPreviewColor);
            g2.drawString(previewText, x, y);
        }
        g2.dispose();
    }

    private void animatePlacement() {
        Timer timer = new Timer(16, null);
        timer.addActionListener(event -> {
            symbolScale += (1.0f - symbolScale) * 0.32f;
            symbolAlpha += (1.0f - symbolAlpha) * 0.30f;
            if (Math.abs(1.0f - symbolScale) < 0.02f && Math.abs(1.0f - symbolAlpha) < 0.02f) {
                symbolScale = 1.0f;
                symbolAlpha = 1.0f;
                timer.stop();
            }
            repaint();
        });
        timer.start();
    }
}
