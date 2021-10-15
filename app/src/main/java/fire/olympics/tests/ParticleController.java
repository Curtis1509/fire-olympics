package fire.olympics.tests;

import fire.olympics.App;
import fire.olympics.display.Controller;
import fire.olympics.display.Renderer;
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
        particleSystem.texture = loader.loadTexture("textures", "fire_particle.png");
        particleSystem.randomGenerator = new Random(123);
        particleSystem.position.set(0, 0, -10);
        particleSystem.placeOnLattice();
        renderer.add(particleSystem);
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


