package fire.olympics.particles;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import fire.olympics.display.Node;
import fire.olympics.graphics.Texture;
import fire.olympics.graphics.VertexArrayObject;

import static org.lwjgl.opengl.GL33C.*;

public class ParticleSystem extends Node {

    /**
     * The speed of the simulation.
     */
    public float speed;

    /**
     * The maximum number of particles in the particle system. Note that you can
     * achieve a variable number of particles by making some particles completely
     * transparent, although this will still incur the performance impact of
     * rendering all of the particles.
     */
    public final int maximumNumberOfParticles;

    /**
     * Set to the texture of the particle. This must be set before the particle
     * system is rendered.
     */
    public Texture texture;

    /**
     * Stores the position of particles relative to this node's local coordinate
     * system.
     */
    private final float[] positionBuffer;

    /**
     * Stores the size of particles relative to this node's local coordinate system.
     */
    private final float[] sizeBuffer;

    /**
     * Stores the color of particles.
     */
    private final float[] colorBuffer;

    private final VertexArrayObject vao = new VertexArrayObject();

    public ParticleSystem(int maxNumberOfParicles) {
        super();
        this.maximumNumberOfParticles = maxNumberOfParicles;
        positionBuffer = new float[3 * maximumNumberOfParticles];
        sizeBuffer = new float[2 * maximumNumberOfParticles];
        colorBuffer = new float[4 * maximumNumberOfParticles];

        vao.bindFloats(positionBuffer, 0, GL_DYNAMIC_DRAW, 3, GL_FLOAT);
        vao.bindFloats(colorBuffer, 1, GL_DYNAMIC_DRAW, 4, GL_FLOAT);
        vao.bindFloats(sizeBuffer, 2, GL_DYNAMIC_DRAW, 2, GL_FLOAT);
    }

    /**
     * Override this method to update the particle's parameters.
     * 
     * The particles are updated by changing the vector's values. This works because
     * the vectors are reference types and the new values are copied back into the
     * vertex buffer arrays.
     * 
     * @param index     A identifier for the particle that is greater than or equal
     *                  to zero and less than {@code maximumNumberOfParticles}
     * @param deltaTime The number of seconds that has passed since the last update
     *                  method.
     * @param position  The current position of the particle.
     * @param color     The current color of the particle.
     * @param size      The current size of the particle.
     */
    protected void updateParticle(int index, float deltaTime, Vector3f position, Vector4f color, Vector2f size) {

    }

    public void update(double dt) {
        // Rules:
        // 1. A dead fire particle has a 45% chance of spawning.
        // 2. A fire partcile is dead if it's age is greater than its lifetime.
        // 3. A fire particle's color ranges between hot and cold, where hot is red and
        // cold is blue.
        // 4. A fire particle is located inside of a force field which moves the
        // particles.

        Vector3f position = new Vector3f();
        Vector4f color = new Vector4f();
        Vector2f size = new Vector2f();

        for (int index = 0; index < maximumNumberOfParticles; index += 1) {
            // Read the particle data from the buffer.
            getParticleParameters(index, position, color, size);

            // Update the paricle data.
            updateParticle(index, (float) dt, position, color, size);

            // Write the particle data to the buffer.
            setParticleParameters(index, position, color, size);
        }

        // Tell OpenGL to use the updated data.
        updateBuffers();
    }

    /**
     * Positions the particles on integer coordinates of a quarter of a rectangular
     * pyrimid.
     */
    public void placeOnLattice() {
        Vector3f position = new Vector3f();
        Vector2f size = new Vector2f(0.5f, 0.5f);
        Vector4f color = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);
        int y = 0;
        for (int index = 0; index < maximumNumberOfParticles;) {
            position.set(y, y, y);
            setParticleParameters(index, position, color, size);
            index += 1;
            for (int x = y; x > 0 && index < maximumNumberOfParticles; x -= 1) {
                position.set(x - 1, y, y);
                setParticleParameters(index, position, color, size);
                index += 1;
            }
            for (int z = y; z > 0 && index < maximumNumberOfParticles; z -= 1) {
                position.set(y, y, z - 1);
                setParticleParameters(index, position, color, size);
                index += 1;
            }
            y += 1;
        }
        updateBuffers();
    }

    private void updateBuffers() {
        vao.updateBuffer(0, positionBuffer);
        vao.updateBuffer(1, colorBuffer);
        vao.updateBuffer(2, sizeBuffer);
    }

    private void setParticleParameters(int index, Vector3f position, Vector4f color, Vector2f size) {
        positionBuffer[3 * index + 0] = position.x;
        positionBuffer[3 * index + 1] = position.y;
        positionBuffer[3 * index + 2] = position.z;
        sizeBuffer[2 * index + 0] = size.x;
        sizeBuffer[2 * index + 1] = size.y;
        colorBuffer[4 * index + 0] = color.x;
        colorBuffer[4 * index + 1] = color.y;
        colorBuffer[4 * index + 2] = color.z;
        colorBuffer[4 * index + 3] = color.w;
    }

    private void getParticleParameters(int index, Vector3f position, Vector4f color, Vector2f size) {
        position.x = positionBuffer[3 * index + 0];
        position.y = positionBuffer[3 * index + 1];
        position.z = positionBuffer[3 * index + 2];
        color.x = colorBuffer[4 * index + 0];
        color.y = colorBuffer[4 * index + 1];
        color.z = colorBuffer[4 * index + 2];
        color.w = colorBuffer[4 * index + 3];
        size.x = sizeBuffer[2 * index + 0];
        size.y = sizeBuffer[2 * index + 1];
    }

    public void render() {
        vao.use();
        texture.bind();
        glDrawArrays(GL_POINTS, 0, maximumNumberOfParticles);
        texture.unbind();
        vao.done();
    }
}
