package fire.olympics.display;

import fire.olympics.audio.WavPlayer;
import fire.olympics.game.GameController;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class Camera extends Node {
    protected GameController gameController; // used for audio volume calculations only

    public void update(double timeDelta) { }

    public void mouseDown(Vector2f position, int button) { }

    public void mouseMoved(Vector2f delta) { }

    public void mouseUp(Vector2f position, int button) { }

    public void volumeUpdate(Vector3f position) {
        if (gameController != null) {
            WavPlayer.setVolume(4,6f - ((float)Math.sqrt(gameController.pointToBrazierDistance(position)) * 1.5f));
            WavPlayer.setVolume(3,4f - ((float)Math.sqrt(gameController.pointToCrowdDistance(position)) * 0.9f));
        }
    }

}
