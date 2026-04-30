package app.ui;

import java.util.Scanner;

public final class ConsoleInput {
    private final Scanner scanner;

    public ConsoleInput(Scanner scanner) {
        this.scanner = scanner;
    }

    public int[] readMove() {
        while (true) {
            System.out.print("Enter your move as row,col (1-3 each): ");
            String raw = scanner.nextLine().trim();
            String[] parts = raw.split(",");
            if (parts.length != 2) {
                System.out.println("Invalid format. Example: 2,3");
                continue;
            }
            try {
                int row = Integer.parseInt(parts[0].trim());
                int col = Integer.parseInt(parts[1].trim());
                if (row < 1 || row > 3 || col < 1 || col > 3) {
                    System.out.println("Row and column must be between 1 and 3.");
                    continue;
                }
                return new int[] {row - 1, col - 1};
            } catch (NumberFormatException error) {
                System.out.println("Row and column must be numeric values.");
            }
        }
    }

    public boolean askPlayAgain() {
        while (true) {
            System.out.print("Play again? (y/n): ");
            String answer = scanner.nextLine().trim().toLowerCase();
            if (answer.equals("y") || answer.equals("yes")) {
                return true;
            }
            if (answer.equals("n") || answer.equals("no")) {
                return false;
            }
            System.out.println("Please enter y or n.");
        }
    }
}
