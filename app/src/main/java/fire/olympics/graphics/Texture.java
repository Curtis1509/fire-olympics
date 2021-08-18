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

    @SuppressWarnings("StatementWithEmptyBody")
    public Texture(Path path) {
        ByteBuffer imb;

        try {
            SeekableByteChannel fc = Files.newByteChannel(path);
            imb = MemoryUtil.memAlloc((int)fc.size() + 1);
            while(fc.read(imb) != -1) ;
        } catch(Exception e) {
            id = 0;
            System.out.printf("Texture: %s%n", e);
            return;
        }

        imb.flip();

        IntBuffer w = MemoryUtil.memAllocInt(1);
        IntBuffer h = MemoryUtil.memAllocInt(1);
        IntBuffer comp = MemoryUtil.memAllocInt(1);

        STBImage.stbi_info_from_memory(imb, w, h, comp);
        ByteBuffer im = STBImage.stbi_load_from_memory(imb, w, h, comp, 0);

        if(im == null) {
            id = 0;
            System.out.printf("Texture: %s%n", STBImage.stbi_failure_reason());
            return;
        }

        id = glGenTextures();
        bind();
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, w.get(0), h.get(0), 0, GL_RGB, GL_UNSIGNED_BYTE, im);
        glGenerateMipmap(GL_TEXTURE_2D);
        unbind();
        STBImage.stbi_image_free(im);
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
