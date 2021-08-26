package fire.olympics.graphics;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.joml.Matrix4f;
import org.lwjgl.system.MemoryUtil;
import static org.lwjgl.opengl.GL33C.*;

public class Mesh {
    private VertexArrayObject vao;
    private int vertexCount;
    private Texture texture = null;

    int POSITIONS = 0;
    int NORMALS = 1;
    int AMBIENT = 2;
    int DIFFUSE = 3;
    int SPECULAR = 4;

    public Mesh(float[] positions, int[] indices, float[] normals) {
        FloatBuffer buffer = MemoryUtil.memAllocFloat(positions.length);
        vertexCount = indices.length;
        assert vertexCount * 3 == positions.length;
        buffer.put(positions).flip();
        
        vao = new VertexArrayObject();
        vao.bindFloats(buffer, POSITIONS, GL_STATIC_DRAW, 3, GL_FLOAT);
        MemoryUtil.memFree(buffer);

        IntBuffer intBuffer = MemoryUtil.memAllocInt(indices.length);
        intBuffer.put(indices).flip();
        vao.bindElements(intBuffer, GL_STATIC_DRAW);
        MemoryUtil.memFree(intBuffer);

        FloatBuffer normalBuffer = MemoryUtil.memAllocFloat(normals.length);
        normalBuffer.put(normals).flip();
        vao.bindFloats(normalBuffer, NORMALS, GL_STATIC_DRAW, 3, GL_FLOAT);
        MemoryUtil.memFree(normalBuffer);
    }

    public void attachMaterial(Texture t, float[] uv) {
        FloatBuffer uvBuffer = MemoryUtil.memAllocFloat(uv.length);
        uvBuffer.put(uv).flip();
        vao.bindFloats(uvBuffer, DIFFUSE, GL_STATIC_DRAW, 2, GL_FLOAT);
        MemoryUtil.memFree(uvBuffer);

        texture = t;
    }

    public void attachMaterial(float[] vertColours) {
        FloatBuffer colourBuffer = MemoryUtil.memAllocFloat(vertColours.length);
        colourBuffer.put(vertColours).flip();
        vao.bindFloats(colourBuffer, DIFFUSE, GL_STATIC_DRAW, 3, GL_FLOAT);
        MemoryUtil.memFree(colourBuffer);
    }

    public boolean hasTexture() {
        return texture != null;
    }

    public void render(Matrix4f projection, Matrix4f world) {
        vao.use();
        if (hasTexture()) texture.bind();
        glDrawElements(GL_TRIANGLES, vertexCount, GL_UNSIGNED_INT, 0);
        if (hasTexture()) texture.unbind();
        vao.done();
    }

    public void close() throws Exception {
        vao.close();    
    }
}
