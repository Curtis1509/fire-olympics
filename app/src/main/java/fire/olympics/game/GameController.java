package fire.olympics.game;

import fire.olympics.App;
import fire.olympics.audio.WavPlayer;
import fire.olympics.display.Controller;
import fire.olympics.display.GameItem;
import fire.olympics.display.Node;
import fire.olympics.display.Renderer;
import fire.olympics.display.Window;
import fire.olympics.graphics.ModelLoader;
import fire.olympics.particles.ParticleSystem;
import fire.olympics.particles.SoftCampFireEmitter;
import fire.olympics.physics.EllipsoidConstraint;
import fire.olympics.physics.PhysicsBody;
import fire.olympics.physics.PlaneConstraint;
import fire.olympics.fontMeshCreator.FontType;
import fire.olympics.fontMeshCreator.GUIText;

import java.util.Optional;
import java.util.ArrayList;
import org.joml.Random;

import org.joml.Vector2f;
import org.joml.Vector3f;

import static org.joml.Vector3f.distance;
import static org.joml.Vector2f.distance;
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
    private final Vector3f skyInitPosition = new Vector3f(-300, -25, 0);

    private WavPlayer wavPlayer;
    private int score = 0;

    private int collisionTick = 0;
    private Node collidedObject;
    private Node ring;
    private Node brazier;
    private Node sky;

    private final SoftCampFireEmitter brazierFire = new SoftCampFireEmitter(500);
    private final SoftCampFireEmitter arrowFire = new SoftCampFireEmitter(200);
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

        followCamera = new FollowCamera(window, this);
        freeCamera = new FreeCamera(window, this);
        panningCamera.setGameController(this);
        renderer.setCamera(panningCamera);

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
        loader.loadTexture("textures", "stadium_crowd.jpg").repeat(14000f / 1920f * 6f, 3500f / 1080f * 6f);
        loader.loadTexture("textures", "stadium_grass.jpg").repeat(8000f / 550f, 8000f / 550f);
        loader.loadTexture("textures", "stadium_sky.jpg");
        loader.loadTexture("textures", "stadium_track.jpg").repeat(28000f / 800f, 28000f / 557f);
        loader.loadTexture("textures", "stadium_lane.jpg").repeat(3000f / 800f, 7000f / 557f);
        loader.loadTexture("textures", "stadium_wood.jpeg").repeat(36000f / 474f, 9000f / 235f);
        loader.loadTexture("textures", "stadium_sky.jpg");
        loader.loadTexture("textures", "ring+pole_brushed_metal.jpg");
        loader.loadTexture("textures", "ring_black.jpg").repeat(3f,1f);
        loader.loadTexture("textures", "ring_blue.jpg").repeat(3f,1f);
        loader.loadTexture("textures", "ring_green.jpg").repeat(3f,1f);
        loader.loadTexture("textures", "ring_red.jpg").repeat(3f,1f);
        loader.loadTexture("textures", "ring_yellow.jpg").repeat(3f,1f);
        loader.loadTexture("textures", "pole_metal.jpg").repeat(1f,9f);

        Node stadium = loader.loadModel("models", "stadium_nosky.obj");
        stadium.name = "stadium";
        stadium.scale = 7.0f;
        stadium.position.y -= 10;
        add(stadium);

        sky = loader.loadModel("models", "sky_dome.obj");
        sky.name = "sky";
        sky.scale = 10.0f;
        sky.position.set(skyInitPosition);
        sky.rotation.y = 180;
        renderer.addSky(sky);
        followCamera.setSky(sky);

        arrow = loader.loadModel("models", "proto_arrow_textured.obj");
        arrow.name = "arrow";
        arrow.position.set(arrowInitPosition);
        arrow.rotation.set(arrowInitRotation);
        followCamera.target = arrow;
        add(arrow);

        arrowFire.texture = loader.loadTexture("textures", "fire_particle.png");
        arrowFire.position.z = 3.5f;
        arrowFire.rotation.x = -90.0f;
        arrowFire.startRadius = 0.5f;
        arrowFire.endRadius = 2.5f;
        arrowFire.fanPosition.y = 3.5f;
        arrowFire.lookAtFan = true;
        arrow.addChild(arrowFire);

        brazier = loader.loadModel("models", "Brazier v2 Textured.obj");
        brazier.name = "brazier";
        brazier.position.set(0, -3, -10);
        brazier.scale = 5.0f;
        add(brazier);

        brazier.physicsBody = new PhysicsBody();
        PlaneConstraint plane = new PlaneConstraint();
        plane.origin.y = 1.0f;
        brazier.physicsBody.constraints.add(plane);
        EllipsoidConstraint ellipsoid = new EllipsoidConstraint();
        ellipsoid.origin.y = 1.0f;
        ellipsoid.scale.y = 4.0f;
        ellipsoid.scale.x = 2.0f;
        ellipsoid.scale.z = 2.0f;
        brazier.physicsBody.constraints.add(ellipsoid);

        brazierFire.texture = loader.loadTexture("textures", "fire_particle.png");
        brazierFire.position.y = 2.0f;
        brazierFire.enabled = false;
        brazier.addChild(brazierFire);

        ring = loader.loadModel("models", "ring.obj");
        ring.name = "ringt";
        ring.position.set(0, 2, -10);

        addRings(5, ringLocations);

        wavPlayer.playSound(2, true);
        wavPlayer.playSound(3, true);
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
                SoftCampFireEmitter emitter = new SoftCampFireEmitter(100);
                emitter.enabled = false;
                GameItem actualRing = (GameItem) ring.children.get(1);
                emitter.position.y = actualRing.mesh.vertexLessThanEveryOtherVertex.y + actualRing.mesh.getHeight()/2;
                emitter.name = "fire-emitter";
                emitter.texture = loader.loadTexture("textures", "fire_particle.png");
                ring.addChild(emitter);
                add(ring);
            }
        }
    }

    @Override
    public void update(double timeDelta) {
        checkCollision();
        tryLightBrazierOnFire();
        for (Node child : children) {
            child.update(timeDelta);
        }
    }


    private void tryLightBrazierOnFire() {
        // Okay, so what we need to do is check whether the position of the arrow in the
        // coordinate space of the brazier is inside of the brazier's physics body.
        // In particular I think we're interested in the tip of the arrow which is
        // located where the particle effect is positioned. Note that a node's position
        // is relative to it's parent's coordinate space, which means that
        // arrowFire.position is meaningful in the arrow's local coordinate space.
        Vector3f fireSource = brazier.convertPointFromCoordinateSpace(arrow, arrowFire.position);
        if (brazier.physicsBody.isPointInsideBody(fireSource)) {
            if (!brazierFire.enabled)
                wavPlayer.playSound(1, false);
            if (!wavPlayer.isPlaying(4))
                wavPlayer.playSound(4, true);
            brazierFire.enabled = true;
        }
    }

    double currentTime = 0;
    double boostStartTime = 0;
    double coolDownStartTime = 0;
    boolean boosting = false;
    float oldFOV;

    public synchronized void boost() {
        new Thread(new Runnable() {
            public void run() {
                while (true) {
                    if (!wavPlayer.isPlaying(5)) {
                        System.out.println("boosting");
                        boostText.value = "BOOSTING";
                        wavPlayer.playSound(5, false);
                        currentTime = glfwGetTime();
                        boostStartTime = glfwGetTime();
                        followCamera.arrowSpeed+=5;
                        renderer.setFieldOfView(renderer.getFieldOfView()+0.25f);

                    }
                    else if (glfwGetTime() >= currentTime + 0.2 && wavPlayer.isPlaying(5)){
                        followCamera.arrowSpeed+=5;
                        currentTime = glfwGetTime();
                        boostText.value="|"+boostText.value+"|";
                        renderer.setFieldOfView(renderer.getFieldOfView()+0.25f);
                    }
                    else if (currentTime > boostStartTime + 3.0){
                        System.out.println("boosting stopped");
                        boostText.value = "BACKING OFF";
                        break;
                    }
                }

                currentTime = glfwGetTime();
                while (true){
                    if (followCamera.arrowSpeed>40f){
                        if (glfwGetTime() > currentTime+0.2) {
                            followCamera.arrowSpeed -= 5;
                            currentTime = glfwGetTime();
                            boostText.value="|"+boostText.value+"|";
                            if (renderer.getFieldOfView() > oldFOV)
                            renderer.setFieldOfView(renderer.getFieldOfView()-0.25f);
                        }
                    }
                    else {
                        break;
                    }
                }
                followCamera.arrowSpeed=40f;
                boosting = false;
                coolDownStartTime = glfwGetTime();
                renderer.setFieldOfView(oldFOV);
                int timer = 5;
                while (timer >= 0){
                    if (glfwGetTime() > currentTime+1) {
                        currentTime = glfwGetTime();
                        boostText.value = "" + timer;
                        timer--;
                    }
                }
                boostText.value = "Press Shift to Booooooost!";
            }
        }).start();
    }

    @Override
    public void keyDown(int key, int mods) {
        super.keyDown(key, mods);
        switch (key) {
            case GLFW_KEY_LEFT_SHIFT:
                if ((coolDownStartTime==0 || coolDownStartTime < glfwGetTime() - 6) && !boosting && isPlaying()) {
                    boosting = true;
                    oldFOV = renderer.getFieldOfView();
                    boost();
                }
                break;
            case GLFW_KEY_P:
                if (isInFreeCameraMode()) {
                    freeCamera.positionToConsole();
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
                break;
            case GLFW_KEY_P:
                // System.out.println(renderer.camera.position);
                break;
            default:
                break;
        }
    }

    private void togglePlayingMode() {
        setIsPlaying(!isPlaying());
        if (isPlaying()) {
            renderer.setCamera(followCamera);
        } else {
            renderer.setCamera(panningCamera);
        }
    }

    private boolean isInFreeCameraMode() {
        return freeCamera.isActiveCamera;
    }

    private void enterFreeRoamMode() {
        renderer.setCamera(freeCamera);
        setIsPlaying(false);
        this.fireOlympicsText.isHidden = true;
        this.scoreText.isHidden = true;
        this.pressSpaceToPlayText.isHidden = true;
        this.helpText.isHidden = true;
    }

    private void leaveFreeRoamMode() {
        setIsPlaying(false);
        renderer.setCamera(panningCamera);
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

        if (arrow.position.y < -6.8) {
            System.out.println("Crashed into the ground!");
            arrow.position.set(arrowInitPosition);
            arrow.rotation.set(arrowInitRotation);
            sky.position.set(skyInitPosition);
            wavPlayer.playSound(6, false);
            score = 0;
            scoreText.value = "" + score;
            brazierFire.enabled = false;
            // TODO andrew how do we turn off fire on the rings?
            for (Node child : children) {
                Optional<Node> emitter = child.findNodeNamed("fire-emitter");
                if (emitter.isPresent() && emitter.get() instanceof SoftCampFireEmitter) {
                    SoftCampFireEmitter fireEmitter = (SoftCampFireEmitter) emitter.get();
                    fireEmitter.enabled = false;
                }
            }
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
                    Optional<Node> emitter = child.findNodeNamed("fire-emitter");
                    if (emitter.isPresent() && emitter.get() instanceof SoftCampFireEmitter) {
                        SoftCampFireEmitter fireEmitter = (SoftCampFireEmitter) emitter.get();
                        fireEmitter.enabled = true;
                    }
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
            score++;
            scoreText.value = "" + score;
            wavPlayer.playSound(0, false);
        }
    }

    public float arrowToBrazierDistance() { // used for calculating sound
        return pointToBrazierDistance(arrow.position);
    }

    public float pointToBrazierDistance(Vector3f point) { // used for calculating sound
        return vectorPointDistance(point, brazier.position);
    }

    public float arrowToCrowdDistance() {
        return pointToCrowdDistance(arrow.position);
    }

    public float pointToCrowdDistance(Vector3f point) { // used for calculating sound
        // TODO: use distance between point and line formulas instead
        return Math.min(distance(point.x,point.y,380f,25f),
                Math.min(distance(point.x, point.y, -380f, 25f),
                 Math.min(distance(point.y,point.z,25f,240f),
                  Math.min(distance(point.y,point.z,25f,-240f),
                   100f))));
    }

    private float vectorPointDistance(Vector3f p0, Vector3f p1) {
        // this exists because the vector3f distance formula takes coordinates not vectors
        return distance(p0.x, p0.y, p0.z, p1.x, p1.y, p1.z);
    }

    private float vectorPointDistance2D(Vector3f p0, float x, float y, float z) {
        // this exists because the vector3f distance formula takes coordinates not vectors
        return distance(p0.x, p0.y, p0.z, x, y, z);
    }

    // Adjust angle of camera to match mouse movement
    @Override
    public void mouseMoved(Vector2f delta) {
        if (isInFreeCameraMode()) {
            freeCamera.mouseMoved(delta);
        }
    }

    @Override
    public void mouseUp(Vector2f position, int button) {
        super.mouseUp(position, button);
        if (isInFreeCameraMode()) {
            freeCamera.mouseUp(position, button);
        }
    }

    @Override
    public void windowDidClose() {
        wavPlayer.stopAll();
    }
}