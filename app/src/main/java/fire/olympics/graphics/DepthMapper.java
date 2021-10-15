package fire.olympics.graphics;

import fire.olympics.App;
import fire.olympics.display.GameItem;
import fire.olympics.display.Node;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import java.nio.IntBuffer;
import java.nio.file.Path;
import java.util.ArrayList;

import static org.lwjgl.opengl.GL33C.*;


public class DepthMapper {
    private static final int MAP_RES_X = 1024, MAP_RES_Y = 1024;
    private final float near = 0.1f, far = 5000;
    private final int depthFBO, depthTex;
    private final ShaderProgram program;
    private final Matrix4f lightSpace;

    public DepthMapper(Vector3f light, boolean directional) {
        this.depthFBO = glGenFramebuffers();
        this.depthTex = glGenTextures();
        this.program = new ShaderProgram();
        this.lightSpace = new Matrix4f();

        // Setup shader program
        Path vert = App.resource("shaders", "depth.vert");
        Path frag = App.resource("shaders", "depth.frag");

        try {
            program.load(GL_VERTEX_SHADER, vert);
            program.load(GL_FRAGMENT_SHADER, frag);
            program.link();
            program.validate();

            program.createUniform("lightSpace");
            program.createUniform("worldMat");
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Setup depth map texture
        glBindTexture(GL_TEXTURE_2D, depthTex);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT, MAP_RES_X, MAP_RES_Y, 0, GL_DEPTH_COMPONENT, GL_FLOAT, (IntBuffer) null);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_BORDER);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_BORDER);

        // Attach texture to FBO
        glBindFramebuffer(GL_FRAMEBUFFER, depthFBO);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, depthTex, 0);

        // Don't render anything other than depth values
        glDrawBuffer(GL_NONE);
        glReadBuffer(GL_NONE);

        // FBO and texture setup done, unbind
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        glBindTexture(GL_TEXTURE_2D, 0);

        // Compute light space
        if(directional)
            lightSpace.setOrthoSymmetric(300, 300, near, far);
        else
            lightSpace.setPerspective((float)Math.PI / 3f,MAP_RES_X / (float) MAP_RES_Y, near, far);

        lightSpace.mul(
                new Matrix4f().setLookAt(
                        light,
                        new Vector3f(),
                        new Vector3f(0, 1, 0)
                )
        );
    }

    /**
     * Computes the depth map of a scene.
     * @param list The list of GameItems in the scene
     * @param windowWidth Width of the window being rendered (to restore the viewport)
     * @param windowHeight Height of the window being rendered (^)
     */
    public void ComputeDepthMap(ArrayList<Node> list, int windowWidth, int windowHeight) {
        program.bind();
        glViewport(0, 0, MAP_RES_X, MAP_RES_Y);

        glBindFramebuffer(GL_FRAMEBUFFER, depthFBO);
        glClear(GL_DEPTH_BUFFER_BIT);

        // Cull front faces instead of back faces to better calculate depth map (peter panning)
        glCullFace(GL_FRONT);

        program.setUniform("lightSpace", lightSpace);

        for(Node node : list)
            render(node);

        // Reset cull face to back for rendering
        glCullFace(GL_BACK);

        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        glViewport(0, 0, windowWidth, windowHeight);
        program.unbind();
    }

    private void render(Node node) {
        if(node instanceof GameItem item) {
            program.setUniform("worldMat", item.getMatrix());
            item.mesh.render();
        }

        for(Node child : node.children)
            render(child);
    }

    public int getDepthMap() {
        return depthTex;
    }

    public Matrix4f getLightSpaceMatrix() {
        return lightSpace;
    }
}
