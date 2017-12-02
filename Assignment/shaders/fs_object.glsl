#version 330 core

in vec3 fragPos;
in vec3 ourNormal;
in vec2 ourTexCoord;

out vec4 fragColor;

uniform vec3 viewPos;

struct Light {
  vec3 position;
  vec3 ambient;
  vec3 diffuse;
  vec3 specular;
};


uniform Light light;

struct Material {
  sampler2D diffuse;
  sampler2D specular;
  float shininess;
};

uniform Material material;

void main() {
  // ambient
  //note ambient just from world light, so no calculation needed
  vec3 ambient = light.ambient * vec3(texture(material.diffuse, ourTexCoord));

  // diffuse
  // add up diffuse values for all types of light
  vec3 norm = normalize(ourNormal);
  // add multiple light positions
  vec3 lightDir = normalize(light.position - fragPos);

  float diff = max(dot(norm, lightDir), 0.0);

  vec3 diffuse = light.diffuse * diff * vec3(texture(material.diffuse, ourTexCoord));

  // specular
  // add up specular values for all types of light
  vec3 viewDir = normalize(viewPos - fragPos);
  vec3 reflectDir = reflect(-lightDir, norm);
  float spec = pow(max(dot(viewDir, reflectDir), 0.0), material.shininess);
  vec3 specular = light.specular * spec * vec3(texture(material.specular, ourTexCoord));

  // function to calculate if light is inside or outside of spotlight

  vec3 result = ambient + diffuse + specular;
  fragColor = vec4(result, 1.0);
}