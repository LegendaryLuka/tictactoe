package app.ui;

import app.engine.TicTacToeEngine;
import java.util.Scanner;

public final class ConsoleGameRunner {
    private final TicTacToeEngine engine;
    private final ConsoleRenderer renderer;

    public ConsoleGameRunner(TicTacToeEngine engine) {
        this.engine = engine;
        this.renderer = new ConsoleRenderer();
    }

    public void run() {
        renderer.printWelcome();
        try (Scanner scanner = new Scanner(System.in)) {
            ConsoleInput input = new ConsoleInput(scanner);
            boolean keepPlaying = true;
            while (keepPlaying) {
                playSingleGame(input);
                keepPlaying = input.askPlayAgain();
                if (keepPlaying) {
                    engine.resetGame();
                    System.out.println();
                }
            }
            renderer.printGoodbye();
        }
    }

    private void playSingleGame(ConsoleInput input) {
        while (!engine.isGameOver()) {
            renderer.printBoard(engine.getGrid());
            renderer.printTurn(engine.getCurrentPlayer());
            int[] move = input.readMove();
            boolean applied = engine.applyMove(move[0], move[1]);
            if (!applied) {
                renderer.printCellOccupied();
            }
        }

        renderer.printBoard(engine.getGrid());
        Character winner = engine.getWinner();
        if (winner != null) {
            renderer.printWinner(winner);
        } else {
            renderer.printDraw();
        }
    }
}
