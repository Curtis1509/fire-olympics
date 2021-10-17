#version 330 core

layout (location=0) in vec3 position;
layout (location=1) in vec4 color;
layout (location=2) in vec2 size;

out VertexData
{
    vec3 position;
    vec4 color;
    vec2 size;
} vs_out;

void main()
{
    gl_Position = vec4(0);
    vs_out.position = position;
    vs_out.color = color;
    vs_out.size = size;
}