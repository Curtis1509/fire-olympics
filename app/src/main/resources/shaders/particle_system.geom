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

    vec4 upDirectionWorldSpace = vec4(0, 1, 0, 0);
    vec4 upDirectionParticleSystem = inverse(worldMatrix) * upDirectionWorldSpace;
    vec4 cameraLocationParticleSystem = inverse(worldMatrix) * vec4(cameraLocation, 1);
    vec4 particlePositionParticleSystem = vec4(gs_in[0].position, 1);
    vec4 normalToBillboardPlaneFacingCameraParticleSystem  = normalize(cameraLocationParticleSystem - particlePositionParticleSystem);

    vec4 billboardUpDirectionParticleSystem = upDirectionParticleSystem - dot(normalToBillboardPlaneFacingCameraParticleSystem, upDirectionParticleSystem) * normalToBillboardPlaneFacingCameraParticleSystem;
    vec3 sideDirectionParticleSystem = cross(upDirectionParticleSystem.xyz, normalToBillboardPlaneFacingCameraParticleSystem.xyz);

    mat4 particleSystemToParticleMatrix = mat4(
        vec4(normalize(sideDirectionParticleSystem), gs_in[0].position.x),
        vec4(normalize(billboardUpDirectionParticleSystem).xyz, gs_in[0].position.y),
        vec4(normalize(normalToBillboardPlaneFacingCameraParticleSystem).xyz, gs_in[0].position.z),
        vec4(0, 0, 0, 1)
    );

    mat4 particleToParticleSystem = inverse(particleSystemToParticleMatrix);

    float w = gs_in[0].size.x;
    float h = gs_in[0].size.y;

    vec4 v0 = vec4(-w/2, -h/2, -1, 1);
    vec4 v1 = vec4(-w/2, h/2, -1, 1);
    vec4 v2 = vec4(w/2, -h/2, -1, 1);
    vec4 v3 = vec4(w/2, h/2, -1, 1);

    gs_out.age = gs_in[0].age;
    gs_out.lifetime = gs_in[0].lifetime;
    gs_out.textureCoordinate = vec2(0.0, 0.0);
    gl_Position = projectionMatrix * cameraMatrix * particleToParticleSystem * v0;
    EmitVertex();

    gs_out.age = gs_in[0].age;
    gs_out.lifetime = gs_in[0].lifetime;
    gs_out.textureCoordinate = vec2(0.0, 1.0);
    gl_Position = projectionMatrix * cameraMatrix * particleToParticleSystem * v1;
    EmitVertex();

    gs_out.age = gs_in[0].age;
    gs_out.lifetime = gs_in[0].lifetime;
    gs_out.textureCoordinate = vec2(1.0, 0.0);
    gl_Position = projectionMatrix * cameraMatrix * particleToParticleSystem * v2;
    EmitVertex();

    gs_out.age = gs_in[0].age;
    gs_out.lifetime = gs_in[0].lifetime;
    gs_out.textureCoordinate = vec2(1.0, 1.0);
    gl_Position = projectionMatrix * cameraMatrix * particleToParticleSystem * v3;
    EmitVertex();

    EndPrimitive();
}