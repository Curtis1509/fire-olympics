package fire.olympics.display;

import fire.olympics.graphics.ModelLoader;
import fire.olympics.App;

import java.util.ArrayList;
import java.util.Random;

import fire.olympics.particles.ParticleSystem;

import org.joml.Vector2f;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.*;

/**
 * The {@code Controller} contains game logic code such as: 1. What happens
 * after the result of a collision? 2. How are things in the scene updated as a
 * result of user interaction?
 */
public class GameController extends Controller {
    private static final float MOUSE_SENSITIVITY = 5;
    private FollowCamera followCamera;
    private FreeCamera freeCamera;
    private boolean mouseEnabled = true;
    private boolean enableFreeCamera = true; // Used to determine if camera should be locked to arrow or not
    private boolean keyVPrev = false; // Allows V key to toggle camera type
//    private float movementSpeed = 5f;
    private Vector3f angle = new Vector3f();
    private Vector3f position = new Vector3f();
    private ArrayList<GameItemGroup> objects = new ArrayList<>();
    private GameItemGroup arrow;
    private int numOfPoles = 10;

    private ParticleSystem particleSystem = new ParticleSystem(100);

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
        loader.loadTexture("textures", "stadium_lane.jpg");
        loader.loadTexture("textures", "stadium_wood.jpeg");
        loader.loadTexture("textures", "stadium_sky.jpg");
        loader.loadTexture("textures", "ring+pole_brushed_metal.jpg");

        // adding to ArrayList with indices, to explicitly place objects in order
        // skipping an index, or adding them out of order, will break things!!

        objects.add(0, new GameItemGroup(loader.loadModel("models", "proto_arrow_textured.obj")));

        objects.add(1, new GameItemGroup(loader.loadModel("models", "Brazier v2 Textured.obj")));

        // stadium_old has the black sky. sky4 has the smoothest sky that fits in github. export sky5 from blender
        // for the smoothest sky
//        objects.add(2, new GameItemGroup(loader.loadModel("models", "stadium_old.obj")));
        objects.add(2, new GameItemGroup(loader.loadModel("models", "stadium_sky4.obj")));
//        objects.add(2, new GameItemGroup(loader.loadModel("models", "stadium_sky5.obj")));

        objects.add(3, new GameItemGroup(loader.loadModel("models", "ring.obj")));

        int size = objects.size();
        //int sizeBeforePoles = size;
        // Add ring poles to the model loader
        for (int i = size; i < numOfPoles; i++) {
            objects.add(i, new GameItemGroup(loader.loadModel("models", "ring+pole.obj")));
        }

        // setting initial positions
        objects.get(0).setPosition(0, 0, 10);
        objects.get(1).setPosition(0, -3, -10);
        objects.get(2).setPosition(0, -7, 0);
        objects.get(2).setScale(7);
        objects.get(3).setPosition(0, 2, -10);

        Random r = new Random();
        int lowX = -100;
        int highX = 100;
        int lowY = -15;
        int highY = -5;
        int lowZ = -100;
        int highZ = 100;

        // Spawn ring and poles in random positions
        for (int i = size; i < objects.size(); i++) {
            int resultX = r.nextInt(highX - lowX) + lowX;
            int resultY = r.nextInt(highY - lowY) + lowY;
            int resultZ = r.nextInt(highZ - lowZ) + lowZ;

            objects.get(i).setPosition(resultX, resultY, -resultZ);
            objects.get(i).setScale(2);
        }

        for (GameItemGroup object : objects) {
            for (GameItem item : object.getAll())
                renderer.add(item);
        }

        // Particle effects are disabled at the moment because they are buggy.
        // particleSystem.texture = loader.loadTexture("textures", "fire_particle.png");
        // renderer.add(particleSystem);

        arrow = objects.get(0);
        followCamera = new FollowCamera(window, arrow);

        freeCamera = new FreeCamera(window, renderer, position, angle);
    }

    @Override
    public void update(double timeDelta) {
        checkCollision();
        // Enable or Disable free Camera
        boolean keyV = window.isKeyDown(GLFW_KEY_V);
        if (window.checkKeyState(GLFW_KEY_V, keyVPrev) == 1) {
            enableFreeCamera = !enableFreeCamera;
        }
        keyVPrev = keyV;

        // Check if freeCamera is enabled
        if (enableFreeCamera) {
            freeCamera.freeCameraControl(timeDelta);
        } else {
            followCamera.followCameraControl(timeDelta);

            double arrowSpeed = 25;

            // Move player
            float dx = (float) ((arrowSpeed * timeDelta) * Math.sin(Math.toRadians(arrow.getRotation().y)));
            float dz = (float) ((arrowSpeed * timeDelta) * Math.cos(Math.toRadians(arrow.getRotation().y)));
            arrow.movePosition(dx, 0, dz);

            float dy = (float) ((arrowSpeed * timeDelta) * Math.sin(Math.toRadians(arrow.getRotation().x)));
            arrow.movePosition(0, -dy, 0);

            followCamera.moveCamera();
            renderer.updateCamera(followCamera.getPosition().negate(), followCamera.getRotation());
        }

        particleSystem.update(timeDelta);
    }

    int collisionTick = 0;

    public void checkCollision() {
        // System.out.println("a" + arrow.getPosition().x + " r" +
        // objects.get(3).getPosition().z);
        if (collisionTick == 0 && arrow.getPosition().x >= objects.get(3).getPosition().x
                && arrow.getPosition().x <= objects.get(3).getPosition().x + 4f
                && arrow.getPosition().y >= objects.get(3).getPosition().y
                && arrow.getPosition().y <= objects.get(3).getPosition().y + 4f
                && arrow.getPosition().z >= objects.get(3).getPosition().z
                && arrow.getPosition().z <= objects.get(3).getPosition().z + 4f) {
            collisionTick++;
            App.score++;
            renderer.updateText(1, "" + App.score);

            System.out.println("COLLIDE");
        } else if (collisionTick > 0 && (!(arrow.getPosition().x >= objects.get(3).getPosition().x
                && arrow.getPosition().x <= objects.get(3).getPosition().x + 4f
                && arrow.getPosition().y >= objects.get(3).getPosition().y
                && arrow.getPosition().y <= objects.get(3).getPosition().y + 4f
                && arrow.getPosition().z >= objects.get(3).getPosition().z
                && arrow.getPosition().z <= objects.get(3).getPosition().z + 4f))) {
            collisionTick = 0;
        }
    }

    @Override
    public void keyDown(int key, int mods) {
        System.out.println("key down: " + key);

        if (freeCamera.isEnabled())
            renderer.updateCamera(position, angle);
    }

    @Override
    public void keyUp(int key, int mods) {
        System.out.println("key up: " + key);
        super.keyUp(key, mods);
    }

    @Override
    public void mouseDown(Vector2f position, int button) {
        System.out.printf("mouse down: %s; position: %4.2f, %4.2f%n",
                button == GLFW_MOUSE_BUTTON_LEFT ? "left" : (button == GLFW_MOUSE_BUTTON_RIGHT ? "right" : "middle"),
                position.x, position.y);
    }

    @Override
    public void mouseUp(Vector2f position, int button) {
        System.out.printf("mouse up: %s; position: %4.2f, %4.2f%n",
                button == GLFW_MOUSE_BUTTON_LEFT ? "left" : (button == GLFW_MOUSE_BUTTON_RIGHT ? "right" : "middle"),
                position.x, position.y);

        if (button == GLFW_MOUSE_BUTTON_LEFT) {
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
