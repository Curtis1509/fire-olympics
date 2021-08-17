package fire.olympics.display;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import org.joml.Vector3f;
import org.lwjgl.system.MemoryUtil;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL33.*;

public class Renderer implements AutoCloseable {

    private long window;
    private ShaderProgram program;
    private VertexArrayObject vao;
    private final int vertexAttributeIndex = 0;

    ArrayList<Renderable> objects = new ArrayList<>();

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

        vao.bindFloats(verticies, vertexAttributeIndex, GL_STATIC_DRAW, 3, GL_FLOAT);
        MemoryUtil.memFree(verticies);

        // An exception will be thrown if your shader program is invalid.
        vao.use();
        program.validate();
        vao.done();
    }

    public void add(Renderable m) {
        objects.add(m);
    }

    public void update(Renderable m){objects.get(0).equals(m);}

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
            for (Renderable r : objects) {

                r.render();
            }

            program.unbind();

            glfwSwapBuffers(window);

            // Poll for window events. The key callback above will only be
            // invoked during this call.
            glfwPollEvents();
        }
    }

    @Override
    public void close() throws Exception {
        vao.close();
    }
}
