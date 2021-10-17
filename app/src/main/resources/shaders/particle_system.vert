#version 330 core

layout (location=0) in vec3 position;
layout (location=1) in vec4 color;
layout (location=2) in vec2 size;
layout (location=3) in vec3 upDirection;

out VertexData
{
    vec3 position;
    vec4 color;
    vec2 size;
    vec3 upDirection;
} vs_out;

void main()
{
    gl_Position = vec4(0);
    vs_out.position = position;
    vs_out.color = color;
    vs_out.size = size;
    vs_out.upDirection = upDirection;
}