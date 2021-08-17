package fire.olympics.display;

import java.util.ArrayList;

import org.joml.Matrix4f;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL33C.*;

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

    public Renderer(long window, ShaderProgram program) throws Exception {
        transformation = new Transformation();
        this.program = program;
        this.window = window;
        this.vao = new VertexArrayObject();

        float aspectRatio = (float) 800/600;
        projectionMatrix = new Matrix4f().setPerspective(FOV, aspectRatio,
                Z_NEAR, Z_FAR);
        // An exception will be thrown if your shader program is invalid.
        vao.use();
        program.validate();
        vao.done();
    }

    public void run(GameItem[] gameItems) {
        while (!glfwWindowShouldClose(window)) {

            // Set the color.
            glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
            // Apply the color.
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            // Use this program.
            program.bind();

            // Update projection Matrix
            // TODO set width and height to dynamic window width and height
            Matrix4f projectionMatrix = transformation.getProjectionMatrix(FOV, 800,600, Z_NEAR, Z_FAR);
            program.setUniform("projectionMatrix", projectionMatrix);

            //program.setUniform("texture_sampler", 0);
            // Render each gameItem
            for (GameItem gameItem : gameItems) {
                // Set world matrix for this item
                Matrix4f worldMatrix =
                        transformation.getWorldMatrix(
                                gameItem.getPosition(),
                                gameItem.getRotation(),
                                gameItem.getScale());
                program.setUniform("worldMatrix", worldMatrix);
                // Render the mesh for this game item
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
