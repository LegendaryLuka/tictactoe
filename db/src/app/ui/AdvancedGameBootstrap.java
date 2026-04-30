package app.ui;

import app.ai.AiMoveSelector;
import app.config.AppSettings;
import app.config.SettingsManager;
import app.data.CsvBoardDataSource;
import app.game.AdvancedGameController;
import app.game.GameRulesEvaluator;
import db.GameLogic;
import java.nio.file.Path;
import javax.swing.SwingUtilities;

public final class AdvancedGameBootstrap {
    private AdvancedGameBootstrap() {
    }

    public static void launch(SettingsManager settingsManager) {
        AppSettings settings = settingsManager.load();
        GameRulesEvaluator evaluator = new GameRulesEvaluator(new GameLogic());
        AiMoveSelector aiMoveSelector = new AiMoveSelector(evaluator);
        char[][] template = new CsvBoardDataSource(Path.of("src", "board", "board.csv")).loadBoard();
        AdvancedGameController controller = new AdvancedGameController(settings, evaluator, aiMoveSelector, template);
        SwingUtilities.invokeLater(() -> {
            AdvancedTicTacToeFrame frame = new AdvancedTicTacToeFrame(settings, settingsManager, controller);
            frame.setVisible(true);
        });
    }
}
