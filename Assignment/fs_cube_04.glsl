#version 330 core

const int pointLightCount = 4;

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
};

uniform PointLight pointLight;

struct Material {
  sampler2D diffuse;
  sampler2D specular;
  float shininess;
}; 

uniform Material material;

void main() {
  // ambient
  vec3 ambient = pointLight.ambient * vec3(texture(material.diffuse, ourTexCoord));

  // diffuse
  vec3 norm = normalize(ourNormal);
  vec3 lightDir = normalize(pointLight.position - fragPos);
  float diff = max(dot(norm, lightDir), 0.0);
  
  vec3 diffuse = pointLight.diffuse * diff * vec3(texture(material.diffuse, ourTexCoord));
  
  // specular 
  vec3 viewDir = normalize(viewPos - fragPos);
  vec3 reflectDir = reflect(-lightDir, norm);  
  float spec = pow(max(dot(viewDir, reflectDir), 0.0), material.shininess);
  vec3 specular = pointLight.specular * spec * vec3(texture(material.specular, ourTexCoord));

  vec3 result = ambient + diffuse + specular;
  fragColor = vec4(result, 1.0);
}