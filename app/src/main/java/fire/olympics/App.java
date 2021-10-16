/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package fire.olympics;

import fire.olympics.display.*;
import fire.olympics.fontMeshCreator.FontType;
import fire.olympics.game.GameController;
import fire.olympics.graphics.ModelLoader;
import fire.olympics.graphics.Texture;
import fire.olympics.graphics.ShaderLoader;

import fire.olympics.tests.TextController;
import fire.olympics.tests.ParticleController;

import static org.lwjgl.opengl.GL33C.*;

import org.lwjgl.*;
import org.lwjgl.glfw.GLFWErrorCallback;
import static org.lwjgl.glfw.GLFW.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Objects;

public class App implements AutoCloseable {
    public static void main(String[] args) {
        // GLFW says that some methods should be called on the main thread,
        // plus on some systems (like macOS) the program *must* run on the main
        // thread. So we double check it here.
        Thread t = Thread.currentThread();
        if (!t.getName().equals("main")) {
            System.out.println("warning: not running on main thread!");
        }

        resourcePath = Path.of("app", "src", "main", "resources");
        if (!Files.exists(resourcePath)) {
            resourcePath = Path.of("app").relativize(resourcePath);
        }

        try (App app = new App()) {
            // You can technically create two windows by calling this twice.
            app.createMainWindow();
            // app.addParticleController();
            MemoryUsage.print(Texture.class);
            app.mainLoop();
        } catch (Exception e) {
            System.out.printf("error: %s%n", e);
            e.printStackTrace();
        }

        MemoryUsage.summary();
    }


    /**
     * A path relative to the current working directory that points to the resources directory.
     */

    private static Path resourcePath;
    private final ArrayList<Controller> controllers = new ArrayList<>();
    private FileReader fileReader = new FileReader();

    public App() {
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set();
        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }
    }

    /**
     * The main loop of the app. This is responsible for coordinating when everything updates.
     * 
     * The sequence of events is important, and is broadly:
     * 1. Everything is created.
     * 2. Everything is loaded.
     * 3. Everything is updated.
     * 4. Everything is released.
     * 
     * Many opengl commands assume a window context is present. The controller's load method
     * for example assumes this, as well as the create main window method. This method respects
     * these assumptions.
     * 
     * The main loop also takes into account that:
     * 1. Multiple windows need to be updated in a loop.
     * 2. glfwPollEvents triggers the callbacks for all events, regardless of the window.
     * 3. A window that's closed should no longer be rendered too.
     * 4. The program needs to be running until all windows have been closed.
     * 5. One window might disable a cursor and cause it to be hidden, however enabling/disabling
     *    the cursor affects all windows.
     */

    public void mainLoop() {
        ArrayList<Window> closedWindows = new ArrayList<>();
        while (controllers.size() > 0) {
            for (Controller controller : controllers) {
                boolean shouldClose = controller.window.update(controller.renderer);
                if (shouldClose) {
                    controller.window.restoreCursorIfDisabledOnWindow();
                    closedWindows.add(controller.window);
                }
            }

            if (closedWindows.size() > 0) {
                for (Window window : closedWindows) {
                    window.close();
                    controllers.removeIf(c -> c.window == window);
                }
                closedWindows.clear();
            }

            glfwPollEvents(); // i.e. processKeyboardEvents() for all windows
        }
    }

    private void setupController(Controller c) {
        try {
            c.window.use();
            c.load();
            c.renderer.setAspectRatio(c.window.aspectRatio());
            c.window.done();
            c.window.showWindow();
        } catch (Exception e) {
            System.out.println("error loading window: " + e);
        }
    }

    public void addMainWindow() {
        try {
            createMainWindow();
        } catch (Exception e) {
            System.out.println("Error occured while initailasing TextContreller.");
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }


    public void createMainWindow() throws Exception {
        System.out.println("LWJGL version: " + Version.getVersion());

        Window window = new Window("Fire Olympics", 1280, 720);

        window.init();
        window.use();
        System.out.println(window.openGlVersion());

        Texture texture = Texture.loadPngTexture(resource("fonts", "fontfile.png"));
        FontType fontType = new FontType(resource("fonts", "fontfile.fnt"), texture);
        ModelLoader loader = new ModelLoader(resourcePath);
        ShaderLoader shaderLoader = new ShaderLoader(resourcePath);
        Renderer renderer = new Renderer(shaderLoader);
        GameController controller = new GameController(this, window, renderer, loader, fontType, fileReader.read(resource("data", "ringlocations.txt").toString()));
        controllers.add(controller);
        window.done();
        setupController(controller);
    }

    public void addTextController() {
        Window window = new Window("Text Tests", 800, 600);
        window.init();
        window.use();
        try {
            ModelLoader loader = new ModelLoader(resourcePath);
            Texture texture = Texture.loadPngTexture(resource("fonts", "fontfile.png"));
            FontType fontType = new FontType(resource("fonts", "fontfile.fnt"), texture);
            ShaderLoader shaderLoader = new ShaderLoader(resourcePath);
            Renderer renderer = new Renderer(shaderLoader);
            TextController controller = new TextController(this, window, renderer, loader, fontType);
            controllers.add(controller);
            setupController(controller);
        } catch (Exception e) {
            System.out.println("Error occured while initailasing TextContreller.");
            e.printStackTrace();
        } finally {
            window.done();
        }
    }

    public void addParticleController() {
        Window window = new Window("Particle Tests", 800, 600);
        window.init();
        window.use();
        try {
            ModelLoader loader = new ModelLoader(resourcePath);
            ShaderLoader shaderLoader = new ShaderLoader(resourcePath);
            Renderer renderer = new Renderer(shaderLoader);
            ParticleController controller = new ParticleController(this, window, renderer, loader);
            controllers.add(controller);
            setupController(controller);
        } catch (Exception e) {
            System.out.println("Error occured while initailasing Particle Controller.");
            e.printStackTrace();
        } finally {
            window.done();
        }
    }

    /**
     * Constructs a path object relative to the {@code resourcePath} from the arguments.
     * @param first A file or directory.
     * @param more A file or directory.
     */
    public Path resource(String first, String... more) {
        return resourcePath.resolve(Path.of(first, more)).toAbsolutePath();
    }

    @Override
    public void close() {
        // Terminate GLFW and free the error callback
        glfwTerminate();
        Objects.requireNonNull(glfwSetErrorCallback(null)).free();
    }

    public static void checkError(String message) {
        int errorCode = glGetError();
        while (errorCode != GL_NO_ERROR) {
            String error = switch (errorCode) {
                case GL_INVALID_ENUM -> "INVALID_ENUM";
                case GL_INVALID_VALUE -> "INVALID_VALUE";
                case GL_INVALID_OPERATION -> "INVALID_OPERATION";
                case GL_STACK_OVERFLOW -> "STACK_OVERFLOW";
                case GL_STACK_UNDERFLOW -> "STACK_UNDERFLOW";
                case GL_OUT_OF_MEMORY -> "OUT_OF_MEMORY";
                case GL_INVALID_FRAMEBUFFER_OPERATION -> "INVALID_FRAMEBUFFER_OPERATION";
                default -> "unknown error code (" + errorCode + ")";
            };

            System.out.printf("opengl error: %s, message: %s%n", error, message);
            errorCode = glGetError();
        }
    }
}
