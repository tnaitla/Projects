#version 330 core

smooth in float  depth;
smooth in vec3 color;
//smooth in float zt;
smooth in vec3 normalt ; 
smooth in vec3 fragPos ; 


uniform vec3 lightpos;
uniform vec3 viewpos ; 
vec3 lightcolor = vec3(1,1,1) ; 


out vec4 coloro;

void main(){
  float ambientlength = 0.9 ;
  float specularlength= 0.7 ; 
  
  vec3 ambient = ambientlength * lightcolor; 
  
  vec3 norm = normalize(normalt) ; 
  
  vec3 lightdir = normalize(lightpos - fragPos) ;
  
  vec3 viewdir = normalize (viewpos -fragPos) ; 
  
  vec3 reflectdir = reflect(-lightdir , norm ) ;
  
  float diff = max(dot(norm,lightdir),0.0);
  
  vec3 difuse = diff * lightcolor; 
  
  float spec = pow(max(dot(viewdir,reflectdir),0.0),32);
 
  vec3 specular = specularlength*spec*lightcolor ; 

  coloro = vec4 (ambient+difuse+specular,1.0) * vec4(color,1.0);
  
  //coloro          = vec4(color, 1.0);
  gl_FragDepth    = 1.0-depth/10.0;
}
