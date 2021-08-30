package fire.olympics.display;

import org.joml.Vector2f;

public interface EventDelegate {
    void updatePlayerMovement(double timeDelta);
    void keyDown(int key);
    void keyUp(int key);
    void mouseDown(Vector2f position, int button);
    void mouseUp(Vector2f position, int button);
    void mouseMoved(Vector2f delta);
}
