package app.game;

import db.GameLogic;

public final class GameRulesEvaluatorSelfTest {
    private GameRulesEvaluatorSelfTest() {
    }

    public static void main(String[] args) {
        GameRulesEvaluator evaluator = new GameRulesEvaluator(new GameLogic());

        assertWinner(
            evaluator,
            new char[][] {
                {'X', 'X', 'X'},
                {'E', 'O', 'E'},
                {'O', 'E', 'E'}
            },
            3,
            'X',
            "3x3 horizontal X win"
        );

        assertWinner(
            evaluator,
            new char[][] {
                {'O', 'X', 'E', 'E'},
                {'O', 'X', 'E', 'E'},
                {'O', 'X', 'E', 'E'},
                {'O', 'E', 'E', 'E'}
            },
            4,
            'O',
            "4x4 vertical O win"
        );

        assertWinner(
            evaluator,
            new char[][] {
                {'X', 'E', 'E', 'E', 'E'},
                {'O', 'X', 'E', 'E', 'E'},
                {'O', 'O', 'X', 'E', 'E'},
                {'E', 'E', 'E', 'X', 'E'},
                {'E', 'E', 'E', 'E', 'E'}
            },
            4,
            'X',
            "5x5 diagonal X win with win=4"
        );

        System.out.println("All GameRulesEvaluator tests passed.");
    }

    private static void assertWinner(
        GameRulesEvaluator evaluator,
        char[][] board,
        int winCondition,
        char expectedWinner,
        String label
    ) {
        GameRulesEvaluator.Result result = evaluator.evaluate(board, winCondition);
        if (result.getWinner() == null || result.getWinner() != expectedWinner) {
            throw new IllegalStateException("Failed: " + label);
        }
    }
}
