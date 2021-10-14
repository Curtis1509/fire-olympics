package fire.olympics.display;

import fire.olympics.graphics.Mesh;

import org.joml.Vector3f;

public class GameItem {
    private Vector3f position;
    private Vector3f rotation;
    private float scale;
    public Mesh mesh;

    public GameItem(Mesh mesh) {
        position = new Vector3f(0, 0, 0);
        rotation = new Vector3f(0, 0, 0);
        scale = 1;
        this.mesh = mesh;
    }

    public GameItem(Mesh mesh, Vector3f pos, Vector3f rotate) {
        position = pos;
        rotation = rotate;
        scale = 1;
        this.mesh = mesh;
    }

    public float getWidth(){
        return mesh.getWidth();
    }
    public float getHeight(){
        return mesh.getHeight();
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        // setting these one-by-one prevents a bug where updating position with a vector affects speed
        this.position.x = position.x;
        this.position.y = position.y;
        this.position.z = position.z;
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
        // setting these one-by-one prevents a bug where updating position with a vector affects rotation speed
        this.rotation.x = rotation.x;
        this.rotation.y = rotation.y;
        this.rotation.z = rotation.z;
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