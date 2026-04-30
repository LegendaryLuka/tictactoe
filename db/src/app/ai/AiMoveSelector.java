package app.ai;

import app.config.AiDifficulty;
import app.game.GameRulesEvaluator;
import app.game.Move;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class AiMoveSelector {
    private final Random random;
    private final GameRulesEvaluator evaluator;

    public AiMoveSelector(GameRulesEvaluator evaluator) {
        this.random = new Random();
        this.evaluator = evaluator;
    }

    public Move pickMove(char[][] board, char aiPlayer, int winCondition, AiDifficulty difficulty) {
        List<int[]> available = availableMoves(board);
        if (available.isEmpty()) {
            return null;
        }
        if (difficulty == AiDifficulty.EASY) {
            int[] move = available.get(random.nextInt(available.size()));
            return new Move(move[0], move[1], aiPlayer);
        }
        if (difficulty == AiDifficulty.MEDIUM) {
            int[] medium = pickMediumMove(board, aiPlayer, winCondition, available);
            return new Move(medium[0], medium[1], aiPlayer);
        }
        int[] hard = pickHardMove(board, aiPlayer, winCondition, available);
        return new Move(hard[0], hard[1], aiPlayer);
    }

    public Move randomMove(char[][] board, char player) {
        List<int[]> available = availableMoves(board);
        if (available.isEmpty()) {
            return null;
        }
        int[] move = available.get(random.nextInt(available.size()));
        return new Move(move[0], move[1], player);
    }

    private int[] pickMediumMove(char[][] board, char aiPlayer, int winCondition, List<int[]> available) {
        char opponent = otherPlayer(aiPlayer);
        int[] winning = findImmediateWin(board, aiPlayer, winCondition, available);
        if (winning != null) {
            return winning;
        }
        int[] block = findImmediateWin(board, opponent, winCondition, available);
        if (block != null) {
            return block;
        }
        int center = board.length / 2;
        if (board.length % 2 == 1 && board[center][center] == 'E') {
            return new int[] {center, center};
        }
        List<int[]> corners = corners(board);
        List<int[]> freeCorners = new ArrayList<>();
        for (int[] corner : corners) {
            if (board[corner[0]][corner[1]] == 'E') {
                freeCorners.add(corner);
            }
        }
        if (!freeCorners.isEmpty()) {
            return freeCorners.get(random.nextInt(freeCorners.size()));
        }
        return available.get(random.nextInt(available.size()));
    }

    private int[] pickHardMove(char[][] board, char aiPlayer, int winCondition, List<int[]> available) {
        int depthLimit = board.length <= 3 ? 9 : (board.length == 4 ? 6 : 4);
        int bestScore = Integer.MIN_VALUE;
        int[] bestMove = available.get(0);
        for (int[] move : available) {
            board[move[0]][move[1]] = aiPlayer;
            int score = minimax(board, aiPlayer, otherPlayer(aiPlayer), winCondition, 1, depthLimit, Integer.MIN_VALUE, Integer.MAX_VALUE);
            board[move[0]][move[1]] = 'E';
            if (score > bestScore) {
                bestScore = score;
                bestMove = move;
            }
        }
        return bestMove;
    }

    private int minimax(
        char[][] board,
        char maximizingPlayer,
        char currentPlayer,
        int winCondition,
        int depth,
        int depthLimit,
        int alpha,
        int beta
    ) {
        GameRulesEvaluator.Result result = evaluator.evaluate(board, winCondition);
        if (result.isGameOver() || depth >= depthLimit) {
            return evaluatePosition(result, board, maximizingPlayer, winCondition, depth);
        }
        List<int[]> moves = availableMoves(board);
        if (currentPlayer == maximizingPlayer) {
            int maxScore = Integer.MIN_VALUE;
            for (int[] move : moves) {
                board[move[0]][move[1]] = currentPlayer;
                int score = minimax(
                    board,
                    maximizingPlayer,
                    otherPlayer(currentPlayer),
                    winCondition,
                    depth + 1,
                    depthLimit,
                    alpha,
                    beta
                );
                board[move[0]][move[1]] = 'E';
                maxScore = Math.max(maxScore, score);
                alpha = Math.max(alpha, score);
                if (beta <= alpha) {
                    break;
                }
            }
            return maxScore;
        }
        int minScore = Integer.MAX_VALUE;
        for (int[] move : moves) {
            board[move[0]][move[1]] = currentPlayer;
            int score = minimax(
                board,
                maximizingPlayer,
                otherPlayer(currentPlayer),
                winCondition,
                depth + 1,
                depthLimit,
                alpha,
                beta
            );
            board[move[0]][move[1]] = 'E';
            minScore = Math.min(minScore, score);
            beta = Math.min(beta, score);
            if (beta <= alpha) {
                break;
            }
        }
        return minScore;
    }

    private int evaluatePosition(GameRulesEvaluator.Result result, char[][] board, char maximizingPlayer, int winCondition, int depth) {
        if (result.isGameOver()) {
            if (result.getWinner() == null) {
                return 0;
            }
            return result.getWinner() == maximizingPlayer ? 10_000 - depth : depth - 10_000;
        }
        char minimizingPlayer = otherPlayer(maximizingPlayer);
        int maxPotential = linePotential(board, maximizingPlayer, winCondition);
        int minPotential = linePotential(board, minimizingPlayer, winCondition);
        return maxPotential - minPotential;
    }

    private int linePotential(char[][] board, char player, int winCondition) {
        int size = board.length;
        int[][] dirs = {{0, 1}, {1, 0}, {1, 1}, {1, -1}};
        int score = 0;
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                for (int[] d : dirs) {
                    int endRow = row + d[0] * (winCondition - 1);
                    int endCol = col + d[1] * (winCondition - 1);
                    if (endRow < 0 || endRow >= size || endCol < 0 || endCol >= size) {
                        continue;
                    }
                    int mine = 0;
                    int other = 0;
                    for (int step = 0; step < winCondition; step++) {
                        char cell = board[row + d[0] * step][col + d[1] * step];
                        if (cell == player) {
                            mine++;
                        } else if (cell != 'E') {
                            other++;
                        }
                    }
                    if (other == 0 && mine > 0) {
                        score += mine * mine;
                    }
                }
            }
        }
        return score;
    }

    private int[] findImmediateWin(char[][] board, char player, int winCondition, List<int[]> available) {
        for (int[] move : available) {
            board[move[0]][move[1]] = player;
            boolean wins = evaluator.evaluate(board, winCondition).getWinner() != null;
            board[move[0]][move[1]] = 'E';
            if (wins) {
                return move;
            }
        }
        return null;
    }

    private static char otherPlayer(char player) {
        return player == 'X' ? 'O' : 'X';
    }

    private static List<int[]> availableMoves(char[][] board) {
        List<int[]> moves = new ArrayList<>();
        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board.length; col++) {
                if (board[row][col] == 'E') {
                    moves.add(new int[] {row, col});
                }
            }
        }
        return moves;
    }

    private static List<int[]> corners(char[][] board) {
        int max = board.length - 1;
        List<int[]> corners = new ArrayList<>();
        corners.add(new int[] {0, 0});
        corners.add(new int[] {0, max});
        corners.add(new int[] {max, 0});
        corners.add(new int[] {max, max});
        return corners;
    }
}
