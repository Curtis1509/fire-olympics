package fire.olympics.display;

public class Controller {
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
