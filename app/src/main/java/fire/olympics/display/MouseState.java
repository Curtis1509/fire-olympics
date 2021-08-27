package fire.olympics.display;

import java.util.Objects;

public class MouseState {

    public Point2D position = new Point2D(0, 0);
    public Point2D lastPosition = new Point2D(0, 0);
    public boolean leftButtonDown = false;
    public boolean rightButtonDown = false;
    public MouseState() { }

    public double dx() {
        return position.x - lastPosition.x;
    }

    public double dy() {
        return position.y - lastPosition.y;
    }

    @Override
    public boolean equals(Object o) {
        // self check
        if (this == o)
            return true;
        // null check
        if (o == null)
            return false;
        // type check and cast
        if (getClass() != o.getClass())
            return false;
        MouseState other = (MouseState) o;
        return leftButtonDown == other.leftButtonDown 
                && rightButtonDown == other.rightButtonDown 
                && Objects.equals(position, other.position)
                && Objects.equals(lastPosition, other.lastPosition);
    }

    public MouseState clone() {
        MouseState copy = new MouseState();
        copy.position = position.clone();
        copy.lastPosition = lastPosition.clone();
        copy.leftButtonDown = leftButtonDown;
        copy.rightButtonDown = rightButtonDown;
        return copy;
    }
}