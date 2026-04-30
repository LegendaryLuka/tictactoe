package app.game;

import app.ai.AiMoveSelector;
import app.config.AppSettings;
import app.config.GameMode;
import app.config.TimerExpiryAction;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import javax.swing.Timer;

public final class AdvancedGameController {
    public interface Listener {
        void onStateChanged(GameState state);

        void onTimerTick(int secondsLeft, char player);

        void onInvalidMove();
    }

    private final AppSettings settings;
    private GameState state;
    private final GameRulesEvaluator evaluator;
    private final AiMoveSelector aiMoveSelector;
    private final List<Listener> listeners;
    private final Deque<GameSnapshot> undoStack;
    private final Deque<GameSnapshot> redoStack;
    private final Timer timer;
    private final char[][] threeByThreeTemplate;
    private int secondsLeft;

    public AdvancedGameController(
        AppSettings settings,
        GameRulesEvaluator evaluator,
        AiMoveSelector aiMoveSelector,
        char[][] threeByThreeTemplate
    ) {
        this.settings = settings;
        this.evaluator = evaluator;
        this.aiMoveSelector = aiMoveSelector;
        this.state = new GameState(settings.getBoardSize(), settings.getWinCondition());
        this.listeners = new ArrayList<>();
        this.undoStack = new ArrayDeque<>();
        this.redoStack = new ArrayDeque<>();
        this.threeByThreeTemplate = threeByThreeTemplate;
        this.secondsLeft = settings.getTimerSeconds();
        this.timer = new Timer(1000, event -> handleTimerTick());
    }

    public AppSettings getSettings() {
        return settings;
    }

    public GameState getState() {
        return state;
    }

    public void addListener(Listener listener) {
        listeners.add(listener);
        listener.onStateChanged(state);
        listener.onTimerTick(secondsLeft, state.getCurrentPlayer());
    }

    public void startNewGame() {
        undoStack.clear();
        redoStack.clear();
        if (
            state.getBoardSize() != settings.getBoardSize() ||
            state.getWinCondition() != settings.getWinCondition()
        ) {
            state = new GameState(settings.getBoardSize(), settings.getWinCondition());
        } else {
            state.reset();
        }
        applyTemplateIfAvailable();
        evaluateState();
        restartTimer();
        notifyStateChanged();
        maybePlayAiTurn();
    }

    public boolean playHumanMove(int row, int col) {
        if (state.isGameOver()) {
            return false;
        }
        if (row < 0 || row >= state.getBoardSize() || col < 0 || col >= state.getBoardSize()) {
            notifyInvalidMove();
            return false;
        }
        if (state.getBoard()[row][col] != 'E') {
            notifyInvalidMove();
            return false;
        }
        applyMove(new Move(row, col, state.getCurrentPlayer()));
        maybePlayAiTurn();
        return true;
    }

    public boolean canUndo() {
        return !undoStack.isEmpty();
    }

    public boolean canRedo() {
        return !redoStack.isEmpty();
    }

    public void undo() {
        if (!canUndo()) {
            return;
        }
        redoStack.push(GameSnapshot.from(state));
        restoreFrom(undoStack.pop());
        notifyStateChanged();
        restartTimer();
    }

    public void redo() {
        if (!canRedo()) {
            return;
        }
        undoStack.push(GameSnapshot.from(state));
        restoreFrom(redoStack.pop());
        notifyStateChanged();
        restartTimer();
    }

    public void stopTimer() {
        timer.stop();
    }

    public void restartTimer() {
        timer.stop();
        if (!settings.isTimerEnabled() || state.isGameOver()) {
            return;
        }
        secondsLeft = settings.getTimerSeconds();
        notifyTimerTick();
        timer.start();
    }

    public void pauseTimer() {
        timer.stop();
    }

    public void resumeTimer() {
        if (settings.isTimerEnabled() && !state.isGameOver()) {
            timer.start();
        }
    }

    private void handleTimerTick() {
        if (!settings.isTimerEnabled() || state.isGameOver()) {
            timer.stop();
            return;
        }
        secondsLeft--;
        notifyTimerTick();
        if (secondsLeft > 0) {
            return;
        }
        timer.stop();
        if (settings.getTimerExpiryAction() == TimerExpiryAction.FORFEIT_TURN) {
            state.setCurrentPlayer(nextPlayer(state.getCurrentPlayer()));
            restartTimer();
            notifyStateChanged();
            maybePlayAiTurn();
            return;
        }
        Move autoMove = aiMoveSelector.randomMove(state.getBoard(), state.getCurrentPlayer());
        if (autoMove != null) {
            applyMove(autoMove);
            maybePlayAiTurn();
        }
    }

    private void maybePlayAiTurn() {
        if (state.isGameOver()) {
            return;
        }
        if (settings.getGameMode() != GameMode.HUMAN_VS_AI) {
            return;
        }
        if (state.getCurrentPlayer() != 'O') {
            return;
        }
        Move aiMove = aiMoveSelector.pickMove(
            state.getBoard(),
            'O',
            state.getWinCondition(),
            settings.getAiDifficulty()
        );
        if (aiMove == null) {
            return;
        }
        applyMove(aiMove);
    }

    private void applyMove(Move move) {
        undoStack.push(GameSnapshot.from(state));
        redoStack.clear();
        state.getBoard()[move.getRow()][move.getCol()] = move.getPlayer();
        state.setCurrentPlayer(nextPlayer(move.getPlayer()));
        evaluateState();
        restartTimer();
        notifyStateChanged();
    }

    private void evaluateState() {
        GameRulesEvaluator.Result result = evaluator.evaluate(state.getBoard(), state.getWinCondition());
        state.setWinner(result.getWinner());
        state.setGameOver(result.isGameOver());
        state.setWinningCells(result.getWinningCells());
    }

    private void restoreFrom(GameSnapshot snapshot) {
        char[][] target = state.getBoard();
        char[][] source = snapshot.getBoard();
        for (int row = 0; row < target.length; row++) {
            System.arraycopy(source[row], 0, target[row], 0, target.length);
        }
        state.setCurrentPlayer(snapshot.getCurrentPlayer());
        state.setWinner(snapshot.getWinner());
        state.setGameOver(snapshot.isGameOver());
        state.setWinningCells(snapshot.getWinningCells());
    }

    private void notifyStateChanged() {
        for (Listener listener : listeners) {
            listener.onStateChanged(state);
        }
    }

    private void notifyTimerTick() {
        for (Listener listener : listeners) {
            listener.onTimerTick(secondsLeft, state.getCurrentPlayer());
        }
    }

    private void notifyInvalidMove() {
        for (Listener listener : listeners) {
            listener.onInvalidMove();
        }
    }

    private static char nextPlayer(char currentPlayer) {
        return currentPlayer == 'X' ? 'O' : 'X';
    }

    private void applyTemplateIfAvailable() {
        if (threeByThreeTemplate == null || state.getBoardSize() != 3) {
            return;
        }
        char[][] target = state.getBoard();
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                target[row][col] = threeByThreeTemplate[row][col];
            }
        }
    }
}
