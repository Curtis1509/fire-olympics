#version 330 core

layout (location=0) in vec3 position;
layout (location=1) in vec3 normal;
layout (location=3) in vec3 inColour;
layout (location=2) in vec3 ambient;
layout (location=4) in vec3 specular;
layout (location=6) in float shininess;

out vec3 exColour;
out vec3 ambientColour;
out vec3 specularColour;
out vec3 fragNormal;
out float fragShiny;
out vec3 fragPos;

uniform mat4 projectionMatrix;
uniform mat4 worldMatrix;

void main()
{
    gl_Position = projectionMatrix * worldMatrix * vec4(position, 1);
    fragPos = vec3(worldMatrix * vec4(position, 1));
    ambientColour = ambient;
    specularColour = specular;
    fragShiny = shininess;
    exColour = inColour;
    fragNormal = normalize(worldMatrix * vec4(normal, 0.0)).xyz; // apply rotation transformation to normal vector
}