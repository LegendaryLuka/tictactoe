package app.ui;

public final class ConsoleRenderer {
    public void printWelcome() {
        System.out.println("=== Tic Tac Toe ===");
        System.out.println("Players alternate turns: X then O.");
        System.out.println();
    }

    public void printBoard(char[][] grid) {
        System.out.println("   1   2   3");
        for (int row = 0; row < 3; row++) {
            System.out.print((row + 1) + " ");
            for (int col = 0; col < 3; col++) {
                char value = grid[row][col];
                char display = value == 'E' ? ' ' : value;
                System.out.print(" " + display + " ");
                if (col < 2) {
                    System.out.print("|");
                }
            }
            System.out.println();
            if (row < 2) {
                System.out.println("  ---+---+---");
            }
        }
        System.out.println();
    }

    public void printTurn(char player) {
        System.out.println("Current turn: " + player);
    }

    public void printCellOccupied() {
        System.out.println("That cell is already occupied. Choose a different move.");
    }

    public void printWinner(char winner) {
        System.out.println("Game Over: Player " + winner + " wins!");
    }

    public void printDraw() {
        System.out.println("Game Over: It's a draw.");
    }

    public void printGoodbye() {
        System.out.println("Thanks for playing.");
    }
}
