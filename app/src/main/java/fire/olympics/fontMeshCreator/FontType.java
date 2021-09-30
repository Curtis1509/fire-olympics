package fire.olympics.fontMeshCreator;

import fire.olympics.graphics.TextMeshData;
import fire.olympics.graphics.Texture;
import java.util.ArrayList;
import java.nio.file.Path;
import java.util.HashMap;

public class FontType {
    public static final class Padding {
        public int left;
        public int right;
        public int top;
        public int bottom;

        public int width() {
            return left + right;
        }

        public int height() {
            return top + bottom;
        }
    }

    public static final class CharacterAttribute {
        public int id;
        public int x;
        public int y;
        public int width;
        public int height;
        public int xoffset;
        public int yoffset;
        public int xadvance;
    }

    /**
     * Simple data structure class holding information about a certain glyph in the
     * font texture atlas. All sizes are for a font-size of 1.
     */
    public static final class Character {
        /**
         * @param id
         *            - the ASCII value of the character.
         * @param xTextureCoord
         *            - the x texture coordinate for the top left corner of the
         *            character in the texture atlas.
         * @param yTextureCoord
         *            - the y texture coordinate for the top left corner of the
         *            character in the texture atlas.
         * @param xTexSize
         *            - the width of the character in the texture atlas.
         * @param yTexSize
         *            - the height of the character in the texture atlas.
         * @param xOffset
         *            - the x distance from the curser to the left edge of the
         *            character's quad.
         * @param yOffset
         *            - the y distance from the curser to the top edge of the
         *            character's quad.
         * @param sizeX
         *            - the width of the character's quad in screen space.
         * @param sizeY
         *            - the height of the character's quad in screen space.
         * @param xAdvance
         *            - how far in pixels the cursor should advance after adding
         *            this character.
         */

        public int id;
        public double xTextureCoord;
        public double yTextureCoord;
        public double xMaxTextureCoord;
        public double yMaxTextureCoord;
        public double xOffset;
        public double yOffset;
        public double sizeX;
        public double sizeY;
        public double xAdvance;
    }


    public final Texture texture;

    private final Padding padding = new Padding();
    private final int lineHeight;
    private final int imageWidth;
    private final int imageHeight;
    private final HashMap<Integer, CharacterAttribute> characters = new HashMap<>();

    public FontType(Path path, Texture texture) {
        this.texture = texture;
        MetaFile reader = new MetaFile(path.toFile(), 1.0);
        reader.processNextLine();

        int[] pads = reader.getValuesOfVariable("padding");
        padding.top = pads[0];
        padding.left = pads[1];
        padding.bottom = pads[2];
        padding.right = pads[3];

        reader.processNextLine();
        lineHeight = reader.getValueOfVariable("lineHeight"); 
        
        imageWidth = reader.getValueOfVariable("scaleW");
        imageHeight = reader.getValueOfVariable("scaleH");
        reader.processNextLine();
        reader.processNextLine();
        while (reader.processNextLine()) {
            CharacterAttribute attribute = new CharacterAttribute();
            attribute.id = reader.getValueOfVariable("id");
            attribute.x = reader.getValueOfVariable("x");
            attribute.y = reader.getValueOfVariable("y");
            attribute.width = reader.getValueOfVariable("width");
            attribute.height = reader.getValueOfVariable("height");
            attribute.xoffset = reader.getValueOfVariable("xoffset");
            attribute.yoffset = reader.getValueOfVariable("yoffset");
            attribute.xadvance = reader.getValueOfVariable("xadvance");
            characters.put(attribute.id, attribute);
        }
    }
 
    private double spaceWidth(double aspectRatio, double lineHeight) {
        double verticalPerPixelSize = lineHeight / (double) this.lineHeight;
        double horizontalPerPixelSize = verticalPerPixelSize / aspectRatio;
        return (characters.get(32).xadvance - padding.width()) / horizontalPerPixelSize;
    }

    /**
     * Loads all the data about one character in the texture atlas and converts
     * it all from 'pixels' to 'screen-space' before storing. The effects of
     * padding are also removed from the data.
     * 
     * @param imageSize
     *            - the size of the texture atlas in pixels.
     * @return The data about the character.
     */
    private Character character(int asciiCode, double aspectRatio, double lineHeight) {
        double verticalPerPixelSize = lineHeight / (double) this.lineHeight;
        double horizontalPerPixelSize = verticalPerPixelSize / aspectRatio;

        FontType.CharacterAttribute attribute = characters.get(asciiCode);

        int desiredPadding = 3;
        int width = attribute.width - (padding.width() - (2 * desiredPadding));
        int height = attribute.height - ((padding.height()) - (2 * desiredPadding));

        Character c = new Character();
        c.id = attribute.id;
        c.xTextureCoord = ((double) attribute.x + padding.left - desiredPadding) / imageWidth;
        c.yTextureCoord = ((double) attribute.y + padding.top - desiredPadding) / imageHeight;
        c.sizeX = width * horizontalPerPixelSize;
        c.sizeY = height * verticalPerPixelSize;
        c.xMaxTextureCoord = c.xTextureCoord + (double) width / imageWidth;
        c.yMaxTextureCoord = c.yTextureCoord + (double) height / imageHeight;
        c.xOffset = (attribute.xoffset + padding.left - desiredPadding) * horizontalPerPixelSize;
        c.yOffset = (attribute.yoffset + (padding.top - desiredPadding)) * verticalPerPixelSize;
        c.xAdvance = (attribute.xadvance - padding.width()) * horizontalPerPixelSize;
        return c;
    }

    public TextMeshData createTextMesh(GUIText text, double aspectRatio) {
        ArrayList<Line> lines = createStructure(text, aspectRatio);
        TextMeshData data = createQuadVertices(text, lines, aspectRatio);
        return data;
    }

    private ArrayList<Line> createStructure(GUIText text, double aspectRatio) {
        char[] chars = text.value.toCharArray();
        ArrayList<Line> lines = new ArrayList<Line>();
        Line currentLine = new Line(spaceWidth(aspectRatio, text.lineHeight), text.fontSize, text.lineMaxSize);
        Word currentWord = new Word(text.fontSize);
        for (char c : chars) {
            int ascii = (int) c;
            if (ascii == 32 /* i.e. space */) {
                boolean added = currentLine.attemptToAddWord(currentWord);
                if (!added) {
                    lines.add(currentLine);
                    currentLine = new Line(spaceWidth(aspectRatio, text.lineHeight), text.fontSize, text.lineMaxSize);
                    currentLine.attemptToAddWord(currentWord);
                }
                currentWord = new Word(text.fontSize);
                continue;
            }
            Character character = character(ascii, aspectRatio, text.lineHeight);
            currentWord.addCharacter(character);
        }
        completeStructure(lines, currentLine, currentWord, text, aspectRatio);
        return lines;
    }

    private void completeStructure(ArrayList<Line> lines, Line currentLine, Word currentWord, GUIText text, double aspectRatio) {
        boolean added = currentLine.attemptToAddWord(currentWord);
        if (!added) {
            lines.add(currentLine);
            currentLine = new Line(spaceWidth(aspectRatio, text.lineHeight), text.fontSize, text.lineMaxSize);
            currentLine.attemptToAddWord(currentWord);
        }
        lines.add(currentLine);
    }

    private TextMeshData createQuadVertices(GUIText text, ArrayList<Line> lines, double aspectRatio) {
        double curserX = 0f;
        double curserY = 0f;
        ArrayList<Float> vertices = new ArrayList<Float>();
        ArrayList<Float> textureCoords = new ArrayList<Float>();
        for (Line line : lines) {
            if (text.isCentered) {
                curserX = (line.getMaxLength() - line.getLineLength()) / 2;
            }
            for (Word word : line.getWords()) {
                for (Character letter : word.getCharacters()) {
                    addVerticesForCharacter(curserX, curserY, letter, text.fontSize, vertices);
                    addQuad(textureCoords, letter.xTextureCoord, letter.yTextureCoord, letter.xMaxTextureCoord, letter.yMaxTextureCoord);
                    curserX += letter.xAdvance * text.fontSize;
                }
                curserX += spaceWidth(aspectRatio, text.lineHeight) * text.fontSize;
            }
            curserX = 0;
            curserY += text.lineHeight * text.fontSize;
        }
        return new TextMeshData(listToArray(vertices), listToArray(textureCoords));
    }

    private void addVerticesForCharacter(double curserX, double curserY, Character character, double fontSize,
            ArrayList<Float> vertices) {
        double x = curserX + (character.xOffset * fontSize);
        double y = curserY + (character.yOffset * fontSize);
        double maxX = x + (character.sizeX * fontSize);
        double maxY = y + (character.sizeY * fontSize);
        double properX = (2 * x) - 1;
        double properY = (-2 * y) + 1;
        double properMaxX = (2 * maxX) - 1;
        double properMaxY = (-2 * maxY) + 1;
        addQuad(vertices, properX, properY, properMaxX, properMaxY);
    }

    private static void addQuad(ArrayList<Float> list, double x, double y, double maxX, double maxY) {
        list.add((float) x);
        list.add((float) y);
        list.add((float) x);
        list.add((float) maxY);
        list.add((float) maxX);
        list.add((float) maxY);
        list.add((float) maxX);
        list.add((float) maxY);
        list.add((float) maxX);
        list.add((float) y);
        list.add((float) x);
        list.add((float) y);
    }

    private static float[] listToArray(ArrayList<Float> listOfFloats) {
        float[] array = new float[listOfFloats.size()];
        for (int i = 0; i < array.length; i++) {
            array[i] = listOfFloats.get(i);
        }
        return array;
    }
}
