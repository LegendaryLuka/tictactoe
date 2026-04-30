package app;

import java.nio.file.Path;
import app.config.SettingsManager;
import app.ui.AdvancedGameBootstrap;

public final class TicTacToeGuiApplication {
    private TicTacToeGuiApplication() {
    }

    public static void main(String[] args) {
        SettingsManager settingsManager = new SettingsManager(Path.of("config", "tictactoe.properties"));
        AdvancedGameBootstrap.launch(settingsManager);
    }
}
