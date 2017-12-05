#version 330 core

out vec4 fragColor;

uniform float lightColor;

void main() {
  fragColor = vec4(lightColor);
}