#version 330 core

const int lightSourceCount = 2;

in vec3 fragPos;
in vec3 ourNormal;
in vec2 ourTexCoord;
in vec3 position; // position of the vertex (and fragment) in world space
out vec4 fragColor;
 
uniform vec3 viewPos;

vec3 sceneAmbient = vec3(0.2, 0.2, 0.2);

struct LightSource {
  vec3 position;
  vec3 diffuse;
  vec3 specular;
  float falloffConstant, falloffLinear, falloffQuadratic;
  float spotCutoff, spotExponent;
  vec3 spotDirection;
};

LightSource lightSources[lightSourceCount];

LightSource light0 = LightSource(
    vec3(-3.0,  8.0,  -2.0),    // position
    vec3(0.0,  0.0,  0.0),      // diffuse
    vec3(1.0,  1.0,  1.0),      // specular
    0.0, 1.0, 0.0,              // const, lin, quad
    180.0, 0.0,                 // cutOff, exponent
    vec3(0.0, 0.0, 0.0)         // spotDirection
);

LightSource light1 = LightSource(
    vec3(4.0, 2.0,  5.0),       // position
    vec3(4.0,  1.0,  1.0),      // diffuse
    vec3(1.0,  1.0,  1.0),      // specular
    0.0, 1.0, 0.0,              // const, lin, qua
    180, 10.0,                 // cutOff, exponen
    vec3(0.0, 1.0, 0.0)         // spotDirection
);

struct Material {
    sampler2D diffuse;
    sampler2D specular;
    float shininess;
}; 
  
uniform Material material;

void main() {
    lightSources[0] = light0;
    lightSources[1] = light1;

    vec3 normDir = normalize(ourNormal);
    vec3 viewDir = normalize(viewPos - fragPos);
    vec3 lightDir;
    float falloff;

    vec3 result = vec3(0,0,0);
    // for all light sources
    for (int i = 0; i < lightSourceCount; i++) {
        vec3 lightDir = normalize(lightSources[i].position - fragPos);

        vec3 positionToLightSource = vec3(lightSources[i].position - position);
        float distance = length(positionToLightSource);
        falloff = 1.0 / (lightSources[i].falloffConstant + lightSources[i].falloffLinear * distance + lightSources[i].falloffQuadratic * distance * distance);

        // spotlight?
        if (lightSources[i].spotCutoff <= 90.0) {
            float clampedCosine = max(0.0, dot(-lightDir, normalize(lightSources[i].spotDirection)));
            // outside of spotlight cone?
            if (clampedCosine < cos(radians(lightSources[i].spotCutoff))) {
                falloff = 0.0;
            } else {
                falloff = falloff * pow(clampedCosine, lightSources[i].spotExponent);
            }
        }

        float diff = max(dot(normDir, lightDir), 0.0);

        vec3 diffuse = falloff * vec3(lightSources[i].diffuse) * diff * vec3(texture(material.diffuse, ourTexCoord));

        vec3 reflectDir = reflect(-lightDir, normDir);
        float spec = pow(max(dot(viewDir, reflectDir), 0.0), material.shininess);
        vec3 specular = falloff * lightSources[i].specular * spec * vec3(texture(material.specular, ourTexCoord));

        vec3 ambient  = sceneAmbient  * vec3(texture(material.diffuse, ourTexCoord));

        result = result + diffuse + specular + ambient;
    }

    fragColor = vec4(result, 1.0);
}