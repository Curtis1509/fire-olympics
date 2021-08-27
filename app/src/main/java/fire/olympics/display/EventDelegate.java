package fire.olympics.display;

public interface EventDelegate {
    void keyDown(int key);
    void keyUp(int key);
    void mouseDown(MouseState event);
    void mouseUp(MouseState event);
    void mouseMoved(MouseState event);
}
