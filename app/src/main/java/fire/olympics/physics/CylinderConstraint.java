package fire.olympics.physics;

import org.joml.Vector3f;

public class CylinderConstraint extends Constraint {

    public Vector3f origin = new Vector3f();
    public float radius = 1.0f;
    public float height = 1.0f;

    @Override
    public boolean evaluate(Vector3f point) {
        Vector3f a = new Vector3f(point);
        a.sub(origin);
        return a.x * a.x + a.z * a.z < radius * radius &&
                a.y > -height / 2 && a.y < height / 2;
    }
}
