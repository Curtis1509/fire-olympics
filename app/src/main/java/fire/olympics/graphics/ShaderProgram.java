package fire.olympics.graphics;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryStack;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glValidateProgram;

class Shader {
    public int shaderId;
    public int type;
    public Path path;
    public String sourceCode;

    public Shader(Path path, int type) throws Exception {
        this.path = path;
        this.sourceCode = Files.readString(path);
        this.type = type;
        this.shaderId = glCreateShader(type);

        glShaderSource(shaderId, sourceCode);
        glCompileShader(shaderId);

        if (glGetShaderi(shaderId, GL_COMPILE_STATUS) == GL_FALSE) {
            String log = glGetShaderInfoLog(shaderId);
            Path filename = path.getFileName();
            String msg = String.format("Failed to compile vertex shader %s%n%s", filename, log);
            throw new Exception(msg);
        }
    }

    public void close() {
        glDeleteShader(shaderId);
    }
}

/**
 * An improvement to this class could be to use glBindAttribLocation
 * to tell opengl the name of our attributes that are being passed through the
 * shader program.
 */
public class ShaderProgram {
    private int program;
    private final HashMap<String, Integer> uniforms = new HashMap<>();
    private final ArrayList<Shader> shaders = new ArrayList<>();

    public ShaderProgram() { }

    public void setUniform(String uniformName, Vector3f value) {
        int id = uniforms.getOrDefault(uniformName, -1);
        if (id != -1) {
            glUniform3f(id, value.x, value.y, value.z);
        }
    }

    public void setUniform(String uniformName, Vector2f value) {
        int id = uniforms.getOrDefault(uniformName, -1);
        if (id != -1) {
            glUniform2f(id, value.x, value.y);
        }
    }

    public void setUniform(String uniformName, int value) {
        int id = uniforms.getOrDefault(uniformName, -1);
        if (id != -1) {
            glUniform1i(id, value);
        }
    }

    public void setUniform(String uniformName, Matrix4f value) {
        int id = uniforms.getOrDefault(uniformName, -1);
        if (id != -1) {
            try (MemoryStack stack = MemoryStack.stackPush()) {
                glUniformMatrix4fv(id, false, value.get(stack.mallocFloat(16)));
            }
        }
    }

    public void setUniform(String uniformName, Vector4f v) {
        int id = uniforms.getOrDefault(uniformName, -1);
        if (id != -1) { 
            glUniform4f(id, v.x, v.y, v.z, v.w);
        }
    }

    public void createUniform(String uniformName) throws Exception {
        int uniformLocation = glGetUniformLocation(program, uniformName);
        if (uniformLocation < 0) {
            System.out.println(String.format("warning: could not find uniform named: \"%s\"", uniformName));
            System.out.println(String.format("note: shaders considered were are:"));
            for (Shader s : shaders) {
                System.out.println(String.format("\t%s", s.path.getFileName()));
            }
        } else {
            uniforms.put(uniformName, uniformLocation);
        }
    }

    public void load(int type, Path shaderPath) throws Exception {
        Shader shader = new Shader(shaderPath, type);
        shaders.add(shader);
    }

    public void link() throws Exception {
        program = glCreateProgram();

        for (Shader s : shaders) {
            glAttachShader(program, s.shaderId);
        }

        glLinkProgram(program);
        
        for (Shader s: shaders) {
            glDetachShader(program, s.shaderId);
        }

        if (glGetProgrami(program, GL_LINK_STATUS) == 0) {
            throw new Exception("Error linking Shader code: " + glGetProgramInfoLog(program));
        }
    }

    public void validate() throws Exception {
        VertexArrayObject vao = new VertexArrayObject();
        vao.use();
        glValidateProgram(program);
        int result = glGetProgrami(program, GL_VALIDATE_STATUS);
        vao.done();
        vao.close();
        if (result == 0) {
            String msg = "Failed to validate shader code: " + glGetProgramInfoLog(program, 1024);
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
