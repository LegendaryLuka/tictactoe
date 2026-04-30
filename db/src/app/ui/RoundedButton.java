package app.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import javax.swing.JButton;
import javax.swing.Timer;

public class RoundedButton extends JButton {
    private Color baseColor;
    private Color hoverColor;
    private Color pressColor;
    private Color shadowColor;
    private Color borderColor;
    private boolean hovered;
    private boolean pressed;
    private float scale = 1.0f;

    public RoundedButton(String text) {
        super(text);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setOpaque(false);
        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                hovered = true;
                repaint();
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                hovered = false;
                pressed = false;
                repaint();
            }

            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                pressed = true;
                animateScale(0.97f);
            }

            @Override
            public void mouseReleased(java.awt.event.MouseEvent e) {
                pressed = false;
                animateScale(1.0f);
            }
        });
    }

    public void applyPalette(ThemePalette palette) {
        this.baseColor = palette.getBoard();
        this.hoverColor = palette.getButtonHover();
        this.pressColor = palette.getButtonPress();
        this.shadowColor = palette.getShadow();
        this.borderColor = palette.getGridLine();
        setForeground(palette.getText());
        repaint();
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        Graphics2D g2 = (Graphics2D) graphics.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int width = getWidth();
        int height = getHeight();
        int drawW = (int) (width * scale);
        int drawH = (int) (height * scale);
        int x = (width - drawW) / 2;
        int y = (height - drawH) / 2;
        int radius = 12;

        Color fill = baseColor;
        if (pressed && pressColor != null) {
            fill = pressColor;
        } else if (hovered && hoverColor != null) {
            fill = hoverColor;
        }

        if (shadowColor != null) {
            g2.setColor(shadowColor);
            g2.fill(new RoundRectangle2D.Float(x + 2, y + 3, drawW, drawH, radius, radius));
        }
        if (fill != null) {
            g2.setColor(fill);
            g2.fill(new RoundRectangle2D.Float(x, y, drawW, drawH, radius, radius));
        }
        if (borderColor != null) {
            g2.setColor(borderColor);
        } else {
            g2.setColor(getForeground());
        }
        g2.setStroke(new BasicStroke(1.0f));
        g2.draw(new RoundRectangle2D.Float(x, y, drawW, drawH, radius, radius));
        g2.dispose();
        super.paintComponent(graphics);
    }

    private void animateScale(float target) {
        Timer timer = new Timer(15, null);
        timer.addActionListener(event -> {
            float diff = target - scale;
            if (Math.abs(diff) < 0.01f) {
                scale = target;
                repaint();
                timer.stop();
                return;
            }
            scale += diff * 0.45f;
            repaint();
        });
        timer.start();
    }
}
