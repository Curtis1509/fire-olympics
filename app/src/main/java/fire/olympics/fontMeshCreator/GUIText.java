package fire.olympics.fontMeshCreator;

import org.joml.Vector2f;
import org.joml.Vector3f;

/**
 * Represents a piece of text in the game.
 */
public class GUIText {

    private String textString;
    public float fontSize;
    public float lineHeight = 0.03f;
    public final Vector3f color = new Vector3f(0f, 0f, 0f);

    /**
     * @return The position of the top-left corner of the text in screen-space.
     *         (0, 0) is the top left corner of the screen, (1, 1) is the bottom
     *         right.
     */
    public final Vector2f position;
    public float lineMaxSize;
    public FontType font;

    /**
     * @return {@code true} if the text should be centered.
     */
    public boolean isCentered = false;

    /**
     * Creates a new text, loads the text's quads into a VAO, and adds the text
     * to the screen.
     * 
     * @param text
     *            - the text.
     * @param fontSize
     *            - the font size of the text, where a font size of 1 is the
     *            default size.
     * @param font
     *            - the font that this text should use.
     * @param position
     *            - the position on the screen where the top left corner of the
     *            text should be rendered. The top left corner of the screen is
     *            (0, 0) and the bottom right is (1, 1).
     * @param maxLineLength
     *            - basically the width of the virtual page in terms of screen
     *            width (1 is full screen width, 0.5 is half the width of the
     *            screen, etc.) Text cannot go off the edge of the page, so if
     *            the text is longer than this length it will go onto the next
     *            line. When text is centered it is centered into the middle of
     *            the line, based on this line length value.
     * @param centered
     *            - whether the text should be centered or not.
     */
    public GUIText(String text, float fontSize, FontType font, Vector2f position, float maxLineLength,
            boolean centered) {
        this.textString = text;
        this.fontSize = fontSize;
        this.font = font;
        this.position = position;
        this.lineMaxSize = maxLineLength;
        this.isCentered = centered;
    }

    /**
     * @return The string of text.
     */
    public String text() {
        return textString;
    }
}
