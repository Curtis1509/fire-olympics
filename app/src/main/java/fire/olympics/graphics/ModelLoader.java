package fire.olympics.graphics;

import fire.olympics.display.GameItem;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ModelLoader implements AutoCloseable {
    private final Map<String, Texture> loadedTextures = new HashMap<>();

    public ModelLoader() { }

    @Override
    public void close() {
        loadedTextures.forEach((s, t) -> t.close());
    }

    public void loadTexture(Path path) {
        assert !loadedTextures.containsKey(path.toString());
        Texture t = new Texture(path);
        String name = path.toAbsolutePath().toString();
        loadedTextures.put(name, t);
    }

    public ArrayList<GameItem> loadModel(Path path) throws Exception {
        AIScene scene = Assimp.aiImportFile(path.toAbsolutePath().toString(), 0);
        if (scene == null) {
            error(String.format("File %s could not be loaded.", path.getFileName()));
        }

        PointerBuffer importMeshes = scene.mMeshes();
        if (importMeshes == null) {
            error(String.format("PointerBuffer was null for %s.", path.getFileName()));
        }

        int numMeshes = scene.mNumMeshes();
        ArrayList<GameItem> objects = new ArrayList<>(numMeshes);
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

            int len = mesh.mNumVertices() * 3;

            for(int x = 0; x < mat.mNumProperties(); x++) {
                AIMaterialProperty prop = AIMaterialProperty.create(mat.mProperties().get(x));
                System.out.printf("Property %s has index of %d%n", prop.mKey().dataString(), x);
            }

            if(texPath.dataString().isEmpty()) {
                float[] diffuse = new float[len];

                AIMaterialProperty diffData = AIMaterialProperty.create(mat.mProperties().get(3));
                FloatBuffer diffBuffer = diffData.mData().asFloatBuffer();
                System.out.printf("Material had diffuse data with length of %d%n", diffBuffer.capacity());
                float[] col = {diffBuffer.get(),diffBuffer.get(),diffBuffer.get()};

                for(int x = 0; x < len; x++) {
                    diffuse[x] = col[x%3];
                }

                newMesh.attachMaterial(diffuse);
            } else {
                Path p = Path.of(texPath.dataString());
                Path key = path.getParent().resolve(p).normalize().toAbsolutePath();
                Texture t = loadedTextures.get(key.toString());
                if (t == null || !t.imageLoaded()) {
                    System.out.println("warning: could not find texture " + key);
                    System.out.println("note: is it loaded?");
                }

                int lenUv = mesh.mNumVertices();
                float[] newUv = new float[lenUv*2];
                AIVector3D.Buffer b = mesh.mTextureCoords(0);

                for (int x = 0; x < lenUv; x++) {
                    AIVector3D vec = b.get();
                    newUv[x*2] = vec.x();
                    newUv[(x*2)+1] = vec.y();
                }

                newMesh.attachMaterial(t, newUv);
            }

            float[] ambient = new float[len];
            float[] specular = new float[len];
            float[] shininess = new float[mesh.mNumVertices()];

            AIMaterialProperty ambData = AIMaterialProperty.create(mat.mProperties().get(2));
            AIMaterialProperty specData = AIMaterialProperty.create(mat.mProperties().get(4));
            AIMaterialProperty shinyData = AIMaterialProperty.create(mat.mProperties().get(6));

            FloatBuffer ambBuffer = ambData.mData().asFloatBuffer();
            FloatBuffer specBuffer = specData.mData().asFloatBuffer();
            FloatBuffer shinyBuffer = shinyData.mData().asFloatBuffer();

            System.out.printf("Material had ambient data with length of %d%n", ambBuffer.capacity());
            System.out.printf("Material had specular data with length of %d%n", specBuffer.capacity());
            System.out.printf("Material had shininess data with length of %d%n", shinyBuffer.capacity());

            float[] ambColour = { ambBuffer.get(), ambBuffer.get(), ambBuffer.get() };
            float[] specColour = { specBuffer.get(), specBuffer.get(), specBuffer.get() };
            float shinyAmount = shinyBuffer.get();

            for (int x = 0; x < len; x++) {
                ambient[x] = ambColour[x%3];
                specular[x] = specColour[x%3];
            }

            for (int x = 0; x < mesh.mNumVertices(); x++) {
                shininess[x] = shinyAmount;
            }

            newMesh.attachLightingData(ambient, specular, shininess);

            objects.add(new GameItem(newMesh));
        }

        Assimp.aiReleaseImport(scene);
        return objects;
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
            if(face.mNumIndices() != 3) continue; // face is not a triangle, skip it
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

    private static void error(String error) throws Exception {
        String msg = "ModelLoader: " + error;
        throw new Exception(msg);
    }
}
