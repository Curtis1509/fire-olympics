package fire.olympics.display;

import java.nio.file.Files;
import java.nio.file.Path;

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

    public ShaderProgram(Path vertexPath, Path fragmentPath) {
        this.vertexPath = vertexPath;
        this.fragmentPath = fragmentPath;
    }

    public void readCompileAndLink() throws Exception {
        String vertex = Files.readString(vertexPath);
        String fragment = Files.readString(fragmentPath);
        compileAndLink(vertex, fragment);
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

        if (vertID != 0) glDetachShader(program, vertID);
        if (fragID != 0) glDetachShader(program, fragID);
        
        if (glGetProgrami(program, GL_LINK_STATUS) == 0) {
            throw new Exception("Error linking Shader code: " + glGetProgramInfoLog(program, 1024));
        }

        validate();
    }

    public void validate() throws Exception {
        glValidateProgram(program);
        if (glGetProgrami(program, GL_VALIDATE_STATUS) == 0) {
            String msg = "warning: validating shader code: " + glGetProgramInfoLog(program, 1024);
            System.err.println(msg);
        }
    }

    public void bind() {
        glUseProgram(program);
    }

    public void unbind() {
        glUseProgram(0);
    }
}


