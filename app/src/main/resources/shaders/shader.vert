#version 330 core

layout (location=0) in vec3 position;
layout (location=1) in vec3 normal;
layout (location=3) in vec3 inColour;
layout (location=6) in float shininess;

out vec3 exColour;
out vec3 fragNormal;
out float fragShiny;
out vec3 fragPos;
out vec4 posLightSpace;

uniform mat4 projectionMatrix;
uniform mat4 worldMatrix;
uniform mat4 lightSpace;

void main()
{
    vec4 worldPos = worldMatrix * vec4(position, 1);
    gl_Position = projectionMatrix * worldPos;
    fragPos = worldPos.xyz;
    posLightSpace = lightSpace * vec4(fragPos, 1);
    fragShiny = shininess;
    exColour = inColour;
    fragNormal = normal;
}