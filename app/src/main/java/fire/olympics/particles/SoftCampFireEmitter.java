package fire.olympics.particles;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.joml.Random;

public class SoftCampFireEmitter extends ParticleSystem {

    public Random random = new Random();
    public float radius = 5.0f;

    public SoftCampFireEmitter(int maxNumberOfParicles) {
        super(maxNumberOfParicles);
        speed = 1.0f;

        Vector3f position = new Vector3f();
        Vector4f color = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);
        Vector2f size = new Vector2f();
        for (int index = 0; index < maximumNumberOfParticles; index += 1) {
            setParticleParameters(index, position, color, size);
        }
    }

    protected void updateParticle(int index, float deltaTime, Vector3f position, Vector4f color, Vector2f size) {
        if (position.equals(0, 0, 0)) {
            placeOnSphere(1.0f, position);
        }
        if (position.distanceSquared(0, 0, 0) < radius * radius) {
            Vector3f dp = new Vector3f();
            position.normalize(dp);
            dp.mul(speed * deltaTime);
            position.add(dp);
        } else {
            placeOnSphere(1.0f, position);
        }

        color.set(1.0f, 1.0f, 1.0f, 1.0f);
        size.set(0.5f, 0.5f);
    }

    private void placeOnSphere(float radius, Vector3f position) {
        float lambda = (float) Math.asin(2 * random.nextFloat() - 1);
        float phi = 2 * (float) Math.PI * random.nextFloat();
        position.x = radius * (float) Math.cos(lambda) * (float) Math.cos(phi);
        position.y = radius * (float) Math.cos(lambda) * (float) Math.sin(phi);
        position.z = radius * (float) Math.sin(lambda);
    }
}
