package fire.olympics.display;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL33.*;
import static org.lwjgl.system.MemoryUtil.memAllocFloat;

public class Render {

    long window;
    int vbo;
    int vao;
    ShaderProgram pipeline;

    public Render(long window, ShaderProgram pipeline) {
        this.window = window;
        this.pipeline = pipeline;
    }

    public void run() throws Exception {
        while (!glfwWindowShouldClose(window)) {
            createVertexBuffer();

            renderTriangle();

            clearColor(0.0f, 0.0f, 0.0f);
            clear();

            pipeline.bind();

            glBindVertexArray(vao);
            glDrawArrays(GL_TRIANGLES, 0, 3);
            glBindVertexArray(0);

            pipeline.unbind();

            glfwSwapBuffers(window);

            // Poll for window events. The key callback above will only be
            // invoked during this call.
            glfwPollEvents();
        }
    }

    private void renderTriangle() throws Exception {
        float[] vertices = new float[]{
                0.0f, 0.5f, 0.0f,
                -0.5f, -0.5f, 0.0f,
                0.5f, -0.5f, 0.0f
        };

        FloatBuffer vertBuffer = null;

        vertBuffer = memAllocFloat(vertices.length);
        if (vertBuffer == null) {
            throw new Exception("Could not allocate memory.");
        }

        vertBuffer.put(vertices).flip();

        vao = glGenVertexArrays();
        glBindVertexArray(vao);

        vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, vertBuffer, GL_STATIC_DRAW);

        glEnableVertexAttribArray(0);

        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);

        MemoryUtil.memFree(vertBuffer);
    }

    private void clear() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer
    }

    private void clearColor(float r, float b, float g) {
        glClearColor(r, g, b, 1.0f);
    }
    private void createVertexBuffer() {
        FloatBuffer vertices = BufferUtils.createFloatBuffer(3);
        vertices.put(0.0f);
        vertices.put(0.0f);
        vertices.put(0.0f);

        vertices.flip();

        vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
    }
}
