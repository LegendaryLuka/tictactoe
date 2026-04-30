package app.ui;

import app.config.AppSettings;
import java.awt.Toolkit;
import javax.swing.Timer;

public final class SoundService {
    private final AppSettings settings;
    private Timer musicTimer;

    public SoundService(AppSettings settings) {
        this.settings = settings;
    }

    public void startMusicLoop() {
        stopMusicLoop();
        if (!settings.isMusicEnabled() || !settings.isSoundEnabled()) {
            return;
        }
        int interval = Math.max(700, 2200 - (settings.getVolumePercent() * 12));
        musicTimer = new Timer(interval, event -> Toolkit.getDefaultToolkit().beep());
        musicTimer.start();
    }

    public void stopMusicLoop() {
        if (musicTimer != null) {
            musicTimer.stop();
            musicTimer = null;
        }
    }

    public void playMove() {
        if (settings.isSoundEnabled()) {
            Toolkit.getDefaultToolkit().beep();
        }
    }

    public void playInvalid() {
        if (settings.isSoundEnabled()) {
            Toolkit.getDefaultToolkit().beep();
            Toolkit.getDefaultToolkit().beep();
        }
    }

    public void playWin() {
        if (settings.isSoundEnabled()) {
            Toolkit.getDefaultToolkit().beep();
        }
    }

    public void playDraw() {
        if (settings.isSoundEnabled()) {
            Toolkit.getDefaultToolkit().beep();
        }
    }
}
