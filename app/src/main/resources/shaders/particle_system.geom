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
    // Disable interpolation when sending color to the fragment shader.
    flat vec4 color;
    vec2 textureCoordinate;
} gs_out;

/**
* Converts from the particle system's local coordinate space into the camera's local coordinate space.
*/
uniform mat4 viewProjectionMatrix;

/**
* The location of the camera in the partice system's local coordinate space.
*/
uniform vec3 cameraLocation;

void emitAt(vec4 point, vec2 textureCoordinate) {
    gs_out.textureCoordinate = textureCoordinate;
    gs_out.color = gs_in[0].color;
    gl_Position = viewProjectionMatrix * point;
    EmitVertex();
}

void main()
{
    // Point p is the position of the particle in the particle system's local coordinate space.
    vec4 p = vec4(gs_in[0].position, 1);
    // Vector u points in the direction of up in the particle system's local coordinate space.
    vec4 u = vec4(0, 1, 0, 0);
    // Vector w points in the direction perpendicular to the camera and vector u.
    vec4 w = vec4(normalize(cross(u.xyz, cameraLocation.xyz - p.xyz)), 0);

    float width = gs_in[0].size.x;
    float height = gs_in[0].size.y;

    // Calculate the location of the billboard's corners such that the billboard is rotated
    // around the up axis to face the camera.
    vec4 v0 = p - width * w/2 - height * u/2;
    vec4 v1 = p - width * w/2 + height * u/2;
    vec4 v2 = p + width * w/2 - height * u/2;
    vec4 v3 = p + width * w/2 + height * u/2;

    // Render two triangles with coordinates v0, v1, v2, and v1, v2, v3.
    emitAt(v0, vec2(0.0, 0.0));
    emitAt(v1, vec2(0.0, 1.0));
    emitAt(v2, vec2(1.0, 0.0));
    emitAt(v3, vec2(1.0, 1.0));

    EndPrimitive();
}