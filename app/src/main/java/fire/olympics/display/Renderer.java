package fire.olympics.display;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import fire.olympics.graphics.ShaderProgram;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.opengl.GL33C.*;

import java.util.ArrayList;

public class Renderer {

    private Window window;
    private static final float FOV = (float) Math.toRadians(60.0f);
    private static final float Z_NEAR = 0.01f;
    private static final float Z_FAR = 1000.f;
    private ArrayList<GameItem> gameItems = new ArrayList<>();
    private ArrayList<GameItem> gameItemsWithTextures = new ArrayList<>();
    private ArrayList<GameItem> gameItemsWithOutTextures = new ArrayList<>();

    private ShaderProgram program;
    private ShaderProgram programWithTexture;
    private static final Vector3f sunDirection = new Vector3f(0, 1, 1); // sun is behind and above camera

    public Renderer(Window window, ShaderProgram program, ShaderProgram programWithTexture) {
        this.window = window;
        this.program = program;
        this.programWithTexture = programWithTexture;
    }

    public void add(GameItem tree) {
        gameItems.add(tree);
        if (!tree.mesh.hasTexture()) {
            gameItemsWithOutTextures.add(tree);
        } else {
            gameItemsWithTextures.add(tree);
        }
    }

    public void update() {
        // Update rotation angle
        for (GameItem gameItem : gameItems) {
            float rotation = gameItem.getRotation().y() + 0.5f;
            if (rotation > 360) {
                rotation = 0;
            }
            gameItem.setRotation(rotation, rotation, rotation);
        }
    }

    public void run() {
        final Matrix4f projectionMatrix = new Matrix4f();
        final Matrix4f worldMatrix = new Matrix4f();

        while (!glfwWindowShouldClose(window.getWindow())) {
            update();

            if (window.isResized()) {
                glViewport(0, 0, window.getWidth(), window.getHeight());
                window.setResized(false);
            }

            // Set the color.
            glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
            // Apply the color.
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            // Update projection Matrix
            float aspectRatio = (float) window.getWidth() / window.getHeight();
            projectionMatrix.setPerspective(FOV, aspectRatio, Z_NEAR, Z_FAR);

            // Start issueing render commands.

            program.bind();
            render(gameItemsWithOutTextures, projectionMatrix, worldMatrix, program);
            program.unbind();

            programWithTexture.bind();
            render(gameItemsWithTextures, projectionMatrix, worldMatrix, programWithTexture);
            programWithTexture.unbind();

            window.update();
        }
    }

    private static void render(ArrayList<GameItem> objects, Matrix4f projectionMatrix, Matrix4f worldMatrix,
            ShaderProgram program) {
        // Render each gameItem
        for (GameItem object : objects) {
            // Set world matrix for this item
            Vector3f rotation = object.getRotation();
            worldMatrix
                    .translation(object.getPosition()).rotateAffineXYZ((float) Math.toRadians(rotation.x),
                            (float) Math.toRadians(rotation.y), (float) Math.toRadians(rotation.z))
                    .scale(object.getScale());

            program.setUniform("projectionMatrix", projectionMatrix);
            program.setUniform("worldMatrix", worldMatrix);
            program.setUniform("sun", sunDirection);
            object.render(projectionMatrix, worldMatrix);
        }
    }
}
