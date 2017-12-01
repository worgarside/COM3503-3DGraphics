const int numberOfLights = 2;

struct lightSource{
  vec4 position;
  vec4 diffuse;
  vec4 specular;
  float constantAttenuation, linearAttenuation, quadraticAttenuation;
  float spotCutoff, spotExponent;
  vec3 spotDirection;
};

lightSource lights[numberOfLights];

lightSource light0 = lightSource(
  vec4(0.0,  1.0,  2.0, 1.0),
  vec4(1.0,  1.0,  1.0, 1.0),
  vec4(1.0,  1.0,  1.0, 1.0),
  0.0, 1.0, 0.0,
  180.0, 0.0,
  vec3(0.0, 0.0, 0.0)
);

lightSource light1 = lightSource(
    vec4(0.0, -2.0,  0.0, 1.0),
    vec4(2.0,  0.0,  0.0, 1.0),
    vec4(0.1,  0.1,  0.1, 1.0),
    0.0, 1.0, 0.0,
    80.0, 10.0,
    vec3(0.0, 1.0, 0.0)
);

void main()
{
  lights[0] = light0;
  lights[1] = light1;
}