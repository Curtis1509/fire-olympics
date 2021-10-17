package fire.olympics.game;

import org.joml.Vector2f;

import fire.olympics.display.Camera;
import fire.olympics.display.Window;

import static org.lwjgl.glfw.GLFW.*;

public class FreeCamera extends Camera {
    public float mouseSensitivity = 5;
    public Window window;
    public float movementSpeed = 35f;

    public FreeCamera(Window window) {
        this.window = window;
    }

    // Controls for free Camera
    public void update(double timeDelta) {
        if (window.isKeyDown(GLFW_KEY_LEFT_SHIFT))
            movementSpeed = 60f;
        else if (window.isKeyDown(GLFW_KEY_LEFT_ALT))
            movementSpeed = 2f;
        else
            movementSpeed = 35f;

        float offsetX = 0;
        float offsetY = 0;
        float offsetZ = 0;

        if (window.isKeyDown(GLFW_KEY_A)) 
            offsetX += movementSpeed * timeDelta;

        if (window.isKeyDown(GLFW_KEY_D))
            offsetX -= movementSpeed * timeDelta;

        if (window.isKeyDown(GLFW_KEY_W))
            offsetZ += movementSpeed * timeDelta;

        if (window.isKeyDown(GLFW_KEY_S))
            offsetZ -= movementSpeed * timeDelta;

        if (window.isKeyDown(GLFW_KEY_LEFT_CONTROL))
            offsetY -= movementSpeed * timeDelta;

        if (window.isKeyDown(GLFW_KEY_SPACE))
            offsetY += movementSpeed * timeDelta;

        if (window.isKeyDown(GLFW_KEY_R)) {
            position.zero();
            rotation.zero();
        }

        updateCameraPos(offsetX, offsetY, offsetZ);
    }

    // Update camera position taking into account camera rotation
    private void updateCameraPos(float offsetX, float offsetY, float offsetZ) {
        position.x -= (float) Math.sin(Math.toRadians(rotation.y)) * offsetZ;
        position.z -= (float) Math.cos(Math.toRadians(rotation.y)) * offsetZ;
        position.y += offsetY;
        position.x += (float) Math.sin(Math.toRadians(rotation.y - 90)) * offsetX;
        position.z += (float) Math.cos(Math.toRadians(rotation.y - 90)) * offsetX;

        System.out.println("x " + position.x + " y " + position.y + " z"  + position.z + " a" + rotation.y);
    }

    @Override
    public void mouseMoved(Vector2f delta) {
        rotation.y -= delta.x / mouseSensitivity;
        rotation.x -= delta.y / mouseSensitivity;
    }
}
