package fire.olympics.graphics;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.joml.Matrix4f;
import org.lwjgl.system.MemoryUtil;
import static org.lwjgl.opengl.GL33C.*;

public class Mesh {
    private VertexArrayObject vao;
    private int vertexCount;
    private ShaderProgram program;
    private Texture tex;
    private boolean hasTexture = false;

    public Mesh(float[] positions, int[] indices, float[] normals) {
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

        FloatBuffer normalBuffer = MemoryUtil.memAllocFloat(normals.length);
        normalBuffer.put(normals).flip();
        vao.bindFloats(normalBuffer, 2, GL_STATIC_DRAW, 3, GL_FLOAT);
        MemoryUtil.memFree(normalBuffer);
    }

    public void attachMaterial(Texture t, float[] uv) {
        FloatBuffer uvBuffer = MemoryUtil.memAllocFloat(uv.length);
        uvBuffer.put(uv).flip();
        vao.bindFloats(uvBuffer, 1, GL_STATIC_DRAW, 2, GL_FLOAT);
        MemoryUtil.memFree(uvBuffer);

        tex = t;
        hasTexture = true;
    }

    public void attachMaterial(float[] vertColours) {
        FloatBuffer colourBuffer = MemoryUtil.memAllocFloat(vertColours.length);
        colourBuffer.put(vertColours).flip();
        vao.bindFloats(colourBuffer, 1, GL_STATIC_DRAW, 3, GL_FLOAT);
        MemoryUtil.memFree(colourBuffer);
        hasTexture = false;
    }

    public void setProgram(ShaderProgram program) {
        this.program = program;
    }

    public boolean hasTexture() {
        return hasTexture;
    }

    public void render(Matrix4f projection, Matrix4f world) {
        vao.use();
        if(hasTexture) tex.bind();
        program.bind();
        program.setUniform("projectionMatrix", projection);
        program.setUniform("worldMatrix", world);
        glDrawElements(GL_TRIANGLES, vertexCount, GL_UNSIGNED_INT, 0);
        program.unbind();
        if(hasTexture) tex.unbind();
        vao.done();
    }

    public void close() throws Exception {
        vao.close();    
    }
}
