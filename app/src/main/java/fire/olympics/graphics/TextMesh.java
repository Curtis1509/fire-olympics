package fire.olympics.graphics;

import fire.olympics.fontMeshCreator.GUIText;

import static org.lwjgl.opengl.GL33C.*;

import org.joml.Vector2f;
import org.joml.Vector3f;

public class TextMesh {
    private VertexArrayObject vao;
    private GUIText text;
    private int vertexCount = 0;
    private float aspectRatio = 1.0f;
    private String renderedText = null;

    public TextMesh(GUIText text) {
        this.text = text;
        vao = new VertexArrayObject();
        TextMeshData data = text.font.createTextMesh(text, aspectRatio);
        vao.use();
        vao.bindFloats(data.getVertexPositions(), 0, GL_STATIC_DRAW, 2, GL_FLOAT);
        vao.bindFloats(data.getTextureCoords(), 1, GL_STATIC_DRAW, 2, GL_FLOAT);
        vao.done();
        vertexCount = data.getVertexCount();
    }

    public Texture getFontTexture() {
        return text.font.texture;
    }

    public void recalculate(float aspectRatio) {
        if (aspectRatio != this.aspectRatio) {
            this.aspectRatio = aspectRatio;
            updateData();
        }
    }

    public void updateText(String t){
        text.value = t;
    }

    private void updateData() {
        TextMeshData data = text.font.createTextMesh(text, aspectRatio);
        vao.discardAndBindBuffer(0, data.getVertexPositions());
        vao.discardAndBindBuffer(1, data.getTextureCoords());
        vertexCount = data.getVertexCount();
    }

    public void render() {
        if (renderedText != text.value) {
            renderedText = text.value;
            updateData();
        }
        vao.use();
        glDrawArrays(GL_TRIANGLES, 0, vertexCount);
        vao.done();
    }

    public Vector3f getColor() {
        return text.color;
    }

    public Vector2f getPosition() {
        return text.position;
    }
}
