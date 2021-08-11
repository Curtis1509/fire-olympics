package fire.olympics.display;

import static org.lwjgl.opengl.GL33.*;

import java.nio.FloatBuffer;

public class VertexArrayObject {

    private int name;

    public VertexArrayObject() {
        name = glGenVertexArrays();
    }

    public int bindFloats(FloatBuffer buffer, int index, int usage, int componentCount, int componentType) {
        // todo: keep a reference to gpuId instead of returning it.
        // This will clean up memory management in the renderer, for example.
        int gpuId = glGenBuffers();
        use();
        glBindBuffer(GL_ARRAY_BUFFER, gpuId);
        glBufferData(GL_ARRAY_BUFFER, buffer, usage);
        glEnableVertexAttribArray(index);
        glVertexAttribPointer(index, componentCount, componentType, false, 0, 0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        done();
        return gpuId;
    }

    public void use() {
        glBindVertexArray(name);
    }

    public void done() {
        // 0 is like a vertex array that does not exist.
        glBindVertexArray(0);
    }

    public void delete() {
        glDeleteVertexArrays(name);
    }
}
