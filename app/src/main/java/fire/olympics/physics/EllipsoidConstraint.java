package fire.olympics.physics;

import org.joml.Vector3f;

public class EllipsoidConstraint extends Constraint {

    public Vector3f origin = new Vector3f();
    public Vector3f scale = new Vector3f(1, 1, 1);

    @Override
    public boolean evaluate(Vector3f point) {
        Vector3f b = new Vector3f(scale);
        Vector3f a = new Vector3f();
        point.sub(origin, a);
        a.div(b);
        return a.dot(a) < 1;
    }
}
