package db;

import javax.swing.*;
import javax.swing.Timer; 
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import javax.sound.sampled.*;
import java.net.URL;

public class TicTacToeGUI extends JFrame {
    private Board board;
    private GameLogic logic;
    private JButton[][] buttons = new JButton[3][3];
    private JLabel statusLabel;
    private JPanel gridPanel;
    
    private boolean isAnimating = false;
    private boolean vsComputer = false;
    private boolean isMuted = false;

    private int currentThemeIndex = 0;
    private Color colorBg, colorGrid, colorX, colorO, colorText;

    private final List<Particle> particles = Collections.synchronizedList(new ArrayList<>());
    private Image resultImage = null;
    private boolean showResultOverlay = false;
    private int[] winningIndices = null; // FEATURE: Stores the winning line to draw it

    private final String BUBBLE_BASS_URL = "https://preview.redd.it/my-friend-group-has-been-using-the-bubble-bass-thumbs-down-v0-87dpbvtngteg1.png?width=720&format=png&auto=webp&s=e4e249ece97d0d7e75deda3a743b1ce459d8d67e";

    public TicTacToeGUI() {
        this.board = new Board("board.csv");
        this.logic = new GameLogic();
        board.clearBoard();

        applyTheme(0); 
        setupWindow();
        initializeGrid();
        setupKeyBindings(); // Improved keyboard focus
        
        new Timer(20, e -> { updateParticles(); repaint(); }).start();

        startBackgroundAmbience();
        startConsoleListener();

        setVisible(true);
        runStartupAnimation(); // RESTORED: Dramatic entrance
        
        System.out.println("--- SYSTEM ONLINE ---");
        System.out.println("Console Commands: 'row,col' | 'theme' | 'reset'");
    }

    private void setupKeyBindings() {
        // This ensures 'T' works even if a button has focus
        InputMap im = getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = getRootPane().getActionMap();
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_T, 0), "cycleTheme");
        am.put("cycleTheme", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) { cycleTheme(); }
        });
    }

    private void runStartupAnimation() {
        isAnimating = true;
        List<JButton> btnList = new ArrayList<>();
        for (JButton[] row : buttons) {
            for (JButton b : row) {
                b.setVisible(false);
                btnList.add(b);
            }
        }
        Collections.shuffle(btnList);

        final int[] idx = {0};
        Timer startTimer = new Timer(80, null);
        startTimer.addActionListener(e -> {
            if (idx[0] < btnList.size()) {
                JButton b = btnList.get(idx[0]);
                b.setVisible(true);
                spawnParticles(b, colorX);
                if (!isMuted) new Thread(() -> playMoveSound(600 + (idx[0] * 100), 50, 0.2f)).start();
                idx[0]++;
            } else {
                startTimer.stop();
                isAnimating = false;
                printConsoleBoard();
            }
        });
        startTimer.start();
    }

    private void applyTheme(int index) {
        switch (index) {
            case 1 -> { // KRUSTY KRAB
                colorBg = new Color(139, 69, 19); 
                colorGrid = new Color(205, 133, 63);
                colorX = new Color(255, 0, 0);
                colorO = new Color(255, 215, 0);
                colorText = Color.WHITE;
            }
            case 2 -> { // ROCK BOTTOM
                colorBg = new Color(20, 0, 40);
                colorGrid = new Color(40, 10, 80);
                colorX = new Color(127, 255, 0);
                colorO = new Color(255, 0, 255);
                colorText = new Color(200, 200, 255);
            }
            default -> { // CYBERPUNK
                colorBg = new Color(15, 15, 25);
                colorGrid = new Color(35, 35, 50);
                colorX = new Color(255, 50, 100);
                colorO = new Color(50, 200, 255);
                colorText = Color.WHITE;
            }
        }
        if (gridPanel != null) updateComponentColors();
    }

    private void cycleTheme() {
        currentThemeIndex = (currentThemeIndex + 1) % 3;
        applyTheme(currentThemeIndex);
        System.out.println("[THEME] Set to: " + currentThemeIndex);
    }

    private void updateComponentColors() {
        getContentPane().setBackground(colorBg);
        statusLabel.setForeground(colorText);
        gridPanel.setBackground(colorBg);
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                buttons[r][c].setBackground(colorGrid);
            }
        }
        updateUIFromBoard();
    }

    private void startConsoleListener() {
        Thread t = new Thread(() -> {
            Scanner sc = new Scanner(System.in);
            while (true) {
                try {
                    String input = sc.nextLine().toLowerCase().trim();
                    if (input.equals("theme")) SwingUtilities.invokeLater(this::cycleTheme);
                    else if (input.equals("reset")) SwingUtilities.invokeLater(this::resetGame);
                    else if (input.contains(",")) {
                        String[] pts = input.split(",");
                        int r = Integer.parseInt(pts[0].trim());
                        int c = Integer.parseInt(pts[1].trim());
                        SwingUtilities.invokeLater(() -> handleMove(r, c));
                    }
                } catch (Exception e) { System.out.println("Console Error: Use 'row,col'"); }
            }
        });
        t.setDaemon(true);
        t.start();
    }

    private void initializeGrid() {
        gridPanel = new JPanel(new GridLayout(3, 3, 15, 15)) {
            @Override
            public void paint(Graphics g) {
                super.paint(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // FEATURE: Draw the Winning Glow Line
                if (winningIndices != null) {
                    g2.setStroke(new BasicStroke(10, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g2.setColor(new Color(255, 255, 255, 150));
                    JButton b1 = buttons[winningIndices[0]/3][winningIndices[0]%3];
                    JButton b3 = buttons[winningIndices[2]/3][winningIndices[2]%3];
                    g2.drawLine(b1.getX() + b1.getWidth()/2, b1.getY() + b1.getHeight()/2,
                                b3.getX() + b3.getWidth()/2, b3.getY() + b3.getHeight()/2);
                }

                if (showResultOverlay && resultImage != null) {
                    g2.setColor(new Color(0, 0, 0, 180));
                    g2.fillRect(0, 0, getWidth(), getHeight());
                    g2.drawImage(resultImage, (getWidth()-320)/2, (getHeight()-320)/2, 320, 320, null);
                }
                synchronized(particles) {
                    for (Particle p : new ArrayList<>(particles)) {
                        g2.setColor(new Color(p.color.getRed(), p.color.getGreen(), p.color.getBlue(), p.life * 10));
                        g2.fillOval((int)p.x, (int)p.y, 8, 8);
                    }
                }
            }
        };
        gridPanel.setBackground(colorBg);
        gridPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                buttons[r][c] = new JButton();
                buttons[r][c].setBackground(colorGrid);
                buttons[r][c].setFocusable(false);
                buttons[r][c].setBorder(BorderFactory.createLineBorder(new Color(60, 60, 80), 2));
                final int row = r, col = c;
                buttons[r][c].addActionListener(e -> handleMove(row, col));
                gridPanel.add(buttons[r][c]);
            }
        }
        add(gridPanel, BorderLayout.CENTER);
    }

    private void handleMove(int row, int col) {
        if (isAnimating || logic.isGameOver(board) || showResultOverlay) return;
        if (board.getCell(row, col) != 'E') { shakeScreen(); return; }

        char player = logic.getCurrentPlayer(board);
        executeMove(row, col, player);

        if (vsComputer && !logic.isGameOver(board)) {
            isAnimating = true;
            Timer aiTimer = new Timer(600, e -> {
                int[] aiMove = logic.getRandomMove(board);
                if (aiMove != null) executeMove(aiMove[0], aiMove[1], logic.getCurrentPlayer(board));
                isAnimating = false;
                ((Timer)e.getSource()).stop();
            });
            aiTimer.start();
        }
    }

    private void executeMove(int row, int col, char player) {
        if (logic.makeMove(board, row, col)) {
            spawnParticles(buttons[row][col], player == 'X' ? colorX : colorO);
            if (!isMuted) new Thread(() -> playMoveSound(player == 'X' ? 450 : 900, 120, 0.5f)).start();
            updateUIFromBoard();
            printConsoleBoard();
            checkGameState();
        }
    }

    private void checkGameState() {
        int[] winX = logic.getWinIndices(board, 'X');
        int[] winO = logic.getWinIndices(board, 'O');
        
        if (winX != null || winO != null) {
            winningIndices = (winX != null) ? winX : winO;
            statusLabel.setText((winX != null ? "X" : "O") + " VICTORIOUS");
            triggerResultOverlay(BUBBLE_BASS_URL);
        } else if (logic.isDraw(board)) {
            statusLabel.setText("DRAW");
        }
    }

    private void resetGame() {
        board.clearBoard();
        showResultOverlay = false;
        resultImage = null;
        winningIndices = null;
        updateUIFromBoard();
        statusLabel.setText("READY");
        runStartupAnimation();
    }

    private void updateUIFromBoard() {
        char[][] grid = board.getGrid();
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                char cell = grid[r][c];
                buttons[r][c].setText(cell == 'E' ? "" : String.valueOf(cell));
                buttons[r][c].setFont(new Font("Arial", Font.BOLD, 60));
                buttons[r][c].setForeground(cell == 'X' ? colorX : colorO);
            }
        }
    }

    private void printConsoleBoard() {
        char[][] grid = board.getGrid();
        System.out.println("\n--- BOARD ---");
        for (char[] row : grid) {
            for (char cell : row) System.out.print((cell == 'E' ? "." : cell) + " ");
            System.out.println();
        }
    }

    private void triggerResultOverlay(String urlStr) {
        new Thread(() -> {
            try {
                resultImage = new ImageIcon(new URL(urlStr)).getImage();
                showResultOverlay = true;
                repaint();
            } catch (Exception e) {}
        }).start();
    }

    private void setupWindow() {
        setTitle("Tic-Tac-Toe: Sponge-Themes");
        setSize(450, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        statusLabel = new JLabel("READY", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        add(statusLabel, BorderLayout.NORTH);

        JPanel footer = new JPanel(new GridLayout(2, 1));
        JButton aiBtn = new JButton("TOGGLE AI MODE");
        aiBtn.addActionListener(e -> { vsComputer = !vsComputer; resetGame(); });
        JButton resetBtn = new JButton("RESET (OR PRESS 'T' FOR THEMES)");
        resetBtn.addActionListener(e -> resetGame());
        
        footer.add(aiBtn);
        footer.add(resetBtn);
        add(footer, BorderLayout.SOUTH);
    }

    private void startBackgroundAmbience() {
        new Thread(() -> {
            while (true) {
                if (!isMuted) playMoveSound(60, 50, 0.05f); 
                try { Thread.sleep(2000); } catch (Exception e) {}
            }
        }).start();
    }

    private void playMoveSound(int freq, int durationMs, float volume) {
        try {
            AudioFormat af = new AudioFormat(8000f, 8, 1, true, false);
            SourceDataLine sdl = AudioSystem.getSourceDataLine(af);
            sdl.open(af); sdl.start();
            int len = (8000 * durationMs / 1000);
            byte[] buf = new byte[len];
            for (int i = 0; i < len; i++) {
                double angle = i / (8000f / freq) * 2.0 * Math.PI;
                buf[i] = (byte) (Math.sin(angle) > 0 ? (127 * volume) : (-127 * volume));
                buf[i] = (byte) (buf[i] * (len - i) / len);
            }
            sdl.write(buf, 0, len);
            sdl.drain(); sdl.close();
        } catch (Exception e) {}
    }

    private void spawnParticles(JButton btn, Color color) {
        int x = btn.getX() + btn.getWidth() / 2;
        int y = btn.getY() + btn.getHeight() / 2;
        synchronized(particles) {
            for (int i = 0; i < 15; i++) particles.add(new Particle(x, y, color));
        }
    }

    private void updateParticles() {
        synchronized(particles) {
            particles.removeIf(p -> { p.update(); return p.life <= 0; });
        }
    }

    private void shakeScreen() {
        Point loc = getLocation();
        new Thread(() -> {
            try {
                for (int i = 0; i < 5; i++) {
                    setLocation(loc.x + (int)(Math.random()*10-5), loc.y + (int)(Math.random()*10-5));
                    Thread.sleep(20);
                }
                setLocation(loc);
            } catch (Exception e) {}
        }).start();
    }

    public static void main(String[] args) { SwingUtilities.invokeLater(TicTacToeGUI::new); }

    class Particle {
        double x, y, vx, vy; int life = 25; Color color;
        Particle(int x, int y, Color color) {
            this.x = x; this.y = y; this.color = color;
            this.vx = (Math.random()-0.5)*10; this.vy = (Math.random()-0.5)*10;
        }
        void update() { x += vx; y += vy; life--; }
    }
}