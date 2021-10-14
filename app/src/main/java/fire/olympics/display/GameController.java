package fire.olympics.display;

import fire.olympics.App;
import fire.olympics.audio.WavPlayer;
import fire.olympics.graphics.ModelLoader;
import fire.olympics.particles.ParticleSystem;
import fire.olympics.fontMeshCreator.FontType;
import fire.olympics.fontMeshCreator.GUIText;

import java.util.ArrayList;
import java.util.Random;

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
    private boolean mouseEnabled = true;
    private boolean keyVPrev = false; // Allows V key to toggle camera type
    private boolean keyOPrev = false;
    private boolean keyPPrev = false;

    private ArrayList<GameItemGroup> objects = new ArrayList<>();
    private GameItemGroup arrow;
    private Vector3f arrowInitPosition = new Vector3f(-300, 35, 0);
    private Vector3f arrowInitRotation = new Vector3f(0, 90, 0);

    private int numOfPoles = 1; // number of each of the five colours

    private WavPlayer wavPlayer;
    private int score = 0;

    private final ParticleSystem particleSystem = new ParticleSystem(100);
    private final GUIText fireOlympicsText;
    private final GUIText scoreText;
    private final GUIText pressSpaceToPlayText;

    /**
     * Prefer using isPlaying() and setIsPlaying(_) over reading and writing to this field directly
     * because of the side effects associated with setting isPlaying.
     */
    private boolean _playing = false;

    /**
     * Set the state of whether the game is in playing mode or not.
     * @param isPlaying true if the user is controlling the arrow.
     */
    private void setIsPlaying(boolean isPlaying) {
        this._playing = isPlaying;
        freeCamera.isPlaying = isPlaying;
        // Toggle the visibility of text on the screen.
        fireOlympicsText.isHidden = isPlaying;
        pressSpaceToPlayText.isHidden = isPlaying;
        scoreText.isHidden = !isPlaying;
    }

    private boolean isPlaying() {
        return _playing;
    }

    public GameController(App app, Window window, Renderer renderer, ModelLoader loader, FontType fontType) {
        super(app, window, renderer, loader);

        wavPlayer = new WavPlayer(app);

        followCamera = new FollowCamera(window);
        freeCamera = new FreeCamera(window);
        freeCamera.isPlaying = this._playing;
        renderer.camera = freeCamera;

        fireOlympicsText = new GUIText(fontType, "FIRE OLYMPICS");
        fireOlympicsText.fontSize = 8.0f;
        fireOlympicsText.isCentered = true;
        fireOlympicsText.color.set(1.0f, 0.0f, 0.0f);
        fireOlympicsText.position.y=0.3f;
        renderer.addText(fireOlympicsText);

        pressSpaceToPlayText = new GUIText(fontType, "press [space] to play");
        pressSpaceToPlayText.fontSize = 3.0f;
        pressSpaceToPlayText.isCentered = true;
        pressSpaceToPlayText.color.set(1.0f, 0.0f, 0.0f);
        pressSpaceToPlayText.position.y=0.65f;
        renderer.addText(pressSpaceToPlayText);

        scoreText = new GUIText(fontType, "" + score);
        scoreText.fontSize = 4.0f;
        scoreText.isCentered = false;
        scoreText.color.set(1.0f, 0.0f, 0.0f);
        renderer.addText(scoreText);
    }

    @Override
    public void load() throws Exception {
        loader.loadTexture("textures", "metal_test.png");
        loader.loadTexture("textures", "wood_test_2.png");
        loader.loadTexture("textures", "stadium_aluminium.jpg").repeat(12000f/1024f, 5000f/1024f);
        loader.loadTexture("textures", "stadium_crowd.jpg");
        loader.loadTexture("textures", "stadium_grass.jpg").repeat(7000f/550f, 7000f/550f);
        loader.loadTexture("textures", "stadium_sky.jpg");
        loader.loadTexture("textures", "stadium_track.jpg").repeat(7000f/800f, 7000f/557f);
        loader.loadTexture("textures", "stadium_lane.jpg");
        loader.loadTexture("textures", "stadium_wood.jpeg").repeat(12000f/474f, 4500f/235f);
        loader.loadTexture("textures", "stadium_sky.jpg");
        loader.loadTexture("textures", "ring+pole_brushed_metal.jpg");
        loader.loadTexture("textures", "ring_black.jpg");
        loader.loadTexture("textures", "ring_blue.jpg");
        loader.loadTexture("textures", "ring_green.jpg");
        loader.loadTexture("textures", "ring_red.jpg");
        loader.loadTexture("textures", "ring_yellow.jpg");
        loader.loadTexture("textures", "pole_metal.jpg");


        // adding to ArrayList with indices, to explicitly place objects in order
        // skipping an index, or adding them out of order, will break things!!

        objects.add(0, new GameItemGroup("arrow", loader.loadModel(1, "models", "proto_arrow_textured.obj")));
        this.arrow = objects.get(0);
        followCamera.target = arrow;

        objects.add(1, new GameItemGroup("brazier", loader.loadModel(1, "models", "Brazier v2 Textured.obj")));

        // sky4 has the smoothest sky that fits in github. export sky5 from blender for the smoothest sky
        objects.add(2, new GameItemGroup("stadium", loader.loadModel(1, "models", "stadium_sky4.obj")));
        // objects.add(2, new GameItemGroup(loader.loadModel("models", "stadium_sky5.obj")));

        objects.add(3, new GameItemGroup("ringt", loader.loadModel(1, "models", "ring.obj")));
        objects.add(4, new GameItemGroup("ringtp", loader.loadModel(1, "models", "ring+pole.obj")));

        int size = objects.size();

        // coloured rings
        for (int i = 0; i < numOfPoles; i++) {
            objects.add(new GameItemGroup("ring", loader.loadModel(1, "models", "ring+pole_black.obj")));
            objects.add(new GameItemGroup("ring", loader.loadModel(1, "models", "ring+pole_blue.obj")));
            objects.add(new GameItemGroup("ring", loader.loadModel(1, "models", "ring+pole_green.obj")));
            objects.add(new GameItemGroup("ring", loader.loadModel(1, "models", "ring+pole_red.obj")));
            objects.add(new GameItemGroup("ring", loader.loadModel(1, "models", "ring+pole_yellow.obj")));
        }

        // setting initial positions
        objects.get(0).setPosition(arrowInitPosition);
        objects.get(0).setRotation(arrowInitRotation);
        objects.get(1).setPosition(0, -5, -10);
        objects.get(2).setPosition(0, -7, 0);
        objects.get(2).setScale(7);
        objects.get(3).setPosition(0, 2, -10);
        objects.get(4).setPosition(0, -5, -45);

        Random r = new Random();
        int lowX = -190;
        int highX = 190;
        int lowY = -35;
        int highY = -15;
        int lowZ = -110;
        int highZ = 110;
        int lowR = 0;
        int highR = 360;

        // randomise positions of coloured rings
        for (int i = size; i < objects.size(); i++) {
            int resultX = r.nextInt(highX - lowX) + lowX;
            int resultY = r.nextInt(highY - lowY) + lowY;
            int resultZ = r.nextInt(highZ - lowZ) + lowZ;
            int resultR = r.nextInt(highR - lowR) + lowR;

            objects.get(i).setPosition(resultX, resultY, -resultZ);
            objects.get(i).setRotation(0, resultR,0);
            objects.get(i).setScale(3);
        }

        for (GameItemGroup object : objects) {
            if (!(object.getString().equals("ringt") || object.getString().equals("ringtp")))
            for (GameItem item : object.getAll())
                renderer.add(item);
        }

        // Particle effects are disabled at the moment because they are buggy.
        // particleSystem.texture = loader.loadTexture("textures", "fire_particle.png");
        // renderer.add(particleSystem);

        arrow = objects.get(0);
        wavPlayer.playSound(2);
        wavPlayer.playSound(3);
    }

    @Override
    public void update(double timeDelta) {
        checkCollision();
        // Enable or Disable free Camera
        boolean keyV = window.isKeyDown(GLFW_KEY_SPACE);
        if (!freeCamera.override && window.checkKeyState(GLFW_KEY_SPACE, keyVPrev) == 1) {
            setIsPlaying(!isPlaying());
            if (isPlaying()) {
                renderer.camera = followCamera;
            } else {
                renderer.camera = freeCamera;
            }
        }
        keyVPrev = keyV;

        boolean keyP = window.isKeyDown(GLFW_KEY_P);
        if (window.checkKeyState(GLFW_KEY_P, keyPPrev) == 1) {
            System.out.println(freeCamera.position);
        }
        keyPPrev = keyP;

        boolean keyO = window.isKeyDown(GLFW_KEY_O);
        if (window.checkKeyState(GLFW_KEY_O, keyOPrev) == 1) {
            freeCamera.override = !freeCamera.override;
            System.out.println("Override set to " + freeCamera.override);
        }
        keyOPrev = keyO;

        renderer.camera.update(timeDelta);
        particleSystem.update(timeDelta);
    }



    public boolean isInside(float yRot, double width, double height, double objectx, double objecty, double objectz, double playerx, double playery, double playerz) {
        double r = width / 2;
        double total = objecty+r+(height/2);
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
        if (d <= width/2) {
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

    int collisionTick = 0;
    int collisionIndex = 6969;


    public void checkCollision() {

        if (arrow.getPosition().y < -6.8){
            System.out.println("Crashed into the ground!");
            arrow.setPosition(arrowInitPosition);
            arrow.setRotation(arrowInitRotation);
            wavPlayer.playSound(6);
        }

        for (int i = 0; i < objects.size(); i++) {
            if (objects.get(i).getString().equals("ring")) {
                // System.out.println("found ring at "+ i);
                if (collisionTick == 0 && isInside(objects.get(i).getRotation().y, objects.get(3).getWidth(0)*objects.get(i).getScale(), objects.get(i).getHeight(0)*objects.get(i).getScale(), objects.get(i).getPosition().x, objects.get(i).getPosition().y, objects.get(i).getPosition().z, arrow.getPosition().x(), arrow.getPosition().y, arrow.getPosition().z)
                ) {
                    //isInside(objects.get(3).getRotation(i).y, objects.get(3).getWidth(0), objects.get(3).getHeight(0), objects.get(3).getPosition(i).x, objects.get(3).getPosition(i).y, objects.get(3).getPosition(i).z, arrow.getPosition().x(), arrow.getPosition().y, arrow.getPosition().z);
                    collisionTick++;
                    collisionIndex = i;
                    System.out.println("COLLIDE");
                }
            }
        }

        if (collisionIndex != 6969 && collisionTick > 0 && (!(isInside(objects.get(collisionIndex).getRotation().y, objects.get(3).getWidth(0)*objects.get(collisionIndex).getScale(), objects.get(collisionIndex).getHeight(0)*objects.get(collisionIndex).getScale(), objects.get(collisionIndex).getPosition().x, objects.get(collisionIndex).getPosition().y, objects.get(collisionIndex).getPosition().z, arrow.getPosition().x(), arrow.getPosition().y, arrow.getPosition().z)))) {
            collisionTick = 0;
            collisionIndex = 6969;
            score++;
            scoreText.value = "" + score;
            wavPlayer.playSound(0);
        }
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
        if (!mouseEnabled) {
            freeCamera.mouseMoved(delta);
        }
    }
}