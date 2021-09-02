package fire.olympics.display;

import org.joml.Vector2f;

/**
 * Specifies the contract between the controller and the window class.
 * 
 * A window will call these methods when an event occurs.
 */
public interface EventDelegate {
    void update(double timeDelta);
    // key is not ascii, compare to GLFW_KEY_* instead.
    void keyDown(int key);
    void keyUp(int key);
    // Compare button against GLFW_MOUSE_BUTTON_*
    void mouseDown(Vector2f position, int button);
    void mouseUp(Vector2f position, int button);
    void mouseMoved(Vector2f delta);
}
