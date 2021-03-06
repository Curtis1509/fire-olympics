package fire.olympics.tests;

import fire.olympics.App;
import fire.olympics.display.Camera;
import fire.olympics.display.Controller;
import fire.olympics.display.Renderer;
import fire.olympics.display.Node;
import fire.olympics.display.Window;
import fire.olympics.game.FreeCamera;
import fire.olympics.graphics.ModelLoader;

import fire.olympics.particles.ParticleSystem;
import fire.olympics.particles.SoftCampFireEmitter;

import static org.lwjgl.glfw.GLFW.*;
import org.joml.Vector2f;

public class ParticleController extends Controller {

    private ParticleSystem particleSystem = new ParticleSystem(100);
    private SoftCampFireEmitter fireEmitter = new SoftCampFireEmitter(500);
    private Camera camera;

    public ParticleController(App app, Window window, Renderer renderer, ModelLoader loader) {
        super(app, window, renderer, loader);
        camera = new FreeCamera(window);
        renderer.setCamera(camera);
        renderer.backgroundColor.set(1.0f, 1.0f, 1.0f, 1.0f);
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


        Node arrow = loader.loadModel("models", "proto_arrow_textured.obj");
        arrow.name = "arrow";
        arrow.position.z = 20;
        arrow.rotation.y = 90;

        Node stadium = loader.loadModel("models", "stadium_sky4.obj");
        stadium.name = "stadium";
        stadium.scale = 7.0f;
        stadium.position.y -= 10;
        renderer.add(stadium);

        Node brazier = loader.loadModel("models", "Brazier v2 Textured.obj");
        brazier.name = "brazier";
        brazier.position.set(0, -2, -10);
        brazier.scale = 5.0f;
        renderer.add(brazier);

        // Particles are added after opaque objects because they are transparent.
        fireEmitter.position.y = 2;
        fireEmitter.texture = loader.loadTexture("textures", "fire_particle.png");
        brazier.addChild(fireEmitter);

        particleSystem.texture = loader.loadTexture("textures", "fire_particle.png");
        particleSystem.placeOnLattice();
        arrow.addChild(particleSystem);
        renderer.add(arrow);
    }

    @Override
    public void update(double timeDelta) {
        particleSystem.update(timeDelta);
        fireEmitter.update(timeDelta);
        camera.update(timeDelta);
    }

    @Override
    public void mouseUp(Vector2f position, int button) {
        System.out.printf("mouse up: %s; position: %4.2f, %4.2f%n", button == GLFW_MOUSE_BUTTON_LEFT ? "left" : (button == GLFW_MOUSE_BUTTON_RIGHT ? "right" : "middle"), position.x, position.y);
        camera.mouseUp(position, button);
    }

    // Adjust angle of camera to match mouse movement
    @Override
    public void mouseMoved(Vector2f delta) {
        camera.mouseMoved(delta);
    }
}


