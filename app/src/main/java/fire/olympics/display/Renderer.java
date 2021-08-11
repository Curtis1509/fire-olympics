package fire.olympics.display;

import java.nio.FloatBuffer;
import org.lwjgl.system.MemoryUtil;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL33.*;

public class Renderer implements AutoCloseable {

    long window;
    ShaderProgram program;
    VertexArrayObject vao;
    int verticiesGpuBufferId = -1;
    final int vertexAttributeIndex = 0;

    public Renderer(long window, ShaderProgram program) throws Exception {
        this.window = window;
        this.program = program;
        this.vao = new VertexArrayObject();

        float[] vertices = new float[]{
                0.0f, 0.5f, 0.0f,
                -0.5f, -0.5f, 0.0f,
                0.5f, -0.5f, 0.0f
        };

        FloatBuffer verticies = MemoryUtil.memAllocFloat(vertices.length);
        
        if (verticies == null) {
            throw new Exception("Could not allocate memory.");
        } else {
            verticies.put(vertices).flip();
        }

        verticiesGpuBufferId = vao.bindFloats(verticies, vertexAttributeIndex, GL_STATIC_DRAW, 3, GL_FLOAT);
        MemoryUtil.memFree(verticies);

        // An exception will be thrown if your shader program is invalid.
        vao.use();
        program.validate();
        vao.done();
    }

    public void run() {
        while (!glfwWindowShouldClose(window)) {

            // Set the color.
            glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
            // Apply the color.
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            // Use this program.
            program.bind();

            // Set the current vertex array object to our one.
            vao.use();
            // Issue a draw command. Implicitly uses the vertex array object.
            glDrawArrays(GL_TRIANGLES, 0, 3);
            // Unbind the vertex array object so it's not accidentally used somewhere else.
            vao.done();

            program.unbind();

            glfwSwapBuffers(window);

            // Poll for window events. The key callback above will only be
            // invoked during this call.
            glfwPollEvents();
        }
    }

    @Override
    public void close() {
        if (verticiesGpuBufferId != -1) {
            vao.use();
            glDisableVertexAttribArray(vertexAttributeIndex);
            glBindBuffer(GL_ARRAY_BUFFER, 0);
            glDeleteBuffers(verticiesGpuBufferId);
            vao.done();
            vao.delete();
        }
    }
}
