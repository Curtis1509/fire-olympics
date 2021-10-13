#version 330 core

in  vec3 exColour;
in  vec3 ambientColour;
in  vec3 specularColour;
in  vec3 fragNormal;
in  float fragShiny;
in  vec3 fragPos;
out vec4 fragColor;

uniform vec3 sun;

void main()
{
    vec3 lightColour = vec3(1, 1, 1);
    float ambientStrength = 0.4;
    float specularStrength = 0.5;

    /* Ambient */
    vec3 ambientLight = ambientStrength * lightColour;
    vec3 ambient = ambientColour * ambientLight;

    /* Diffuse */
    vec3 lightDir = normalize(sun.xyz - fragPos);
    float diffStrength = max(dot(fragNormal, lightDir), 0);
    vec3 diffuse = diffStrength * exColour;

    /* Specular */
    vec3 reflectDir = reflect(-lightDir, fragNormal);
    float temp = max(dot(normalize(-fragPos), reflectDir), 0);
    float spec = temp == 0 && fragShiny == 0 ? 0 : pow(temp, fragShiny);
    vec3 specular = specularStrength * spec * specularColour;


    vec3 result = (ambient + diffuse + specular) * exColour;
    fragColor = vec4(result, 1.0);
}