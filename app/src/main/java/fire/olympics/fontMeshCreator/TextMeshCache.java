package fire.olympics.fontMeshCreator;

import fire.olympics.graphics.MeshText;
import java.util.HashMap;

public class TextMeshCache {
    private HashMap<String, MeshText> cache = new HashMap<>();
    private float aspectRatio;

    public void register(MeshText mesh) {
        if (mesh.data == null) {
            if (cache.containsKey(mesh.text.toString())) {
                mesh.data = cache.get(mesh.text.toString()).data;
            } else {
                // mesh.setTextMeshData(mesh.text.font.createTextMesh(mesh.text, aspectRatio));
                cache.put(mesh.text.text(), mesh);
            }
        } else {
            System.out.println("Warning: registering cached mesh twice!");
        }
    }

    public void recalculate(float ratio) {
        aspectRatio = ratio;
        for (MeshText mesh : cache.values()) {
            mesh.setTextMeshData(mesh.text.font.createTextMesh(mesh.text, aspectRatio));
        }
    }
}
