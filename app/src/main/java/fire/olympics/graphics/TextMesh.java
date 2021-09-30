package fire.olympics.graphics;

import fire.olympics.fontMeshCreator.GUIText;

import static org.lwjgl.opengl.GL33C.*;

import org.joml.Vector2f;
import org.joml.Vector3f;

public class TextMesh {
    private VertexArrayObject vao;
    private GUIText text;
    private TextMeshData data;
    private float aspectRatio = 1.0f;
    private String renderedText = null;

    public TextMesh(GUIText text) {
        this.text = text;
        vao = new VertexArrayObject();
        text.setMesh(this);
    }

    public Texture getFontTexture() {
        return text.font.texture;
    }

    private void setTextMeshData(TextMeshData data) {
        vao.use();
        this.data = data;
        vao.bindFloats(data.getVertexPositions(), 0, GL_STATIC_DRAW, 2, GL_FLOAT);
        vao.bindFloats(data.getTextureCoords(), 1, GL_STATIC_DRAW, 2, GL_FLOAT);
        vao.done();
    }

    public void recalculate(float aspectRatio) {
        if (aspectRatio != this.aspectRatio) {
            this.aspectRatio = aspectRatio;
            setTextMeshData(text.font.createTextMesh(text, aspectRatio));
        }
    }

    public void updateData() {
        setTextMeshData(text.font.createTextMesh(text, aspectRatio));
    }

    public void render() {
        if (renderedText != text.text()) {
            renderedText = text.text();
            updateData();
        }
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
