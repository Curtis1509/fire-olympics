package fire.olympics.display;

import fire.olympics.graphics.ModelLoader;
import java.util.ArrayList;

import org.checkerframework.checker.units.qual.A;
import org.joml.Vector2f;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.*;

/**
 * The {@code Controller} contains game logic code such as:
 * 1. What happens after the result of a collision?
 * 2. How are things in the scene updated as a result of user interaction?
 */
public class Controller implements EventDelegate {
    public final Renderer renderer;
    public final Window window;
    private final ModelLoader loader;
    private boolean mouseEnabled = true;
    private float movementSpeed = 5f;
    private Vector3f angle = new Vector3f();
    private Vector3f position = new Vector3f();
    private ArrayList<GameItemGroup> objects = new ArrayList<GameItemGroup>();

    public Controller(Window window, Renderer renderer, ModelLoader loader) {
        this.renderer = renderer;
        this.window = window;
        this.loader = loader;
        window.eventDelegate = this;
    }

    public void load() throws Exception {
        loader.loadTexture("textures", "metal_test.png");
        loader.loadTexture("textures", "wood_test_2.png");
        loader.loadTexture("textures", "stadium_aluminium.jpg");
        loader.loadTexture("textures", "stadium_crowd.jpg");
        loader.loadTexture("textures", "stadium_grass.jpg");
        loader.loadTexture("textures", "stadium_sky.jpg");
        loader.loadTexture("textures", "stadium_track.jpg");
        loader.loadTexture("textures", "stadium_wood.jpeg");
        loader.loadTexture("textures", "stadium_sky.jpg");
        loader.loadTexture("textures", "ring+pole_brushed_metal.jpg");

        // adding to ArrayList with indices, to explicitly place objects in order
        // skipping an index, or adding them out of order, will break things!!

        objects.add(0,new GameItemGroup(
                loader.loadModel("models","proto_arrow_textured.obj")
        ));

        objects.add(1, new GameItemGroup(
                loader.loadModel("models","Brazier v2 Textured.obj")
        ));

        objects.add(2, new GameItemGroup(
                loader.loadModel("models","Stadium_w_sky_sphere.obj")
        ));

        objects.add(3, new GameItemGroup(
                loader.loadModel("models","ring.obj")
        ));



        // setting initial positions
        objects.get(0).setPosition(0, 0, -10);
        objects.get(1).setPosition(0, -3, -10);
        objects.get(2).setPosition(0, -7, 0);
        objects.get(2).setScale(7);
        objects.get(3).setPosition(0, 2, -10);

        for (GameItemGroup object : objects) {
            for (GameItem item : object.getAll())
                renderer.add(item);
        }
    }

    public void update(double timeDelta) {
        // Update rotation angle of arrow
        float rotation = objects.get(0).getRotation().y() + 0.5f;
        if (rotation > 360) {
            rotation = 0;
        }
        objects.get(0).setRotation(rotation, rotation, rotation);


        // todo: use key down and up to store the delta?
        if(glfwGetKey(window.getWindowId(), GLFW_KEY_LEFT_SHIFT) == GLFW_PRESS)
            movementSpeed = 35f;
        else if(glfwGetKey(window.getWindowId(), GLFW_KEY_LEFT_ALT) == GLFW_PRESS)
            movementSpeed = 2f;
        else movementSpeed = 5f;

        if(glfwGetKey(window.getWindowId(), GLFW_KEY_A) == GLFW_PRESS)
            position.x += movementSpeed * timeDelta;

        if(glfwGetKey(window.getWindowId(), GLFW_KEY_D) == GLFW_PRESS)
            position.x -= movementSpeed * timeDelta;

        if(glfwGetKey(window.getWindowId(), GLFW_KEY_W) == GLFW_PRESS)
            position.z += movementSpeed * timeDelta;

        if(glfwGetKey(window.getWindowId(), GLFW_KEY_S) == GLFW_PRESS)
            position.z -= movementSpeed * timeDelta;

        if(glfwGetKey(window.getWindowId(), GLFW_KEY_LEFT_CONTROL) == GLFW_PRESS)
            position.y += movementSpeed * timeDelta;

        if(glfwGetKey(window.getWindowId(), GLFW_KEY_SPACE) == GLFW_PRESS)
            position.y -= movementSpeed * timeDelta;

        renderer.updateCamera(position, angle);
    }

    public void keyDown(int key) {
        System.out.println("key down: " + key);
        switch (key) {
            case GLFW_KEY_R:
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

        renderer.updateCamera(position, angle);
    }
}
