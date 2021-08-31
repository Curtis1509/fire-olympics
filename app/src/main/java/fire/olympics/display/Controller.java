package fire.olympics.display;

import fire.olympics.graphics.ModelLoader;
import java.util.ArrayList;

import org.joml.Vector2f;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.*;

public class Controller implements EventDelegate {
    public final Renderer renderer;
    public final Window window;
    private final ModelLoader loader;
    private boolean mouseEnabled = true;
    private float movementSpeed = 5f;
    private Vector3f angle = new Vector3f();
    private Vector3f position = new Vector3f();
    private Vector3f forward = new Vector3f();
    private Vector3f right = new Vector3f();

    private static final double PI_2 = Math.pow(Math.PI, 2);

    public Controller(Window window, Renderer renderer, ModelLoader loader) {
        this.renderer = renderer;
        this.window = window;
        this.loader = loader;
        window.eventDelegate = this;

        forward.set(
                Math.cos(angle.x) * Math.sin(angle.y),
                Math.sin(angle.x),
                Math.sin(angle.x) * Math.cos(angle.y)
        );

        right.set(
                Math.sin(angle.y - PI_2),
                0,
                Math.cos(angle.y - PI_2)
        );
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

    public void updatePlayerMovement(double timeDelta) {
        if(glfwGetKey(window.getWindowId(), GLFW_KEY_A) == GLFW_PRESS)
            position.add(forward.mul((float) (movementSpeed * timeDelta)));

        if(glfwGetKey(window.getWindowId(), GLFW_KEY_D) == GLFW_PRESS)
            position.sub(forward.mul((float) (movementSpeed * timeDelta)));

        if(glfwGetKey(window.getWindowId(), GLFW_KEY_W) == GLFW_PRESS)
            position.add(right.mul((float) (movementSpeed * timeDelta)));

        if(glfwGetKey(window.getWindowId(), GLFW_KEY_S) == GLFW_PRESS)
            position.sub(right.mul((float) (movementSpeed * timeDelta)));

        renderer.updateCamera(position, angle);
    }

    public void keyDown(int key) {
        System.out.println("key down: " + key);
        switch (key) {
            case GLFW_KEY_SPACE:
                position.zero();
                angle.zero();
                break;
            default:
                return;
        }

        renderer.updateCamera(position, angle);
    }

    public void keyUp(int key) {
        System.out.println("key up: " + key);
        switch (key) {
            case GLFW_KEY_ESCAPE:
                window.setShouldClose(true);
        }
    }

    public void mouseDown(Vector2f position, int button) {
        System.out.printf("mouse down: %s; position: %4.2f, %4.2f%n", button == GLFW_MOUSE_BUTTON_LEFT ? "left" : (button == GLFW_MOUSE_BUTTON_RIGHT ? "right" : "middle"), position.x, position.y);
    }

    public void mouseUp(Vector2f position, int button) {
        System.out.printf("mouse up: %s; position: %4.2f, %4.2f%n", button == GLFW_MOUSE_BUTTON_LEFT ? "left" : (button == GLFW_MOUSE_BUTTON_RIGHT ? "right" : "middle"), position.x, position.y);

        if(button == GLFW_MOUSE_BUTTON_LEFT) {
            if (mouseEnabled) {
                window.disableCursor();
            } else {
                window.restoreCursor();
            }

            mouseEnabled = !mouseEnabled;
        }
    }

    public void mouseMoved(Vector2f delta) {
        if (!mouseEnabled) {
            angle.y += delta.x / 1000;
            angle.x += delta.y / 1000;
        }

        forward.set(
                Math.cos(angle.x) * Math.sin(angle.y),
                Math.sin(angle.x),
                Math.sin(angle.x) * Math.cos(angle.y)
        );

        right.set(
                Math.sin(angle.y - PI_2),
                0,
                Math.cos(angle.y - PI_2)
        );

        renderer.updateCamera(position, angle);
    }
}
