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
    float ambientStrength = 0.1;
    float specularStrength = 0.5;

    /* Ambient */
    vec3 ambientLight = ambientStrength * lightColour;
    vec3 ambient = ambientColour * ambientLight;

    /* Diffuse */
    vec3 norm = normalize(fragNormal);
    vec3 lightDir = normalize(sun);
    float diffStrength = max(dot(norm, lightDir), 0);
    vec3 diffuse = diffStrength * exColour;

    /* Specular */
    vec3 reflectDir = reflect(-lightDir, norm);
    float spec = pow(max(dot(normalize(-fragPos), reflectDir), 0), fragShiny);
    vec3 specular = specularStrength * spec * specularColour;

    fragColor = vec4(ambient + diffuse + specular, 1.0);
}