package app.ui;

import app.config.AppSettings;
import java.awt.Toolkit;
import java.io.File;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.swing.Timer;

public final class SoundService {
    private final AppSettings settings;
    private Timer musicTimer;

    public SoundService(AppSettings settings) {
        this.settings = settings;
    }

    private void playSoundFile(String relativePath) {
        if (!settings.isSoundEnabled()) return;
        try {
            File audioFile = new File(relativePath);
            if (!audioFile.exists()) {
                Toolkit.getDefaultToolkit().beep(); // Fallback
                return;
            }
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            
            if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                float volume = Math.max(0.0001f, settings.getVolumePercent() / 100f);
                float db = (float) (Math.log10(volume) * 20.0);
                gainControl.setValue(db);
            }
            
            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
            Toolkit.getDefaultToolkit().beep();
        }
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
        playSoundFile("res/sounds/move.wav");
    }

    public void playInvalid() {
        if (settings.isSoundEnabled()) {
            Toolkit.getDefaultToolkit().beep();
            Toolkit.getDefaultToolkit().beep();
        }
    }

    public void playWin() {
        playSoundFile("res/sounds/win.wav");
    }

    public void playDraw() {
        playSoundFile("res/sounds/draw.wav");
    }
}
