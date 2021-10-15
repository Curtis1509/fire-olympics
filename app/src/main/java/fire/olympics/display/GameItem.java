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

    public GameItem clone() {
        GameItem copy = new GameItem(mesh);
        copy.position.set(position);
        copy.rotation.set(rotation);
        copy.scale = scale;
        copy.name = name;
        for (Node child : children) {
            copy.addChild(child.clone());
        }
        return copy;
    }
}