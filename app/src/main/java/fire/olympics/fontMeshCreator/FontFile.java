package fire.olympics.fontMeshCreator;

import java.nio.file.Path;
import java.util.HashMap;

public class FontFile {
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
        public int page;
        public int channel;
    }

    public final Padding padding = new Padding();
    public final int lineHeight;
    public final int imageWidth;
    public final int imageHeight;
    public final int base;
    public final HashMap<Integer, CharacterAttribute> characters = new HashMap<>();


    public FontFile(Path path) {
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
        base = reader.getValueOfVariable("base");
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
            attribute.page = reader.getValueOfVariable("page");
            attribute.channel = reader.getValueOfVariable("chnl");
            characters.put(attribute.id, attribute);
        }
    }
}
