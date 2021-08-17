package fire.olympics.display;

import fire.olympics.App;
import fire.olympics.graphics.ShaderProgram;
import fire.olympics.graphics.Transformation;
import fire.olympics.graphics.VertexArrayObject;
import org.joml.Matrix4f;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL33C.*;

public class Renderer implements AutoCloseable {

    private Window window;
    private ShaderProgram program;
    private VertexArrayObject vao;
    private final int vertexAttributeIndex = 0;
    private static final float FOV = (float) Math.toRadians(60.0f);
    private static final float Z_NEAR = 0.01f;
    private static final float Z_FAR = 1000.f;
    private Matrix4f projectionMatrix;
    private Transformation transformation;
    private GameItem[] gameItems;

    public Renderer(Window window, ShaderProgram program, GameItem[] gameItems) {
        transformation = new Transformation();
        this.gameItems = gameItems;
        this.program = program;
        this.window = window;
        this.vao = new VertexArrayObject();
    }

    public void update() {
        // Update rotation angle
        for (GameItem gameItem:gameItems) {
            float rotation = gameItem.getRotation().y() + 1.5f;
            if ( rotation > 360 ) {
                rotation = 0;
            }
            gameItem.setRotation(rotation, rotation, rotation);
        }
    }

    public void run() {
        while (!glfwWindowShouldClose(window.getWindow())) {

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

            update();

            window.update();
        }
    }

    @Override
    public void close() {
        vao.close();
    }
}
