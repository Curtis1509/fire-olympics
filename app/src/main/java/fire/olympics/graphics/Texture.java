package fire.olympics.graphics;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.file.Path;

import static org.lwjgl.opengl.GL33C.*;

import de.matthiasmann.twl.utils.PNGDecoder;
import org.lwjgl.BufferUtils;
import org.lwjgl.stb.*;
import org.lwjgl.system.MemoryUtil;

public class Texture {
    int id;

    public Texture(int id){
        this.id=id;
    }

    public Texture(Path path) {
        System.out.println("Loading texture at : " + path);
        IntBuffer w = MemoryUtil.memAllocInt(1);
        IntBuffer h = MemoryUtil.memAllocInt(1);
        IntBuffer comp = MemoryUtil.memAllocInt(1);

        STBImage.stbi_info(path.toAbsolutePath().toString(), w, h, comp);
        ByteBuffer imageData = STBImage.stbi_load(path.toAbsolutePath().toString(), w, h, comp, STBImage.STBI_rgb);

        if (imageData == null) {
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

    public static Texture loadPNGTexture(String fileName) throws IOException {

        //load png file
        PNGDecoder decoder = new PNGDecoder(new java.io.FileInputStream(new File(fileName)));

        //create a byte buffer big enough to store RGBA values
        ByteBuffer buffer = BufferUtils.createByteBuffer(4 * decoder.getWidth() * decoder.getHeight());

        //decode
        decoder.decode(buffer, decoder.getWidth() * 4, PNGDecoder.Format.RGBA);

        //flip the buffer so its ready to read
        buffer.flip();

        //create a texture
        int id = glGenTextures();

        //bind the texture
        glBindTexture(GL_TEXTURE_2D, id);

        //tell opengl how to unpack bytes
        glPixelStorei(GL_UNPACK_ALIGNMENT, 1);

        //set the texture parameters, can be GL_LINEAR or GL_NEAREST
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        //upload texture
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, decoder.getWidth(), decoder.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);

        // Generate Mip Map
        glGenerateMipmap(GL_TEXTURE_2D);

        return new Texture(id);
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

    public int getId(){
        return id;
    }

}
