#version 330 core

layout (points) in;
layout (triangle_strip, max_vertices = 4) out;

in VertexData
{
    vec3 position;
    vec4 color;
    vec2 size;
} gs_in[1];

out GeometryOutput
{
    flat vec4 color;
    vec2 textureCoordinate;
} gs_out;

uniform mat4 projectionMatrix;
uniform mat4 particleSystemMatrix;
uniform vec3 cameraLocation;
uniform mat4 cameraMatrix;

void emitAt(vec4 point, vec2 textureCoordinate) {
    gs_out.textureCoordinate = textureCoordinate;
    gs_out.color = gs_in[0].color;
    gl_Position = projectionMatrix * cameraMatrix * particleSystemMatrix * point;
    EmitVertex();
}

void main()
{
    vec4 cameraLocationParticleSystem = inverse(particleSystemMatrix) * vec4(cameraLocation, 1);
    vec4 p = vec4(gs_in[0].position, 1);
    vec4 u = vec4(0, 1, 0, 0);
    vec4 w = vec4(normalize(cross(u.xyz, cameraLocationParticleSystem.xyz - p.xyz)), 0);

    float width = gs_in[0].size.x;
    float height = gs_in[0].size.y;

    vec4 v0 = p - width * w/2 - height * u/2;
    vec4 v1 = p - width * w/2 + height * u/2;
    vec4 v2 = p + width * w/2 - height * u/2;
    vec4 v3 = p + width * w/2 + height * u/2;

    emitAt(v0, vec2(0.0, 0.0));
    emitAt(v1, vec2(0.0, 1.0));
    emitAt(v2, vec2(1.0, 0.0));
    emitAt(v3, vec2(1.0, 1.0));

    EndPrimitive();
}