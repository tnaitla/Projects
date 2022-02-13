#version 330 core

//Entrees
layout(location = 0) in vec3 vertexPosition_modelspace;

// Sortie
smooth out float zt;
smooth out float depth;

// Params
uniform mat4 modelMatrix;
uniform mat4 viewMatrix;
uniform mat4 projectionMatrix;

void main(){
  gl_Position = projectionMatrix * viewMatrix * modelMatrix * vec4(vertexPosition_modelspace, 1.0);
  depth       = gl_Position.z;
  zt          = vertexPosition_modelspace.z;
}
