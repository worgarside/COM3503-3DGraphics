#version 330 core

const int pointLightCount = 2;

in vec3 fragPos;
in vec3 ourNormal;
in vec2 ourTexCoord;

out vec4 fragColor;

uniform vec3 viewPos;

struct PointLight {
  vec3 position;
  vec3 ambient;
  vec3 diffuse;
  vec3 specular;
  float falloffConstant;
  float falloffLinear;
  float falloffSquare;
};

uniform PointLight pointLights[pointLightCount];
//uniform PointLight pointLight;

struct Material {
  sampler2D diffuse;
  sampler2D specular;
  float shininess;
}; 

uniform Material material;


vec3 pointLightCalcs(PointLight pLight, vec3 normal, vec3 fragPos, vec3 viewDir) {
    vec3 lightDir = normalize(pLight.position - fragPos);
    float diffuse = max(dot(normal, lightDir), 0.0);

    vec3 reflectDir = reflect(-lightDir, normal);

    float spec = pow(max(dot(viewDir, reflectDir), 0.0), material.shininess);

    float distance    = length(pLight.position - fragPos);
    float falloff = 1.0 / (pLight.falloffConstant + pLight.falloffLinear * distance + pLight.falloffSquare * (distance * distance));
    // combine results
    vec3 ambient  = pLight.ambient  * vec3(texture(material.diffuse, ourTexCoord));
    vec3 diffuseVec3  = pLight.diffuse  * diffuse * vec3(texture(material.diffuse, ourTexCoord));
    vec3 specular = pLight.specular * spec * vec3(texture(material.specular, ourTexCoord));
    ambient  *= falloff;
    diffuseVec3  *= falloff;
    specular *= falloff;
    vec3 result = ambient + diffuseVec3 + specular;
    return result;
}

void main() {

  // diffuse
  vec3 norm = normalize(ourNormal);
  vec3 viewDir = normalize(viewPos - fragPos);
  vec3 result;

  for(int i = 0; i < pointLightCount; i++)
        result += pointLightCalcs(pointLights[i], norm, fragPos, viewDir);

  fragColor = vec4(result, 1.0);
}