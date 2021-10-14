#version 330 core

in  vec2 outTexCoord;
in  vec3 ambientColour;
in  vec3 specularColour;
in  vec3 fragNormal;
in  vec3 fragPos;
in  float fragShiny;
in vec4 lightWorldMatrixPos;
out vec4 fragColor;

uniform sampler2D texture_sampler;
uniform sampler2D shadowMap;
uniform vec3 sun;

float calcShadow() {
    float shadowFactor = 1.0;
    vec3 projCoords = lightWorldMatrixPos.xyz;

    projCoords = projCoords * 0.5 + 0.5;
    float bias = 0.05;

    if (projCoords.z-bias < texture(shadowMap, projCoords.xy).r) {
        shadowFactor = 0;
    }

    return 1 - shadowFactor;
}

void main()
{
    vec4 tex = texture(texture_sampler, outTexCoord);
    vec3 lightColour = vec3(1, 1, 1);
    float ambientStrength = 0.4;
    float specularStrength = 0.5;

    /* Ambient */
    vec3 ambientLight = ambientStrength * lightColour;
    vec3 ambient = (ambientColour * ambientLight);

    /* Diffuse */
    vec3 lightDir = normalize(sun.xyz - fragPos);
    float diffStrength = max(dot(fragNormal, lightDir), 0);
    vec3 diffuse = diffStrength * tex.rgb;

    /* Specular */
    vec3 reflectDir = reflect(-lightDir, fragNormal);
    float temp = max(dot(normalize(-fragPos), reflectDir), 0);
    float spec = temp == 0 && fragShiny == 0 ? 0 : pow(temp, fragShiny);
    vec3 specular = specularStrength * spec * specularColour;

    float shadow = calcShadow();

    vec3 result =  (ambient + ((diffuse + specular) * shadow)) * tex.xyz;
    fragColor = vec4(result, tex.a);
}