#version 330 core

const int lightSourceCount = 4;

in vec3 fragPos;
in vec3 ourNormal;
in vec2 ourTexCoord;
in vec3 position;
out vec4 fragColor;

uniform vec3 viewPos;

struct LightSource {
    vec3 position;
    vec3 ambient;
    vec3 diffuse;
    vec3 specular;
    float falloffConstant, falloffLinear, falloffQuadratic;
    float spotCutOff, spotOuterCutOff;
    vec3 spotDirection;
    int spotlight;
};

uniform LightSource lightSources[lightSourceCount];

struct Material {
    sampler2D diffuse;
    sampler2D specular;
    float shininess;
};

uniform Material material;

void main() {
    vec3 result = vec3(0, 0, 0);
    vec3 norm = normalize(ourNormal);
    vec3 viewDir = normalize(viewPos - fragPos);

    for (int i = 0; i < lightSourceCount; i++) {
        if (lightSources[i].spotlight == 1) {
            vec3 lightDir = normalize(lightSources[i].position - fragPos);
            float theta = dot(lightDir, normalize(-lightSources[i].spotDirection));
            float epsilon = (lightSources[i].spotCutOff - lightSources[i].spotOuterCutOff);
            vec3 result = vec3(0,0,0);

            if(theta > lightSources[i].spotOuterCutOff) {

                float diff = max(dot(norm, lightDir), 0.0);

                vec3 reflectDir = reflect(-lightDir, norm);
                float spec = pow(max(dot(viewDir, reflectDir), 0.0), material.shininess);

                // intensity
                float intensity = clamp((theta - lightSources[i].spotOuterCutOff) / epsilon, 0.0, 1.0);

                float distance = length(lightSources[i].position - fragPos);
                float falloff = 1.0 / (lightSources[i].falloffConstant + lightSources[i].falloffLinear * distance +
                lightSources[i].falloffQuadratic * (distance * distance));

                vec3 ambient  = lightSources[i].ambient * vec3(texture(material.diffuse, ourTexCoord));
                vec3 diffuse  = lightSources[i].diffuse * diff * intensity * vec3(texture(material.diffuse, ourTexCoord)) * falloff;
                vec3 specular = lightSources[i].specular * spec * intensity * vec3(texture(material.specular, ourTexCoord)) * falloff;
                result += ambient + diffuse + specular;
            }
        } else {

            vec3 ambient = lightSources[i].ambient  * vec3(texture(material.diffuse, ourTexCoord));
            vec3 lightDir = normalize(lightSources[i].position - fragPos);
            float diff = max(dot(norm, lightDir), 0.0);
            vec3 diffuse = lightSources[i].diffuse  * diff * vec3(texture(material.diffuse, ourTexCoord));
            vec3 reflectDir = reflect(-lightDir, norm);
            float spec = pow(max(dot(viewDir, reflectDir), 0.0), material.shininess);
            vec3 specular = lightSources[i].specular * spec * vec3(texture(material.specular, ourTexCoord));
            result += ambient + diffuse + specular;
        }
    }




    fragColor = vec4(result, 1.0);
}