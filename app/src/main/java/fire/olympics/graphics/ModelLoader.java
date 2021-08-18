package fire.olympics.graphics;

import fire.olympics.display.GameItem;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;

public class ModelLoader {
    private final static Map<String, Texture> loadedTextures = new HashMap<>();

    public static void unloadTextures() {
        loadedTextures.forEach((s, t) -> t.close());
    }

    public static GameItem loadModel(Path path) {
        AIScene scene = Assimp.aiImportFile(path.toAbsolutePath().toString(), 0);

        if(scene == null) {
            errorLog(String.format("File %s could not be loaded.", path.getFileName()));
            return null;
        }

        int numMeshes = scene.mNumMeshes();

        Mesh[] meshes = new Mesh[numMeshes];
        PointerBuffer importMeshes = scene.mMeshes();

        if(importMeshes == null) {
            errorLog(String.format("PointerBuffer was null for %s.", path.getFileName()));
            return null;
        }

        System.out.printf("%d mesh(es) in %s%n", numMeshes, path.getFileName());

        for(int i = 0; i < numMeshes; i++) {
            AIMesh mesh = AIMesh.create(importMeshes.get(i));

            Mesh newMesh = new Mesh(
                convertPositions(mesh),
                convertIndexes(mesh),
                convertNormals(mesh)
            );

            AIMaterial mat = AIMaterial.create(scene.mMaterials().get(mesh.mMaterialIndex()));
            AIString texPath = AIString.create();
            Assimp.aiGetMaterialTexture(mat, Assimp.aiTextureType_DIFFUSE, 0, texPath, (int[]) null, null, null, null, null, null);

            Path vertShader;
            Path fragShader;

            if(texPath.dataString().isEmpty()) {
                vertShader = path.resolve(Path.of("..", "..", "shaders", "shader.vert")).normalize();
                fragShader = path.resolve(Path.of("..", "..", "shaders", "shader.frag")).normalize();
                int len = mesh.mNumVertices() * 3;
                float[] colours = new float[len];

                AIMaterialProperty prop = AIMaterialProperty.create(mat.mProperties().get(3));
                FloatBuffer buffer = prop.mData().asFloatBuffer();
                float[] col = {buffer.get(),buffer.get(),buffer.get()};

                for(int x = 0; x < len; x++) {
                    colours[x] = col[x%3];
                }

                newMesh.attachMaterial(colours);
            } else {
                Texture t;

                if(loadedTextures.containsKey(texPath.dataString())) {
                    t = loadedTextures.get(texPath.dataString());
                } else {
                    t = new Texture(path.resolve(Path.of("..", texPath.dataString())).normalize());
                    loadedTextures.put(texPath.dataString(), t);
                }

                vertShader = path.resolve(Path.of("..", "..", "shaders", "shader_with_texture.vert")).normalize();
                fragShader = path.resolve(Path.of("..", "..", "shaders", "shader_with_texture.frag")).normalize();

                if(t.imageLoaded()) {
                    int len = mesh.mNumVertices();
                    float[] newUv = new float[len*2];
                    AIVector3D.Buffer b = mesh.mTextureCoords(0);

                    for(int x = 0; x < len; x++) {
                        AIVector3D vec = b.get();
                        newUv[x*2] = vec.x();
                        newUv[(x*2)+1] = vec.y();
                    }

                    newMesh.attachMaterial(t, newUv);
                }
            }

            //System.out.printf("%s%n%s%n", vertShader.toAbsolutePath(), fragShader.toAbsolutePath());
            ShaderProgram p = new ShaderProgram(vertShader, fragShader);
            try {
                p.readCompileAndLink();

                if(newMesh.hasTexture()) p.createUniform("texture_sampler");

                p.validate();
            } catch(Exception e) {
                System.out.printf("ModelLoader: Error compiling shaders for mesh %d in file %s: %s%n", i, path.getFileName(), e);
            }

            newMesh.setProgram(p);

            texPath.close();
            meshes[i] = newMesh;
        }

        Assimp.aiReleaseImport(scene);
        return new GameItem(meshes);
    }

    private static float[] convertPositions(AIMesh mesh) {
        AIVector3D.Buffer b = mesh.mVertices();
        float[] pos = new float[mesh.mNumVertices() * 3];

        int i = 0;
        while(b.hasRemaining()) {
            AIVector3D vec = b.get();

            pos[i*3] = vec.x();
            pos[(i*3)+1] = vec.y();
            pos[(i*3)+2] = vec.z();

            i++;
        }

        return pos;
    }

    private static int[] convertIndexes(AIMesh mesh) {
        AIFace.Buffer b = mesh.mFaces();
        int[] ind = new int[mesh.mNumFaces() * 3]; //triangle faces

        int i = 0;
        while(b.hasRemaining()) {
            AIFace face = b.get();
            IntBuffer intB = face.mIndices();

            for(int x = 0; x < 3; x++)
                ind[(i * 3) + x] = intB.get(x);

            i++;
        }

        return ind;
    }

    private static float[] convertNormals(AIMesh mesh) {
        AIVector3D.Buffer b = mesh.mNormals();
        float[] norm = new float[mesh.mNumVertices() * 3];

        int i = 0;
        while(b.hasRemaining()) {
            AIVector3D vec = b.get();

            norm[i*3] = vec.x();
            norm[(i*3)+1] = vec.y();
            norm[(i*3)+2] = vec.z();

            i++;
        }

        return norm;
    }

    private static void errorLog(String error) {
        System.out.println("ModelLoader: " + error);
    }
}
