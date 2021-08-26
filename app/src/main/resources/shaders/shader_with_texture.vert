#version 330 core

layout (location=0) in vec3 position;
layout (location=3) in vec2 inColour;

out vec2 outTexCoord;

uniform mat4 projectionMatrix;
uniform mat4 worldMatrix;

void main()
{
    gl_Position = projectionMatrix * worldMatrix * vec4(position, 1.0);
    outTexCoord = inColour;
}