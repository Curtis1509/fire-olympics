package fire.olympics.display;

import org.joml.Vector3f;

interface Renderable {
    void render();
    Vector3f getPosition();
    Vector3f getRotation();
    float getScale();
}