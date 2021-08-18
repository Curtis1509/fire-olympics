package fire.olympics.display;

import org.joml.Matrix4f;
import org.joml.Vector3f;

interface Renderable {
    void render(Matrix4f projection, Matrix4f world);
    Vector3f getPosition();
    Vector3f getRotation();
    float getScale();
}