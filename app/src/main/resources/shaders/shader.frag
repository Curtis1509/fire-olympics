#version 330 core

in  vec3 exColour;
in  vec3 fragNormal;
in  float fragShiny;
in  vec3 fragPos;
in  vec4 posLightSpace;
out vec4 fragColor;

uniform vec3 sun;
uniform sampler2D depthMap;

vec3 lightColour = vec3(1, 1, 1);
float ambientStrength = 0.2;
float specularStrength = 0.5;
float shadowBias = 0.0005;

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
    norm = normalize(fragNormal);
    lightDir = normalize(sun);

    vec3 ambient = ambientCalc();
    float diffuse = diffuseCalc();
    float specular = specCalc();
    float shadow = shadowCalc();

    vec3 colour = (ambient + ((1 - shadow) * (diffuse + specular))) * exColour;

    fragColor = vec4(colour, 1.0);
}