package fire.olympics.display;

import fire.olympics.graphics.ModelLoader;
import fire.olympics.App;
import java.util.ArrayList;

import org.joml.Vector2f;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.*;

/**
 * The {@code Controller} contains game logic code such as:
 * 1. What happens after the result of a collision?
 * 2. How are things in the scene updated as a result of user interaction?
 */
public class GameController extends Controller {
    private static final float MOUSE_SENSITIVITY = 5;
    private FollowCamera followCamera;
    private boolean mouseEnabled = true;
    private boolean enableFreeCamera = true; // Used to determine if camera should be locked to arrow or not
    private boolean keyVPrev = false; // Allows V key to toggle camera type
    private float movementSpeed = 5f;
    private Vector3f angle = new Vector3f();
    private Vector3f position = new Vector3f();
    private ArrayList<GameItemGroup> objects = new ArrayList<>();
    private GameItemGroup arrow;

    public GameController(App app, Window window, Renderer renderer, ModelLoader loader) {
        super(app, window, renderer, loader);
    }

    @Override
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
        objects.get(0).setPosition(0, 0, 10);
        objects.get(1).setPosition(0, -3, -10);
        objects.get(2).setPosition(0, -7, 0);
        objects.get(2).setScale(7);
        objects.get(3).setPosition(0, 2, -10);

        for (GameItemGroup object : objects) {
            for (GameItem item : object.getAll())
                renderer.add(item);
        }

        arrow = objects.get(0);
        followCamera = new FollowCamera(arrow);
    }

    @Override
    public void update(double timeDelta) {
        // Enable or Disable free Camera
        boolean keyV = window.isKeyDown(GLFW_KEY_V);
        if(window.checkKeyState(GLFW_KEY_V, keyVPrev) == 1) {
            enableFreeCamera = !enableFreeCamera;
        }
        keyVPrev = keyV;

        // Check if freeCamera is enabled
        if (enableFreeCamera) {
            freeCameraControl(timeDelta);
        } else {
            followCameraControl(timeDelta);

            double arrowSpeed = 25;

            // Move player
            float dx = (float) ((arrowSpeed * timeDelta) * Math.sin(Math.toRadians(arrow.getRotation().y)));
            float dz = (float) ((arrowSpeed * timeDelta) * Math.cos(Math.toRadians(arrow.getRotation().y)));
            arrow.movePosition(dx, 0 , dz);

            float dy = (float) ((arrowSpeed * timeDelta) * Math.sin(Math.toRadians(arrow.getRotation().x)));
            arrow.movePosition(0, -dy, 0);

            followCamera.moveCamera();
            renderer.updateCamera(followCamera.getPosition().negate(), followCamera.getRotation());
        }

        renderer.particleSystem.update(timeDelta);
    }

    public void followCameraControl(double timeDelta) {
        if (window.isKeyDown(GLFW_KEY_W)) {
            if (arrow.getRotation().x > 75) {
                arrow.setRotX(75);
            }
            arrow.increaseRotX((float) (timeDelta * 25f));
        } else if (window.isKeyDown(GLFW_KEY_S)) {
            if (arrow.getRotation().x < -15) {
                arrow.setRotX(-15);
            }
            arrow.increaseRotX((float) - (timeDelta * 25f));
        }
        if (window.isKeyDown(GLFW_KEY_A)) {
            arrow.increaseRotY((float) (timeDelta * 50f));
            arrow.setRotZ((float) (-(timeDelta * 100f)));
        } else if (window.isKeyDown(GLFW_KEY_D)) {
            arrow.increaseRotY((float) - (timeDelta * 50f));
            arrow.setRotZ((float) ((timeDelta * 100f)));
        } else {
            arrow.setRotZ(0);
        }
    }

    // Controls for free Camera
    public void freeCameraControl(double timeDelta) {
        if (window.isKeyDown(GLFW_KEY_LEFT_SHIFT))
            movementSpeed = 35f;
        else if (window.isKeyDown(GLFW_KEY_LEFT_ALT))
            movementSpeed = 2f;
        else
            movementSpeed = 5f;

        float offsetX = 0;
        float offsetY = 0;
        float offsetZ = 0;

        if (window.isKeyDown(GLFW_KEY_A))
            offsetX += movementSpeed * timeDelta;

        if (window.isKeyDown(GLFW_KEY_D))
            offsetX -= movementSpeed * timeDelta;

        if (window.isKeyDown(GLFW_KEY_W))
            offsetZ += movementSpeed * timeDelta;

        if (window.isKeyDown(GLFW_KEY_S))
            offsetZ -= movementSpeed * timeDelta;

        if (window.isKeyDown(GLFW_KEY_LEFT_CONTROL))
            offsetY += movementSpeed * timeDelta;

        if (window.isKeyDown(GLFW_KEY_SPACE))
            offsetY -= movementSpeed * timeDelta;

        updateCameraPos(offsetX, offsetY, offsetZ);

        renderer.updateCamera(position, angle);
    }

    // Update camera position taking into account camera rotation
    public void updateCameraPos(float offsetX, float offsetY, float offsetZ) {
        if ( offsetZ != 0 ) {
            position.x += (float)Math.sin(Math.toRadians(angle.y)) * -1.0f * offsetZ;
            position.z += (float)Math.cos(Math.toRadians(angle.y)) * offsetZ;
        }
        if ( offsetX != 0) {
            position.x += (float)Math.sin(Math.toRadians(angle.y - 90)) * -1.0f * offsetX;
            position.z += (float)Math.cos(Math.toRadians(angle.y - 90)) * offsetX;
        }
        position.y += offsetY;
    }

    @Override
    public void keyDown(int key, int mods) {
        System.out.println("key down: " + key);
        switch (key) {
            case GLFW_KEY_R:
                if (enableFreeCamera) {
                    position.zero();
                    angle.zero();
                }
                break;
            default:
                return;
        }

        if (enableFreeCamera) renderer.updateCamera(position, angle);
    }

    @Override
    public void keyUp(int key, int mods) {
        System.out.println("key up: " + key);
        super.keyUp(key, mods);
    }

    @Override
    public void mouseDown(Vector2f position, int button) {
        System.out.printf("mouse down: %s; position: %4.2f, %4.2f%n", button == GLFW_MOUSE_BUTTON_LEFT ? "left" : (button == GLFW_MOUSE_BUTTON_RIGHT ? "right" : "middle"), position.x, position.y);
    }

    @Override
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

    // Adjust angle of camera to match mouse movement
    @Override
    public void mouseMoved(Vector2f delta) {
        if (enableFreeCamera) {
            if (!mouseEnabled) {
                angle.y += delta.x / MOUSE_SENSITIVITY;
                angle.x += delta.y / MOUSE_SENSITIVITY;
            }

            renderer.updateCamera(position, angle);
        }
    }
}
