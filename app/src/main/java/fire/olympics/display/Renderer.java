package fire.olympics.display;

import java.nio.FloatBuffer;
import org.lwjgl.system.MemoryUtil;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL33.*;

public class Renderer implements AutoCloseable {

    long window;
    ShaderProgram program;
    FloatBuffer verticies;
    VertexArrayObject vao;

    public Renderer(long window, ShaderProgram program) throws Exception {
        this.window = window;
        this.program = program;
        this.vao = new VertexArrayObject();

        float[] vertices = new float[]{
                0.0f, 0.5f, 0.0f,
                -0.5f, -0.5f, 0.0f,
                0.5f, -0.5f, 0.0f
        };

        verticies = MemoryUtil.memAllocFloat(vertices.length);
        if (verticies == null) {
            throw new Exception("Could not allocate memory.");
        } else {
            verticies.put(vertices).flip();
        }

        vao.bindFloats(verticies, 0, GL_STATIC_DRAW, 3, GL_FLOAT);
    }

    public void run() {
        while (!glfwWindowShouldClose(window)) {

            glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

            program.bind();

            vao.use();
            glDrawArrays(GL_TRIANGLES, 0, 3);
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
        if (verticies != null) MemoryUtil.memFree(verticies);
    }
}
