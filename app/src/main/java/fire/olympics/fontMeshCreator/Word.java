package fire.olympics.fontMeshCreator;

import java.util.ArrayList;
import java.util.List;

public class Word {

    private List<FontType.Character> characters = new ArrayList<FontType.Character>();
    private double width = 0;
    private double fontSize;

    protected Word(double fontSize) {
        this.fontSize = fontSize;
    }

    protected void addCharacter(FontType.Character character) {
        characters.add(character);
        width += character.xAdvance * fontSize;
    }

    protected List<FontType.Character> getCharacters() {
        return characters;
    }

    protected double getWordWidth() {
        return width;
    }

}
