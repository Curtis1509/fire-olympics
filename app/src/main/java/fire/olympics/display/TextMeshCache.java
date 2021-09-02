package fire.olympics.display;

import java.util.HashMap;

import fire.olympics.graphics.TextMesh;

public class TextMeshCache {
    private HashMap<String, TextMesh> cache = new HashMap<>();
    private float aspectRatio;

    public void register(TextMesh mesh) {
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
        for (TextMesh mesh : cache.values()) {
            mesh.setTextMeshData(mesh.text.font.createTextMesh(mesh.text, aspectRatio));
        }
    }
}
