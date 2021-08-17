package fire.olympics.display;

import org.joml.Vector3f;

public class GameItem implements Renderable {
    private final Mesh mesh;
    private Vector3f position;
    private Vector3f rotation;
    private float scale;

    public GameItem(Mesh mesh) {
        this.mesh = mesh;
        position = new Vector3f(0, 0, 0);
        rotation = new Vector3f(0, 0, 0);
        scale = 1;
    }

    public GameItem(Mesh mesh, Vector3f pos, Vector3f rotate) {
        this.mesh = mesh;
        position = pos;
        rotation = rotate;
        scale = 1;
    }

    @Override
    public void render() {
        mesh.render();
    }

    @Override
    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public void setPosition(float x, float y, float z) {
        this.position.x = x;
        this.position.y = y;
        this.position.z = z;
    }

    @Override
    public Vector3f getRotation() {
        return rotation;
    }

    public void setRotation(Vector3f rotation) {
        this.rotation = rotation;
    }

    public void setRotation(float x, float y, float z) {
        this.rotation.x = x;
        this.rotation.y = y;
        this.rotation.z = z;
    }

    @Override
    public Vector3f getScale() {
        return new Vector3f(scale, scale, scale);
    }

    public void setScale(float scale) {
        this.scale = scale;
    }
}