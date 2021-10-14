package fire.olympics.display;

import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;

public class FollowCamera {
    private final GameItemGroup arrow;
    private final Vector3f position;
    private final Window window;

    private float pitch = 0;
    private float yaw = 0;

    private final float distanceFromArrow = 15;
    private float angleAboveArrow = 0;
    private float angleAroundArrow = 0;

    /**
     * Constructor for follow camera
     * @param window Window this camera is in
     * @param arrow The object to focus the camera on
     */
    FollowCamera(Window window, GameItemGroup arrow) {
        this.arrow = arrow;
        this.window = window;
        position = new Vector3f(0, 0, 0);
    }

    /**
     * Allows camera to move in relation to arrow
     */
    public void moveCamera() {
        this.pitch = arrow.getRotation().x + angleAboveArrow;

        float horizontalDistance = calculateHorizontalDistance();
        float verticalDistance = calculateVerticalDistance();
        calculateCameraPosition(horizontalDistance, verticalDistance);

        this.yaw = (180 - arrow.getRotation().y) + angleAroundArrow;

    }

    /**
     * Allows follow camera control
     * @param timeDelta normalised frame time difference
     */
    public void followCameraControl(double timeDelta) {
        // Up and Down (Pitch) control
        if (window.isKeyDown(GLFW_KEY_W)) {
            // Lock angle
            if (angleAboveArrow < -15) {
                angleAboveArrow = -15;
            }
            // Increase arrow x rotation and angle above
            else {
                arrow.increaseRotX((float) (timeDelta * 25f));
                increaseAngleAboveObj((float) (-timeDelta * 10f));
            }
        } else if (window.isKeyDown(GLFW_KEY_S)) {
            // Lock angle
            if (angleAboveArrow > 15) {
                angleAboveArrow = 15;
            }
            // Decrease arrow x rotation and angle above
            else {
                arrow.increaseRotX((float) (-timeDelta * 25f));
                increaseAngleAboveObj((float) (timeDelta * 10f));
            }
        }
        // If no button press return angle above to baseline
        else {
            if (angleAboveArrow > 0) {
                increaseAngleAboveObj(((float) (-timeDelta * 5f)));
            } else if (angleAboveArrow < 0) {
                increaseAngleAboveObj(((float) (timeDelta * 5f)));
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
                increaseAngleAroundObj((float) (-timeDelta * 3f));
            }
            // Increase arrow y rotation left
            arrow.increaseRotY((float) (timeDelta * 50f));
        } else if (window.isKeyDown(GLFW_KEY_D)) {
            // Lock angle
            if (angleAroundArrow > 7) {
                angleAroundArrow = 7;
            }
            // Increase angle toward Right
            else {
                increaseAngleAroundObj((float) (timeDelta * 3f));
            }
            // Increase arrow y rotation Right
            arrow.increaseRotY((float) -(timeDelta * 50f));
        }
        // If no button press return angle above to baseline
        else {
            if (angleAroundArrow > 0) {
                increaseAngleAroundObj(((float) (-timeDelta * 10f)));
            } else if (angleAroundArrow < 0) {
                increaseAngleAroundObj(((float) (timeDelta * 10f)));
            }
        }
    }

    /**
     * Calculates the position the camera needs to be in to look at the object
     * Using the objects rotation and position
     * The position is calculated as an offset of the objects position tasking into account the x and y rotations
     * @param horizDistance Horizontal distance to arrow
     * @param verticDistance Vertical distance to arrow
     */
    public void calculateCameraPosition(float horizDistance, float verticDistance) {
        float theta = arrow.getRotation().y() + angleAroundArrow;
        float offsetX = (float) (horizDistance * Math.sin(Math.toRadians(theta)));
        float offsetZ = (float) (horizDistance * Math.cos(Math.toRadians(theta)));
        position.x = arrow.getPosition().x - offsetX;
        position.z = arrow.getPosition().z - offsetZ;
        position.y = arrow.getPosition().y + verticDistance;
    }

    /**
     * Calculates the cameras horizontal distance from the arrow
     * Using the camera pitch to match the rotation of the arrow
     * @return Horizontal Distance to arrow
     */
    private float calculateHorizontalDistance() {
        return (float) (distanceFromArrow * Math.cos(Math.toRadians(pitch)));
    }

    /**
     * Calculates the cameras vertical distance from the arrow
     * Using the camera pitch to match the rotation of the arrow
     * @return Vertical Distance to arrow
     */
    private float calculateVerticalDistance() {
        return (float) (distanceFromArrow * Math.sin(Math.toRadians(pitch)));
    }

    public void increaseAngleAboveObj(float amount) {
        angleAboveArrow += amount;
    }

    public void increaseAngleAroundObj(float amount) {
        angleAroundArrow += amount;
    }

    public Vector3f getPosition() {
        return position;
    }

    public Vector3f getRotation() {
        return new Vector3f(pitch, yaw, 0);
    }
}
