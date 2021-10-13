package fire.olympics.display;

import fire.olympics.graphics.shadow.ShadowMap;
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
    private ShaderProgram depthShadowShaderProgram;
    private Vector3f sunDirection = new Vector3f(0, 300, 10); // sun is behind and above camera
    private Matrix4f projectionMatrix = new Matrix4f();
    private Matrix4f orthoProjectionMatrix = new Matrix4f();
    private Matrix4f worldMatrix = new Matrix4f();
    private Matrix4f lightViewMatrix = new Matrix4f();
    private Vector3f cameraPosition = new Vector3f(0, 0, 0);
    private Vector3f cameraAngle = new Vector3f();
    private ShadowMap shadowMap = new ShadowMap();

    public Matrix4f camera = new Matrix4f();

    public Renderer(ShaderProgram program, ShaderProgram programWithTexture, ShaderProgram textShaderProgram, ShaderProgram particleShader, ShaderProgram depthShadowShaderProgram) throws Exception {
        this.program = program;
        this.programWithTexture = programWithTexture;
        this.textShaderProgram = textShaderProgram;
        this.particleShader = particleShader;
        this.depthShadowShaderProgram = depthShadowShaderProgram;
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

    public void addText(GUIText text, boolean menuText) {
        textMeshes.add(new TextMesh(text,menuText));
    }
    public void updateText(int index, String text){
        textMeshes.get(index).updateText(text);
    }

    public void updateCamera(Vector3f position, Vector3f angle) {
        Matrix4f translation = new Matrix4f().translation(position);
        cameraPosition = position;
        camera.identity();
        camera.rotate((float) Math.toRadians(angle.x), new Vector3f(1, 0, 0));
        camera.rotate((float) Math.toRadians(angle.y), new Vector3f(0, 1, 0));
        camera.rotate((float) Math.toRadians(angle.z), new Vector3f(0, 0, 1));
        Vector4f result = new Vector4f();
        camera.transform(0.0f, 0.0f, -1.0f, 0.0f, result);
        cameraAngle.set(result.x, result.y, result.z);
        camera.mul(translation);
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

    public void render(Window window) {
        // Set the color.
        glClearColor(0f, 0f, 0f, 1.0f);
        // Apply the color.
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);


//        if (depthShadowShaderProgram != null) {
//            renderDepthMap(window, ob);
//            window.updateViewPort(this);
//        }

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
                if (GameController.isPlaying()) {
                    if (!meshText.isMenuText()) {
                        glActiveTexture(GL_TEXTURE0);
                        meshText.getFontTexture().bind();
                        textShaderProgram.setUniform("colour", meshText.getColor());
                        textShaderProgram.setUniform("translation", meshText.getPosition());
                        meshText.render();
                        meshText.getFontTexture().unbind();
                    }
                } else {
                    if (meshText.isMenuText()) {
                        glActiveTexture(GL_TEXTURE0);
                        meshText.getFontTexture().bind();
                        textShaderProgram.setUniform("colour", meshText.getColor());
                        textShaderProgram.setUniform("translation", meshText.getPosition());
                        meshText.render();
                        meshText.getFontTexture().unbind();
                    }
                }
            }
            textShaderProgram.unbind();
            glDisable(GL_BLEND);
            glEnable(GL_DEPTH_TEST);
            glEnable(GL_CULL_FACE);
        }

        if (particleShader != null) {
            particleShader.bind();
            glDisable(GL_CULL_FACE);
            glDisable(GL_DEPTH_TEST);
            particleShader.setUniform("projectionMatrix", projectionMatrix);
            for (ParticleSystem particleSystem : particleSystems) {
                renderParticleSystem(particleSystem);
            }
            particleShader.unbind();
            glEnable(GL_CULL_FACE);
            glEnable(GL_DEPTH_TEST);
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
            worldMatrix.mulLocal(camera);

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
        particleShader.setUniform("worldMatrix", worldMatrix);
        particleShader.setUniform("hotColor", particleSystem.hotColor);
        particleShader.setUniform("coldColor", particleSystem.coldColor);
        particleShader.setUniform("cameraMatrix", camera);
        particleShader.setUniform("cameraLocation", cameraPosition);
        particleShader.setUniform("cameraDirection", cameraAngle);
        particleSystem.render();
    }

    private void renderDepthMap(Window window, ArrayList<GameItem> objects) {
        glBindFramebuffer(GL_FRAMEBUFFER, shadowMap.getDepthMapFBO());
        window.setViewPort(this, ShadowMap.SHADOW_MAP_WIDTH, ShadowMap.SHADOW_MAP_HEIGHT);

        glClear(GL_DEPTH_BUFFER_BIT);

        depthShadowShaderProgram.bind();

        float lightAngleX = (float) Math.acos(-sunDirection.z);
        float lightAngleY = (float) Math.asin(-sunDirection.x);
        float lightAngleZ = 0;

        lightViewMatrix.identity();
        // First do the rotation so camera rotates over its position
        lightViewMatrix.rotate((float)Math.toRadians(lightAngleX), new Vector3f(1, 0, 0))
                .rotate((float)Math.toRadians(lightAngleY), new Vector3f(0, 1, 0));
        // Then do the translation
        lightViewMatrix.translate(-sunDirection.x, -sunDirection.y, -sunDirection.z);


        orthoProjectionMatrix.identity();
        orthoProjectionMatrix.setOrtho(-35.0f, 35.0f, -35.0f, 35.0f, 0.1f, 75.0f);

        depthShadowShaderProgram.setUniform("orthoProjectionMatrix", orthoProjectionMatrix);
        // Render each gameItem
        for (GameItem object : objects) {
            // Set world matrix for this item
            Vector3f rotation = object.getRotation();

            // TODO render gameObjects to shadowMap

            program.setUniform("worldMatrix", worldMatrix);
            object.mesh.render();
        }

        depthShadowShaderProgram.unbind();
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }
}
