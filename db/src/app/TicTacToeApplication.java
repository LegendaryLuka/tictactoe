package app;

import app.data.CsvBoardDataSource;
import app.engine.TicTacToeEngine;
import app.ui.ConsoleGameRunner;
import db.Board;
import db.GameLogic;
import java.nio.file.Path;

public final class TicTacToeApplication {
    private TicTacToeApplication() {
    }

    public static void main(String[] args) {
        Path templateBoard = Path.of("src", "board", "board.csv");
        CsvBoardDataSource dataSource = new CsvBoardDataSource(templateBoard);
        Board board = new Board("board.csv");
        GameLogic gameLogic = new GameLogic();
        TicTacToeEngine engine = new TicTacToeEngine(board, gameLogic, dataSource);
        ConsoleGameRunner runner = new ConsoleGameRunner(engine);
        runner.run();
    }
}
