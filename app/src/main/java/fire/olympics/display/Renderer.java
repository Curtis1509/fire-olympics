package fire.olympics.display;

import java.awt.*;
import java.nio.FloatBuffer;
import java.nio.file.Path;
import java.util.ArrayList;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryUtil;

import static fire.olympics.App.gameItem;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL33.*;

public class Renderer implements AutoCloseable {

    private long window;
    private ShaderProgram program;
    private VertexArrayObject vao;
    private final int vertexAttributeIndex = 0;
    Transformation transformation;
    private static final float FOV = (float) Math.toRadians(60.0f);
    private static final float Z_NEAR = 0.01f;
    private static final float Z_FAR = 1000.f;
    private Matrix4f projectionMatrix;

    ArrayList<Renderable> objects = new ArrayList<Renderable>();

    public Renderer(long window, ShaderProgram program) throws Exception {
        transformation = new Transformation();
        this.program = program;
        this.window = window;
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

        float aspectRatio = (float) 800/600;
        projectionMatrix = new Matrix4f().setPerspective(FOV, aspectRatio,
                Z_NEAR, Z_FAR);
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
// Update projection Matrix

            // Use this program.
            program.bind();

            // Update projection Matrix
            Matrix4f projectionMatrix = transformation.getProjectionMatrix(FOV, 800,600, Z_NEAR, Z_FAR);
            program.setUniform("projectionMatrix", projectionMatrix);

            //program.setUniform("texture_sampler", 0);
            // Render each gameItem
            for (GameItem gameItem : gameItem) {
                // Set world matrix for this item
                Matrix4f worldMatrix = transformation.getWorldMatrix(
                        gameItem.getPosition(),
                        gameItem.getRotation(),
                        gameItem.getScale());
                //program.setUniform("worldMatrix", worldMatrix);
                // Render the mes for this game item
                gameItem.getMesh().render();
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
