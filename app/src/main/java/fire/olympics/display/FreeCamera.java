package fire.olympics.display;

import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE;

public class FreeCamera {
    float movementSpeed = 5f;
    Window window;
    Renderer renderer;
    Vector3f position;
    Vector3f angle;
    boolean enabled;
    public static boolean override = false;

    public FreeCamera(Window window, Renderer renderer, Vector3f position, Vector3f angle) {
        this.window = window;
        this.renderer = renderer;
        this.position = position;
        this.angle = angle;
    }

    // Controls for free Camera
    public void freeCameraControl(double timeDelta) {
        if (window.isKeyDown(GLFW_KEY_LEFT_SHIFT))
            movementSpeed = 35f;
        else if (window.isKeyDown(GLFW_KEY_LEFT_ALT))
            movementSpeed = 2f;
        else
            movementSpeed = 5f;

        float offsetX = 0;
        float offsetY = 0;
        float offsetZ = 0;


        if (window.isKeyDown(GLFW_KEY_A) || !GameController.isPlaying()) {
            if (override && window.isKeyDown(GLFW_KEY_A))
                offsetX += movementSpeed * timeDelta;
            else if (!GameController.isPlaying() && !override) {
                offsetX += movementSpeed * timeDelta;
                angle.y += 0.001;
            }
        }
        if (override) {
            if (window.isKeyDown(GLFW_KEY_D))
                offsetX -= movementSpeed * timeDelta;

            if (window.isKeyDown(GLFW_KEY_W))
                offsetZ += movementSpeed * timeDelta;

            if (window.isKeyDown(GLFW_KEY_S))
                offsetZ -= movementSpeed * timeDelta;

            if (window.isKeyDown(GLFW_KEY_LEFT_CONTROL))
                offsetY += movementSpeed * timeDelta;

            if (window.isKeyDown(GLFW_KEY_SPACE))
                offsetY -= movementSpeed * timeDelta;

            if (window.isKeyDown(GLFW_KEY_R) && enabled) {
                position.zero();
                angle.zero();
            }
        }

        updateCameraPos(offsetX, offsetY, offsetZ);

        renderer.camera.updateCamera(position, angle);
    }

    // Update camera position taking into account camera rotation
    public void updateCameraPos(float offsetX, float offsetY, float offsetZ) {
            if (offsetZ != 0) {
                position.x += (float) Math.sin(Math.toRadians(angle.y)) * -1.0f * offsetZ;
                position.z += (float) Math.cos(Math.toRadians(angle.y)) * offsetZ;
            }
            if (offsetX != 0) {
                position.x += (float) Math.sin(Math.toRadians(angle.y - 90)) * -1.0f * offsetX;
                position.z += (float) Math.cos(Math.toRadians(angle.y - 90)) * offsetX;
            }
            position.y += offsetY;

    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
