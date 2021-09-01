package fire.olympics.graphics;

import fire.olympics.fontMeshCreator.FontType;
import fire.olympics.fontMeshCreator.GUIText;
import fire.olympics.fontMeshCreator.TextMeshData;

import static org.lwjgl.opengl.GL33C.*;

import org.joml.Vector2f;
import org.joml.Vector3f;

public class MeshText {
    private VertexArrayObject vao;
    private GUIText text;
    private TextMeshData data;

    public MeshText(GUIText text) {
        this.text = text;
        vao = new VertexArrayObject();
        rebindTextMesh();
    }

    public Texture getFontTexture() {
        return text.getFont().texture;
    }

    private void rebindTextMesh() {
        vao.use();
        data = text.getFont().loader.createTextMesh(text);
        float[] positions = data.getVertexPositions();
        vao.bindFloats(positions, 0, GL_STATIC_DRAW, 2, GL_FLOAT);
        float[] textureCoordinates = data.getTextureCoords();
        vao.bindFloats(textureCoordinates, 1, GL_STATIC_DRAW, 2, GL_FLOAT);
        vao.done();
    }

    public void render() {
        vao.use();
        glDrawArrays(GL_TRIANGLES, 0, data.getVertexCount());
        vao.done();
    }

    public Vector3f getColor() {
        return text.getColour();
    }

    public Vector2f getPosition() {
        return text.getPosition();
    }
}
