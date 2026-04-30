package app.game;

import java.util.ArrayList;
import java.util.List;

public final class GameState {
    private final int boardSize;
    private final int winCondition;
    private final char[][] board;
    private char currentPlayer;
    private Character winner;
    private boolean gameOver;
    private List<int[]> winningCells;

    public GameState(int boardSize, int winCondition) {
        this.boardSize = boardSize;
        this.winCondition = winCondition;
        this.board = new char[boardSize][boardSize];
        this.winningCells = new ArrayList<>();
        reset();
    }

    public void reset() {
        for (int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize; col++) {
                board[row][col] = 'E';
            }
        }
        currentPlayer = 'X';
        winner = null;
        gameOver = false;
        winningCells.clear();
    }

    public int getBoardSize() {
        return boardSize;
    }

    public int getWinCondition() {
        return winCondition;
    }

    public char[][] getBoard() {
        return board;
    }

    public char[][] copyBoard() {
        char[][] copy = new char[boardSize][boardSize];
        for (int row = 0; row < boardSize; row++) {
            System.arraycopy(board[row], 0, copy[row], 0, boardSize);
        }
        return copy;
    }

    public char getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(char currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public Character getWinner() {
        return winner;
    }

    public void setWinner(Character winner) {
        this.winner = winner;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

    public List<int[]> getWinningCells() {
        return winningCells;
    }

    public void setWinningCells(List<int[]> winningCells) {
        this.winningCells = winningCells;
    }
}
