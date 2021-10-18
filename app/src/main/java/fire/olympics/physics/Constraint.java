package fire.olympics.physics;

import org.joml.Vector3f;

public abstract class Constraint {
    abstract public boolean evaluate(Vector3f point);
}
