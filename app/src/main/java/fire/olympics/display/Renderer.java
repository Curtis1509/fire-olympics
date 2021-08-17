package fire.olympics.display;

import fire.olympics.App;
import fire.olympics.graphics.ShaderProgram;
import fire.olympics.graphics.Transformation;
import fire.olympics.graphics.VertexArrayObject;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL33C.*;

public class Renderer {

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

    public Renderer(long window, ShaderProgram program) throws Exception {
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
        final Matrix4f projectionMatrix = new Matrix4f();
        final Matrix4f worldMatrix = new Matrix4f();

        while (!glfwWindowShouldClose(window.getWindow())) {

            // Set the color.
            glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
            // Apply the color.
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            // Use this program.
            program.bind();

            // Update projection Matrix
            projectionMatrix.setPerspective(FOV, width / height, Z_NEAR, Z_FAR);
            program.setUniform("projectionMatrix", projectionMatrix);

            // Render each gameItem
            for (Renderable object : gameItems) {
                // Set world matrix for this item
                Vector3f rotation = object.getRotation();
                worldMatrix
                    .translation(object.getPosition())
                    .rotateAffineXYZ(rotation.x, rotation.y, rotation.z)
                    .scale(object.getScale());

                program.setUniform("worldMatrix", worldMatrix);
                object.render();
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
