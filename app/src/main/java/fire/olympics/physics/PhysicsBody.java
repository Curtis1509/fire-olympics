package fire.olympics.physics;

import java.util.ArrayList;
import org.joml.Vector3f;

public class PhysicsBody {
    public ArrayList<Constraint> constraints = new ArrayList<>();

    public boolean isPointInsideBody(Vector3f point) {
        for (Constraint c : constraints) {
            if (!c.evaluate(point)) {
                return false;
            }
        }
        return true;
    }
}
