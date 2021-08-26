#version 330 core

layout (location=0) in vec3 position;
layout (location=1) in vec3 normal;
layout (location=3) in vec2 inColour;
layout (location=2) in vec3 ambient;
layout (location=4) in vec3 specular;
layout (location=6) in float shininess;

out vec2 outTexCoord;
out vec3 ambientColour;
out vec3 specularColour;
out vec3 fragNormal;
out vec3 fragPos;
out float fragShiny;

uniform mat4 projectionMatrix;
uniform mat4 worldMatrix;

void main()
{
    gl_Position = projectionMatrix * worldMatrix * vec4(position, 1);
    fragPos = vec3(worldMatrix * vec4(position, 1));
    outTexCoord = inColour;
    ambientColour = ambient;
    specularColour = specular;
    fragShiny = shininess;
    fragNormal = mat3(worldMatrix) * normal;
}