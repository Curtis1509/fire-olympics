package fire.olympics.display;

import org.joml.Vector3f;

public class FollowCamera {
    private GameItemGroup arrow;
    private Vector3f position;

    private float pitch = 0;
    private float yaw = 0;
    private float roll = 0;

    // This is how far the camera will be from the object
    private final float distanceFromObject = 5;
    // This is the angle that the camera will have above to arrow
    private final float angleAboveObj = 10;

    FollowCamera(GameItemGroup arrow) {
        this.arrow = arrow;
        position = new Vector3f(0, 0, 0);
    }

    public void moveCamera() {
        this.pitch = arrow.getRotation().x + angleAboveObj;

        float horizontalDistance = calculateHorizontalDistance();
        float verticalDistance = calculateVerticalDistance();
        calculateCameraPosition(horizontalDistance, verticalDistance);

        this.yaw = (180 - arrow.getRotation().y);

    }

    // This calculates the position the camera needs to be in to look at the object
    // Using the objects rotation and position
    // The position is calculate as an offset of the objects position tasking into account the x and y rotations
    public void calculateCameraPosition(float horizDistance, float verticDistance) {
        float theta = arrow.getRotation().y();
        float offsetX = (float) (horizDistance * Math.sin(Math.toRadians(theta)));
        float offsetZ = (float) (horizDistance * Math.cos(Math.toRadians(theta)));
        position.x = arrow.getPosition().x - offsetX;
        position.z = arrow.getPosition().z - offsetZ;
        position.y = arrow.getPosition().y + verticDistance;
    }

    // This calculates the cameras horizontal distance from the object
    // Using the objects pitch to match the rotation of it
    private float calculateHorizontalDistance() {
        return (float) (distanceFromObject * Math.cos(Math.toRadians(pitch)));
    }

    // This calculates the cameras vertical distance from the object
    // Using the objects pitch to match the rotation of it
    private float calculateVerticalDistance() {
        return (float) (distanceFromObject * Math.sin(Math.toRadians(pitch)));
    }

    public Vector3f getPosition() {
        return position;
    }

    public Vector3f getRotation() {
        return new Vector3f(-pitch, -yaw, roll);
    }
}
