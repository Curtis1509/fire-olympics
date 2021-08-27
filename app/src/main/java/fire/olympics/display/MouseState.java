package fire.olympics.display;

import java.util.Objects;

import org.joml.Vector2f;

public class MouseState {

    public Vector2f position = new Vector2f(0, 0);
    public Vector2f lastPosition = new Vector2f(0, 0);
    public boolean leftButtonDown = false;
    public boolean rightButtonDown = false;
    public MouseState() { }

    public float dx() {
        return position.x - lastPosition.x;
    }

    public float dy() {
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
        copy.position = new Vector2f(position);
        copy.lastPosition = new Vector2f(lastPosition);
        copy.leftButtonDown = leftButtonDown;
        copy.rightButtonDown = rightButtonDown;
        return copy;
    }
}