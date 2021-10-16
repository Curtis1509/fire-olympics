package fire.olympics.game;

import fire.olympics.App;
import fire.olympics.audio.WavPlayer;
import fire.olympics.display.Controller;
import fire.olympics.display.Node;
import fire.olympics.display.Renderer;
import fire.olympics.display.Window;
import fire.olympics.graphics.ModelLoader;
import fire.olympics.particles.ParticleSystem;
import fire.olympics.fontMeshCreator.FontType;
import fire.olympics.fontMeshCreator.GUIText;

import java.util.ArrayList;
import org.joml.Random;

import org.joml.Vector2f;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.*;

/**
 * The {@code Controller} contains game logic code such as: 1. What happens
 * after the result of a collision? 2. How are things in the scene updated as a
 * result of user interaction?
 */
public class GameController extends Controller {
    private final FollowCamera followCamera;
    private final FreeCamera freeCamera;
    private final PanningCamera panningCamera = new PanningCamera();

    private Node arrow;
    private final Vector3f arrowInitPosition = new Vector3f(-300, 35, 0);
    private final Vector3f arrowInitRotation = new Vector3f(0, 90, 0);

    private WavPlayer wavPlayer;
    private int score = 0;

    private int collisionTick = 0;
    private Node collidedObject;
    private Node ring;
    private Node ringWithPole;

    private final ParticleSystem particleSystem = new ParticleSystem(10);
    private final GUIText fireOlympicsText;
    private final GUIText scoreText;
    private final GUIText pressSpaceToPlayText;
    private final GUIText helpText;
    private final GUIText boostText;
    private String ringLocations;

    private final ArrayList<Node> children = new ArrayList<>();

    /**
     * Prefer using isPlaying() and setIsPlaying(_) over reading and writing to this field directly
     * because of the side effects associated with setting isPlaying.
     */
    private boolean _playing = false;

    /**
     * Set the state of whether the game is in playing mode or not.
     *
     * @param isPlaying true if the user is controlling the arrow.
     */
    private void setIsPlaying(boolean isPlaying) {
        this._playing = isPlaying;
        // Toggle the visibility of text on the screen.
        fireOlympicsText.isHidden = isPlaying;
        pressSpaceToPlayText.isHidden = isPlaying;
        scoreText.isHidden = !isPlaying;
        helpText.isHidden = isPlaying;
        boostText.isHidden = !isPlaying;
    }

    private boolean isPlaying() {
        return _playing;
    }

    public GameController(App app, Window window, Renderer renderer, ModelLoader loader, FontType fontType, String ringLocations) {
        super(app, window, renderer, loader);
        this.ringLocations = ringLocations;
        wavPlayer = new WavPlayer(app);
        wavPlayer.enabled = true;

        followCamera = new FollowCamera(window);
        freeCamera = new FreeCamera(window);
        renderer.camera = panningCamera;

        add(followCamera);
        add(freeCamera);
        add(panningCamera);

        fireOlympicsText = new GUIText(fontType, "FIRE OLYMPICS");
        fireOlympicsText.fontSize = 8.0f;
        fireOlympicsText.isCentered = true;
        fireOlympicsText.color.set(1.0f, 0.0f, 0.0f);
        fireOlympicsText.position.y = 0.3f;
        renderer.addText(fireOlympicsText);

        pressSpaceToPlayText = new GUIText(fontType, "press [space] to play");
        pressSpaceToPlayText.fontSize = 3.0f;
        pressSpaceToPlayText.isCentered = true;
        pressSpaceToPlayText.color.set(1.0f, 1.0f, 1.0f);
        pressSpaceToPlayText.position.y = 0.65f;
        renderer.addText(pressSpaceToPlayText);

        scoreText = new GUIText(fontType, "" + score);
        scoreText.fontSize = 4.0f;
        scoreText.isCentered = false;
        scoreText.color.set(1.0f, 0.0f, 0.0f);
        scoreText.isHidden = true;
        renderer.addText(scoreText);

        helpText = new GUIText(fontType, "Press f for free roam.");
        helpText.fontSize = 1.5f;
        helpText.isCentered = true;
        helpText.color.set(1.0f, 1.0f, 1.0f);
        helpText.position.y = 0.75f;
        renderer.addText(helpText);

        boostText = new GUIText(fontType, "Press Shift to Booooooost!");
        boostText.fontSize = 1.5f;
        boostText.isCentered = true;
        boostText.isHidden = true;
        boostText.color.set(1.0f, 0f, 0f);
        boostText.position.y = 0.95f;
        boostText.position.x = 0f;
        renderer.addText(boostText);
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
        ringWithPole = loader.loadModel("models", "ring+pole.obj");
        ringWithPole.name = "ringtp";
        ringWithPole.position.set(0, -5, -45);

        addRings(5, ringLocations);

        // Particle effects are disabled at the moment because they are buggy.
        particleSystem.texture = loader.loadTexture("textures", "fire_particle.png");
        particleSystem.randomGenerator = new Random(123);
        particleSystem.placeOnLattice();
        brazier.addChild(particleSystem);

        wavPlayer.playSound(2);
        wavPlayer.playSound(3);
    }


    private void add(Node node) {
        children.add(node);
        renderer.add(node);
    }

    private void addRings(int numOfPoles, String inputData) throws Exception {

        // System.out.println(inputData);
        // Load Rings
        // Surely there was a better way of doing this with a Stream?
        String[] names = new String[]{
                "ring+pole_black.obj",
                "ring+pole_blue.obj",
                "ring+pole_green.obj",
                "ring+pole_red.obj",
                "ring+pole_yellow.obj"
        };
        Node[] rings = new Node[names.length];
        for (int i = 0; i < names.length; i += 1) {
            rings[i] = loader.loadModel("models", names[i]);
        }

        // Randomise positions of coloured rings
        Random random = new Random();
        String dataIn = inputData;
        String[] data = dataIn.split(" ");
        int index = 1;
        boolean EOF = false;
        System.out.println("---------------------------\nLoading Rings\n-------------------------");
        for (int i = 0; i < numOfPoles; i++) {
            System.out.println("Trying ring");
            if (data[index].equals("ring" + (i))) {
                System.out.println("Found ring");
                int counter = 0;
                index++;
                while (true) {
                    if (!EOF && !data[index].contains("ring")) {
                        counter++;
                        index += 4;
                        if (data[index].equals("EOF"))
                            EOF = true;
                    } else {
                        break;
                    }
                }
                int selection = random.nextInt(counter);
                if (selection == 0)
                    selection += 1;
                selection = index - (selection * 4);
                int x = Integer.parseInt(data[selection]);
                //int y = Integer.parseInt(data[selection + 1]);
                int y = random.nextInt(45);
                y = y * -1;
                int z = Integer.parseInt(data[selection + 2]);
                int a = Integer.parseInt(data[selection + 3]);
                System.out.println("selecting: " + x + " " + y + " " + z + " " + a);
                Node ring = rings[i % rings.length].clone();
                ring.name = "ring";
                ring.position.set(x, y, z);
                ring.rotation.set(0, a, 0);
                ring.scale = 3.0f;
                add(ring);
            }
        }
    }


    @Override
    public void update(double timeDelta) {
        checkCollision();
        renderer.camera.update(timeDelta);
        particleSystem.update(timeDelta);
    }

    @Override
    public void keyDown(int key, int mods) {
        super.keyDown(key, mods);
        switch (key) {
            case GLFW_KEY_LEFT_SHIFT:
                if (isPlaying() && !wavPlayer.isPlaying(5)) {
                    wavPlayer.playSound(5);
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
            case GLFW_KEY_F:
                if (isInFreeCameraMode()) {
                    leaveFreeRoamMode();
                } else {
                    enterFreeRoamMode();
                }
                break;
            case GLFW_KEY_SPACE:
                if (!isInFreeCameraMode()) {
                    togglePlayingMode();
                }
                break;
            case GLFW_KEY_LEFT_SHIFT:
                if (isPlaying()) {
                    followCamera.arrowSpeed /= 2;
                    wavPlayer.stopSound(5);
                }
                break;
            case GLFW_KEY_P:
                System.out.println(renderer.camera.position);
            default:
                break;
        }
    }

    private void togglePlayingMode() {
        setIsPlaying(!isPlaying());
        if (isPlaying()) {
            renderer.camera = followCamera;
        } else {
            renderer.camera = panningCamera;
        }
    }

    private boolean isInFreeCameraMode() {
        return renderer.camera == freeCamera;
    }

    private void enterFreeRoamMode() {
        renderer.camera = freeCamera;
        setIsPlaying(false);
        this.fireOlympicsText.isHidden = true;
        this.scoreText.isHidden = true;
        this.pressSpaceToPlayText.isHidden = true;
        this.helpText.isHidden = true;
        window.disableCursor();
    }

    private void leaveFreeRoamMode() {
        setIsPlaying(false);
        renderer.camera = panningCamera;
        window.restoreCursor();
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
            wavPlayer.playSound(6);
        }

        for (Node child : children) {
            String name = child.name;
            if (name != null && name.equals("ring")) {
                if (collisionTick == 0 && isInside(
                        child.getRotation().y,
                        arrow.getWidth(0) * child.getScale(),
                        child.getHeight(0) * child.getScale(),
                        child.getPosition().x, child.getPosition().y, child.getPosition().z,
                        arrow.getPosition().x(), arrow.getPosition().y, arrow.getPosition().z)
                ) {
                    collisionTick++;
                    collidedObject = child;
                    System.out.println("COLLIDE");
                }
            }
        }

        if (collidedObject != null && collisionTick > 0
                && (!(isInside(collidedObject.getRotation().y,
                arrow.getWidth(0) * collidedObject.getScale(),
                collidedObject.getHeight(0) * collidedObject.getScale(),
                collidedObject.getPosition().x, collidedObject.getPosition().y,
                collidedObject.getPosition().z, arrow.getPosition().x(), arrow.getPosition().y,
                arrow.getPosition().z)))) {
            collisionTick = 0;
            collidedObject = null;
            score++;
            scoreText.value = "" + score;
            wavPlayer.playSound(0);
        }
    }

    // Adjust angle of camera to match mouse movement
    @Override
    public void mouseMoved(Vector2f delta) {
        if (isInFreeCameraMode()) {
            freeCamera.mouseMoved(delta);
        }
    }
}