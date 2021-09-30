package fire.olympics.display;

import org.joml.Vector2f;

import fire.olympics.App;
import fire.olympics.graphics.ModelLoader;

import static org.lwjgl.glfw.GLFW.*;

public class Controller implements EventDelegate {
    public final Renderer renderer;
    public final Window window;
    protected final ModelLoader loader;
    // App is used to create new windows. You typically *should not* need to use this reference for 
    // anything else. More to the point, it is impossible to create a new window without a reference
    // because the app is an instance variable, and keyboard callbacks are scoped to a particular
    // window. (That is, the app cannot recieve keyboard callbacks even though it would be more 
    // appropriate for it to recieve them, because it does not have an opengl id, and more broadly
    // the keyboard focus might not be on the app's windows).
    private final App app;

    public Controller(App app, Window window, Renderer renderer, ModelLoader loader) {
        this.renderer = renderer;
        this.window = window;
        this.loader = loader;
        this.app = app;
        window.eventDelegate = this;
    }

    public void load() throws Exception {

    }

    @Override
    public void update(double timeDelta) {

    }

    @Override
    public void keyDown(int key) {

    }

    @Override
    public void keyUp(int key) {
        switch (key) {
            case GLFW_KEY_ESCAPE:
                window.setShouldClose(true);
                break;
            case GLFW_KEY_0:
                app.addTextController();
                break;
        }
    }

    @Override
    public void mouseDown(Vector2f position, int button) {

    }

    @Override
    public void mouseUp(Vector2f position, int button) {

    }

    @Override
    public void mouseMoved(Vector2f delta) {

    }
}
