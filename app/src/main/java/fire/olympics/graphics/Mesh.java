package fire.olympics.graphics;

import static org.lwjgl.opengl.GL33C.*;

public class Mesh {
    private VertexArrayObject vao;
    private int vertexCount;
    private Texture texture = null;
    private float width;
    private float height;

    int POSITIONS = 0;
    int NORMALS = 1;
    int AMBIENT = 2;
    int DIFFUSE = 3;
    int SPECULAR = 4;
    int SHININESS = 6;


    public Mesh(float[] positions, int[] indices, float[] normals) {
        assert indices.length == positions.length;
        vao = new VertexArrayObject();
        vertexCount = indices.length;
        setWidth(positions);
        setHeight(positions);
        vao.bindFloats(positions, POSITIONS, GL_STATIC_DRAW, 3, GL_FLOAT);
        vao.bindElements(indices, GL_STATIC_DRAW);
        vao.bindFloats(normals, NORMALS, GL_STATIC_DRAW, 3, GL_FLOAT);
    }

    public void attachMaterial(Texture t, float[] uv) {
        vao.bindFloats(uv, DIFFUSE, GL_STATIC_DRAW, 2, GL_FLOAT);
        texture = t;
    }

    public void setHeight(float[] positions){
        float yMin = 696969, yMax = 696969;
        for (int i = 1; i < positions.length; i+=3){
            if (i < positions.length){
                if (yMin == 696969)
                    yMin = positions[i];
                else {
                    if (yMin > positions[i])
                        yMin = positions[i];
                }
            }
        }
        for (int i = 1; i < positions.length; i+=3){
            if (i < positions.length){
                if (yMax == 696969)
                    yMax = positions[i];
                else {
                    if (yMax < positions[i])
                        yMax = positions[i];
                }
            }
        }
        height = yMax-yMin;
        System.out.println("height: " + height);
    }

    public void setWidth(float[] positions){
        float xMin = 696969, xMax = 696969;
        for (int i = 0; i < positions.length; i+=3){
            if (i < positions.length){
                if (xMin == 696969)
                    xMin = positions[i];
                else {
                    if (xMin > positions[i])
                        xMin = positions[i];
                }
            }
        }
        for (int i = 0; i < positions.length; i+=3){
            if (i < positions.length){
                if (xMax == 696969)
                    xMax = positions[i];
                else {
                    if (xMax < positions[i])
                        xMax = positions[i];
                }
            }
        }
        width = xMax-xMin;

        //System.out.println("Width = " + (width));
    }

    public float getWidth(){
        //return texture.getWidth();
        //System.out.println(texture.name);
        return width;
    }
    public float getHeight(){
        return height;
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
