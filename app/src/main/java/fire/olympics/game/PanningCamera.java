package fire.olympics.game;

import fire.olympics.display.Camera;

public class PanningCamera extends Camera {
    // Angle around a half circle measured in degrees. Valid domain is 0 to 360.
    private float theta = 0.0f;
    // Displacement along the straight sections of track. Valid domain is 0 to length.
    private float displacement = 0.0f;
    // Tracks which quadrant the camera is currently located in.
    private int stage = 0;

    /**
     * The speed of the camera measured in units (meters?) per second.
     */
    private float speed = 30.0f;

    /**
     * The radius of a half-circle in the stadium.
     */
    public float radius = 100.0f;
    
    /**
     * The length of the field long ways, excluding the half-circle's radius.
     */
    public float length = 300.0f;

    /**
     * The direction the camera is looking relative to the direction it's moving around the field.
     */
    public float viewingAngleOffset = 45.0f;

    @Override
    public void updateIfActive(double timeDelta) {
        switch (stage) {
            case 0:
                theta += Math.toDegrees(timeDelta * speed / radius);
                if (theta >= 180) {
                    theta = 180;
                    stage += 1;
                    displacement = 0.0f;
                    log("stage: %d%n", stage);
                } else {
                    position.x = length/2 + radius * (float) Math.cos(Math.toRadians(-theta + 90));
                    position.z = radius * (float) Math.sin(Math.toRadians(-theta + 90));
                    rotation.y = viewingAngleOffset + theta - 90; 
                }
                break;
            case 1:
                displacement += timeDelta * speed;
                if (displacement >= length) {
                    stage += 1;
                    log("stage: %d%n", stage);
                } else {
                    position.x = length/2 - displacement;
                    position.z = -radius;
                    rotation.y = viewingAngleOffset + 90.0f;
                }
                break;
            case 2:
                theta += Math.toDegrees(timeDelta * speed / radius);
                if (theta >= 360) {
                    theta = 0;
                    stage += 1;
                    displacement = 0.0f;
                    log("stage: %d%n", stage);
                } else {
                    position.x = -length/2 + radius * (float) Math.cos(Math.toRadians(-theta + 90));
                    position.z = radius * (float) Math.sin(Math.toRadians(-theta + 90));
                    rotation.y = viewingAngleOffset + theta - 90;
                }
                break;
            case 3:
                displacement += timeDelta * speed;
                if (displacement >= length) {
                    stage += 1;
                    log("stage: %d%n", stage);
                } else {
                    position.x = -length/2 + displacement;
                    position.z = radius;
                    rotation.y = viewingAngleOffset + -90.0f;
                }
                break;
            default:
                break;
        }
        stage = stage % 4;
    }

    private void log(String format, Object... args) {
        System.out.printf(format, args);
    }
}
