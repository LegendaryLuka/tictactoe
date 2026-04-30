package app.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import javax.swing.JPanel;

public final class BoardGridPanel extends JPanel {
    private final JPanel gridPanel;
    private final WinLinePanel winLinePanel;

    public BoardGridPanel() {
        setLayout(new javax.swing.OverlayLayout(this));
        winLinePanel = new WinLinePanel();
        gridPanel = new JPanel();
        gridPanel.setOpaque(false);
        add(winLinePanel);
        add(gridPanel);
    }

    @Override
    public boolean isOptimizedDrawingEnabled() {
        return false;
    }

    public JPanel getGridPanel() {
        return gridPanel;
    }

    @Override
    public void setBackground(Color bg) {
        super.setBackground(bg);
        if (gridPanel != null) gridPanel.setBackground(bg);
    }

    public void clearWinningLine() {
        winLinePanel.clearWinningLine();
    }

    public void setWinningLine(int[] startCell, int[] endCell, float lineProgress, Color lineColor) {
        winLinePanel.setWinningLine(startCell, endCell, lineProgress, lineColor);
    }

    private class WinLinePanel extends JPanel {
        private int[] startCell;
        private int[] endCell;
        private float lineProgress;
        private Color lineColor = Color.GREEN;
        private float lineThickness = 6.0f;

        public WinLinePanel() {
            setOpaque(false);
        }

        @Override
        public boolean contains(int x, int y) {
            return false;
        }

        public void clearWinningLine() {
        startCell = null;
        endCell = null;
        lineProgress = 0.0f;
        repaint();
    }

    public void setWinningLine(int[] startCell, int[] endCell, float lineProgress, Color lineColor) {
            this.startCell = startCell;
            this.endCell = endCell;
            this.lineProgress = lineProgress;
            this.lineColor = lineColor;
            int size = (int) Math.round(Math.sqrt(Math.max(1, gridPanel.getComponentCount())));
            this.lineThickness = Math.max(4.0f, 8.0f - (size - 3));
            repaint();
        }

        @Override
        public void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            if (startCell == null || endCell == null || gridPanel.getComponentCount() == 0) {
                return;
            }
            int size = (int) Math.round(Math.sqrt(gridPanel.getComponentCount()));
            if (size <= 0) {
                return;
            }
        Point p1 = centerForCell(startCell[0], startCell[1], size);
        Point p2 = centerForCell(endCell[0], endCell[1], size);
        int x2 = (int) (p1.x + (p2.x - p1.x) * lineProgress);
        int y2 = (int) (p1.y + (p2.y - p1.y) * lineProgress);

            Graphics2D g2 = (Graphics2D) graphics.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(lineColor);
            g2.setStroke(new BasicStroke(lineThickness, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.drawLine(p1.x, p1.y, x2, y2);
            g2.dispose();
        }

        private Point centerForCell(int row, int col, int size) {
            int index = row * size + col;
            java.awt.Component component = gridPanel.getComponent(index);
            return new Point(component.getX() + component.getWidth() / 2, component.getY() + component.getHeight() / 2);
        }
    }
}
