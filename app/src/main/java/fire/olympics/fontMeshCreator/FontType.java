package fire.olympics.fontMeshCreator;

import fire.olympics.graphics.Texture;
import java.util.ArrayList;

public class FontType {

    public final Texture texture;
    public final FontFile fontFile;

    public void setAspectRatio(double ratio) {
    }

    public FontType(FontFile fontFile, Texture texture) {
        this.fontFile = fontFile;
        this.texture = texture;
    }
 
    private double spaceWidth(double aspectRatio, double lineHeight) {
        double verticalPerPixelSize = lineHeight / (double) fontFile.lineHeight;
        double horizontalPerPixelSize = verticalPerPixelSize / aspectRatio;
        return (fontFile.characters.get(32).xadvance - fontFile.padding.width()) / horizontalPerPixelSize;
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
        double verticalPerPixelSize = lineHeight / (double) fontFile.lineHeight;
        double horizontalPerPixelSize = verticalPerPixelSize / aspectRatio;

        FontFile.CharacterAttribute attribute = fontFile.characters.get(asciiCode);

        int desiredPadding = 3;
        double xTex = ((double) attribute.x + fontFile.padding.left - desiredPadding) / fontFile.imageWidth;
        double yTex = ((double) attribute.y + fontFile.padding.top - desiredPadding) / fontFile.imageHeight;
        int width = attribute.width - (fontFile.padding.width() - (2 * desiredPadding));
        int height = attribute.height - ((fontFile.padding.height()) - (2 * desiredPadding));
        double quadWidth = width * horizontalPerPixelSize;
        double quadHeight = height * verticalPerPixelSize;
        double xTexSize = (double) width / fontFile.imageWidth;
        double yTexSize = (double) height / fontFile.imageHeight;
        double xOff = (attribute.xoffset + fontFile.padding.left - desiredPadding) * horizontalPerPixelSize;
        double yOff = (attribute.yoffset + (fontFile.padding.top - desiredPadding)) * verticalPerPixelSize;
        double xAdvance = (attribute.xadvance - fontFile.padding.width()) * horizontalPerPixelSize;
        return new Character(attribute.id, xTex, yTex, xTexSize, yTexSize, xOff, yOff, quadWidth, quadHeight, xAdvance);
    }

    public TextMeshData createTextMesh(GUIText text, double aspectRatio) {
        ArrayList<Line> lines = createStructure(text, aspectRatio);
        TextMeshData data = createQuadVertices(text, lines, aspectRatio);
        return data;
    }

    private ArrayList<Line> createStructure(GUIText text, double aspectRatio) {
        char[] chars = text.getTextString().toCharArray();
        ArrayList<Line> lines = new ArrayList<Line>();
        Line currentLine = new Line(spaceWidth(aspectRatio, text.lineHeight), text.getFontSize(), text.getMaxLineSize());
        Word currentWord = new Word(text.getFontSize());
        for (char c : chars) {
            int ascii = (int) c;
            if (ascii == 32 /* i.e. space */) {
                boolean added = currentLine.attemptToAddWord(currentWord);
                if (!added) {
                    lines.add(currentLine);
                    currentLine = new Line(spaceWidth(aspectRatio, text.lineHeight), text.getFontSize(), text.getMaxLineSize());
                    currentLine.attemptToAddWord(currentWord);
                }
                currentWord = new Word(text.getFontSize());
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
            currentLine = new Line(spaceWidth(aspectRatio, text.lineHeight), text.getFontSize(), text.getMaxLineSize());
            currentLine.attemptToAddWord(currentWord);
        }
        lines.add(currentLine);
    }

    private TextMeshData createQuadVertices(GUIText text, ArrayList<Line> lines, double aspectRatio) {
        text.setNumberOfLines(lines.size());
        double curserX = 0f;
        double curserY = 0f;
        ArrayList<Float> vertices = new ArrayList<Float>();
        ArrayList<Float> textureCoords = new ArrayList<Float>();
        for (Line line : lines) {
            if (text.isCentered()) {
                curserX = (line.getMaxLength() - line.getLineLength()) / 2;
            }
            for (Word word : line.getWords()) {
                for (Character letter : word.getCharacters()) {
                    addVerticesForCharacter(curserX, curserY, letter, text.getFontSize(), vertices);
                    addTexCoords(textureCoords, letter.getxTextureCoord(), letter.getyTextureCoord(),
                            letter.getXMaxTextureCoord(), letter.getYMaxTextureCoord());
                    curserX += letter.getxAdvance() * text.getFontSize();
                }
                curserX += spaceWidth(aspectRatio, text.lineHeight) * text.getFontSize();
            }
            curserX = 0;
            curserY += text.lineHeight * text.getFontSize();
        }
        return new TextMeshData(listToArray(vertices), listToArray(textureCoords));
    }

    private void addVerticesForCharacter(double curserX, double curserY, Character character, double fontSize,
            ArrayList<Float> vertices) {
        double x = curserX + (character.getxOffset() * fontSize);
        double y = curserY + (character.getyOffset() * fontSize);
        double maxX = x + (character.getSizeX() * fontSize);
        double maxY = y + (character.getSizeY() * fontSize);
        double properX = (2 * x) - 1;
        double properY = (-2 * y) + 1;
        double properMaxX = (2 * maxX) - 1;
        double properMaxY = (-2 * maxY) + 1;
        addVertices(vertices, properX, properY, properMaxX, properMaxY);
    }

    private static void addVertices(ArrayList<Float> vertices, double x, double y, double maxX, double maxY) {
        vertices.add((float) x);
        vertices.add((float) y);
        vertices.add((float) x);
        vertices.add((float) maxY);
        vertices.add((float) maxX);
        vertices.add((float) maxY);
        vertices.add((float) maxX);
        vertices.add((float) maxY);
        vertices.add((float) maxX);
        vertices.add((float) y);
        vertices.add((float) x);
        vertices.add((float) y);
    }

    private static void addTexCoords(ArrayList<Float> texCoords, double x, double y, double maxX, double maxY) {
        texCoords.add((float) x);
        texCoords.add((float) y);
        texCoords.add((float) x);
        texCoords.add((float) maxY);
        texCoords.add((float) maxX);
        texCoords.add((float) maxY);
        texCoords.add((float) maxX);
        texCoords.add((float) maxY);
        texCoords.add((float) maxX);
        texCoords.add((float) y);
        texCoords.add((float) x);
        texCoords.add((float) y);
    }

    private static float[] listToArray(ArrayList<Float> listOfFloats) {
        float[] array = new float[listOfFloats.size()];
        for (int i = 0; i < array.length; i++) {
            array[i] = listOfFloats.get(i);
        }
        return array;
    }
}
