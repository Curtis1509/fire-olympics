#version 330 core

layout(location=0) in vec3 pos;

uniform mat4 lightSpace;
uniform mat4 worldMat;

void main() {
    gl_Position = lightSpace * worldMat * vec4(pos, 1);
}