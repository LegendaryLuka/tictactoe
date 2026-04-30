package app.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class WindowHeaderPanel extends JPanel {
    private final Window window;
    private int initialClickX;
    private int initialClickY;
    private final JButton closeBtn;
    private final JButton minimizeBtn;
    private final JButton fullscreenBtn;
    private boolean controlsHovered = false;

    private void updateHover(boolean hover) {
        if (controlsHovered != hover) {
            controlsHovered = hover;
            closeBtn.repaint();
            minimizeBtn.repaint();
            fullscreenBtn.repaint();
        }
    }

    public WindowHeaderPanel(Window window) {
        this.window = window;
        setLayout(new FlowLayout(FlowLayout.LEFT, 8, 8));
        setOpaque(true);

        closeBtn = createMacControlButton(new Color(255, 95, 86), 0);
        minimizeBtn = createMacControlButton(new Color(255, 189, 46), 1);
        fullscreenBtn = createMacControlButton(new Color(39, 201, 63), 2);

        MouseAdapter hoverAdapter = new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                updateHover(true);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                updateHover(false);
            }
        };
        closeBtn.addMouseListener(hoverAdapter);
        minimizeBtn.addMouseListener(hoverAdapter);
        fullscreenBtn.addMouseListener(hoverAdapter);

        closeBtn.addActionListener(e -> {
            window.dispose();
            if (window instanceof JFrame) {
                System.exit(0);
            }
        });
        minimizeBtn.addActionListener(e -> {
            if (window instanceof JFrame) {
                ((JFrame) window).setState(JFrame.ICONIFIED);
            }
        });
        fullscreenBtn.addActionListener(e -> {
            if (window instanceof JFrame) {
                JFrame frame = (JFrame) window;
                GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
                if (frame.getExtendedState() == JFrame.MAXIMIZED_BOTH || gd.getFullScreenWindow() == frame) {
                    gd.setFullScreenWindow(null);
                    frame.setExtendedState(JFrame.NORMAL);
                } else {
                    frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
                    // Or gd.setFullScreenWindow(frame);
                }
            }
        });

        add(closeBtn);
        if (window instanceof JFrame) {
            add(minimizeBtn);
            add(fullscreenBtn);
        }

        MouseAdapter dragger = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                initialClickX = e.getX();
                initialClickY = e.getY();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                int thisX = window.getLocation().x;
                int thisY = window.getLocation().y;
                int xMoved = e.getX() - initialClickX;
                int yMoved = e.getY() - initialClickY;
                window.setLocation(thisX + xMoved, thisY + yMoved);
            }
        };
        addMouseListener(dragger);
        addMouseMotionListener(dragger);

        window.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                applyWindowShape();
            }
        });
        applyWindowShape();
    }

    private void applyWindowShape() {
        if (window.getWidth() > 0 && window.getHeight() > 0) {
            window.setShape(
                    new java.awt.geom.RoundRectangle2D.Double(0, 0, window.getWidth(), window.getHeight(), 14, 14));
        }
    }

    private JButton createMacControlButton(Color color, int type) {
        JButton btn = new JButton() {
            @Override
            protected void paintComponent(java.awt.Graphics g) {
                java.awt.Graphics2D g2 = (java.awt.Graphics2D) g.create();
                g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING,
                        java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                int size = 12;
                int x = (getWidth() - size) / 2;
                int y = (getHeight() - size) / 2;
                g2.fillOval(x, y, size, size);

                if (controlsHovered) {
                    g2.setColor(new Color(0, 0, 0, 150));
                    g2.setStroke(new java.awt.BasicStroke(1.2f, java.awt.BasicStroke.CAP_ROUND,
                            java.awt.BasicStroke.JOIN_ROUND));
                    int cx = getWidth() / 2;
                    int cy = getHeight() / 2;
                    if (type == 0) { // close
                        g2.drawLine(cx - 3, cy - 3, cx + 3, cy + 3);
                        g2.drawLine(cx + 3, cy - 3, cx - 3, cy + 3);
                    } else if (type == 1) { // minimize
                        g2.drawLine(cx - 3, cy, cx + 3, cy);
                    } else if (type == 2) { // fullscreen
                        g2.fillPolygon(new int[] { cx - 3, cx, cx - 3 }, new int[] { cy - 3, cy - 3, cy }, 3);
                        g2.fillPolygon(new int[] { cx + 3, cx, cx + 3 }, new int[] { cy + 3, cy + 3, cy }, 3);
                    }
                }
                g2.dispose();
            }
        };
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setPreferredSize(new Dimension(16, 16));
        return btn;
    }

    public void applyPalette(ThemePalette palette) {
        setBackground(palette.getBoard());
    }
}
