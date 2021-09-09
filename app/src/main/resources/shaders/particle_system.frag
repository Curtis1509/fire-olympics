#version 330 core

in float fragAge;
in float fragLifetime;

out vec4 fragColor;

uniform vec4 hotColor;
uniform vec4 coldColor;

void main()
{
    vec4 result = min(1, fragAge) * coldColor + (1 - fragAge) * hotColor;
    if (fragLifetime < fragAge) {
        result.w = 0;
    }
    fragColor = result;
}