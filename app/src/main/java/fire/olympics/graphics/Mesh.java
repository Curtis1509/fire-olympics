package fire.olympics.graphics;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.system.MemoryUtil;
import static org.lwjgl.opengl.GL33C.*;

public class Mesh {
    private VertexArrayObject vao;
    private int vertexCount;

    public Mesh(float[] positions, int[] indices, float[] colors) {
        FloatBuffer buffer = MemoryUtil.memAllocFloat(positions.length);
        vertexCount = indices.length;
        assert vertexCount * 3 == positions.length;
        buffer.put(positions).flip();
        
        vao = new VertexArrayObject();
        vao.bindFloats(buffer, 0, GL_STATIC_DRAW, 3, GL_FLOAT);
        MemoryUtil.memFree(buffer);

        IntBuffer intBuffer = MemoryUtil.memAllocInt(indices.length);
        intBuffer.put(indices).flip();
        vao.bindElements(intBuffer, GL_STATIC_DRAW);
        MemoryUtil.memFree(intBuffer);

        FloatBuffer colorBuffer = MemoryUtil.memAllocFloat(colors.length);
        colorBuffer.put(colors).flip();
        vao.bindFloats(colorBuffer, 1, GL_STATIC_DRAW, 3, GL_FLOAT);
        MemoryUtil.memFree(colorBuffer);
    }

    public void render() {
        vao.use();
        glDrawElements(GL_TRIANGLES, vertexCount, GL_UNSIGNED_INT, 0);
        vao.done();
    }

    public void close() {
        vao.close();    
    }
}
