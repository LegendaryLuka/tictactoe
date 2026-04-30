package app.ui;

import app.engine.TicTacToeEngine;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public final class TicTacToeSwingFrame extends JFrame {
    private final TicTacToeEngine engine;
    private final JButton[][] cellButtons;
    private final JLabel statusLabel;

    public TicTacToeSwingFrame(TicTacToeEngine engine) {
        this.engine = engine;
        this.cellButtons = new JButton[3][3];
        this.statusLabel = new JLabel("", SwingConstants.CENTER);

        setTitle("Tic Tac Toe");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(420, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(8, 8));

        JPanel root = new JPanel(new BorderLayout(8, 8));
        root.setBorder(new EmptyBorder(12, 12, 12, 12));
        add(root, BorderLayout.CENTER);

        statusLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
        root.add(statusLabel, BorderLayout.NORTH);
        root.add(createBoardPanel(), BorderLayout.CENTER);
        root.add(createActionPanel(), BorderLayout.SOUTH);

        refreshUi();
    }

    private JPanel createBoardPanel() {
        JPanel boardPanel = new JPanel(new GridLayout(3, 3, 8, 8));
        Font cellFont = new Font(Font.SANS_SERIF, Font.BOLD, 48);
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                final int selectedRow = row;
                final int selectedCol = col;
                JButton button = new JButton("");
                button.setFocusPainted(false);
                button.setFont(cellFont);
                button.addActionListener(event -> onCellClicked(selectedRow, selectedCol));
                cellButtons[row][col] = button;
                boardPanel.add(button);
            }
        }
        return boardPanel;
    }

    private JPanel createActionPanel() {
        JPanel actionPanel = new JPanel(new GridLayout(1, 2, 8, 8));
        JButton newGameButton = new JButton("New Game");
        newGameButton.addActionListener(event -> {
            engine.resetGame();
            refreshUi();
        });
        JButton exitButton = new JButton("Exit");
        exitButton.addActionListener(event -> dispose());
        actionPanel.add(newGameButton);
        actionPanel.add(exitButton);
        return actionPanel;
    }

    private void onCellClicked(int row, int col) {
        if (engine.isGameOver()) {
            return;
        }
        boolean accepted = engine.applyMove(row, col);
        if (!accepted) {
            JOptionPane.showMessageDialog(
                this,
                "That cell is already occupied. Choose another one.",
                "Invalid Move",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }
        refreshUi();
        maybeShowGameOverDialog();
    }

    private void maybeShowGameOverDialog() {
        if (!engine.isGameOver()) {
            return;
        }
        Character winner = engine.getWinner();
        String message = winner == null ? "It's a draw. Start a new game?" : "Player " + winner + " wins! Start a new game?";
        int option = JOptionPane.showConfirmDialog(
            this,
            message,
            "Game Over",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.INFORMATION_MESSAGE
        );
        if (option == JOptionPane.YES_OPTION) {
            engine.resetGame();
            refreshUi();
        } else {
            updateInteractiveState();
        }
    }

    private void refreshUi() {
        char[][] grid = engine.getGrid();
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                char cell = grid[row][col];
                cellButtons[row][col].setText(cell == 'E' ? "" : String.valueOf(cell));
            }
        }
        statusLabel.setText(engine.isGameOver() ? buildGameOverStatus() : "Current turn: " + engine.getCurrentPlayer());
        updateInteractiveState();
    }

    private void updateInteractiveState() {
        boolean gameOver = engine.isGameOver();
        char[][] grid = engine.getGrid();
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                boolean empty = grid[row][col] == 'E';
                cellButtons[row][col].setEnabled(!gameOver && empty);
            }
        }
    }

    private String buildGameOverStatus() {
        Character winner = engine.getWinner();
        return winner == null ? "Game over: Draw" : "Game over: " + winner + " wins";
    }
}
