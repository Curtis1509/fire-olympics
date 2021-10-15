package fire.olympics.display;

import java.util.Optional;
import java.util.ArrayList;

import org.joml.Vector3f;
import org.joml.Matrix4f;

/**
 * Class purpose: Manage entities such as the arrow, stadium etc. that are made
 * of multiple gameItems with different meshes and textures, so we can treat
 * them as a single thing without breaking any lower-level code.
 *
 * All child nodes are relative their parent's coordinate spaces. This makes
 * positioning realative to each other easier.
 */
public class Node {
    public final Vector3f position = new Vector3f();
    public final Vector3f rotation = new Vector3f();
    public float scale = 1.0f;
    private final Matrix4f matrix = new Matrix4f();
    private Optional<Node> parent = Optional.empty();;
    public final ArrayList<Node> children = new ArrayList<>();

    public String name;
 
    /**
     * @deprecated Use {@code name } instead.
     */
    @Deprecated
    public String getString() {
        return name;
    }

    public Node() {

    }

    public Node clone() {
        Node copy = new Node();
        copy.position.set(position);
        copy.rotation.set(rotation);
        copy.scale = scale;
        copy.name = name;
        for (Node child : children) {
            copy.addChild(child.clone());
        }
        return copy;
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
        parent.ifPresent(p -> matrix.mulLocal(p.getMatrix()));
        return matrix;
    }

    public void setParent(Node parent) {
        removeFromParent();
        addChild(this);
    }

    public void removeFromParent() {
        this.parent.ifPresent(p -> p.removeChild(this));
    }

    public void removeChild(Node child) {
        this.children.remove(child);
        child.parent = Optional.empty();
    }

    public void addChild(Node child) {
        assert child.parent.isEmpty();
        this.children.add(child);
        child.parent = Optional.of(this);
    }

    public Optional<Node> getParent() {
        return parent;
    }

    /**
     * @deprecated Use {@code position } instead 
     */
    @Deprecated()
    public Vector3f getPosition() {
        return position;
    }

    /**
     * @deprecated Use {@code position.set() } instead.
     */
    @Deprecated()
    public void setPosition(Vector3f position) {
        this.position.set(position);
    }

    /**
     * @deprecated Use {@code rotation) } instead.
     */
    @Deprecated()
    public Vector3f getRotation() {
        return rotation;
    }

    /**
     * @deprecated Use {@code rotation.set() } instead.
     */
    @Deprecated()
    public void setRotation(Vector3f rotation) {
        this.rotation.set(rotation);
    }

    /**
     * @deprecated Use {@code scale } instead.
     */
    @Deprecated()
    public float getScale() {
        return scale;
    }

    /**
     * @deprecated Use {@code scale } instead.
     */
    @Deprecated()
    public void setScale(float scale) {
        this.scale = scale;
    }

    public float getWidth(int i) {
        // fixme: the width of a node **is** poorly defined.
        // What happens when the node is rotated? What about scaling?
        // What about a parent node's scaling? What coordinate space is the width being measured in?
        return ((GameItem) children.get(i)).getWidth();
    }

    public float getHeight(int i) {
        // fixme: the height of a node **is** poorly defined.
        return ((GameItem) children.get(i)).getHeight();
    }
}
