package fire.olympics.fontRendering;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fire.olympics.fontMeshCreator.FontType;
import fire.olympics.fontMeshCreator.GUIText;
import fire.olympics.fontMeshCreator.TextMeshData;
import fire.olympics.graphics.Texture;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;

public class TextMaster {

    private final List<Integer> vbos = new ArrayList<>();
    private final Map<FontType, List<GUIText>> texts = new HashMap<>();
    private FontRenderer renderer;
    private final List<Integer> textures = new ArrayList<>();

    public void init() {
        renderer = new FontRenderer();
    }

    private int loadToVAO(float[] positions, float[] textureCoords) {
        int vaoID = createVAO();
        storeDataInAttributeList(0, 2, positions);
        storeDataInAttributeList(1, 2, textureCoords);
        unbindVAO();
        return vaoID;
    }

    private void unbindVAO() {
        GL30.glBindVertexArray(0);
    }

    private void storeDataInAttributeList(int attributeNumber, int coordinateSize, float[] data) {
        int vboID = GL15.glGenBuffers();
        vbos.add(vboID);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
        FloatBuffer buffer = storeDataInFloatBuffer(data);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(attributeNumber, coordinateSize, GL11.GL_FLOAT, false, 0, 0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
    }

    private FloatBuffer storeDataInFloatBuffer(float[] data) {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
        buffer.put(data);
        buffer.flip();
        return buffer;
    }

    private int createVAO() {
        int vaoID = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vaoID);
        return vaoID;
    }

    public void render() {
        renderer.render(texts);
    }

    public Texture texture;

    public int loadTexture(String fileName) throws IOException {
        texture = Texture.loadPNGTexture(fileName);
        try {
            GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
            GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, 0f);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Tried to load texture " + fileName + ".png , didn't work");
            System.exit(-1);
        }
        textures.add(texture.getId());
        System.out.println(texture.getId());
        return texture.getId();
    }

    public void loadText(GUIText text) {
        FontType font = text.getFont();
        TextMeshData data = font.loadText(text);
        int vao = loadToVAO(data.getVertexPositions(), data.getTextureCoords());
        text.setMeshInfo(vao, data.getVertexCount());
        List<GUIText> textBatch = texts.computeIfAbsent(font, k -> new ArrayList<>());
        textBatch.add(text);
    }

    public void removeText(GUIText text) {
        List<GUIText> textBatch = texts.get(text.getFont());
        textBatch.remove(text);
        if (textBatch.isEmpty()) {
            texts.remove(texts.get(text.getFont()));
        }
    }

    public void cleanUp() {
        renderer.cleanUp();
    }

}
