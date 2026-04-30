package app.config;

public final class AppSettings {
    private GameMode gameMode = GameMode.HUMAN_VS_AI;
    private AiDifficulty aiDifficulty = AiDifficulty.MEDIUM;
    private int boardSize = 3;
    private int winCondition = 3;
    private int timerSeconds = 10;
    private boolean timerEnabled = true;
    private TimerExpiryAction timerExpiryAction = TimerExpiryAction.RANDOM_MOVE;
    private boolean soundEnabled = true;
    private boolean musicEnabled = false;
    private int volumePercent = 70;
    private ThemeMode themeMode = ThemeMode.LIGHT;

    public GameMode getGameMode() {
        return gameMode;
    }

    public void setGameMode(GameMode gameMode) {
        this.gameMode = gameMode;
    }

    public AiDifficulty getAiDifficulty() {
        return aiDifficulty;
    }

    public void setAiDifficulty(AiDifficulty aiDifficulty) {
        this.aiDifficulty = aiDifficulty;
    }

    public int getBoardSize() {
        return boardSize;
    }

    public void setBoardSize(int boardSize) {
        this.boardSize = boardSize;
    }

    public int getWinCondition() {
        return winCondition;
    }

    public void setWinCondition(int winCondition) {
        this.winCondition = winCondition;
    }

    public int getTimerSeconds() {
        return timerSeconds;
    }

    public void setTimerSeconds(int timerSeconds) {
        this.timerSeconds = timerSeconds;
    }

    public boolean isTimerEnabled() {
        return timerEnabled;
    }

    public void setTimerEnabled(boolean timerEnabled) {
        this.timerEnabled = timerEnabled;
    }

    public TimerExpiryAction getTimerExpiryAction() {
        return timerExpiryAction;
    }

    public void setTimerExpiryAction(TimerExpiryAction timerExpiryAction) {
        this.timerExpiryAction = timerExpiryAction;
    }

    public boolean isSoundEnabled() {
        return soundEnabled;
    }

    public void setSoundEnabled(boolean soundEnabled) {
        this.soundEnabled = soundEnabled;
    }

    public boolean isMusicEnabled() {
        return musicEnabled;
    }

    public void setMusicEnabled(boolean musicEnabled) {
        this.musicEnabled = musicEnabled;
    }

    public int getVolumePercent() {
        return volumePercent;
    }

    public void setVolumePercent(int volumePercent) {
        this.volumePercent = volumePercent;
    }

    public ThemeMode getThemeMode() {
        return themeMode;
    }

    public void setThemeMode(ThemeMode themeMode) {
        this.themeMode = themeMode;
    }
}
