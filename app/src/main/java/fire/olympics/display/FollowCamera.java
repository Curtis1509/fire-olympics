package fire.olympics.display;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;

import org.joml.Vector3f;

public class FollowCamera extends Camera {
    private final Window window;
    private final float distanceFromTarget = 15;

    public GameItemGroup target;

    private Vector3f position = new Vector3f(0, 0, 0);
    private Vector3f angle = new Vector3f(0, 0, 0);

    private float pitch = 0;
    private float yaw = 0;

    private float angleAboveArrow = 0;
    private float angleAroundArrow = 0;

    /**
     * Constructor for follow camera
     * @param window Window this camera is in
     * @param target The object to focus the camera on
     */
    public FollowCamera(Window window) {
        this.window = window;
    }

    /**
     * Allows camera to move in relation to arrow
     */
    public void moveCamera() {
        this.pitch = target.getRotation().x + angleAboveArrow;
        this.yaw = (180 - target.getRotation().y) + angleAroundArrow;
        angle.set(pitch, yaw, 0);

        // Calculates the position the camera needs to be in to look at the object Using
        // the objects rotation and position The position is calculated as an offset of
        // the objects position tasking into account the x and y rotations
        float horizontalDistance = calculateHorizontalDistance();
        float verticalDistance = calculateVerticalDistance();
        float theta = target.getRotation().y() + angleAroundArrow;
        float offsetX = (float) (horizontalDistance * Math.sin(Math.toRadians(theta)));
        float offsetZ = (float) (horizontalDistance * Math.cos(Math.toRadians(theta)));
        position.x = target.getPosition().x - offsetX;
        position.z = target.getPosition().z - offsetZ;
        position.y = target.getPosition().y + verticalDistance;
        super.position.set(position);
        super.rotation.set(angle);
    }


    @Override
    public void update(double timeDelta) {
        processKeyBindings(timeDelta);
        // double arrowSpeed = 25;

        // Move player
        // float dx = (float) ((arrowSpeed * timeDelta) * Math.sin(Math.toRadians(target.getRotation().x)));
        // float dy = (float) ((arrowSpeed * timeDelta) * Math.sin(Math.toRadians(target.getRotation().y)));
        // float dz = (float) ((arrowSpeed * timeDelta) * Math.cos(Math.toRadians(target.getRotation().z)));
        // target.movePosition(dx, dy, dz);
        moveCamera();
    }

    /**
     * Allows follow camera control
     * @param timeDelta normalised frame time difference
     */
    private void processKeyBindings(double timeDelta) {
        // Up and Down (Pitch) control
        if (window.isKeyDown(GLFW_KEY_W)) {
            // Lock angle
            if (angleAboveArrow < -15) {
                angleAboveArrow = -15;
            }
            // Increase arrow x rotation and angle above
            else {
                target.increaseRotX((float) (timeDelta * 25f));
                angleAboveArrow -= (float)(timeDelta * 10f);
            }
        } else if (window.isKeyDown(GLFW_KEY_S)) {
            // Lock angle
            if (angleAboveArrow > 15) {
                angleAboveArrow = 15;
            }
            // Decrease arrow x rotation and angle above
            else {
                target.increaseRotX((float) (-timeDelta * 25f));
                angleAboveArrow += (float)(timeDelta * 10f);
            }
        }
        // If no button press return angle above to baseline
        else {
            if (angleAboveArrow > 0) {
                angleAboveArrow -= (float)(timeDelta * 5f);
            } else if (angleAboveArrow < 0) {
                angleAboveArrow += (float)(timeDelta * 5f);
            }
        }

        // Left and Right (Yaw) control
        if (window.isKeyDown(GLFW_KEY_A)) {
            // Lock angle
            if (angleAroundArrow < -7) {
                angleAroundArrow = -7;
            }
            // Increase angle toward left
            else {
                angleAroundArrow -= (float) (timeDelta * 3f);
            }
            // Increase arrow y rotation left
            target.increaseRotY((float) (timeDelta * 50f));
        } else if (window.isKeyDown(GLFW_KEY_D)) {
            // Lock angle
            if (angleAroundArrow > 7) {
                angleAroundArrow = 7;
            }
            // Increase angle toward Right
            else {
                angleAroundArrow += (float) (timeDelta * 3f);
            }
            // Increase arrow y rotation Right
            target.increaseRotY((float) -(timeDelta * 50f));
        }
        // If no button press return angle above to baseline
        else {
            if (angleAroundArrow > 0) {
                angleAroundArrow -= (float) (timeDelta * 10f);
            } else if (angleAroundArrow < 0) {
                angleAroundArrow += (float) (timeDelta * 10f);
            }
        }
    }

    /**
     * Calculates the cameras horizontal distance from the arrow
     * Using the camera pitch to match the rotation of the arrow
     * @return Horizontal Distance to arrow
     */
    private float calculateHorizontalDistance() {
        return (float) (distanceFromTarget * Math.cos(Math.toRadians(pitch)));
    }

    /**
     * Calculates the cameras vertical distance from the arrow
     * Using the camera pitch to match the rotation of the arrow
     * @return Vertical Distance to arrow
     */
    private float calculateVerticalDistance() {
        return (float) (distanceFromTarget * Math.sin(Math.toRadians(pitch)));
    }
}
