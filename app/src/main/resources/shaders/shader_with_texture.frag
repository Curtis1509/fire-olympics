#version 330 core

in  vec2 outTexCoord;
in  vec3 ambientColour;
in  vec3 specularColour;
in  vec3 fragNormal;
in  vec3 fragPos;
in  float fragShiny;
out vec4 fragColor;

uniform sampler2D texture_sampler;
uniform vec3 sun;

void main()
{
    vec4 tex = texture(texture_sampler, outTexCoord);
    vec3 lightColour = vec3(1, 1, 1);
    float ambientStrength = 0.1;
    float specularStrength = 0.1;

    /* Ambient */
    vec3 ambientLight = ambientStrength * lightColour;
    vec3 ambient = ambientColour * ambientLight;

    /* Diffuse */
    vec3 lightDir = normalize(sun.xyz - fragPos);
    float diffStrength = max(dot(fragNormal, lightDir), 0);
    vec3 diffuse = diffStrength * tex.rgb;

    /* Specular */
    vec3 reflectDir = reflect(-lightDir, fragNormal);
    float spec = pow(max(dot(normalize(-fragPos), reflectDir), 0), fragShiny);
    vec3 specular = specularStrength * spec * specularColour;

//    fragColor = vec4(ambient + diffuse + specular, tex.a);

    fragColor = vec4(ambient + diffuse, tex.a);
}