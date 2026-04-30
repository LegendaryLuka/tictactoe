package app.engine;

import app.data.CsvBoardDataSource;
import db.Board;
import db.GameLogic;

public final class TicTacToeEngine {
    private final Board board;
    private final GameLogic gameLogic;
    private final CsvBoardDataSource dataSource;
    private char currentPlayer;

    public TicTacToeEngine(Board board, GameLogic gameLogic, CsvBoardDataSource dataSource) {
        this.board = board;
        this.gameLogic = gameLogic;
        this.dataSource = dataSource;
        resetGame();
    }

    public void resetGame() {
        char[][] loaded = dataSource.loadBoard();
        char[][] target = board.getGrid();
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                target[row][col] = loaded[row][col];
            }
        }
        currentPlayer = determineNextPlayer(target);
    }

    public char getCurrentPlayer() {
        return currentPlayer;
    }

    public char[][] getGrid() {
        return board.getGrid();
    }

    public boolean applyMove(int row, int col) {
        if (!isWithinBounds(row, col)) {
            return false;
        }
        if (board.getGrid()[row][col] != 'E') {
            return false;
        }
        board.getGrid()[row][col] = currentPlayer;
        currentPlayer = currentPlayer == 'X' ? 'O' : 'X';
        return true;
    }

    public boolean isGameOver() {
        return hasWinner('X') || hasWinner('O') || gameLogic.isDraw(board);
    }

    public Character getWinner() {
        if (hasWinner('X')) {
            return 'X';
        }
        if (hasWinner('O')) {
            return 'O';
        }
        return null;
    }

    private boolean hasWinner(char player) {
        // Reuse existing GameLogic core behavior, then supplement strict column checks.
        return gameLogic.checkWin(board, player) || hasColumnWin(player);
    }

    private boolean hasColumnWin(char player) {
        char[][] grid = board.getGrid();
        for (int col = 0; col < 3; col++) {
            if (grid[0][col] == player && grid[1][col] == player && grid[2][col] == player) {
                return true;
            }
        }
        return false;
    }

    private static boolean isWithinBounds(int row, int col) {
        return row >= 0 && row < 3 && col >= 0 && col < 3;
    }

    private static char determineNextPlayer(char[][] grid) {
        int xCount = 0;
        int oCount = 0;
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                if (grid[row][col] == 'X') {
                    xCount++;
                } else if (grid[row][col] == 'O') {
                    oCount++;
                }
            }
        }
        return xCount <= oCount ? 'X' : 'O';
    }
}
