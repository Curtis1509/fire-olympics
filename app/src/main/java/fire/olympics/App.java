/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package fire.olympics;

import fire.olympics.display.*;

import fire.olympics.graphics.Mesh;
import fire.olympics.graphics.ModelLoader;
import fire.olympics.graphics.ShaderProgram;
import org.lwjgl.*;

import java.nio.file.Files;
import java.nio.file.Path;

public class App {
    private Path resourcePath = Path.of("app", "src", "main", "resources");

    public App() {
        if(!Files.exists(resourcePath))
            resourcePath = Path.of("app").relativize(resourcePath);
    }

    Window window;
    GameItem[] gameItem = new GameItem[1];


    public void run() {
        System.out.println("LWJGL version: " + Version.getVersion());

        try {
            window = new Window("Fire Olympics", 800, 600);
            window.init();
            System.out.println(window.openGlVersion());

            // todo: improve resource loading
            // At the moment this assumes the current working directory is the project directory,
            // is not necessarily true. Typically, the shaders would be included as resource files 
            // some how during the build. We could also watch for changes to the files and recompile
            // the shaders to make experimenting easier.

            Path vertPath = resourcePath.resolve(Path.of("shaders", "shader.vert"));
            Path fragPath = resourcePath.resolve(Path.of("shaders", "shader.frag"));

            ShaderProgram pipeline = new ShaderProgram(vertPath, fragPath);
            pipeline.readCompileAndLink();
            // An exception will be thrown if your shader program is invalid.
            pipeline.validate();

            //float x, float y, float z, float length, float height, float width
            //Sample inputs. Follow the variables above to modify constraints
            float[] positions = GenerateModel.createPositions(0f,0f,0f,1f,1f,1f);
            int[] indices = GenerateModel.createIndicies();
            float[] colours = GenerateModel.createColours();

            Mesh mesh = new Mesh(positions,indices, new float[]{});
            mesh.attachMaterial(colours);
            mesh.setProgram(pipeline);

            // Create a gameItem
            //gameItem[0] = new GameItem(new Mesh[]{mesh});
            gameItem[0] = ModelLoader.loadModel(resourcePath.resolve(Path.of("models", "proto_arrow_textured.obj")));
            // This set the object to be behind the camera
            gameItem[0].setPosition(0,0, -10);

            Renderer render = new Renderer(window, gameItem);
            render.run();
        } catch (Exception e) {
            System.out.printf("error: %s%n", e);
        } finally {
            ModelLoader.unloadTextures();
            window.close();
        }
    }

    public static void main(String[] args) {
        Thread t = Thread.currentThread();
        if (!t.getName().equals("main")) {
            System.out.println("warning: not running on main thread!");
        }

        App app = new App();
        app.run();
    }
}
