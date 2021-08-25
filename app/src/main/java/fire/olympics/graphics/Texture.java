package fire.olympics.graphics;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.lwjgl.opengl.GL33C.*;
import org.lwjgl.stb.*;
import org.lwjgl.system.MemoryUtil;

public class Texture {
    int id;

    public Texture(Path path) {
        IntBuffer w = MemoryUtil.memAllocInt(1);
        IntBuffer h = MemoryUtil.memAllocInt(1);
        IntBuffer comp = MemoryUtil.memAllocInt(1);

        STBImage.stbi_info(path.toAbsolutePath().toString(), w, h, comp);
        ByteBuffer imageData = STBImage.stbi_load(path.toAbsolutePath().toString(), w, h, comp, STBImage.STBI_rgb);

        if(imageData == null) {
            id = 0;
            System.out.printf("Texture: %s%n", STBImage.stbi_failure_reason());
            return;
        }

        id = glGenTextures();
        bind();
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, w.get(0), h.get(0), 0, GL_RGB, GL_UNSIGNED_BYTE, imageData);
        glGenerateMipmap(GL_TEXTURE_2D);
        unbind();
        STBImage.stbi_image_free(imageData);
    }

    public boolean imageLoaded() {
        return id != 0;
    }

    public void bind() {
        glBindTexture(GL_TEXTURE_2D, id);
    }

    public void unbind() {
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public void close() {
        glDeleteTextures(id);
    }
}
