package fire.olympics.display;

/*Based on the source code retrieved from the following video by Tutorial Edge
* https://www.youtube.com/watch?v=AjQ6U-xEDmw
* */

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glValidateProgram;

public class Shaders {

    private static int program;

    public static int loadProgram(String vertexPath, String fragmentPath) {
        String vertex = convertToString(vertexPath);
        String fragment = convertToString(fragmentPath);
        return createProgram(vertex, fragment);
    }

    public static int createProgram(String vert, String frag) {
        program = glCreateProgram();
        int vertID = glCreateShader(GL_VERTEX_SHADER);
        int fragID = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(vertID, vert);
        glShaderSource(fragID, frag);
        glCompileShader(vertID);
        if (glGetShaderi(vertID, GL_COMPILE_STATUS) == GL_FALSE) {
            System.err.println("Failed to compile vertex shader");
            //System.err.println(glGetShaderInfoLog(vertID));
        }
        glCompileShader(fragID);
        if (glGetShaderi(fragID, GL_COMPILE_STATUS) == GL_FALSE) {
            System.err.println("Failed to compile fragment shader");
            //System.err.println(glGetShaderInfoLog(fragID));
        }

        glAttachShader(program, vertID);
        glAttachShader(program, fragID);
        glLinkProgram(program);
        glValidateProgram(program);
        return program;
    }

    public static String convertToString(String location) {
        StringBuilder result = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(location));
            String buffer = "";
            while ((buffer = reader.readLine()) != null) {
                result.append(buffer);
                result.append("\n");
            }
            reader.close();
        } catch (IOException e) {
            System.err.println(e);
        }
        return result.toString();
    }
    public void bind() {
        glUseProgram(program);
    }

    public void unbind() {
        glUseProgram(0);
    }

    public void link() throws Exception {
        glLinkProgram(program);
        if (glGetProgrami(program, GL_LINK_STATUS) == 0) {
            throw new Exception("Error linking Shader code: " + glGetProgramInfoLog(program, 1024));
        }

        glValidateProgram(program);
        if (glGetProgrami(program, GL_VALIDATE_STATUS) == 0) {
            System.err.println("Warning validating Shader code: " + glGetProgramInfoLog(program, 1024));
        }

    }
}


