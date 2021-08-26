package fire.olympics.graphics;

import static org.lwjgl.opengl.GL33C.*;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

import org.lwjgl.system.MemoryUtil;

class BoundBuffer {
    int gpuId;
    int vertexAttributeIndex;
    int type;
}

public class VertexArrayObject implements AutoCloseable {
    private int name;

    private ArrayList<BoundBuffer> boundBuffers = new ArrayList<>();

    public VertexArrayObject() {
        name = glGenVertexArrays();
    }

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
