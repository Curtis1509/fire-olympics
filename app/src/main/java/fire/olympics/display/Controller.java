package fire.olympics.display;

import fire.olympics.graphics.ModelLoader;
import java.util.ArrayList;

public class Controller implements EventDelegate {
    public final Renderer renderer;
    public final Window window;
    private final ModelLoader loader;
    private boolean mouseEnabled = true;

    public Controller(Window window, Renderer renderer, ModelLoader loader) {
        this.renderer = renderer;
        this.window = window;
        this.loader = loader;
        window.eventDelegate = this;
    }

    public void load() throws Exception {
        loader.loadTexture("textures", "metal_test.png");
        loader.loadTexture("textures", "wood_test_2.png");
        ArrayList<GameItem> objects = loader.loadModel("models", "proto_arrow_textured.obj");

        for (GameItem object : objects) {
            object.setPosition(0, 0, -10);
            renderer.add(object);
        }
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
        if (mouseEnabled) {
            window.disableCursor();
        } else {
            window.restoreCursor();
        }
        mouseEnabled = !mouseEnabled;
    }

    public void mouseMoved(MouseState event) {
        System.out.println(String.format("mouse: x=%f, y=%f", event.position.x, event.position.y));
    }
}
