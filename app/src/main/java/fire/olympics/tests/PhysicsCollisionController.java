package fire.olympics.tests;

import fire.olympics.App;
import fire.olympics.game.FollowCamera;
import fire.olympics.game.FreeCamera;
import fire.olympics.display.Controller;
import fire.olympics.display.Node;
import fire.olympics.display.Renderer;
import fire.olympics.display.Window;
import fire.olympics.graphics.ModelLoader;
import fire.olympics.particles.ParticleSystem;
import fire.olympics.fontMeshCreator.FontType;
import fire.olympics.fontMeshCreator.GUIText;

import java.util.ArrayList;

import org.joml.Vector2f;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.*;

/**
 * In this scene the arrow starts directly in front of a ring.
 */
public class PhysicsCollisionController extends Controller {
    private final FollowCamera followCamera;
    private final FreeCamera freeCamera;

    private Node arrow;
    private final Vector3f arrowInitPosition = new Vector3f(-30, 35, 0);
    private final Vector3f arrowInitRotation = new Vector3f(0, 90, 0);

    private int collisionTick = 0;
    private Node collidedObject;
    private Node ring;
    private Node ringWithPole;

    private final GUIText boostText;

    private final ArrayList<Node> children = new ArrayList<>();

    /**
     * Prefer using isPlaying() and setIsPlaying(_) over reading and writing to this field directly
     * because of the side effects associated with setting isPlaying.
     */
    private boolean _playing = true;

    /**
     * Set the state of whether the game is in playing mode or not.
     *
     * @param isPlaying true if the user is controlling the arrow.
     */
    private void setIsPlaying(boolean isPlaying) {
        this._playing = isPlaying;
    }

    private boolean isPlaying() {
        return _playing;
    }

    public PhysicsCollisionController(App app, Window window, Renderer renderer, ModelLoader loader, FontType fontType) {
        super(app, window, renderer, loader);

        followCamera = new FollowCamera(window);
        followCamera.arrowSpeed = 10.0f;
        freeCamera = new FreeCamera(window);
        renderer.camera = followCamera;

        add(followCamera);
        add(freeCamera);

        boostText = new GUIText(fontType, "");
        boostText.fontSize = 1.5f;
        boostText.isCentered = true;
        boostText.color.set(1.0f, 0f, 0f);
        boostText.position.y = 0.95f;
        boostText.position.x = 0f;
        renderer.addText(boostText);

        togglePlayingMode();
    }

    @Override
    public void load() throws Exception {
        loader.loadTexture("textures", "metal_test.png");
        loader.loadTexture("textures", "wood_test_2.png");
        loader.loadTexture("textures", "stadium_aluminium.jpg").repeat(12000f / 1024f, 5000f / 1024f);
        loader.loadTexture("textures", "stadium_crowd.jpg");
        loader.loadTexture("textures", "stadium_grass.jpg").repeat(7000f / 550f, 7000f / 550f);
        loader.loadTexture("textures", "stadium_sky.jpg");
        loader.loadTexture("textures", "stadium_track.jpg").repeat(7000f / 800f, 7000f / 557f);
        loader.loadTexture("textures", "stadium_lane.jpg");
        loader.loadTexture("textures", "stadium_wood.jpeg").repeat(12000f / 474f, 4500f / 235f);
        loader.loadTexture("textures", "stadium_sky.jpg");
        loader.loadTexture("textures", "ring+pole_brushed_metal.jpg");
        loader.loadTexture("textures", "ring_black.jpg");
        loader.loadTexture("textures", "ring_blue.jpg");
        loader.loadTexture("textures", "ring_green.jpg");
        loader.loadTexture("textures", "ring_red.jpg");
        loader.loadTexture("textures", "ring_yellow.jpg");
        loader.loadTexture("textures", "pole_metal.jpg");

        arrow = loader.loadModel("models", "proto_arrow_textured.obj");
        arrow.name = "arrow";
        arrow.position.set(arrowInitPosition);
        arrow.rotation.set(arrowInitRotation);
        freeCamera.position.set(arrowInitPosition);
        freeCamera.rotation.set(arrowInitRotation);
        freeCamera.rotation.y *= -1;
        followCamera.target = arrow;
        add(arrow);

        Node brazier = loader.loadModel("models", "Brazier v2 Textured.obj");
        brazier.name = "brazier";
        brazier.position.set(0, 0, -10);
        brazier.scale = 5.0f;
        add(brazier);

        // sky4 has the smoothest sky that fits in github. export sky5 from blender for the smoothest sky
        // Index 2
        Node stadium = loader.loadModel("models", "stadium_sky4.obj");
        stadium.name = "stadium";
        stadium.scale = 7.0f;
        stadium.position.y -= 10;
        add(stadium);

        // Index 3
        ring = loader.loadModel("models", "ring.obj");
        ring.name = "ringt";
        ring.position.set(0, 2, -10);

        // Index 4
        ringWithPole = loader.loadModel("models", "ring+pole_black.obj");
        ringWithPole.name = "ring";
        ringWithPole.position.set(0, 14, 0);
        ringWithPole.rotation.y = -90;

        add(ringWithPole);
    }


    private void add(Node node) {
        children.add(node);
        renderer.add(node);
    }


    @Override
    public void update(double timeDelta) {
        checkCollision();
        if (window.isKeyDown(GLFW_KEY_Q)) {
            freeCamera.position.y -= timeDelta * 10;
        } else if (window.isKeyDown(GLFW_KEY_E)) {
            freeCamera.position.y += timeDelta * 10;
        }
        renderer.camera.update(timeDelta);
    }

    @Override
    public void keyDown(int key, int mods) {
        super.keyDown(key, mods);
        switch (key) {
            case GLFW_KEY_LEFT_SHIFT:
                if (isPlaying()) {
                    followCamera.arrowSpeed *= 2;
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void keyUp(int key, int mods) {
        super.keyUp(key, mods);
        switch (key) {
            case GLFW_KEY_SPACE:
                togglePlayingMode();
                break;
            case GLFW_KEY_LEFT_SHIFT:
                if (isPlaying()) {
                    followCamera.arrowSpeed /= 2;
                }
                break;
            case GLFW_KEY_P:
                System.out.println(renderer.camera.position);
            case GLFW_KEY_R:
                reset();
            default:
                break;
        }
    }

    private void reset() {
        arrow.position.set(arrowInitPosition);
        arrow.rotation.set(arrowInitRotation);
        freeCamera.rotation.y *= -1;
        _playing = true;
        togglePlayingMode();
    }

    private void togglePlayingMode() {
        setIsPlaying(!isPlaying());
        if (isPlaying()) {
            renderer.camera = followCamera;
            window.restoreCursor();
        } else {
            renderer.camera = freeCamera;
            freeCamera.position.set(followCamera.position);
            freeCamera.rotation.set(followCamera.rotation);
            window.disableCursor();
        }
    }

    public boolean isInside(float yRot, double width, double height, double objectx, double objecty, double objectz, double playerx, double playery, double playerz) {
        double r = width / 2;
        double total = objecty + r + (height / 2);
        double d = Math.sqrt(Math.pow(playerx - (objectx), 2) + Math.pow(playery - (total), 2) + Math.pow(playerz - (objectz), 2));
        //System.out.println("ox: " + objectx+ " oy: "+total+"  oz: " + objectz+" oa: " + yRot + "arrowx: "+ playerx+ "arrow y: " + arrow.getPosition().y()+" playerz: "+ playerz + "distance: " + d);
        //System.out.println("radius: " + r + "width: ");
        float aRot = arrow.getRotation().y;
        if (aRot < 0)
            aRot = 360 - (aRot * -1);
        if (aRot >= 360) {
            aRot -= 360;
        }
        // System.out.println("arrow angle: " + aRot + " ring angle: " + objects.get(3).getRotation().y);
        float ringRot = yRot;
        float cutOff = 30;
        float ringRotPlus90 = ringRot + 90;
        float ringRotMinus90 = ringRot - 90;
        if (ringRotMinus90 < 0)
            ringRotMinus90 = 360 - (-1 * ringRotMinus90);
        if (ringRot < 360 && ringRotPlus90 > 360) {
            ringRotPlus90 = 360 - ringRotPlus90;
        }
        if (d <= width / 2) {
            if (aRot > ringRotPlus90 - cutOff && aRot < ringRotPlus90 + cutOff) {
                return false;
            } else if (aRot > ringRotMinus90 - cutOff && aRot < ringRotMinus90 + cutOff) {
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    public void checkCollision() {

        if (arrow.getPosition().y < -6.8) {
            System.out.println("Crashed into the ground!");
            arrow.setPosition(arrowInitPosition);
            arrow.setRotation(arrowInitRotation);
        }

        for (Node child : children) {
            String name = child.name;
            if (name != null && name.equals("ring")) {
                if (collisionTick == 0 && isInside(
                        child.getRotation().y,
                        ring.getWidth(0) * child.getScale(),
                        child.getHeight(0) * child.getScale(),
                        child.getPosition().x, child.getPosition().y, child.getPosition().z,
                        arrow.getPosition().x(), arrow.getPosition().y, arrow.getPosition().z)
                ) {
                    collisionTick++;
                    collidedObject = child;
                    System.out.println("COLLIDE");
                    boostText.value = "Collision Occurred";
                }
            }
        }

        if (collidedObject != null && collisionTick > 0
                && (!(isInside(collidedObject.getRotation().y,
                ring.getWidth(0) * collidedObject.getScale(),
                collidedObject.getHeight(0) * collidedObject.getScale(),
                collidedObject.getPosition().x, collidedObject.getPosition().y,
                collidedObject.getPosition().z, arrow.getPosition().x(), arrow.getPosition().y,
                arrow.getPosition().z)))) {
            collisionTick = 0;
            collidedObject = null;
            boostText.value = "Collision Finished";
        }
    }

    // Adjust angle of camera to match mouse movement
    @Override
    public void mouseMoved(Vector2f delta) {
        if (!isPlaying()) {
            freeCamera.mouseMoved(delta);
        }
    }
}