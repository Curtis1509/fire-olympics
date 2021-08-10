package fire.olympics.display;

import static fire.olympics.App.getWindow;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL33.*;
import static org.lwjgl.system.MemoryUtil.memAllocFloat;

public class Render implements Runnable{

    Shaders shaderProgram;
    int vbo;
    int vao;

    @Override
    public void run() {
        while ( !glfwWindowShouldClose(getWindow()) ) {
            createVertexBuffer();

            try {
                renderTriangle();
            } catch (Exception e) {
                e.printStackTrace();
            }

            clearColor(0.0f, 0.0f, 0.0f);

            while (!glfwWindowShouldClose(getWindow())) {
                clear();

                shaderProgram.bind();

                glBindVertexArray(vao);

                glDrawArrays(GL_TRIANGLES, 0, 3);

                glBindVertexArray(0);

                shaderProgram.unbind();

                glfwSwapBuffers(getWindow());

                // Poll for window events. The key callback above will only be
                // invoked during this call.
                glfwPollEvents();
            }


        }
    }


    private void renderTriangle() throws Exception {
        getShaders();

        float[] vertices = new float[]{
                0.0f, 0.5f, 0.0f,
                -0.5f, -0.5f, 0.0f,
                0.5f, -0.5f, 0.0f
        };

        FloatBuffer vertBuffer = null;

        try {
            vertBuffer = memAllocFloat(vertices.length);
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
        } finally {
            if(vertBuffer != null) {
                MemoryUtil.memFree(vertBuffer);
            }
        }
    }

    private void getShaders() throws Exception {
        shaderProgram = new Shaders();
        shaderProgram.loadProgram("shader.vert","shader.frag");
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
