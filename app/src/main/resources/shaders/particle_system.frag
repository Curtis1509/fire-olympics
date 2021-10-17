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
    // Particles can be transparent, however depth testing will cull particles that have been
    // covered by a translucent particle. In this case, we discard the texel to prevent it from
    // covering other parts of particles that are not translucent. See here for more details:
    // https://www.khronos.org/opengl/wiki/Transparency_Sorting
    if (tex.a < 0.001) {
        discard;
    }
    fragColor = tex * fs_in.color;
}