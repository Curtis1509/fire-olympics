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

    private final String title;
    private int width;
    private int height;
    private long window = NULL;
    private boolean isHidden = false;
    private boolean resized;
    public EventDelegate eventDelegate;
    private MouseState previousMouseEvent = new MouseState();

    private double lastTime;
    private double nbFrames;
    private double frameTime;
    private double fps = 0;

    public Window(String title, int width, int height) {
        this.title = title;
        this.width = width;
        this.height = height;
        this.resized = false;
    }

    public void init() {
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GL_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GL_TRUE); // the window will be resizable
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);

        // Create the window
        window = glfwCreateWindow(width, height, title, NULL, NULL);
        if (window == NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        // Setup resize callback
        glfwSetFramebufferSizeCallback(window, (window, width, height) -> {
            this.width = width;
            this.height = height;
            this.resized = true;
        });

        // Setup a key callback. It will be called every time a key is pressed, repeated
        // or released.
        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            this.processKeyboardEvent(window, key, scancode, action, mods);
        });

        // Get the resolution of the primary monitor
        GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

        if (vidmode != null) {
            // Center our window
            glfwSetWindowPos(window, (vidmode.width() - width) / 2, (vidmode.height() - height) / 2);
        }

        // Make the OpenGL context current
        glfwMakeContextCurrent(window);

        GL.createCapabilities();
        glEnable(GL_CULL_FACE);
        // Enables ordered rendering of triangles
        glEnable(GL_DEPTH_TEST);
        // Enable v-sync
        glfwSwapInterval(1);
        // Set the clear color
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
    }

    public void showWindow() {
        // Make the window visible
        glfwShowWindow(window);
        isHidden = false;
    }

    public void hideWindow() {
        glfwHideWindow(window);
        isHidden = true;
    }

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

    public void changeTitle(String title) {
        glfwSetWindowTitle(window, title);
    }

    public void update() {
        glfwSwapBuffers(window);
        processMouseEvents();
        changeTitle(title + frameCounter(false));
    }

    public void resizeViewportIfNeeded() {
        if (resized) {
            glViewport(0, 0, width, height);
            resized = false;
        }
    }

    private void processKeyboardEvent(long window, int key, int scancode, int action, int mods) {
        if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
            setShouldClose(true);
        }

        if (eventDelegate != null) {
            if (action == GLFW_PRESS) {
                eventDelegate.keyDown(key);        
            } else if (action == GLFW_RELEASE) {
                eventDelegate.keyDown(key);        
            }
        }
    }

    private boolean containsPoint(Vector2f point) {
        return 0 <= point.x && 0 <= point.y && point.x <= width && point.y <= height;
    }

    private void processMouseEvents() {
        MouseState currentEvent = new MouseState();
        currentEvent.lastPosition = previousMouseEvent.position;
        currentEvent.position = cursorPosition();
        currentEvent.leftButtonDown = GLFW_PRESS == glfwGetMouseButton(window, GLFW_MOUSE_BUTTON_LEFT);
        currentEvent.rightButtonDown = GLFW_PRESS == glfwGetMouseButton(window, GLFW_MOUSE_BUTTON_RIGHT);

        if (eventDelegate != null && (containsPoint(currentEvent.position) || mouseDisabled && window == mouseDisabledOnWindowId)) {
            if (!currentEvent.position.equals(currentEvent.lastPosition)) {
                eventDelegate.mouseMoved(currentEvent.clone());
            }

            if (currentEvent.leftButtonDown != previousMouseEvent.leftButtonDown) {
                if (currentEvent.leftButtonDown) {
                    eventDelegate.mouseDown(currentEvent.clone());
                } else {
                    // fixme: cannot determine whether the left or right button was lifted.
                    eventDelegate.mouseUp(currentEvent.clone());
                }
            }

            if (currentEvent.rightButtonDown != previousMouseEvent.rightButtonDown) {
                if (currentEvent.rightButtonDown) {
                    eventDelegate.mouseDown(currentEvent.clone());
                } else {
                    // fixme: cannot determine whether the left or right button was lifted.
                    eventDelegate.mouseUp(currentEvent.clone());
                }
            }
        }

        previousMouseEvent = currentEvent;
    }

    private Vector2f cursorPosition() {
        DoubleBuffer x = BufferUtils.createDoubleBuffer(1);
        DoubleBuffer y = BufferUtils.createDoubleBuffer(1);
        glfwGetCursorPos(window, x, y);
        x.rewind();
        y.rewind();
        return new Vector2f((float)x.get(), (float)y.get());
    }

    public void close() {
        if (window != NULL) {
            // Free the window callbacks and destroy the window
            glfwFreeCallbacks(window);
            glfwDestroyWindow(window);

        }
    }

    private String frameCounter(boolean debug) {
        double currentTime = glfwGetTime();
        double delta = currentTime - lastTime;
        nbFrames++;

        if (delta >= 1) {
            frameTime = 1000 / nbFrames;
            fps = nbFrames / delta;

            if (debug) {
                System.out.println("Frametime: " + frameTime);
                System.out.println("Fps: " + fps);
            }

            nbFrames = 0;
            lastTime = currentTime;
        }
        return String.format(" | Frametime: %.2f | FPS: %.2f", frameTime, fps);
    }

    public float aspectRatio() {
        return (float) width / height;
    }

    private void setShouldClose(boolean value) {
        // Note: GLFW may set this, for example, when the user clicks on the window's close button.
        // To query the value use glfwWindowShouldClose().
        glfwSetWindowShouldClose(window, value); 
    }
    
    public boolean shouldClose() {
        return glfwWindowShouldClose(window);
    }

    public void disableCursor() {
        glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
        mouseDisabled = true;
        mouseDisabledOnWindowId = window;
    }

    public void hideCursor() {
        glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_HIDDEN);
        mouseDisabled = false;
        mouseDisabledOnWindowId = NULL;
    }

    public void restoreCursor() {
        glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
        mouseDisabled = false;
        mouseDisabledOnWindowId = NULL;
    }

    public void restoreCursorIfDisabledOnWindow() {
        if (mouseDisabled && window == mouseDisabledOnWindowId) {
            restoreCursor();
        }
    }

    public void use() {
        glfwMakeContextCurrent(window);
    }

    public void done() {
        glfwMakeContextCurrent(0);
    }
}
