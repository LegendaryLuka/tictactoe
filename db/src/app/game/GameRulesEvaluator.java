package app.game;

import db.Board;
import db.GameLogic;
import java.util.ArrayList;
import java.util.List;

public final class GameRulesEvaluator {
    private final GameLogic gameLogic;
    private final Board legacyBoard;

    public GameRulesEvaluator(GameLogic gameLogic) {
        this.gameLogic = gameLogic;
        this.legacyBoard = new Board("board.csv");
    }

    public Result evaluate(char[][] board, int winCondition) {
        Result generic = evaluateGeneric(board, winCondition);
        int size = board.length;
        if (size == 3 && winCondition == 3) {
            Result legacy = evaluateUsingLegacyLogic(board);
            // Keep reusing legacy GameLogic, but trust generic wrapper if legacy misses a valid win.
            if (legacy.getWinner() != null || !generic.isGameOver()) {
                return legacy;
            }
        }
        return generic;
    }

    public static final class Result {
        private final Character winner;
        private final boolean draw;
        private final boolean gameOver;
        private final List<int[]> winningCells;

        public Result(Character winner, boolean draw, boolean gameOver, List<int[]> winningCells) {
            this.winner = winner;
            this.draw = draw;
            this.gameOver = gameOver;
            this.winningCells = winningCells;
        }

        public Character getWinner() {
            return winner;
        }

        public boolean isDraw() {
            return draw;
        }

        public boolean isGameOver() {
            return gameOver;
        }

        public List<int[]> getWinningCells() {
            return winningCells;
        }
    }

    private Result evaluateUsingLegacyLogic(char[][] board) {
        char[][] grid = legacyBoard.getGrid();
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                grid[row][col] = board[row][col];
            }
        }
        if (gameLogic.checkWin(legacyBoard, 'X')) {
            return new Result('X', false, true, findWinningCells(board, 3, 'X'));
        }
        if (gameLogic.checkWin(legacyBoard, 'O')) {
            return new Result('O', false, true, findWinningCells(board, 3, 'O'));
        }
        boolean draw = gameLogic.isDraw(legacyBoard);
        return new Result(null, draw, draw, new ArrayList<>());
    }

    private Result evaluateGeneric(char[][] board, int winCondition) {
        List<int[]> cellsX = findWinningCells(board, winCondition, 'X');
        if (!cellsX.isEmpty()) {
            return new Result('X', false, true, cellsX);
        }
        List<int[]> cellsO = findWinningCells(board, winCondition, 'O');
        if (!cellsO.isEmpty()) {
            return new Result('O', false, true, cellsO);
        }
        boolean draw = isDraw(board);
        return new Result(null, draw, draw, new ArrayList<>());
    }

    private static boolean isDraw(char[][] board) {
        for (char[] row : board) {
            for (char cell : row) {
                if (cell == 'E') {
                    return false;
                }
            }
        }
        return true;
    }

    public static List<int[]> findWinningCells(char[][] board, int winCondition, char player) {
        int size = board.length;
        int[][] directions = {
            {0, 1},
            {1, 0},
            {1, 1},
            {1, -1}
        };
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                if (board[row][col] != player) {
                    continue;
                }
                for (int[] direction : directions) {
                    int dr = direction[0];
                    int dc = direction[1];
                    List<int[]> line = new ArrayList<>();
                    int r = row;
                    int c = col;
                    while (r >= 0 && r < size && c >= 0 && c < size && board[r][c] == player) {
                        line.add(new int[] {r, c});
                        if (line.size() == winCondition) {
                            return line;
                        }
                        r += dr;
                        c += dc;
                    }
                }
            }
        }
        return new ArrayList<>();
    }
}
