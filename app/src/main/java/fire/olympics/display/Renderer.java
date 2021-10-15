package fire.olympics.display;

import fire.olympics.graphics.DepthMapper;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import fire.olympics.graphics.TextMesh;
import fire.olympics.App;
import fire.olympics.fontMeshCreator.GUIText;
import fire.olympics.particles.ParticleSystem;
import fire.olympics.graphics.ShaderProgram;
import fire.olympics.graphics.ShaderLoader;

import static org.lwjgl.opengl.GL33C.*;

import java.util.ArrayList;

public class Renderer {
    private float FOV = (float) Math.toRadians(60.0f);
    private float z_near = 0.01f;
    private float z_far = 2000f;
    private final ArrayList<Node> gameItems = new ArrayList<>();
    private final ArrayList<TextMesh> textMeshes = new ArrayList<>();
    private final ArrayList<ParticleSystem> particleSystems = new ArrayList<>();

    private float aspectRatio = 1.0f;
    private ShaderProgram program;
    private ShaderProgram programWithTexture;
    private ShaderProgram textShaderProgram;
    private ShaderProgram particleShader;
    private Vector3f sunDirection = new Vector3f(0, 300, 10); // sun is behind and above camera
    private Matrix4f projectionMatrix = new Matrix4f();
    private final DepthMapper mapper;

    public Camera camera = new Camera();
    public final Vector4f backgroundColor = new Vector4f();

    public Renderer(ShaderLoader loader) throws Exception {
        this.program = loader.createPlainShader();
        this.programWithTexture = loader.createTexturedShader();
        this.textShaderProgram = loader.createTextShader();
        this.particleShader = loader.createParticleShader();
        this.mapper = new DepthMapper(sunDirection, true);
    }

    public void add(Node tree) {
        gameItems.add(tree);
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

    public void render(int windowWidth, int windowHeight) {
        mapper.ComputeDepthMap(gameItems, windowWidth, windowHeight);
        App.checkError("");
        glClearColor(backgroundColor.x, backgroundColor.y, backgroundColor.z, backgroundColor.w);
        // Apply the color.
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        glActiveTexture(GL_TEXTURE1);
        glBindTexture(GL_TEXTURE_2D, mapper.getDepthMap());
        glActiveTexture(GL_TEXTURE0);

        for (Node child : gameItems) {
            render(child);
        }

        if (textShaderProgram != null) {
            renderText();
        }

        if (particleShader != null) {
            renderParticleSystems();
        }
    }

    private void render(Node node) {
        if (node instanceof GameItem gameItem) {
            Matrix4f viewProjectionMatrix = new Matrix4f();
            projectionMatrix.mul(camera.getMatrix().invertAffine(), viewProjectionMatrix);
            Matrix4f worldMatrix = node.getMatrix();

            if (gameItem.mesh.hasTexture()) {
                programWithTexture.bind();
                programWithTexture.setUniform("projectionMatrix", viewProjectionMatrix);
                programWithTexture.setUniform("sun", sunDirection);
                programWithTexture.setUniform("worldMatrix", worldMatrix);
                programWithTexture.setUniform("lightSpace", mapper.getLightSpaceMatrix());
            } else {
                program.bind();
                program.setUniform("projectionMatrix", viewProjectionMatrix);
                program.setUniform("sun", sunDirection);
                program.setUniform("worldMatrix", worldMatrix);
                program.setUniform("lightSpace", mapper.getLightSpaceMatrix());
            }
            gameItem.mesh.render();
            if (gameItem.mesh.hasTexture()) {
                programWithTexture.unbind();
            } else {
                program.unbind();
            }
        }

        for (Node child : node.children) {
            render(child);
        }
    }

    private void renderText() {
        glDisable(GL_CULL_FACE);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glDisable(GL_DEPTH_TEST);
        textShaderProgram.bind();
        for (TextMesh meshText : textMeshes) {
            if (meshText.isHidden())
                continue;
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

    private void renderParticleSystems() {
        particleShader.bind();
        glDisable(GL_CULL_FACE);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glDisable(GL_DEPTH_TEST);
        glDepthMask(false);
        particleShader.setUniform("projectionMatrix", projectionMatrix);
        for (ParticleSystem particleSystem : particleSystems) {
            particleShader.setUniform("particleSystemMatrix", particleSystem.getMatrix());
            particleShader.setUniform("hotColor", particleSystem.hotColor);
            particleShader.setUniform("coldColor", particleSystem.coldColor);
            particleShader.setUniform("cameraMatrix", camera.getMatrix().invertAffine());
            particleShader.setUniform("cameraLocation", camera.position);
            particleSystem.render();
        }
        particleShader.unbind();
        glDepthMask(true);
        glEnable(GL_DEPTH_TEST);
        glDisable(GL_BLEND);
        glEnable(GL_CULL_FACE);
    }
}
