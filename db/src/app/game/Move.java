package app.game;

public final class Move {
    private final int row;
    private final int col;
    private final char player;

    public Move(int row, int col, char player) {
        this.row = row;
        this.col = col;
        this.player = player;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public char getPlayer() {
        return player;
    }
}
