package fire.olympics.particles;

import org.joml.Random;
import org.joml.Vector3f;
import org.joml.Vector4f;

import fire.olympics.graphics.VertexArrayObject;

import static org.lwjgl.opengl.GL33C.*;

public class ParticleSystem {
    private Random randomGenerator = new Random();
    private final VertexArrayObject vao = new VertexArrayObject();
    
    public final Vector4f hotColor = new Vector4f(1.0f, 0, 0, 1.0f);
    public final Vector4f coldColor = new Vector4f(0, 0, 1, 1);

    public final Vector3f position = new Vector3f(0, 0, -10);
    public final Vector3f rotation = new Vector3f();
    public final float scale = 1.0f;

    public final int maximumNumberOfParticles;
    private final float[] positionBuffer;
    private final float[] ageBuffer;
    private final float[] lifetimeBuffer;

    public ParticleSystem(int maxNumberOfParicles) {
        this.maximumNumberOfParticles = maxNumberOfParicles;
        positionBuffer = new float[3 * maximumNumberOfParticles];
        ageBuffer = new float[maximumNumberOfParticles];
        lifetimeBuffer = new float[maximumNumberOfParticles];

        vao.bindFloats(positionBuffer, 0, GL_DYNAMIC_DRAW, 3, GL_FLOAT);
        vao.bindFloats(ageBuffer, 1, GL_DYNAMIC_DRAW, 1, GL_FLOAT);
        vao.bindFloats(lifetimeBuffer, 2, GL_DYNAMIC_DRAW, 1, GL_FLOAT);
    }
    
    private boolean shouldSpawnParticle(float age, float lifetime) {
        return age >= lifetime && randomGenerator.nextFloat() < 0.45f;
    }

    public void update(double dt) {
        // Rules:
        // 1. A dead fire particle has a 45% chance of spawning.
        // 2. A fire partcile is dead if it's age is greater than its lifetime.
        // 3. A fire particle's color ranges between hot and cold, where hot is red and cold is blue.
        // 4. A fire particle is located inside of a force field which moves the particles.

        for (int i = 0; i < maximumNumberOfParticles; i += 1) {
            // Read the particle data from the buffer.
            float age = ageBuffer[i];
            float lifetime = lifetimeBuffer[i];
            float x = positionBuffer[3 * i + 0]; 
            float y = positionBuffer[3 * i + 1]; 
            float z = positionBuffer[3 * i + 2];

            // Is the particle dead? Should it spawn?
            if (shouldSpawnParticle(age, lifetime)) {
                lifetime = randomGenerator.nextFloat();
                age = 0;
                x = -1 + randomGenerator.nextFloat() * 2;
                y = -1 + randomGenerator.nextFloat() * 2;
                z = -1 + randomGenerator.nextFloat() * 2;
            }

            // Update the force field.
            x = (float)dt * (10 * (y - x));
            y = (float)dt * (28 * x - y + x * z);
            z = (float)dt * (-8 * z / 3 + x * y);

            // Update age provided it's not dead.
            if (age <= lifetime) {
                age += dt;
            }

            // Write the particle data to the buffer.
            positionBuffer[3 * i + 0] = x;
            positionBuffer[3 * i + 1] = y;
            positionBuffer[3 * i + 2] = z;
            ageBuffer[i] = age;
            lifetimeBuffer[i] = lifetime;
        }

        // Tell OpenGL to use the updated data.
        vao.updateBuffer(0, positionBuffer);
        vao.updateBuffer(1, ageBuffer);
        vao.updateBuffer(2, lifetimeBuffer);
    }

    public void render() {
        vao.use();
        glPointSize(4);
        glDrawArrays(GL_POINTS, 0, maximumNumberOfParticles);
        vao.done();
    }
}
