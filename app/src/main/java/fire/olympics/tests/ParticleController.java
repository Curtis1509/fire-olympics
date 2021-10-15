package fire.olympics.tests;

import fire.olympics.App;
import fire.olympics.display.Controller;
import fire.olympics.display.Renderer;
import fire.olympics.display.Node;
import fire.olympics.display.Window;
import fire.olympics.game.FreeCamera;
import fire.olympics.graphics.ModelLoader;

import fire.olympics.particles.ParticleSystem;

import static org.lwjgl.glfw.GLFW.*;
import org.joml.Vector2f;
import org.joml.Random;

public class ParticleController extends Controller {

    private ParticleSystem particleSystem = new ParticleSystem(100);
    private boolean mouseEnabled = true;

    public ParticleController(App app, Window window, Renderer renderer, ModelLoader loader) {
        super(app, window, renderer, loader);
        renderer.camera = new FreeCamera(window);
        renderer.backgroundColor.set(1.0f, 1.0f, 1.0f, 1.0f);
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


        Node arrow = loader.loadModel("models", "proto_arrow_textured.obj");
        arrow.name = "arrow";
        arrow.position.z -= 10;
        arrow.rotation.y = 90;
        renderer.add(arrow);
        Node stadium = loader.loadModel("models", "stadium_sky4.obj");
        stadium.name = "stadium";
        stadium.scale = 7.0f;
        stadium.position.y -= 10;
        renderer.add(stadium);

        particleSystem.texture = loader.loadTexture("textures", "fire_particle.png");
        particleSystem.randomGenerator = new Random(123);
        // particleSystem.position.set(0, 0, -10);
        particleSystem.placeOnLattice();
        arrow.addChild(particleSystem);
    }
    

    @Override
    public void update(double timeDelta) {
        // particleSystem.update(timeDelta);
        renderer.camera.update(timeDelta);
    }

    @Override
    public void mouseUp(Vector2f position, int button) {
        System.out.printf("mouse up: %s; position: %4.2f, %4.2f%n", button == GLFW_MOUSE_BUTTON_LEFT ? "left" : (button == GLFW_MOUSE_BUTTON_RIGHT ? "right" : "middle"), position.x, position.y);

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
            renderer.camera.mouseMoved(delta);
        }
    }
}


