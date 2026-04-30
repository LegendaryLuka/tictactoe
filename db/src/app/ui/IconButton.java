package app.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Path2D;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import javax.swing.JButton;
import javax.swing.Timer;

public class IconButton extends JButton {
    public enum IconType { UNDO, REDO, REFRESH, GEAR }
    
    private final IconType iconType;
    private Color iconColor;
    private Color hoverColor;
    private Color pressColor;
    
    private float hoverAlpha = 0f;
    private boolean pressed = false;
    private Timer animTimer;

    public IconButton(IconType type, String tooltip) {
        this.iconType = type;
        setToolTipText(tooltip);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setOpaque(false);
        setPreferredSize(new Dimension(36, 36));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!isEnabled()) return;
                animateHover(1f);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                pressed = false;
                animateHover(0f);
            }
            @Override
            public void mousePressed(MouseEvent e) {
                if (!isEnabled()) return;
                pressed = true;
                repaint();
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                if (!isEnabled()) return;
                pressed = false;
                repaint();
            }
        });
    }

    private void animateHover(float target) {
        if (animTimer != null && animTimer.isRunning()) animTimer.stop();
        animTimer = new Timer(16, e -> {
            float diff = target - hoverAlpha;
            if (Math.abs(diff) < 0.05f) {
                hoverAlpha = target;
                animTimer.stop();
            } else {
                hoverAlpha += diff * 0.2f;
            }
            repaint();
        });
        animTimer.start();
    }

    public void applyPalette(ThemePalette palette) {
        boolean dark = palette.getBackground().getRed() < 128;
        if (dark) {
            iconColor = new Color(255, 255, 255);
            hoverColor = new Color(42, 42, 42);
            pressColor = new Color(60, 60, 60);
        } else {
            if (palette.getGridLine().equals(new Color(0x38BDF8))) { // Ocean Theme
                iconColor = new Color(15, 23, 42); 
                hoverColor = new Color(224, 242, 254);
                pressColor = new Color(186, 230, 253);
            } else { // Light theme
                iconColor = new Color(17, 24, 39);
                hoverColor = new Color(229, 231, 235);
                pressColor = new Color(209, 213, 219);
            }
        }
        repaint();
    }

    @Override
    public void setEnabled(boolean b) {
        super.setEnabled(b);
        if (!b) {
            hoverAlpha = 0f;
            pressed = false;
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        if (isEnabled() && hoverAlpha > 0) {
            Color bg = pressed ? pressColor : hoverColor;
            if (bg != null) {
                g2.setColor(new Color(bg.getRed(), bg.getGreen(), bg.getBlue(), (int)(255 * hoverAlpha)));
                g2.fillRoundRect(2, 2, w - 4, h - 4, 10, 10);
            }
        }

        if (iconColor != null) {
            if (isEnabled()) {
                g2.setColor(new Color(iconColor.getRed(), iconColor.getGreen(), iconColor.getBlue(), 255));
            } else {
                // Solid grey to prevent overlap artifacts
                boolean dark = iconColor.getRed() > 128;
                g2.setColor(dark ? new Color(100, 100, 100) : new Color(180, 180, 180));
            }
            
            float cx = w / 2f;
            float cy = h / 2f;
            g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            if (iconType == IconType.UNDO) {
                drawUndo(g2, cx, cy);
            } else if (iconType == IconType.REDO) {
                drawRedo(g2, cx, cy);
            } else if (iconType == IconType.REFRESH) {
                drawRefresh(g2, cx, cy);
            } else if (iconType == IconType.GEAR) {
                drawGear(g2, cx, cy);
            }
        }
        g2.dispose();
    }

    private void drawUndo(Graphics2D g2, float cx, float cy) {
        Path2D.Float path = new Path2D.Float();
        // Head pointing left
        path.moveTo(cx - 8, cy);
        path.lineTo(cx - 1, cy - 6);
        path.lineTo(cx - 1, cy - 2.5f);
        // Curved tail
        path.curveTo(cx + 5, cy - 2.5f, cx + 7, cy + 1, cx + 7, cy + 6);
        path.lineTo(cx + 3.8f, cy + 6);
        path.curveTo(cx + 3.8f, cy + 2, cx + 2, cy + 0.5f, cx - 1, cy + 0.5f);
        // Back to head
        path.lineTo(cx - 1, cy + 6);
        path.closePath();
        g2.fill(path);
    }

    private void drawRedo(Graphics2D g2, float cx, float cy) {
        Path2D.Float path = new Path2D.Float();
        // Head pointing right
        path.moveTo(cx + 8, cy);
        path.lineTo(cx + 1, cy - 6);
        path.lineTo(cx + 1, cy - 2.5f);
        // Curved tail
        path.curveTo(cx - 5, cy - 2.5f, cx - 7, cy + 1, cx - 7, cy + 6);
        path.lineTo(cx - 3.8f, cy + 6);
        path.curveTo(cx - 3.8f, cy + 2, cx - 2, cy + 0.5f, cx + 1, cy + 0.5f);
        // Back to head
        path.lineTo(cx + 1, cy + 6);
        path.closePath();
        g2.fill(path);
    }

    private void drawRefresh(Graphics2D g2, float cx, float cy) {
        float r = 7.0f;
        g2.setStroke(new BasicStroke(2.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        // Circular arc: start at 80 degrees, extent -320 (gap at the top)
        g2.draw(new java.awt.geom.Arc2D.Float(cx - r, cy - r, 2 * r, 2 * r, 80, -320, java.awt.geom.Arc2D.OPEN));
        
        // Symmetrical arrowhead perfectly placed at the start of the arc
        Graphics2D g3 = (Graphics2D) g2.create();
        double startAngle = Math.toRadians(80);
        float ax = (float)(cx + r * Math.cos(startAngle));
        float ay = (float)(cy - r * Math.sin(startAngle));
        
        g3.translate(ax, ay);
        g3.rotate(Math.toRadians(9)); // Aligned tangent to the circle
        
        Path2D.Float head = new Path2D.Float();
        head.moveTo(-1, -5);
        head.lineTo(6, 0);
        head.lineTo(-1, 5);
        head.closePath();
        g3.fill(head);
        g3.dispose();
    }

    private void drawGear(Graphics2D g2, float cx, float cy) {
        Path2D.Float gearPath = new Path2D.Float();
        int teeth = 8;
        float rInner = 6.6f;
        float rOuter = 8.8f;
        float toothBaseWidth = (float) Math.PI / 8;
        float toothTopWidth = (float) Math.PI / 12;

        for (int i = 0; i < teeth; i++) {
            double angle = i * 2 * Math.PI / teeth;
            double a1 = angle - toothBaseWidth / 2;
            double a2 = angle - toothTopWidth / 2;
            double a3 = angle + toothTopWidth / 2;
            double a4 = angle + toothBaseWidth / 2;

            if (i == 0) gearPath.moveTo(cx + rInner * Math.cos(a1), cy + rInner * Math.sin(a1));
            else gearPath.lineTo(cx + rInner * Math.cos(a1), cy + rInner * Math.sin(a1));
            
            gearPath.lineTo(cx + rOuter * Math.cos(a2), cy + rOuter * Math.sin(a2));
            gearPath.lineTo(cx + rOuter * Math.cos(a3), cy + rOuter * Math.sin(a3));
            gearPath.lineTo(cx + rInner * Math.cos(a4), cy + rInner * Math.sin(a4));
        }
        gearPath.closePath();

        Area gearArea = new Area(gearPath);
        gearArea.subtract(new Area(new Ellipse2D.Float(cx - 4.5f, cy - 4.5f, 9, 9)));

        // Even thicker stroke to maximize rounding and smoothness
        g2.setStroke(new BasicStroke(3.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.fill(gearArea);
        g2.draw(gearArea);
    }
}
