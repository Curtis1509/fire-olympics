package fire.olympics.graphics;

import java.nio.file.Path;

import static org.lwjgl.opengl.GL33C.*;

public class ShaderLoader {
    private final Path resourcePath;

    public ShaderLoader(Path resourcePath) {
        this.resourcePath = resourcePath;
    }

    public ShaderProgram createPlainShader() throws Exception {
        ShaderProgram program = new ShaderProgram();
        program.load(GL_VERTEX_SHADER, resource("shaders", "shader.vert"));
        program.load(GL_FRAGMENT_SHADER, resource("shaders", "shader.frag"));
        program.link();
        program.createUniform("projectionMatrix");
        program.createUniform("worldMatrix");
        program.createUniform("sun");
        program.createUniform("lightSpace");
        program.createUniform("depthMap");
        program.validate();
        program.bind();
        program.setUniform("depthMap", 1);
        program.unbind();
        return program;
    }

    public ShaderProgram createTexturedShader() throws Exception {
        ShaderProgram programWithTexture = new ShaderProgram();
        programWithTexture.load(GL_VERTEX_SHADER, resource("shaders", "shader_with_texture.vert"));
        programWithTexture.load(GL_FRAGMENT_SHADER, resource("shaders", "shader_with_texture.frag"));
        programWithTexture.link();
        programWithTexture.createUniform("projectionMatrix");
        programWithTexture.createUniform("worldMatrix");
        programWithTexture.createUniform("sun");
        programWithTexture.createUniform("lightSpace");
        programWithTexture.createUniform("texture_sampler");
        programWithTexture.createUniform("depthMap");
        programWithTexture.validate();
        programWithTexture.bind();
        programWithTexture.setUniform("texture_sampler", 0);
        programWithTexture.setUniform("depthMap", 1);
        programWithTexture.unbind();
        return programWithTexture;
    }

    public ShaderProgram createTextShader() throws Exception {
        ShaderProgram textShaderProgram = new ShaderProgram();
        textShaderProgram.load(GL_VERTEX_SHADER, resource("shaders", "shader_for_text.vert"));
        textShaderProgram.load(GL_FRAGMENT_SHADER, resource("shaders", "shader_for_text.frag"));
        textShaderProgram.link();
        textShaderProgram.createUniform("colour");
        textShaderProgram.createUniform("translation");
        textShaderProgram.validate();
        return textShaderProgram;
    }


    public ShaderProgram createParticleShader() throws Exception {
        ShaderProgram particleShader = new ShaderProgram();
        particleShader.load(GL_VERTEX_SHADER, resource("shaders", "particle_system.vert"));
        particleShader.load(GL_GEOMETRY_SHADER, resource("shaders", "particle_system.geom"));
        particleShader.load(GL_FRAGMENT_SHADER, resource("shaders", "particle_system.frag"));
        particleShader.link();
        particleShader.createUniform("viewProjectionMatrix");
        particleShader.createUniform("cameraLocation");
        return particleShader;
    }

    public ShaderProgram createDepthShader() throws Exception {
        Path vert = resource("shaders", "depth.vert");
        Path frag = resource("shaders", "depth.frag");
        ShaderProgram program = new ShaderProgram();
        program.load(GL_VERTEX_SHADER, vert);
        program.load(GL_FRAGMENT_SHADER, frag);
        program.link();
        program.validate();

        program.createUniform("lightSpace");
        program.createUniform("worldMat");

        return program;
    }

    private Path resource(String first, String... more) {
        return resourcePath.resolve(Path.of(first, more));
    }
}
