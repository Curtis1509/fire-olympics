package fire.olympics.display;

import org.joml.Vector3f;
import org.joml.Matrix4f;

public class Node {
    public final Vector3f position = new Vector3f();
    public final Vector3f rotation = new Vector3f();
    public float scale = 1.0f;
    private final Matrix4f matrix = new Matrix4f();

    public Node() {

    }

    public void logMatricies() {
        System.out.println(String.format("Matrix: %n%s", matrix));
        System.out.println(String.format("Position: %n%s", position));
        System.out.println(String.format("Angle: %n%s", rotation));
    }

    public Matrix4f getMatrix() {
        matrix.translation(getPosition())
            .rotate((float) Math.toRadians(rotation.y), 0, 1, 0)
            .rotate((float) Math.toRadians(rotation.z), 0, 0, 1)
            .rotate((float) Math.toRadians(rotation.x), 1, 0, 0)
            .scale(getScale());
        return matrix;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position.set(position);
    }

    public void setPosition(float x, float y, float z) {
        this.position.x = x;
        this.position.y = y;
        this.position.z = z;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public void setRotation(Vector3f rotation) {
        this.rotation.set(rotation);
    }

    public void setRotation(float x, float y, float z) {
        this.rotation.x = x;
        this.rotation.y = y;
        this.rotation.z = z;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }
}
