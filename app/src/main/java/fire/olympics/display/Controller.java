package fire.olympics.display;

public class Controller implements EventDelegate {
    public final Renderer renderer;
    public final Window window;

    public Controller(Window window, Renderer renderer) {
        this.renderer = renderer;
        this.window = window;
        window.eventDelegate = this;
    }

    public void keyDown(int key) {
        System.out.println("key down: " + key);
    }

    public void keyUp(int key) {
        System.out.println("key up: " + key);
    }

    public void mouseDown(MouseState event) {
        System.out.println(String.format("mouse down: %b, %b", event.leftButtonDown, event.rightButtonDown));
    }

    public void mouseUp(MouseState event) {
        System.out.println(String.format("mouse up: %b, %b", event.leftButtonDown, event.rightButtonDown));
    }

    public void mouseMoved(MouseState event) {
        System.out.println(String.format("mouse: x=%f, y=%f", event.position.x, event.position.y));
    }
}
