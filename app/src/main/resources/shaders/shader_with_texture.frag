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
float ambientStrength = 0.2;
float specularStrength = 2;
float shadowBias = 0.0005;

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
    return specularStrength * (fragShiny == 0 ? 0 : pow(temp, fragShiny));
}

float shadowCalc() {
    vec3 proj = posLightSpace.xyz / posLightSpace.w;
    proj = (proj * 0.5) + 0.5;

    if(proj.z > 1)
        return 0;

    float currentDepth = proj.z - shadowBias;

    float shadow = 0.0;
    vec2 texelSize = 1.0 / textureSize(depthMap, 0);
    for(int x = -1; x <= 1; ++x)
    {
        for(int y = -1; y <= 1; ++y)
        {
            float pcfDepth = texture(depthMap, proj.xy + vec2(x, y) * texelSize).r;
            shadow += currentDepth - shadowBias > pcfDepth ? 1.0 : 0.0;
        }
    }

    shadow /= 9.0;

    return shadow;
}

void main()
{
    tex = texture(texture_sampler, outTexCoord);
    norm = normalize(fragNormal);
    lightDir = normalize(sun);

    vec3 ambient = ambientCalc();
    float diffuse = diffuseCalc();
    float specular = specCalc();
    float shadow = shadowCalc();

    fragColor = vec4(ambient + ((1 - shadow) * (diffuse + specular)), 1) * tex;
}