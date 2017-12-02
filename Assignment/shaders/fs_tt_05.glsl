#version 330 core

const int lightSourceCount = 3;

in vec3 fragPos;
in vec3 ourNormal;
in vec2 ourTexCoord;

out vec4 fragColor;

uniform sampler2D first_texture;
uniform vec3 viewPos;

struct LightSource {
    vec3 position;
    vec3 ambient;
    vec3 diffuse;
    vec3 specular;
    float falloffConstant, falloffLinear, falloffQuadratic;
    float spotCutoff, spotExponent;
    vec3 spotDirection;
};

uniform LightSource lightSources[lightSourceCount];

struct Material {
    vec3 ambient;
    vec3 diffuse;
    vec3 specular;
    float shininess;
}; 
  
uniform Material material;

void main() {
    vec3 result = vec3(0, 0, 0);
    vec3 norm = normalize(ourNormal);
    vec3 viewDir = normalize(viewPos - fragPos);

    for (int i = 0; i < lightSourceCount; i++) {
        vec3 ambient = lightSources[i].ambient * material.ambient * texture(first_texture, ourTexCoord).rgb;
        vec3 lightDir = normalize(lightSources[i].position - fragPos);
        float diff = max(dot(norm, lightDir), 0.0);
        vec3 diffuse = lightSources[i].diffuse * (diff * material.diffuse) * texture(first_texture, ourTexCoord).rgb;
        vec3 reflectDir = reflect(-lightDir, norm);
        float spec = pow(max(dot(viewDir, reflectDir), 0.0), material.shininess);
        vec3 specular = lightSources[i].specular * (spec * material.specular);
        result += ambient + diffuse + specular;
    }

    fragColor = vec4(result, 1.0);
}