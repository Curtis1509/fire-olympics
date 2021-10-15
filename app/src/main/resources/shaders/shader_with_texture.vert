#version 330 core

layout (location=0) in vec3 position;
layout (location=1) in vec3 normal;
layout (location=3) in vec2 inColour;
layout (location=6) in float shininess;

out vec2 outTexCoord;
out vec3 fragNormal;
out vec3 fragPos;
out vec4 posLightSpace;
out float fragShiny;

uniform mat4 projectionMatrix;
uniform mat4 worldMatrix;
uniform mat4 lightSpace;

void main()
{
    vec4 worldPosition = worldMatrix * vec4(position, 1);
    gl_Position = projectionMatrix * worldPosition;
    fragPos = worldPosition.xyz;
    posLightSpace = lightSpace * vec4(fragPos, 1);
    outTexCoord = inColour;
    fragShiny = shininess;
    fragNormal = normal;

}