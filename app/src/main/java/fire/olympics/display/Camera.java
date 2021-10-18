package fire.olympics.display;

import org.joml.Vector2f;

public class Camera extends Node {
    public boolean isActiveCamera = false;

    @Override
    public void update(double timeDelta) {
        if (isActiveCamera) {
            updateIfActive(timeDelta);
        }
    }

    public void updateIfActive(double timeDelta) {

    }

    public void mouseDown(Vector2f position, int button) { }

    public void mouseMoved(Vector2f delta) { }

    public void mouseUp(Vector2f position, int button) { }
}
