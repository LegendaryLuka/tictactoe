package app.data;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public final class CsvBoardDataSource {
    private final Path boardCsvPath;

    public CsvBoardDataSource(Path boardCsvPath) {
        this.boardCsvPath = boardCsvPath;
    }

    public char[][] loadBoard() {
        try {
            List<String> lines = Files.readAllLines(boardCsvPath);
            validateShape(lines);
            char[][] grid = new char[3][3];
            for (int row = 0; row < 3; row++) {
                String[] cells = lines.get(row).trim().split(",");
                for (int col = 0; col < 3; col++) {
                    char value = Character.toUpperCase(cells[col].trim().charAt(0));
                    if (value != 'E' && value != 'X' && value != 'O') {
                        throw new IllegalStateException("Invalid board cell value: " + value);
                    }
                    grid[row][col] = value;
                }
            }
            return grid;
        } catch (IOException error) {
            throw new IllegalStateException("Unable to read board CSV at " + boardCsvPath, error);
        }
    }

    private static void validateShape(List<String> lines) {
        if (lines.size() != 3) {
            throw new IllegalStateException("board.csv must contain exactly 3 rows.");
        }
        for (String line : lines) {
            String[] cells = line.trim().split(",");
            if (cells.length != 3) {
                throw new IllegalStateException("Each row in board.csv must contain exactly 3 comma-separated cells.");
            }
        }
    }
}
