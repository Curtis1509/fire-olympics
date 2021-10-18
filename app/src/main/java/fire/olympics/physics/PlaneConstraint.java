package fire.olympics.physics;

import org.joml.Vector3f;

public class PlaneConstraint extends Constraint {

    public Vector3f origin = new Vector3f();
    public Vector3f normal = new Vector3f(0, 1, 0);

    @Override
    public boolean evaluate(Vector3f point) {
        Vector3f a = new Vector3f();
        point.sub(origin, a);
        return normal.dot(a) > 0;
    }
}
