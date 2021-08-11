package fire.olympics.display;

import static org.lwjgl.opengl.GL33.*;

import java.nio.FloatBuffer;

public class VertexArrayObject {

    private int name;

    public VertexArrayObject() {
        name = glGenVertexArrays();
    }

    public void bindFloats(FloatBuffer buffer, int index, int usage, int componentCount, int componentType) {
        int gpuId = glGenBuffers();
        use();
        glBindBuffer(GL_ARRAY_BUFFER, gpuId);
        glBufferData(GL_ARRAY_BUFFER, buffer, usage);
        glEnableVertexAttribArray(index);
        glVertexAttribPointer(index, componentCount, componentType, false, 0, 0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        done();
    }

    public void use() {
        glBindVertexArray(name);
    }

    public void done() {
        // 0 is like a vertex array that does not exist.
        glBindVertexArray(0);
    }
}
