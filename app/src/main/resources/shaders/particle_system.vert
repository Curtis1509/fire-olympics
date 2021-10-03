#version 330 core

layout (location=0) in vec3 position;
layout (location=1) in float age;
layout (location=2) in float lifetime;
layout (location=3) in vec2 size;

out VertexData
{
    vec3 position;
    float age;
    float lifetime;
    vec2 size;
} vs_out;

void main()
{
    gl_Position = vec4(0);
    vs_out.position = position;
    vs_out.age = age;
    vs_out.lifetime = lifetime;
    vs_out.size = size;
}