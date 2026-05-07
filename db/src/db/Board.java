package db;

import java.io.File;
import java.io.FileWriter;
import java.util.Scanner;

public class Board {
    private char[][] grid;
    private String filename;
    
    // Using a relative path makes the code work on any computer.
    // This looks for a folder named "db" then "bin" inside your project folder.
    private static final String RELATIVE_PATH = "db" + File.separator + "bin" + File.separator;

    public Board(String filename) {
        this.filename = filename;
        this.grid = new char[3][3];
        ensureDirectoryExists();
    }

    /**
     * Creates the directory structure if it doesn't exist.
     */
    private void ensureDirectoryExists() {
        File directory = new File(RELATIVE_PATH);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    /**
     * Helper to get the File object for the current board.
     */
    private File getBoardFile() {
        return new File(RELATIVE_PATH + this.filename);
    }

    public void loadBoardFromFile() {
        File file = getBoardFile();
        if (!file.exists()) {
            System.out.println("No save file found at: " + file.getAbsolutePath());
            return;
        }

        try (Scanner scanner = new Scanner(file)) {
            int row = 0;
            while (scanner.hasNextLine() && row < 3) {
                String line = scanner.nextLine().trim();
                String[] values = line.split(",");
                for (int col = 0; col < Math.min(values.length, 3); col++) {
                    grid[row][col] = values[col].charAt(0);
                }
                row++;
            }
        } catch (Exception error) {
            error.printStackTrace();
        }
    }

    public boolean isValidBoardFile() {
        File file = getBoardFile();
        if (!file.exists()) return false;

        try (Scanner scanner = new Scanner(file)) {
            int xCount = 0, oCount = 0;
            int rowCount = 0;

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                // Validates format: Char,Char,Char
                if (!line.matches("[EXO],[EXO],[EXO]")) {
                    return false;
                }
                
                for (int i = 0; i < line.length(); i++) {
                    char c = line.charAt(i);
                    if (c == 'X') xCount++;
                    else if (c == 'O') oCount++;
                }
                rowCount++;
            }
            // A valid board must have 3 rows and valid turn counts (X goes first or tied)
            return rowCount == 3 && (xCount == oCount || xCount == oCount + 1);
        } catch (Exception error) {
            return false;
        }
    }

    public void saveBoardToFile() {
        try (FileWriter writer = new FileWriter(getBoardFile())) {
            StringBuilder sb = new StringBuilder();
            for (int row = 0; row < grid.length; row++) {
                for (int col = 0; col < grid[0].length; col++) {
                    sb.append(grid[row][col]);
                    if (col < 2) sb.append(",");
                }
                if (row < 2) sb.append("\n");
            }
            writer.write(sb.toString());
        } catch (Exception error) {
            error.printStackTrace();
        }
    }

    public void printGrid() {
        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid[0].length; col++) {
                System.out.print(grid[row][col] + " ");
            }
            System.out.println();
        }
    }

    public void createRandomBoard() {
        char[] options = {'E', 'X', 'O'};
        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid[0].length; col++) {
                int index = (int) (Math.random() * options.length);
                grid[row][col] = options[index];
            }
        }
        this.saveBoardToFile();
    }

    public void clearBoard() {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                grid[row][col] = 'E';
            }
        }
        this.saveBoardToFile();
    }

    public char getCell(int row, int col) {
        return grid[row][col];
    }

    public void setCell(int row, int col, char player) {
        grid[row][col] = player;
        this.saveBoardToFile();
    }

    public char[][] getGrid() {
        return grid;
    }

    public void setGrid(char[][] newGrid) {
        this.grid = newGrid;
        this.saveBoardToFile();
    }

    public static void main(String args[]) {
        Board b = new Board("board.csv");

        
        b.createRandomBoard();
        System.out.println("Random Board Created:");
        b.printGrid();
        
        System.out.println("Is Valid File? " + b.isValidBoardFile());
        
        b.loadBoardFromFile();
        System.out.println("\nLoaded Board From File:");
        b.printGrid();
    }
}