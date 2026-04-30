package app.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public final class SettingsManager {
    private final Path configPath;

    public SettingsManager(Path configPath) {
        this.configPath = configPath;
    }

    public AppSettings load() {
        AppSettings settings = new AppSettings();
        if (!Files.exists(configPath)) {
            return settings;
        }
        Properties properties = new Properties();
        try (InputStream inputStream = Files.newInputStream(configPath)) {
            properties.load(inputStream);
            settings.setGameMode(readEnum(properties, "gameMode", GameMode.class, settings.getGameMode()));
            settings.setAiDifficulty(readEnum(properties, "aiDifficulty", AiDifficulty.class, settings.getAiDifficulty()));
            settings.setBoardSize(readInt(properties, "boardSize", settings.getBoardSize(), 3, 5));
            int maxWin = settings.getBoardSize();
            settings.setWinCondition(readInt(properties, "winCondition", settings.getWinCondition(), 3, maxWin));
            settings.setTimerSeconds(readInt(properties, "timerSeconds", settings.getTimerSeconds(), 5, 30));
            settings.setTimerEnabled(Boolean.parseBoolean(properties.getProperty("timerEnabled", String.valueOf(settings.isTimerEnabled()))));
            settings.setTimerExpiryAction(
                readEnum(properties, "timerExpiryAction", TimerExpiryAction.class, settings.getTimerExpiryAction())
            );
            settings.setSoundEnabled(Boolean.parseBoolean(properties.getProperty("soundEnabled", String.valueOf(settings.isSoundEnabled()))));
            settings.setMusicEnabled(Boolean.parseBoolean(properties.getProperty("musicEnabled", String.valueOf(settings.isMusicEnabled()))));
            settings.setVolumePercent(readInt(properties, "volumePercent", settings.getVolumePercent(), 0, 100));
            settings.setThemeMode(readEnum(properties, "themeMode", ThemeMode.class, settings.getThemeMode()));
            return settings;
        } catch (IOException error) {
            return settings;
        }
    }

    public void save(AppSettings settings) {
        Properties properties = new Properties();
        properties.setProperty("gameMode", settings.getGameMode().name());
        properties.setProperty("aiDifficulty", settings.getAiDifficulty().name());
        properties.setProperty("boardSize", String.valueOf(settings.getBoardSize()));
        properties.setProperty("winCondition", String.valueOf(settings.getWinCondition()));
        properties.setProperty("timerSeconds", String.valueOf(settings.getTimerSeconds()));
        properties.setProperty("timerEnabled", String.valueOf(settings.isTimerEnabled()));
        properties.setProperty("timerExpiryAction", settings.getTimerExpiryAction().name());
        properties.setProperty("soundEnabled", String.valueOf(settings.isSoundEnabled()));
        properties.setProperty("musicEnabled", String.valueOf(settings.isMusicEnabled()));
        properties.setProperty("volumePercent", String.valueOf(settings.getVolumePercent()));
        properties.setProperty("themeMode", settings.getThemeMode().name());
        try {
            Path parent = configPath.getParent();
            if (parent != null && !Files.exists(parent)) {
                Files.createDirectories(parent);
            }
            try (OutputStream outputStream = Files.newOutputStream(configPath)) {
                properties.store(outputStream, "TicTacToe settings");
            }
        } catch (IOException ignored) {
        }
    }

    private static int readInt(Properties properties, String key, int fallback, int min, int max) {
        try {
            int value = Integer.parseInt(properties.getProperty(key, String.valueOf(fallback)));
            return Math.max(min, Math.min(max, value));
        } catch (NumberFormatException error) {
            return fallback;
        }
    }

    private static <T extends Enum<T>> T readEnum(Properties properties, String key, Class<T> type, T fallback) {
        try {
            return Enum.valueOf(type, properties.getProperty(key, fallback.name()));
        } catch (IllegalArgumentException error) {
            return fallback;
        }
    }
}
