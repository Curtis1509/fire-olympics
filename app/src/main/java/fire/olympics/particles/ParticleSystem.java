package fire.olympics.particles;

import org.joml.Random;
import org.joml.Vector4f;

import fire.olympics.display.Node;
import fire.olympics.graphics.Texture;
import fire.olympics.graphics.VertexArrayObject;

import static org.lwjgl.opengl.GL33C.*;

public class ParticleSystem extends Node {
    public Random randomGenerator = new Random();
    private final VertexArrayObject vao = new VertexArrayObject();
    
    public final Vector4f hotColor = new Vector4f(1.0f, 0, 0, 1.0f);
    public final Vector4f coldColor = new Vector4f(0, 0, 1, 1);

    public final int maximumNumberOfParticles;
    private final float[] positionBuffer;
    private final float[] ageBuffer;
    private final float[] lifetimeBuffer;
    private final float[] sizeBuffer;
    private final float[] colorBuffer;

    public Texture texture;

    public ParticleSystem(int maxNumberOfParicles) {
        super();
        this.maximumNumberOfParticles = maxNumberOfParicles;
        positionBuffer = new float[3 * maximumNumberOfParticles];
        ageBuffer = new float[maximumNumberOfParticles];
        lifetimeBuffer = new float[maximumNumberOfParticles];
        sizeBuffer = new float[2 * maximumNumberOfParticles];
        colorBuffer = new float[4 * maximumNumberOfParticles];

        vao.bindFloats(positionBuffer, 0, GL_DYNAMIC_DRAW, 3, GL_FLOAT);
        vao.bindFloats(colorBuffer, 1, GL_DYNAMIC_DRAW, 4, GL_FLOAT);
        vao.bindFloats(sizeBuffer, 2, GL_DYNAMIC_DRAW, 2, GL_FLOAT);
    }
    
    private boolean shouldSpawnParticle(float age, float lifetime) {
        return age >= lifetime && randomGenerator.nextFloat() < 0.45f;
    }

    private void setParticleParameters(int index, float x, float y, float z, float width, float height, float lifetime, float age, Vector4f color) {
        positionBuffer[3 * index + 0] = x;
        positionBuffer[3 * index + 1] = y;
        positionBuffer[3 * index + 2] = z;
        ageBuffer[index] = age;
        lifetimeBuffer[index] = lifetime;
        sizeBuffer[2 * index + 0] = width;
        sizeBuffer[2 * index + 1] = height;
        colorBuffer[4 * index + 0] = color.x;
        colorBuffer[4 * index + 1] = color.y;
        colorBuffer[4 * index + 2] = color.z;
        colorBuffer[4 * index + 3] = color.w;
    }

    public void placeOnLattice() {
        float width = 0.5f;
        float height = 0.5f;
        float lifetime = -1.0f;
        float age = 0.0f;
        int y = 0;
        for (int index = 0; index < maximumNumberOfParticles;) {
            setParticleParameters(index, y, y, y, width, height, lifetime, age, new Vector4f(1.0f, 1.0f, 1.0f, 1.0f));
            index += 1;
            for (int x = y; x > 0 && index < maximumNumberOfParticles; x -= 1) {
                setParticleParameters(index, x-1, y, y, width, height, lifetime, age, new Vector4f(1.0f, 0.0f, 1.0f, 1.0f));
                index += 1;
            }
            for (int z = y; z > 0 && index < maximumNumberOfParticles; z -= 1) {
                setParticleParameters(index, y, y, z-1, width, height, lifetime, age, new Vector4f(0.0f, 1.0f, 1.0f, 1.0f));
                index += 1;
            }
            y += 1;
        }

        // Tell OpenGL to use the updated data.
        vao.updateBuffer(0, positionBuffer);
        vao.updateBuffer(1, colorBuffer);
        vao.updateBuffer(2, sizeBuffer);
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
            float width = sizeBuffer[2 * i + 0];
            float height = sizeBuffer[2 * i + 1];

            // Write the particle data to the buffer.
            updateParticle(i, (float) dt, x, y, z, width, height, age, lifetime);
        }

        // Tell OpenGL to use the updated data.
        vao.updateBuffer(0, positionBuffer);
        vao.updateBuffer(1, colorBuffer);
        vao.updateBuffer(2, sizeBuffer);
    }

    protected void updateParticle(int index, float dt, float x, float y, float z, float width, float height, float age, float lifetime) {
        // Is the particle dead? Should it spawn?
        if (shouldSpawnParticle(age, lifetime)) {
            lifetime = randomGenerator.nextFloat() * 4;
            age = 0;
            x = -1 + randomGenerator.nextFloat() * 2;
            y = -1 + randomGenerator.nextFloat() * 2;
            z = -1 + randomGenerator.nextFloat() * 2;
            width = 1.0f;
            height = 1.0f;
        }

        // Update the force field.
        x = dt * (10 * (y - x));
        y = dt * (28 * x - y + x * z);
        z = dt * (-8 * z / 3 + x * y);

        // Update age provided it's not dead.
        if (age <= lifetime) {
            age += dt;
        }

        // Write the particle data to the buffer.
        setParticleParameters(index, x, y, z, width, height, lifetime, age, new Vector4f(1.0f, 1.0f, 1.0f, 1.0f));
    }

    public void render() {
        vao.use();
        texture.bind();
        glDrawArrays(GL_POINTS, 0, maximumNumberOfParticles);
        texture.unbind();
        vao.done();
    }
}
