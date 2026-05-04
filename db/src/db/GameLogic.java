package db;

public class GameLogic {
    
    public int[] getWinIndices(Board board, char player) {
        char[][] grid = board.getGrid();

        // Check Rows
        for (int r = 0; r < 3; r++) {
            if (grid[r][0] == player && grid[r][1] == player && grid[r][2] == player) {
                return new int[]{r * 3 + 0, r * 3 + 1, r * 3 + 2};
            }
        }

        // Check Columns
        for (int c = 0; c < 3; c++) {
            if (grid[0][c] == player && grid[1][c] == player && grid[2][c] == player) {
                return new int[]{0 * 3 + c, 1 * 3 + c, 2 * 3 + c};
            }
        }

        // Check Diagonal 1 (Top-Left to Bottom-Right)
        if (grid[0][0] == player && grid[1][1] == player && grid[2][2] == player) {
            return new int[]{0, 4, 8};
        }

        // Check Diagonal 2 (Top-Right to Bottom-Left)
        if (grid[0][2] == player && grid[1][1] == player && grid[2][0] == player) {
            return new int[]{2, 4, 6};
        }

        return null; // No win found
    }

    public boolean isDraw(Board board) {
        char[][] grid = board.getGrid();
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                if (grid[row][col] == 'E') return false;
            }
        }
        return true;
    }

    public boolean isGameOver(Board board) {
        return getWinIndices(board, 'X') != null || getWinIndices(board, 'O') != null || isDraw(board);
    }

    public char getCurrentPlayer(Board board) {
        char[][] grid = board.getGrid();
        int xCount = 0, oCount = 0;
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                if (grid[r][c] == 'X') xCount++;
                if (grid[r][c] == 'O') oCount++;
            }
        }
        return (xCount == oCount) ? 'X' : 'O';
    }
    public int[] getRandomMove(Board board) {
        java.util.List<int[]> emptyCells = new java.util.ArrayList<>();
        char[][] grid = board.getGrid();
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                if (grid[r][c] == 'E') emptyCells.add(new int[]{r, c});
            }
        }
        if (emptyCells.isEmpty()) return null;
        return emptyCells.get((int) (Math.random() * emptyCells.size()));
    }

    public boolean makeMove(Board board, int row, int col) {
        if (row < 0 || row > 2 || col < 0 || col > 2) return false;
        if (board.getCell(row, col) != 'E') return false;
        board.setCell(row, col, getCurrentPlayer(board));
        return true;
    }
}