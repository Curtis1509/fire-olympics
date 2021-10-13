#version 330 core

in GeometryOutput
{
    flat float age;
    flat float lifetime;
    vec2 textureCoordinate;
} fs_in;

out vec4 fragColor;

uniform vec4 hotColor;
uniform vec4 coldColor;

uniform sampler2D texture_sampler;

void main()
{
    vec4 tex = texture(texture_sampler, fs_in.textureCoordinate);
    if (fs_in.lifetime < 0) {
        fragColor = tex;
    } else {
        vec4 result = min(1, fs_in.age) * coldColor + (1 - fs_in.age) * hotColor;
        if (fs_in.lifetime < fs_in.age) {
            result.w = 0;
        }
        fragColor = tex * result;
    }
}