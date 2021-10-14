package fire.olympics.display;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import fire.olympics.App;
import fire.olympics.graphics.TextMesh;
import fire.olympics.fontMeshCreator.GUIText;
import fire.olympics.particles.ParticleSystem;
import fire.olympics.graphics.ShaderProgram;

import static org.lwjgl.opengl.GL33C.*;

import java.util.ArrayList;

public class Renderer {
    private float FOV = (float) Math.toRadians(60.0f);
    private float z_near = 0.01f;
    private float z_far = 2000f;
    private final ArrayList<GameItem> gameItems = new ArrayList<>();
    private final ArrayList<GameItem> gameItemsWithTextures = new ArrayList<>();
    private final ArrayList<GameItem> gameItemsWithOutTextures = new ArrayList<>();
    private final ArrayList<TextMesh> textMeshes = new ArrayList<>();
    private final ArrayList<ParticleSystem> particleSystems = new ArrayList<>();

    private float aspectRatio = 1.0f;
    private ShaderProgram program;
    private ShaderProgram programWithTexture;
    private ShaderProgram textShaderProgram;
    private ShaderProgram particleShader;
    private Vector3f sunDirection = new Vector3f(0, 300, 10); // sun is behind and above camera
    private Matrix4f projectionMatrix = new Matrix4f();
    private Matrix4f worldMatrix = new Matrix4f();

    public Camera camera = new Camera();
    public final Vector4f backgroundColor = new Vector4f();

    public Renderer(ShaderProgram program, ShaderProgram programWithTexture, ShaderProgram textShaderProgram, ShaderProgram particleShader) {
        this.program = program;
        this.programWithTexture = programWithTexture;
        this.textShaderProgram = textShaderProgram;
        this.particleShader = particleShader;
    }

    public void add(GameItem tree) {
        gameItems.add(tree);
        if (!tree.mesh.hasTexture()) {
            gameItemsWithOutTextures.add(tree);
        } else {
            gameItemsWithTextures.add(tree);
        }
    }

    public void add(ParticleSystem particleSystem) {
        particleSystems.add(particleSystem);
    }

    public void addText(GUIText text) {
        textMeshes.add(new TextMesh(text));
    }

    public void setAspectRatio(float ratio) {
        aspectRatio = ratio;
        recalculateProjectionMatrix();
        for (TextMesh mesh : textMeshes) {
            mesh.recalculate(aspectRatio);
        }
    }

    public void setFieldOfView(float fov) {
        FOV = (float) Math.toRadians(fov);
        recalculateProjectionMatrix();
    }
    
    public void setZClipping(float near, float far) {
        z_near = near;
        z_far = far;
        recalculateProjectionMatrix();
    }

    private void recalculateProjectionMatrix() {
        projectionMatrix.setPerspective(FOV, aspectRatio, z_near, z_far);
    }

    public void render() {
        glClearColor(backgroundColor.x, backgroundColor.y, backgroundColor.z, backgroundColor.w);
        // Apply the color.
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        // Start issueing render commands.
        if (program != null) {
            program.bind();
            render(gameItemsWithOutTextures, worldMatrix, program);
            program.unbind();
        }

        if (programWithTexture != null) {
            programWithTexture.bind();
            render(gameItemsWithTextures, worldMatrix, programWithTexture);
            programWithTexture.unbind();
        }
        App.checkError("1");


        if (textShaderProgram != null) {
            glDisable(GL_CULL_FACE);
            glEnable(GL_BLEND);
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            glDisable(GL_DEPTH_TEST);
            textShaderProgram.bind();
            for (TextMesh meshText : textMeshes) {
                if (meshText.isHidden()) continue;
                glActiveTexture(GL_TEXTURE0);
                meshText.getFontTexture().bind();
                textShaderProgram.setUniform("colour", meshText.getColor());
                textShaderProgram.setUniform("translation", meshText.getPosition());
                meshText.render();
                meshText.getFontTexture().unbind();
            }
            textShaderProgram.unbind();
            glDisable(GL_BLEND);
            glEnable(GL_DEPTH_TEST);
            glEnable(GL_CULL_FACE);
        }

        if (particleShader != null) {
            particleShader.bind();
            glDisable(GL_CULL_FACE);
            glEnable(GL_BLEND);
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            particleShader.setUniform("projectionMatrix", projectionMatrix);
            for (ParticleSystem particleSystem : particleSystems) {
                renderParticleSystem(particleSystem);
            }
            particleShader.unbind();
            glDisable(GL_BLEND);
            glEnable(GL_CULL_FACE);
        }
    }

    private void render(ArrayList<GameItem> objects, Matrix4f worldMatrix, ShaderProgram program) {
        program.setUniform("projectionMatrix", projectionMatrix);
        program.setUniform("sun", sunDirection);

        // Render each gameItem
        for (GameItem object : objects) {
            // Set world matrix for this item
            Vector3f rotation = object.getRotation();
            worldMatrix
                    .translation(object.getPosition()).
                    rotate((float)Math.toRadians(rotation.y),0, 1, 0).
                    rotate((float)Math.toRadians(rotation.z),0, 0, 1).
                    rotate((float)Math.toRadians(rotation.x), 1, 0, 0)
                    .scale(object.getScale());
            worldMatrix.mulLocal(camera.camera);

            program.setUniform("worldMatrix", worldMatrix);
            object.mesh.render();
        }
    }

    private void renderParticleSystem(ParticleSystem particleSystem) {
        // Set world matrix for this item
        Vector3f rotation = particleSystem.rotation;
        worldMatrix
                .translation(particleSystem.position)
                .rotateAffineXYZ(
                    (float) Math.toRadians(rotation.x),
                    (float) Math.toRadians(rotation.y),
                    (float) Math.toRadians(rotation.z))
                .scale(particleSystem.scale);
        // worldMatrix.mulLocal(camera);
        particleShader.setUniform("particleSystemMatrix", worldMatrix);
        particleShader.setUniform("hotColor", particleSystem.hotColor);
        particleShader.setUniform("coldColor", particleSystem.coldColor);
        particleShader.setUniform("cameraMatrix", camera.camera);
        particleShader.setUniform("cameraLocation", camera.cameraPosition);
        particleShader.setUniform("cameraDirection", camera.cameraAngle);
        particleSystem.render();
    }
}
