package fire.olympics.audio;

import fire.olympics.App;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;

public class WavPlayer {
    private static String[] sounds = new String[] {
        "score.wav",        // 0
        "miss.wav",         // 1
        "theme_8min.wav",   // 2
        "crowd_8min.wav",   // 3
        "fire_loud.wav",    // 4
        "boost.wav",        // 5
        "crash.wav",        // 6
    };

    private final ArrayList<Path> soundURL = new ArrayList<>();
    private final ArrayList<Boolean> playing = new ArrayList<>();
    public boolean enabled = true;

    public WavPlayer(App app) {
        for (String fileName : sounds) {
            soundURL.add(app.resource("sounds", fileName));
            playing.add(playing.size(), false);
        }
    }

    public synchronized void playSound(int index) {
        if (!enabled) return;
        playing.set(index,true);
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
                    while(playing.get(index))
                    {
                        if (clip.getMicrosecondLength() == clip.getMicrosecondPosition()){
                            playing.set(index, false);
                        }
                    }
                    playing.set(index,false);
                    clip.stop();
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
            }
        }).start();
    }

    public void stopSound(int index){
        playing.set(index,false);
    }

    public boolean isPlaying(int index){
        if (playing.get(index))
            return true;
        return false;
    }

}
