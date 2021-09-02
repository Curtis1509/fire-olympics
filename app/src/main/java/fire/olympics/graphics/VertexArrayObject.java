package fire.olympics.graphics;

import static org.lwjgl.opengl.GL33C.*;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

import org.lwjgl.system.MemoryUtil;

/**
 * Stores variables needed to free the buffers when a VertexArrayObject is closed.
 */
class BoundBuffer {
    int gpuId;
    int vertexAttributeIndex;
    int type;
}

/**
 * A VertexArrayObject represents the data that is sent to the graphics card and used for primitive drawing commands (such as glDrawArrays or glDrawElememnts).
 * 
 * A vertex buffer object (not to be confused with a vertex array object!) represents a single array of homogeneous data that is eventually sent to the graphics card.
 * Each vertex buffer object corresponds to a single input attribute in the vertex shader. If you use {@code layout (location=index) in ... } in the vertex shader, the call to
 * {@code glEnableVertexAttribArray(index)} tells OpenGL to use the vertex array buffer for the input attribute with location index.
 * 
 * Typically, there are multiple input attributes to a vertex shader and thus multiple buffers. A VertexArrayObject is essentially a list of vertex buffer objects. It is more
 * efficient to use a VertexArrayObject because it results in less driver calls each time a primitive is drawn (that is, one versus the number of inputs to the vertex shader).
 * The way this works is when each time a VertexArrayObject is used (a.k.a. {@code glBindVertexArray}), the list of vertex buffer objects that was associated previously is
 * recalled. If the buffers are already on the graphics card, this results in the buffers being recalled in a single command.
 * 
 * To use this object correctly, you must:
 * 1. Make sure the layout location of the vertex shader inputs match the index the buffers are assigned.
 * 2. Call {@code bindFloats()} once for each index otherwise the previous buffer will be leaked. (we could fix this)
 * 3. If using {@code glDrawElements} make sure to call {@code bindElements}. The data is layed out differently compared to {@code glDrawArrays}.
 * 4. Call {@code use()} before and {@code done()} after calling OpenGL commands that use the vertex array object.
 */
public class VertexArrayObject implements AutoCloseable {
    private int name;

    private ArrayList<BoundBuffer> boundBuffers = new ArrayList<>();

    public VertexArrayObject() {
        name = glGenVertexArrays();
    }

    /**
     * Creates a new vertex buffer object that specifies the sequence of vertices that make the geometric primitive being rendered. For example,
     * suppose a square is being drawn and the vertices are labeled A, B, C, and D.  The data would
     * be layoud out as follows:
     * 
     * float[] positions = {
     *      A.x, A.y,
     *      B.x, B.y,
     *      C.x, C.y,
     *      D.x, D.y
     * };
     * 
     * float[] indicies = {
     *      0, // A
     *      1, // B
     *      2, // C
     *      1, // B
     *      2, // C
     *      3, // D
     * };
     * 
     * The {@code positions} would be passed using {@code bindFloats}. The {@code indicies} would be passed using this method.
     * The drawing command would be {@code glDrawElements}.
     * 
     * @param buffer Indicies that specify the location of vertex data that is used for drawing a primitive geometric shape.
     * @param usage Specify {@code GL_STATIC_DRAW} unless you have a good reason not to.
     */
    public void bindElements(int[] buffer, int usage) {
        BoundBuffer b = new BoundBuffer();
        b.gpuId = glGenBuffers();
        b.vertexAttributeIndex = -1;
        b.type = GL_ELEMENT_ARRAY_BUFFER;
        boundBuffers.add(b);

        IntBuffer intBuffer = MemoryUtil.memAllocInt(buffer.length);
        intBuffer.put(buffer).flip();

        use();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, b.gpuId);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, buffer, usage);
        done();
        MemoryUtil.memFree(intBuffer);
    }

    /**
     * Creates a new vertex buffer object attached to this vertex array object.
     * @param buffer The data that will be sent to the graphics card.
     * @param index The layout location of the input to the vertex shader.
     * @param usage Specify GL_STATIC_DRAW unless you have a good reason.
     * @param componentCount The number of components of an element in the buffer. (i.e. if the buffer stores two dimensional vectors, this value is 2).
     * @param componentType The type of component. For example, GL_FLOAT, or GL_INT.
     */
    public void bindFloats(float[] buffer, int index, int usage, int componentCount, int componentType) {
        BoundBuffer b = new BoundBuffer();
        b.gpuId = glGenBuffers();
        b.vertexAttributeIndex = index;
        b.type = GL_ARRAY_BUFFER;
        boundBuffers.add(b);

        FloatBuffer floatBuffer = MemoryUtil.memAllocFloat(buffer.length);
        floatBuffer.put(buffer).flip();

        use();
        glBindBuffer(b.type, b.gpuId);
        glBufferData(b.type, buffer, usage);
        glEnableVertexAttribArray(index);
        glVertexAttribPointer(index, componentCount, componentType, false, 0, 0);
        glBindBuffer(b.type, 0);
        done();

        MemoryUtil.memFree(floatBuffer);
    }

    public void use() {
        glBindVertexArray(name);
    }

    public void done() {
        // 0 is like a vertex array that does not exist.
        glBindVertexArray(0);
    }

    @Override
    public void close() {
        use();
        for (BoundBuffer b : boundBuffers) {
            if (b.vertexAttributeIndex != -1)
                glDisableVertexAttribArray(b.vertexAttributeIndex);
            // fixme: should be moved to outside of loop
            glBindBuffer(b.type, 0);
            glDeleteBuffers(b.gpuId);
        }
        done();
        glDeleteVertexArrays(name);
    }
}
