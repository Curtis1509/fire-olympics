package fire.olympics.display;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera {
    public final Matrix4f camera = new Matrix4f();
    public final Vector3f cameraPosition = new Vector3f(0, 0, 0);
    public final Vector3f cameraAngle = new Vector3f();
    
    public void logMatricies() {
        System.out.println(String.format("Camera Matrix: %n%s", camera));
        System.out.println(String.format("Camera Position: %n%s", cameraPosition));
        System.out.println(String.format("Camera Angle: %n%s", cameraAngle));
    }
    public void updateCamera(Vector3f position, Vector3f angle) {
        cameraPosition.set(position);
        cameraAngle.set(angle);
        recalculateMatrix();
    }

    private void recalculateMatrix() {
        camera.identity();
        // Note: this rotates around the z-axis, then the y-axis, then the x-axis.
        camera.rotate((float) Math.toRadians(cameraAngle.x), new Vector3f(1, 0, 0));
        camera.rotate((float) Math.toRadians(cameraAngle.y), new Vector3f(0, 1, 0));
        camera.rotate((float) Math.toRadians(cameraAngle.z), new Vector3f(0, 0, 1));
        camera.translateLocal(cameraPosition);
    }
}
