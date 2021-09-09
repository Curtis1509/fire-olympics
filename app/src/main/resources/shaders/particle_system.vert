#version 330 core

layout (location=0) in vec3 position;
layout (location=1) in float age;
layout (location=2) in float lifetime;

out float fragAge;
out float fragLifetime;
uniform mat4 projectionMatrix;
uniform mat4 worldMatrix;

void main()
{
    gl_Position = projectionMatrix * worldMatrix * vec4(position, 1);
    fragAge = age;
    fragLifetime = lifetime;
}