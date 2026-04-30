package app.game;

import java.util.ArrayList;
import java.util.List;

public final class GameSnapshot {
    private final char[][] board;
    private final char currentPlayer;
    private final Character winner;
    private final boolean gameOver;
    private final List<int[]> winningCells;

    public GameSnapshot(char[][] board, char currentPlayer, Character winner, boolean gameOver, List<int[]> winningCells) {
        this.board = board;
        this.currentPlayer = currentPlayer;
        this.winner = winner;
        this.gameOver = gameOver;
        this.winningCells = winningCells;
    }

    public static GameSnapshot from(GameState state) {
        return new GameSnapshot(
            state.copyBoard(),
            state.getCurrentPlayer(),
            state.getWinner(),
            state.isGameOver(),
            copyCells(state.getWinningCells())
        );
    }

    public char[][] getBoard() {
        return board;
    }

    public char getCurrentPlayer() {
        return currentPlayer;
    }

    public Character getWinner() {
        return winner;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public List<int[]> getWinningCells() {
        return winningCells;
    }

    private static List<int[]> copyCells(List<int[]> source) {
        List<int[]> copy = new ArrayList<>();
        for (int[] cell : source) {
            copy.add(new int[] {cell[0], cell[1]});
        }
        return copy;
    }
}
