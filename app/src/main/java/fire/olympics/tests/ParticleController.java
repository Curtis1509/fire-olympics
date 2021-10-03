package fire.olympics.tests;

import fire.olympics.App;
import fire.olympics.display.Controller;
import fire.olympics.display.Renderer;
import fire.olympics.display.Window;
import fire.olympics.graphics.ModelLoader;

import fire.olympics.particles.ParticleSystem;

public class ParticleController extends Controller {

    ParticleSystem particleSystem = new ParticleSystem(1);

    public ParticleController(App app, Window window, Renderer renderer, ModelLoader loader) {
        super(app, window, renderer, loader);
    }

    @Override
    public void load() throws Exception {
        particleSystem.texture = loader.loadTexture("textures", "fire_particle.png");
        particleSystem.update(0.1);
        renderer.add(particleSystem);
    }
    

    @Override
    public void update(double timeDelta) {
        // particleSystem.update(timeDelta);
    }
}
