package fire.olympics.tests;

import static org.lwjgl.glfw.GLFW.*;

import fire.olympics.App;
import fire.olympics.display.Controller ;
import fire.olympics.display.Renderer;
import fire.olympics.display.Window;
import fire.olympics.fontMeshCreator.FontType;
import fire.olympics.fontMeshCreator.GUIText;
import fire.olympics.graphics.ModelLoader;

public class TextController extends Controller {
    private GUIText text;

    public TextController(App app, Window window, Renderer renderer, ModelLoader loader, FontType fontType) {
        super(app, window, renderer, loader);
        text = new GUIText(fontType, "");
        text.fontSize = 2.0f;
        text.color.set(1.0f, 0.5f, 0.5f);
        renderer.addText(text);
    }

    @Override
    public void update(double timeDelta) {

    }

    @Override
    public void keyDown(int key, int mods) {
        switch (key) {
        case GLFW_KEY_BACKSPACE:
            if (text.value.length() > 0) {
                text.value = text.value.substring(0, text.value.length()-1);
            }
            break;
        case GLFW_KEY_ENTER:
            text.value = text.value + "\n";
            break;
        }
    }

    @Override
    public void keyboardInput(String unicodeCharacter) {
        text.value = text.value + unicodeCharacter;
   }
}
