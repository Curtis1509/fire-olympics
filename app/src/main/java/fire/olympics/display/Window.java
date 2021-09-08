package fire.olympics.display;

import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL33C.*;
import static org.lwjgl.system.MemoryUtil.*;

import java.nio.DoubleBuffer;

import org.joml.Vector2f;
import org.lwjgl.BufferUtils;

public class Window implements AutoCloseable {

    private static boolean mouseDisabled = false;
    private static long mouseDisabledOnWindowId = NULL;

    /**
     * OpenGL's internal window id.
     */
    private long windowId = NULL;


    /**
     * Stores the width of the window. Note that this is not necessarily the same as the opengl 
     * viewport's width when the window is being resized.
     */
    private int width;

    /**
     * Stores the height of the window. Note that this is not necessarily the same as the opengl 
     * viewport's height when the window is being resized.
     */
    private int height;

    /**
     * Set to true when the window is hidden.
     */
    private boolean isHidden = false;

    /**
     * Set to true when the window has been resized and false when the opengl viewport has been 
     * adjusted to match the windows width and height.
     */
    private boolean resized;

    private Vector2f previousMousePosition;

    private double lastTime;
    /**
     * The number of times the update method has been called.
     */
    private double nbFrames;

    /**
     * The average amount of time between update calls over at least a 1 second interval.
     */
    private double frameTime;

    /**
     * The amount of time since the last update call.
     */
    private double frameDelta = 0;

    /**
     * The total amount of time elapised that sums to less than one second. (This is used to update
     * the window title approximately every second).
     */
    private double timeLog = 0;

    /**
     * The average number of update calls per second.
     */
    private double fps = 0;

    /**
     * The name of the window.
     */
    public String titlePrefix;

    /**
     * An object that can respond to window events such as:
     * - Keyboard input
     * - Mouse input
     * - Updating the scene.
     */
    public EventDelegate eventDelegate;

    public Window(String title, int width, int height) {
        this.titlePrefix = title;
        this.width = width;
        this.height = height;
        this.resized = false;
    }

    /**
     * Returns {@code true} if the {@code key} is currently pressed and the focus is on this window.
     * @param key A key code, for example {@code GLFW_KEY_A} or {@code GLFW_KEY_LEFT_CONTROL}.
     * @return {@code true} if the key is pressed otherwise {@code false}.
     */
    public boolean isKeyDown(int key) {
        return glfwGetKey(windowId, key) == GLFW_PRESS;
    }

    /**
     * Initialises the OpenGL graphics context.
     */
    public void init() {
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GL_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GL_TRUE); // the window will be resizable
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);

        // Create the window
        windowId = glfwCreateWindow(width, height, titlePrefix, NULL, NULL);
        if (windowId == NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        // Setup resize callback
        glfwSetFramebufferSizeCallback(windowId, (window, width, height) -> {
            this.width = width;
            this.height = height;
            this.resized = true;
        });

        // Initialize mouse position
        previousMousePosition = cursorPosition();

        // Setup a key callback. It will be called every time a key is pressed, repeated
        // or released.
        glfwSetKeyCallback(windowId, this::processKeyboardEvent);
        glfwSetMouseButtonCallback(windowId, this::processMouseButtonEvents);
        glfwSetCursorPosCallback(windowId, this::processMouseMovementEvents);

        // Get the resolution of the primary monitor
        GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

        if (vidmode != null) {
            // Center our window
            glfwSetWindowPos(windowId, (vidmode.width() - width) / 2, (vidmode.height() - height) / 2);
        }

        // Make the OpenGL context current
        glfwMakeContextCurrent(windowId);

        GL.createCapabilities();
        glEnable(GL_CULL_FACE);
        // Enables ordered rendering of triangles
        glEnable(GL_DEPTH_TEST);
        // Enable v-sync
        glfwSwapInterval(1);
        // Set the clear color
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
    }

    /**
     * Shows the window.
     */
    public void showWindow() {
        glfwShowWindow(windowId);
        isHidden = false;
    }

    /**
     * Hides the window.
     */
    public void hideWindow() {
        glfwHideWindow(windowId);
        isHidden = true;
    }

    /**
     * Returns {@code true} whether the window is hidden.
     * @return
     */
    public boolean isHidden() {
        return isHidden;
    }

    public String openGlVersion() {
        int[] maj = new int[1];
        int[] min = new int[1];
        glGetIntegerv(GL_MAJOR_VERSION, maj);
        glGetIntegerv(GL_MINOR_VERSION, min);
        return "OpenGL Version: " + maj[0] + "." + min[0];
    }

    public boolean update(Renderer renderer) {
        if (!isHidden()) {
            use();
            computeFrameDelta();
            if(eventDelegate != null)
                eventDelegate.update(frameDelta);
            if (resized) {
                glViewport(0, 0, width, height);
                resized = false;
                renderer.setAspectRatio(aspectRatio());
            }
            renderer.render();
            glfwSwapBuffers(windowId);
            done();
        }
        return shouldClose();
    }

    private void processKeyboardEvent(long window, int key, int scancode, int action, int mods) {
        if (eventDelegate != null) {
            if (action == GLFW_PRESS) {
                eventDelegate.keyDown(key);
            } else if (action == GLFW_RELEASE) {
                eventDelegate.keyUp(key);
            }
        }
    }

    private void processMouseButtonEvents(long window, int button, int action, int mods) {
        if(eventDelegate != null) {
            if(action == GLFW_PRESS) {
                eventDelegate.mouseDown(cursorPosition(), button);
            } else if(action == GLFW_RELEASE) {
                eventDelegate.mouseUp(cursorPosition(), button);
            }
        }
    }

    private void processMouseMovementEvents(long window, double x, double y) {
        Vector2f position = new Vector2f((float)x, (float)y);

        if(eventDelegate != null) {
            position.sub(previousMousePosition, previousMousePosition);
            eventDelegate.mouseMoved(previousMousePosition);
        }

        previousMousePosition = position;
    }

    private Vector2f cursorPosition() {
        DoubleBuffer x = BufferUtils.createDoubleBuffer(1);
        DoubleBuffer y = BufferUtils.createDoubleBuffer(1);
        glfwGetCursorPos(windowId, x, y);
        x.rewind();
        y.rewind();
        return new Vector2f((float)x.get(), (float)y.get());
    }

    public void close() {
        if (windowId != NULL) {
            // Free the window callbacks and destroy the window
            glfwFreeCallbacks(windowId);
            glfwDestroyWindow(windowId);
        }
    }

    private void computeFrameDelta() {
        double currentTime = glfwGetTime();
        frameDelta = currentTime - lastTime;
        lastTime = currentTime;

        timeLog += frameDelta;
        nbFrames++;

        if (timeLog >= 1) {
            frameTime = 1000 / nbFrames;
            fps = nbFrames / timeLog;

            String fullTitle = String.format("%s | Frame Time: %.2fms | FPS: %.2f", titlePrefix, frameTime, fps);
            glfwSetWindowTitle(windowId, fullTitle);
            nbFrames = 0;
            timeLog = 0;
        }
    }

    public float aspectRatio() {
        return (float) width / height;
    }

    /**
     * Indicate that the window should close.
     * @param value {@code true} if the window should be closed otherwise {@code false}.
     */
    public void setShouldClose(boolean value) {
        // Note: GLFW may set this, for example, when the user clicks on the window's close button.
        glfwSetWindowShouldClose(windowId, value); 
    }
    
    /**
     * Checks to see whether the window should be closed.
     * @return {@code true} if the window should be closed otherwise {@code false}.

     */
    public boolean shouldClose() {
        return glfwWindowShouldClose(windowId);
    }

    public void disableCursor() {
        glfwSetInputMode(windowId, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
        mouseDisabled = true;
        mouseDisabledOnWindowId = windowId;
    }

    public void hideCursor() {
        glfwSetInputMode(windowId, GLFW_CURSOR, GLFW_CURSOR_HIDDEN);
        mouseDisabled = false;
        mouseDisabledOnWindowId = NULL;
    }

    public void restoreCursor() {
        glfwSetInputMode(windowId, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
        mouseDisabled = false;
        mouseDisabledOnWindowId = NULL;
    }

    public void restoreCursorIfDisabledOnWindow() {
        if (mouseDisabled && windowId == mouseDisabledOnWindowId) {
            restoreCursor();
        }
    }

    public void use() {
        glfwMakeContextCurrent(windowId);
    }

    public void done() {
        glfwMakeContextCurrent(0);
    }
}
