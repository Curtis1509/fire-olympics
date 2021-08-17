package fire.olympics.display;

import org.joml.Matrix4f;
import org.lwjgl.system.MemoryStack;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glValidateProgram;

/*
* Based on the source code retrieved from the following video by Tutorial Edge
* https://www.youtube.com/watch?v=AjQ6U-xEDmw
* */

public class ShaderProgram {

    private int program;
    private Path vertexPath;
    private Path fragmentPath;
    private final Map<String, Integer> uniforms;

    public ShaderProgram(Path vertexPath, Path fragmentPath) {
        uniforms = new HashMap<>();
        this.vertexPath = vertexPath;
        this.fragmentPath = fragmentPath;

    }
    public void setUniform(String uniformName, int value) {
        glUniform1i(uniforms.get(uniformName), value);
    }
    public void setUniform(String uniformName, Matrix4f value) {
        // Dump the matrix into a float buffer
        try (MemoryStack stack = MemoryStack.stackPush()) {
            glUniformMatrix4fv(uniforms.get(uniformName), false,
                    value.get(stack.mallocFloat(16)));
        }
    }

    public void readCompileAndLink() throws Exception {
        String vertex = Files.readString(vertexPath);
        String fragment = Files.readString(fragmentPath);
        compileAndLink(vertex, fragment);
    }
    public void createUniform(String uniformName) throws Exception {
        int uniformLocation = glGetUniformLocation(program, uniformName);
        if (uniformLocation < 0) {
            throw new Exception("Could not find uniform:" + uniformName);
        }
        uniforms.put(uniformName, uniformLocation);
    }


    private void compileAndLink(String vert, String frag) throws Exception {
        program = glCreateProgram();
        int vertID = glCreateShader(GL_VERTEX_SHADER);
        int fragID = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(vertID, vert);
        glShaderSource(fragID, frag);
        glCompileShader(vertID);
        if (glGetShaderi(vertID, GL_COMPILE_STATUS) == GL_FALSE) {
            throw new Exception("Failed to compile vertex shader");
        }

        glCompileShader(fragID);
        if (glGetShaderi(fragID, GL_COMPILE_STATUS) == GL_FALSE) {
            // fixme: resource leak - can you spot it?
            throw new Exception("Failed to compile fragment shader");
        }

        glAttachShader(program, vertID);
        glAttachShader(program, fragID);
        glLinkProgram(program);

        createUniform("projectionMatrix");
        //createUniform("worldMatrix");
        //createUniform("texture_sampler");

        if (vertID != 0) glDetachShader(program, vertID);
        if (fragID != 0) glDetachShader(program, fragID);
        
        if (glGetProgrami(program, GL_LINK_STATUS) == 0) {
            throw new Exception("Error linking Shader code: " + glGetProgramInfoLog(program, 1024));
        }
    }

    public void validate() throws Exception {
        glValidateProgram(program);
        if (glGetProgrami(program, GL_VALIDATE_STATUS) == 0) {
            String msg = "warning: validating shader code: " + glGetProgramInfoLog(program, 1024);
            throw new Exception(msg);
        }
    }

    public void bind() {
        glUseProgram(program);
    }

    public void unbind() {
        glUseProgram(0);
    }


}


