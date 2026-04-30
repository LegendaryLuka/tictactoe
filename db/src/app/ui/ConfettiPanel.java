package app.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.JComponent;
import javax.swing.Timer;

public class ConfettiPanel extends JComponent {
    private static final Color[] COLORS = {
        new Color(255, 95, 86),   // Red
        new Color(255, 189, 46),  // Yellow
        new Color(39, 201, 63),   // Green
        new Color(10, 132, 255),  // Blue
        new Color(191, 90, 242),  // Purple
        new Color(255, 55, 95),   // Pink
        new Color(52, 199, 89)    // Light Green
    };

    private static class Particle {
        float x, y, size;
        float vx, vy;
        float angle, angularVelocity;
        Color color;
        boolean isCircle;

        Particle(int width) {
            Random r = new Random();
            x = r.nextInt(Math.max(1, width));
            y = -r.nextInt(200) - 20; // Start slightly above screen
            size = 8 + r.nextFloat() * 10; // Size between 8 and 18
            vx = (r.nextFloat() - 0.5f) * 6; // Horizontal drift
            vy = 3 + r.nextFloat() * 6; // Vertical initial speed
            angle = r.nextFloat() * 360;
            angularVelocity = (r.nextFloat() - 0.5f) * 15;
            color = COLORS[r.nextInt(COLORS.length)];
            isCircle = r.nextBoolean();
        }

        void update() {
            x += vx;
            y += vy;
            angle += angularVelocity;
            // Gravity
            if (vy < 15) {
                vy += 0.15f;
            }
        }
        
        void draw(Graphics2D g2) {
            AffineTransform old = g2.getTransform();
            g2.translate(x, y);
            g2.rotate(Math.toRadians(angle));
            g2.setColor(color);
            if (isCircle) {
                g2.fillOval((int)(-size/2), (int)(-size/2), (int)size, (int)size);
            } else {
                g2.fillRect((int)(-size/2), (int)(-size/4), (int)size, (int)(size/2));
            }
            g2.setTransform(old);
        }
    }

    private final List<Particle> particles = new ArrayList<>();
    private Timer timer;
    private final Runnable onComplete;

    public ConfettiPanel(Runnable onComplete) {
        this.onComplete = onComplete;
        setOpaque(false);
    }

    public void start() {
        particles.clear();
        int width = getWidth() > 0 ? getWidth() : 800;
        for (int i = 0; i < 200; i++) {
            particles.add(new Particle(width));
        }

        if (timer != null && timer.isRunning()) {
            timer.stop();
        }

        timer = new Timer(16, e -> {
            boolean active = false;
            int height = getHeight() > 0 ? getHeight() : 1000;
            for (Particle p : particles) {
                p.update();
                if (p.y < height + 50) {
                    active = true;
                }
            }
            repaint();
            if (!active) {
                timer.stop();
                if (onComplete != null) onComplete.run();
            }
        });
        timer.start();
        setVisible(true);
    }
    
    public void stop() {
        if (timer != null) {
            timer.stop();
        }
        setVisible(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (particles.isEmpty()) return;

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        for (Particle p : particles) {
            p.draw(g2);
        }
        g2.dispose();
    }
}
