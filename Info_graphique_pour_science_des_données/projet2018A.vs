#version 330 core

//Entrees
layout(location = 0) in vec3 vertexPosition_modelspace;
layout(location = 1) in vec3 vertexColor_modelspace;
layout(location = 2) in vec3 vertexNormal_modelspace;

// Sortie
smooth out vec3  color;
smooth out float depth;
smooth out vec3 normalt ; 
smooth out vec3 fragPos ; 
// Params
uniform mat4 modelMatrix;
uniform mat4 viewMatrix ;
uniform mat4 projectionMatrix ; 



void main(){
  gl_Position =  projectionMatrix * viewMatrix * modelMatrix * vec4(vertexPosition_modelspace, 1.0);
  fragPos     = vec3(modelMatrix * vec4(vertexPosition_modelspace, 1.0));
  depth       = gl_Position.z;
  color       = vertexColor_modelspace;
  normalt =  mat3(transpose(inverse(modelMatrix)))* vertexPosition_modelspace ; 
}
