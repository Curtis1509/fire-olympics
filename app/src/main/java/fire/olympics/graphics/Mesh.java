package fire.olympics.graphics;

import static org.lwjgl.opengl.GL33C.*;

import org.joml.Vector3f;

public class Mesh {
    private VertexArrayObject vao;
    private int vertexCount;
    private Texture texture = null;

    int POSITIONS = 0;
    int NORMALS = 1;
    int AMBIENT = 2;
    int DIFFUSE = 3;
    int SPECULAR = 4;
    int SHININESS = 6;

    /// The x, y, and z components are less than or equal to any x, y, or z values on any vertex
    /// represented by the mesh, but not anymore than they need to be.
    /// For example, if the mesh had vertices (1, 2, 3) and (3, 2, 1), this vertex would be 
    /// (1, 2, 1).
    private final Vector3f vertexLessThanEveryOtherVertex = new Vector3f();
    /// The x, y, and z components are greater than or equal to any x, y, or z values on any vertex
    /// represented by the mesh, but not anymore than they need to be.
    /// For example, if the mesh had vertices (1, 2, 3) and (3, 2, 1), this vertex would be 
    /// (3, 2, 3).
    private final Vector3f vertexGreaterThanEveryOtherVertex = new Vector3f();


    public Mesh(float[] positions, int[] indices, float[] normals) {
        assert indices.length == positions.length;
        vao = new VertexArrayObject();
        vertexCount = indices.length;
        calculateSize(positions);
        vao.bindFloats(positions, POSITIONS, GL_STATIC_DRAW, 3, GL_FLOAT);
        vao.bindElements(indices, GL_STATIC_DRAW);
        vao.bindFloats(normals, NORMALS, GL_STATIC_DRAW, 3, GL_FLOAT);
    }

    public void attachMaterial(Texture t, float[] uv) {
        texture = t;
        if (texture != null && texture.isRepeatEnabled) {
            scaleTextureCoordinates(uv);
        }
        vao.bindFloats(uv, DIFFUSE, GL_STATIC_DRAW, 2, GL_FLOAT);
    }

    private void scaleTextureCoordinates(float[] uv) {
        for (int i = 0; i < uv.length; i += 2) {
            uv[i + 0] = uv[i + 0] * texture.horizontalRepeatFactor;
            uv[i + 1] = uv[i + 1] * texture.verticalRepeatFactor;
        }
    }

    private void calculateSize(float[] positions) {
        if (positions.length < 3) return;
        vertexLessThanEveryOtherVertex.set(positions[0], positions[1], positions[2]);
        vertexGreaterThanEveryOtherVertex.set(positions[0], positions[1], positions[2]);
        Vector3f v = new Vector3f();
        for (int i = 0; i < positions.length; i += 3) {
            v.set(positions[i], positions[i+1], positions[i+2]);
            vertexLessThanEveryOtherVertex.min(v);
            vertexGreaterThanEveryOtherVertex.max(v);
        }
    }

    /// x-axis distance from the vertex with the least x component to the vertex
    /// with the greatest x component.
    public float getWidth() {
        return vertexGreaterThanEveryOtherVertex.x - vertexLessThanEveryOtherVertex.x;
    }

    /// y-axis distance from the vertex with the least y component to the vertex
    /// with the greatest y component.
    public float getHeight() {
        return vertexGreaterThanEveryOtherVertex.y - vertexLessThanEveryOtherVertex.y;
    }

    /// z-axis distance from the vertex with the least z component to the vertex
    /// with the greatest z component.
    public float getLength() {
        return vertexGreaterThanEveryOtherVertex.z - vertexLessThanEveryOtherVertex.z;
    }

    public void attachMaterial(float[] vertColours) {
        vao.bindFloats(vertColours, DIFFUSE, GL_STATIC_DRAW, 3, GL_FLOAT);
    }

    public void attachLightingData(float[] ambient, float[] specular, float[] shininess) {
        vao.bindFloats(ambient, AMBIENT, GL_STATIC_DRAW, 3, GL_FLOAT);
        vao.bindFloats(specular, SPECULAR, GL_STATIC_DRAW, 3, GL_FLOAT);
        vao.bindFloats(shininess, SHININESS, GL_STATIC_DRAW, 1, GL_FLOAT);
    }

    public boolean hasTexture() {
        return texture != null;
    }

    public void render() {
        vao.use();
        if (hasTexture()) {
            texture.bind();
        }
        glDrawElements(GL_TRIANGLES, vertexCount, GL_UNSIGNED_INT, 0);
        if (hasTexture())
            texture.unbind();
        vao.done();
    }

    public void close() throws Exception {
        vao.close();
    }
}
