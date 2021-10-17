#version 330 core

in  vec2 outTexCoord;
in  vec3 ambientColour;
in  vec3 specularColour;
in  vec3 fragNormal;
in  vec3 fragPos;
in  vec4 posLightSpace;
in  float fragShiny;
out vec4 fragColor;

uniform sampler2D texture_sampler;
uniform sampler2D depthMap;
uniform vec3 sun;

vec3 lightColour = vec3(1, 1, 1);
float ambientStrength = 0.5;
float specularStrength = 0.0;

vec4 tex;
vec3 lightDir;
vec3 norm;

vec3 ambientCalc() {
    return ambientStrength * lightColour;
}

float diffuseCalc() {
    return max(dot(norm, lightDir), 0);
}

float specCalc() {
    float temp = max(dot(normalize(-fragPos), reflect(-lightDir, norm)), 0);
    return specularStrength * (temp == 0 && fragShiny == 0 ? 0 : pow(temp, fragShiny));
}

void main()
{
    tex = texture(texture_sampler, outTexCoord);
    norm = normalize(fragNormal);
    lightDir = normalize(sun);

    vec3 ambient = ambientCalc();
    float diffuse = diffuseCalc();
    float specular = specCalc();

    fragColor = vec4(ambient, 1) * tex;
}