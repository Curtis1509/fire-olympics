package fire.olympics.graphics;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.file.Path;

import static org.lwjgl.opengl.GL33C.*;

import de.matthiasmann.twl.utils.PNGDecoder;
import fire.olympics.MemoryUsage;

import org.lwjgl.BufferUtils;
import org.lwjgl.stb.*;
import org.lwjgl.system.MemoryUtil;

public class Texture {
    private int id;
    private float width;
    private float height;
    public String name;
    public boolean isRepeatEnabled = false;
    public float horizontalRepeatFactor = 0;
    public float verticalRepeatFactor = 0;

    public Texture(int id){
        this.id=id;
    }

    public float getWidth(){
        return width;
    }
    public float getHeight(){
        return height;
    }
    public Texture(Path path) {
        name = path.toString();
        IntBuffer width = MemoryUtil.memAllocInt(1);
        IntBuffer height = MemoryUtil.memAllocInt(1);
        IntBuffer comp = MemoryUtil.memAllocInt(1);

        STBImage.stbi_info(path.toAbsolutePath().toString(), width, height, comp);

        int stbimagePixelLayout = STBImage.STBI_rgb_alpha;
        int glteximagePixelLayout = GL_RGBA;
        ByteBuffer imageData = STBImage.stbi_load(path.toAbsolutePath().toString(), width, height, comp, stbimagePixelLayout);

        if (imageData == null) {
            id = 0;
            String reason = String.format("Texture: %s%n", STBImage.stbi_failure_reason());
            throw new RuntimeException(reason);
        }

        MemoryUsage.record(Texture.class, "Texture(_)", path.toString(), imageData.capacity());

        id = glGenTextures();
        bind();

        this.width = width.get(0) / 64.0f / 4;
        this.height = height.get(0) / 64.0f / 4;

        glTexImage2D(GL_TEXTURE_2D, 0, glteximagePixelLayout, width.get(0), height.get(0), 0, glteximagePixelLayout, GL_UNSIGNED_BYTE, imageData);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glGenerateMipmap(GL_TEXTURE_2D);
        unbind();
        STBImage.stbi_image_free(imageData);
    }

    public void repeat(float horizontalRepeatFactor, float verticalRepeatFactor) {
        bind();
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        unbind();

        this.horizontalRepeatFactor = horizontalRepeatFactor;
        this.verticalRepeatFactor = verticalRepeatFactor;
        this.isRepeatEnabled = true;
    }

    public static Texture loadPngTexture(Path path) throws IOException {

        //load png file
        PNGDecoder decoder = new PNGDecoder(new java.io.FileInputStream(path.toFile()));

        //create a byte buffer big enough to store RGBA values
        ByteBuffer buffer = BufferUtils.createByteBuffer(4 * decoder.getWidth() * decoder.getHeight());

        //decode
        decoder.decode(buffer, decoder.getWidth() * 4, PNGDecoder.Format.RGBA);

        //flip the buffer so its ready to read
        buffer.flip();

        MemoryUsage.record(Texture.class, "loadPngTexture", path.toString(), buffer.capacity());

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

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_LOD_BIAS, 0f);

        glBindTexture(GL_TEXTURE_2D, 0);

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
}
