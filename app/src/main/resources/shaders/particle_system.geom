#version 330 core

#define M_PI 3.1415926535897932384626433832795

layout (points) in;
layout (triangle_strip, max_vertices = 4) out;

in VertexData
{
    vec3 position;
    float age;
    float lifetime;
    vec2 size;
} gs_in[1];

out GeometryOutput
{
    flat float age;
    flat float lifetime;
    vec2 textureCoordinate;
} gs_out;

uniform mat4 projectionMatrix;
uniform mat4 worldMatrix;
uniform vec3 cameraLocation;
uniform mat4 cameraMatrix;

void main()
{
    gs_out.age = gs_in[0].age;
    gs_out.lifetime = gs_in[0].lifetime;

    gs_out.textureCoordinate = vec2(0.0, 0.0);
    gl_Position = projectionMatrix * cameraMatrix * vec4(v0, 0.0);
    EmitVertex();

    gs_out.textureCoordinate = vec2(1.0, 0.0);
    gl_Position = projectionMatrix * cameraMatrix * vec4(v1, 0.0);
    EmitVertex();

    gs_out.textureCoordinate = vec2(0.0, 1.0);
    gl_Position = projectionMatrix * cameraMatrix * vec4(v2, 0.0);
    EmitVertex();

    gs_out.textureCoordinate = vec2(1.0, 1.0);
    gl_Position = projectionMatrix * cameraMatrix * vec4(v3, 0.0);
    EmitVertex();

    EndPrimitive();
}