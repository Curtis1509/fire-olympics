package fire.olympics.fontMeshCreator;

import org.joml.Vector2f;
import org.joml.Vector3f;

/**
 * Represents a piece of text in the game.
 */
public class GUIText {

    /**
     * The string that is rendered.
     *
     * @implNote It is save to assign this value any value directly because the
     *           {@code TextMesh } class detects when this value is change and
     *           updates the associated vertex array object data before the text is
     *           rendered.
     */
    public String value;
    /**
     * A scale factor that affects how large the text is rendered. 1.0 is the
     * default and is small. 2.0 is twice the size of 1.0.
     */
    public float fontSize;

    /**
     * The effective line height of the text. This should remain constant.
     */
    public float lineHeight = 0.03f;

    /**
     * The color of the text. The three components represent red, green, blue and
     * have domain 0 to 1.0.
     */
    public final Vector3f color = new Vector3f(0f, 0f, 0f);

    /**
     * The position of the top-left corner of the text in screen-space. (0, 0) is
     * the top left corner of the screen, (1, 1) is the bottom right.
     */
    public final Vector2f position = new Vector2f();

    /**
     * The maximum length of the line in screen space coordinates.
     * 
     * Basically, the width of the virtual page in terms of screen width (1 is full
     * screen width, 0.5 is half the width of the screen, etc.) Text cannot go off
     * the edge of the page, so if the text is longer than this length it will go
     * onto the next line. When text is centered it is centered into the middle of
     * the line, based on this line length value.
     */
    public float lineMaxSize = 1.0f;

    /**
     * A font loaded in memory with character information and the texture.
     */
    public FontType font;

    /**
     * @return {@code true} if the text should be centered.
     */
    public boolean isCentered = false;

    /**
     * Creates text that can be rendered in an OpenGL context.
     *
     * @param font  The font to use for rendering the text.
     * @param value The string to render.
     */
    public GUIText(FontType font, String value) {
        this.value = value;
        this.font = font;
    }
}
