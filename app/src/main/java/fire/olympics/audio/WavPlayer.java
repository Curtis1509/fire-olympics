package fire.olympics.audio;

import fire.olympics.App;
import fire.olympics.display.GameController;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;

public class WavPlayer {

    private App app;
    private ArrayList<Path> soundURL;

    public WavPlayer(App app) {
        this.app = app;
        soundURL = new ArrayList<>();
        loadSound("score.wav"); // 0
        loadSound("miss.wav"); // 1
        loadSound("theme_8min.wav"); // 2
        loadSound("crowd_8min.wav"); // 3
        loadSound("fire_loud.wav"); // 4
        loadSound("boost.wav"); // 5
        loadSound("crash.wav"); // 6
    }

    public void loadSound(String... more) {
        soundURL.add(app.resource("sounds", more));
    }

    public synchronized void playSound(int index) {
        new Thread(new Runnable() {
            // The wrapper thread is unnecessary, unless it blocks on the
            // Clip finishing; see comments.

            public void run() {
                try {
                    URL url = soundURL.get(index).toUri().toURL();
                    AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
                    // Get a sound clip resource.
                    Clip clip = AudioSystem.getClip();
                    // Open audio clip and load samples from the audio input stream.
                    clip.open(audioIn);
                    clip.start();
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
            }
        }).start();
    }

    private synchronized void testSound(int index) {
        new Thread(new Runnable() {
            // The wrapper thread is unnecessary, unless it blocks on the
            // Clip finishing; see comments.

            public void run() {
                try {
                    URL url = soundURL.get(index).toUri().toURL();
                    AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
                    // Get a sound clip resource.
                    Clip clip = AudioSystem.getClip();
                    // Open audio clip and load samples from the audio input stream.
                    clip.open(audioIn);
//                    clip.start();
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
            }
        }).start();
    }
}