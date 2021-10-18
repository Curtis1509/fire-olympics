package fire.olympics.audio;

import fire.olympics.App;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;

public class WavPlayer {
    private static String[] sounds = new String[] {
        "score.wav",        // 0
        "fire_start.wav",   // 1
        "theme.wav",        // 2
        "crowd.wav",        // 3
        "fire.wav",         // 4
        "boost_2s.wav",     // 5
        "crash.wav",        // 6
        "cheer.wav",        // 7
        "miss.wav"          // 8
    };

    private final ArrayList<Path> soundURL = new ArrayList<>();
    private final ArrayList<Boolean> playing = new ArrayList<>();
    private static Float[] volume = new Float[]{
            0f, // score
            -3f, // fire_start
            0f, // theme
            -20f, // crowd
            -20f, // fire
            -2f, // boost
            0f, // crash
            -2f, // cheer
            0f // miss
    };
    public boolean enabled = true;

    public WavPlayer(App app) {
        for (String fileName : sounds) {
            soundURL.add(app.resource("sounds", fileName));
            playing.add(playing.size(), false);
        }
    }

    public synchronized void playSound(int index, boolean loop) {
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
                    FloatControl volumeControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                    volumeControl.setValue(volume[index]);
                    clip.start();
                    if (loop)
                    clip.loop(Clip.LOOP_CONTINUOUSLY);
                    while(playing.get(index))
                    {
                        if (clip.getFrameLength() == clip.getFramePosition()){
                            playing.set(index, false);
                        }
                        volumeControl.setValue(volume[index]);
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

    public void stopAll() {
        for (int i = 0; i < playing.size(); i += 1) {
            playing.set(i, false);
        }
    }

    public static void setVolume(int index, float dB) { volume[index] = dB;}
}
