#version 330 core

in GeometryOutput
{
    flat vec4 color;
    vec2 textureCoordinate;
} fs_in;

out vec4 fragColor;

uniform sampler2D texture_sampler;

void main()
{
    vec4 tex = texture(texture_sampler, fs_in.textureCoordinate);
    fragColor = tex * fs_in.color;
}