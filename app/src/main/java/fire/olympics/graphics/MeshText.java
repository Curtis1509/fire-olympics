package fire.olympics.graphics;

import fire.olympics.fontMeshCreator.GUIText;
import fire.olympics.fontMeshCreator.TextMeshData;

import static org.lwjgl.opengl.GL33C.*;

import org.joml.Vector2f;
import org.joml.Vector3f;

public class MeshText {
    private VertexArrayObject vao;
    public GUIText text;
    public TextMeshData data;

    public MeshText(GUIText text) {
        this.text = text;
        vao = new VertexArrayObject();
    }

    public Texture getFontTexture() {
        return text.font.texture;
    }

    public void setTextMeshData(TextMeshData data) {
        vao.use();
        this.data = data;
        vao.bindFloats(data.getVertexPositions(), 0, GL_STATIC_DRAW, 2, GL_FLOAT);
        vao.bindFloats(data.getTextureCoords(), 1, GL_STATIC_DRAW, 2, GL_FLOAT);
        vao.done();
    }

    public void render() {
        vao.use();
        glDrawArrays(GL_TRIANGLES, 0, data.getVertexCount());
        vao.done();
    }

    public Vector3f getColor() {
        return text.color;
    }

    public Vector2f getPosition() {
        return text.position;
    }
}
