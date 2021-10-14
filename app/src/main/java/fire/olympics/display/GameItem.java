package fire.olympics.display;

import fire.olympics.graphics.Mesh;

public class GameItem extends Node {
    public Mesh mesh;

    public GameItem(Mesh mesh) {
        super();
        this.mesh = mesh;
    }

    public float getWidth() {
        return mesh.getWidth();
    }

    public float getHeight() {
        return mesh.getHeight();
    }
}