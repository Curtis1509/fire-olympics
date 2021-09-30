package fire.olympics.tests;

import org.joml.Vector2f;

import fire.olympics.App;
import fire.olympics.display.Controller ;
import fire.olympics.display.Renderer;
import fire.olympics.display.Window;
import fire.olympics.fontMeshCreator.FontType;
import fire.olympics.fontMeshCreator.GUIText;
import fire.olympics.graphics.ModelLoader;
import fire.olympics.graphics.TextMesh;

public class TextController extends Controller {
    private TextMesh mesh;
    private GUIText text;

    public TextController(App app, Window window, Renderer renderer, ModelLoader loader, FontType fontType) {
        super(app, window, renderer, loader);
        this.text = new GUIText("", 5, fontType, new Vector2f(0f, 0f), 1f, true);
        text.color.set(0.0f, 0.5f, 0.5f);
        mesh = new TextMesh(text);
        renderer.addText(mesh);
    }

    @Override
    public void update(double timeDelta) {

    }

    @Override
    public void keyboardInput(String unicodeCharacter) {
        System.out.println(unicodeCharacter);
        String t = text.text();
        String newT = t + unicodeCharacter;
        text.set(newT);
   }
}
