// sous mac
// g++ -I/usr/local/include/ -lglfw -lGLEW projet2018.cpp -framework OpenGL -oprojet2018
// ./projet2018

// sous linux
// g++ -I/usr/local/include/ -I/public/ig/glm/ -c projet2018.cpp  -o projet2018.o
// g++ -I/usr/local projet2018.o -lglfw  -lGLEW  -lGL  -oprojet2018
// ./projet2018

// Inclut les en-têtes standards
#include <stdio.h>
#include <string>
#include <vector>
#include <iostream>
#include <fstream>
#include <algorithm>
#include <iomanip>
#include <cmath>
using namespace std;

#include <stdlib.h>
#include <string.h>

#include <GL/glew.h>
#include <GLFW/glfw3.h>

#ifdef __APPLE__
#include <OpenGL/gl.h>
#else
#include <GL/gl.h>
#endif

#define GLM_FORCE_RADIANS
#include <glm/glm.hpp>
#include <glm/gtc/matrix_transform.hpp>
#include <glm/gtc/type_ptr.hpp>
#include <glm/gtx/string_cast.hpp>

using namespace glm;

float ptime = 0.0f;
float vv = 1.5f;

float nbBands = 8.0f;
//float angle = 0.0;
int N = 4;

int SMOOTHING_VALS = 1;
int SMOOTHING_VOLS = 1;


// gauss function
float gaussian( float x, float mu, float sigma ) {
  const float a = ( x - mu ) / sigma;
  return std::exp( -0.5 * a * a );
}


// compute a 1D kernel of gauss numbers
vector<float> computeGaussianKernel(int kernelRadius){
  vector<float> kernel(kernelRadius*2);

  for(int i =0 ; i < kernel.size() ; i++ ) {
    kernel[i] =  gaussian(i,0,kernelRadius);}
  return kernel;
}


vector<float> smoothData(vector<float> &data, vector<float> &kernel){
  vector<float> smoothdata(data.size());
  vector<float> sums(data.size());
  float somme = 0; 
  
   for(int i = 0 ; i<data.size()-2 ; i++ ) { 
        sums[i] = data[i] * kernel [0] ; 
        somme = somme +    kernel [0] ; 
        for(int j = 1 ; j < kernel.size() ; j++) {    
           if (i+j < data.size()-2 ) { 
              sums[i] = sums[i] + (data[i+j] * kernel[j] ) ;
              somme = somme +    kernel [j] ;
           } 
           if (i-j >= 0 ) { 
              sums[i] = sums[i] + (data[i-j] * kernel[j] );
              somme = somme +    kernel [j] ;
           } 
      
        
}
 smoothdata[i] = sums[i] /( somme) ; 
 somme = 0 ; 
 } 
 
 smoothdata[data.size()-2] =  data[data.size()-2] ; 
 smoothdata[data.size()-1] =  data[data.size()-1] ; 
  

  return smoothdata;
}


void loadData(string filename, vector<float> &vols, vector<float> &vals){
  ifstream file ( filename ); // declare file stream: http://www.cplusplus.com/reference/iostream/ifstream/
  string value;
  getline ( file, value, '\n' ); //skip first line
  int nbvergul = 0 ; 
  float minval = 0.0f;
  float minvol = 0;
  float maxval = 0.0f;
  float maxvol = 0;
  int indice = 0 ; 
 /// au lieu de stocker le minimum des vols qu'on utilise jamais dans le programe ,
 // on stocke l'indice de maximum qui nous permetra de placer notre source lumineuse 
  while ( file.good() ) {
  	getline ( file, value, ',' );
   	nbvergul++;
   	if(nbvergul == 5){ 
    		nbvergul = 0 ;
 		getline ( file, value, ',' );
   		float volume =std::stof (value); 
   		if (volume>maxvol){
        		maxvol = volume ; 
   		} 
   		getline ( file, value, '\n' );
   		float adjclose = std::stof (value); 
   		if (adjclose>=maxval){
                       cout << "indice " << indice << "  valeur " << adjclose << endl ; 
                       minvol = indice ;
     		       maxval = adjclose ; 
                       
                 }  
                indice++ ;
  		vals.push_back(adjclose);
   		vols.push_back(volume);
                
       }
 } 
  // on rajoute 2 fausses valeurs a la fin que l'on affichera pas .. mais qu'on utilisera pour normaliser !
  vals.push_back(minval);
  vals.push_back(maxval);
  vols.push_back(minvol);
  vols.push_back(maxvol);
}


// la fonction qui calcule la normal avec  la normalisation .
glm::vec3 computeNormal (glm::vec3  a, glm::vec3 b,glm::vec3  c ) { 
     return glm::normalize(glm::cross(c - a, b - a)); 
 } 


GLuint LoadShaders(const char * vertex_file_path,const char * fragment_file_path){

  // Create the shaders
  GLuint VertexShaderID   = glCreateShader(GL_VERTEX_SHADER);
  GLuint FragmentShaderID = glCreateShader(GL_FRAGMENT_SHADER);

  // Read the Vertex Shader code from the file
  std::string VertexShaderCode;
  std::ifstream VertexShaderStream(vertex_file_path, std::ios::in);
  if(VertexShaderStream.is_open()){
    std::string Line = "";
    while(getline(VertexShaderStream, Line))
      VertexShaderCode += "\n" + Line;
    VertexShaderStream.close();
  }else{
    printf("Impossible to open %s. Are you in the right directory ? Don't forget to read the FAQ !\n", vertex_file_path);
    getchar();
    return 0;
  }

  // Read the Fragment Shader code from the file
  std::string FragmentShaderCode;
  std::ifstream FragmentShaderStream(fragment_file_path, std::ios::in);
  if(FragmentShaderStream.is_open()){
    std::string Line = "";
    while(getline(FragmentShaderStream, Line))
      FragmentShaderCode += "\n" + Line;
    FragmentShaderStream.close();
  }

  GLint Result = GL_FALSE;
  int InfoLogLength;


  // Compile Vertex Shader
  printf("Compiling shader : %s\n", vertex_file_path);
  char const * VertexSourcePointer = VertexShaderCode.c_str();
  glShaderSource(VertexShaderID, 1, &VertexSourcePointer , NULL);
  glCompileShader(VertexShaderID);

  // Check Vertex Shader
  glGetShaderiv(VertexShaderID, GL_COMPILE_STATUS, &Result);
  glGetShaderiv(VertexShaderID, GL_INFO_LOG_LENGTH, &InfoLogLength);
  if ( InfoLogLength > 0 ){
    std::vector<char> VertexShaderErrorMessage(InfoLogLength+1);
    glGetShaderInfoLog(VertexShaderID, InfoLogLength, NULL, &VertexShaderErrorMessage[0]);
    printf("%s\n", &VertexShaderErrorMessage[0]);
  }



  // Compile Fragment Shader
  printf("Compiling shader : %s\n", fragment_file_path);
  char const * FragmentSourcePointer = FragmentShaderCode.c_str();
  glShaderSource(FragmentShaderID, 1, &FragmentSourcePointer , NULL);
  glCompileShader(FragmentShaderID);

  // Check Fragment Shader
  glGetShaderiv(FragmentShaderID, GL_COMPILE_STATUS, &Result);
  glGetShaderiv(FragmentShaderID, GL_INFO_LOG_LENGTH, &InfoLogLength);
  if ( InfoLogLength > 0 ){
    std::vector<char> FragmentShaderErrorMessage(InfoLogLength+1);
    glGetShaderInfoLog(FragmentShaderID, InfoLogLength, NULL, &FragmentShaderErrorMessage[0]);
    printf("%s\n", &FragmentShaderErrorMessage[0]);
  }



  // Link the program
  printf("Linking program\n");
  GLuint ProgramID = glCreateProgram();
  glAttachShader(ProgramID, VertexShaderID);
  glAttachShader(ProgramID, FragmentShaderID);
  glLinkProgram(ProgramID);


  // Check the program
  glGetProgramiv(ProgramID, GL_LINK_STATUS, &Result);
  glGetProgramiv(ProgramID, GL_INFO_LOG_LENGTH, &InfoLogLength);
  if ( InfoLogLength > 0 ){
    std::vector<char> ProgramErrorMessage(InfoLogLength+1);
    glGetProgramInfoLog(ProgramID, InfoLogLength, NULL, &ProgramErrorMessage[0]);
    printf("%s\n", &ProgramErrorMessage[0]);
  }


  glDetachShader(ProgramID, VertexShaderID);
  glDetachShader(ProgramID, FragmentShaderID);

  glDeleteShader(VertexShaderID);
  glDeleteShader(FragmentShaderID);

  return ProgramID;
}


int loadModelA(vector<float> &vecVols, vector<float> &vecVals, GLuint VertexArrayIDA){

  GLfloat g_vertex_buffer_dataA[18*(vecVals.size()-1)];
  GLfloat g_vertex_normal_dataA[18*(vecVals.size()-1)];
  GLfloat g_vertex_color_dataA[18*(vecVals.size()-1)];

  for(int i=0; i<18*(vecVals.size()-1); i++){
    g_vertex_buffer_dataA[i] = 0.654321;
    g_vertex_color_dataA[i]  = 0.654321;;
  }
  double moy = vecVals[vecVals.size()-1]/2 ; 
  for (int i=0; i<N-1; i++){
  
   ///////////////////////////
  /////// 1er trinagle  /////
 ///////////////////////////

   /* premier point */
   g_vertex_buffer_dataA[i*18+0]=(-1+((i*2.0)/N))/1.75; // le temps (axe des abcisses )  ,
   g_vertex_buffer_dataA[i*18+2]= (2*vecVals[i]/(vecVals[vecVals.size()-1]))/2; // les valeurs (l'axe des Z) 
   g_vertex_buffer_dataA[i*18+1]=0;// les volumes (l'axe des ordonnées) 
   /*deuxieme point */ 
   g_vertex_buffer_dataA[i*18+3]=(-1+((i*2.0)/N))/1.75;
   g_vertex_buffer_dataA[i*18+5]= (2*vecVals[i]/(vecVals[vecVals.size()-1]))/2;
   g_vertex_buffer_dataA[i*18+4]=-(0.1*std::sqrt(vecVols[i])/std::sqrt(vecVols[vecVols.size()-1]))/1.5;
   /*troisieme point */ 
   g_vertex_buffer_dataA[i*18+6]=(-1+(((i+1)*2.0)/N))/1.75;
   g_vertex_buffer_dataA[i*18+8]= (2*vecVals[i+1]/(vecVals[vecVals.size()-1]))/2;
   g_vertex_buffer_dataA[i*18+7]=-(0.1*std::sqrt(vecVols[i+1])/std::sqrt(vecVols[vecVols.size()-1]))/1.5;
  
   ///////////////////////////
  /////// 2eme trinagle  ////
 ///////////////////////////

   /* premier point */
   g_vertex_buffer_dataA[i*18+9]=(-1+((i*2.0)/N))/1.75;
   g_vertex_buffer_dataA[i*18+11]=(2*vecVals[i]/(vecVals[vecVals.size()-1]))/2;
   g_vertex_buffer_dataA[i*18+10]=0;
   
   /*deuxieme point */
   g_vertex_buffer_dataA[i*18+12]=(-1+(((i+1)*2.0)/N))/1.75;
   g_vertex_buffer_dataA[i*18+14]=(2*vecVals[i+1]/(vecVals[vecVals.size()-1]))/2;
   g_vertex_buffer_dataA[i*18+13]=0.;
   /*troisieme point */ 
   g_vertex_buffer_dataA[i*18+15]=(-1+(((i+1)*2.0)/N))/1.75;
   g_vertex_buffer_dataA[i*18+17]=(2*vecVals[i+1]/(vecVals[vecVals.size()-1]))/2;
   g_vertex_buffer_dataA[i*18+16]=-(0.1*std::sqrt(vecVols[i+1])/std::sqrt(vecVols[vecVols.size()-1]))/1.5;
   
   ///////////////////////////////////////////////////////////////
  ////////// remplissage du buffer des couleurs   ///////////////
 ///////////////////////////////////////////////////////////////
    
   if (moy <= vecVals[i] ) { 
    g_vertex_color_dataA[i*18+0] = 0.0 ; 
    g_vertex_color_dataA[i*18+1] = ((2*vecVals[i]/(vecVals[vecVals.size()-1]))/2) ; 
    g_vertex_color_dataA[i*18+2] = 0.0 ; 
  
    g_vertex_color_dataA[i*18+3] = 0.0 ; 
    g_vertex_color_dataA[i*18+4] = ((2*vecVals[i]/(vecVals[vecVals.size()-1]))/2) ; 
    g_vertex_color_dataA[i*18+5] = 0.0 ; 

    g_vertex_color_dataA[i*18+9] = 0.0 ; 
    g_vertex_color_dataA[i*18+10] =((2*vecVals[i]/(vecVals[vecVals.size()-1]))/2); 
    g_vertex_color_dataA[i*18+11] = 0.0 ; 
   }else{
   g_vertex_color_dataA[i*18+0] = 0.4+(2*vecVals[i]/(vecVals[vecVals.size()-1]))/2; ; 
    g_vertex_color_dataA[i*18+1] = 0.0 ; 
    g_vertex_color_dataA[i*18+2] = 0.0 ; 
  
    g_vertex_color_dataA[i*18+3] = 0.4+(2*vecVals[i]/(vecVals[vecVals.size()-1]))/2; ; 
    g_vertex_color_dataA[i*18+4] = 0.0 ; 
    g_vertex_color_dataA[i*18+5] = 0.0 ; 

    g_vertex_color_dataA[i*18+9] = 0.4+(2*vecVals[i]/(vecVals[vecVals.size()-1]))/2; ; 
    g_vertex_color_dataA[i*18+10] = 0.0 ; 
    g_vertex_color_dataA[i*18+11] = 0.0 ; 
   }
   if(moy < vecVals[i+1] ){
    g_vertex_color_dataA[i*18+6] = 0.0 ; 
    g_vertex_color_dataA[i*18+7] = ((2*vecVals[i+1]/(vecVals[vecVals.size()-1]))/2); ; 
    g_vertex_color_dataA[i*18+8] = 0.0 ; 
   
    g_vertex_color_dataA[i*18+12] = 0.0 ; 
    g_vertex_color_dataA[i*18+13] = ((2*vecVals[i+1]/(vecVals[vecVals.size()-1]))/2) ; 
    g_vertex_color_dataA[i*18+14] = 0.0 ; 
   
    g_vertex_color_dataA[i*18+15] = 0.0 ; 
    g_vertex_color_dataA[i*18+16] = ((2*vecVals[i+1]/(vecVals[vecVals.size()-1]))/2); ; 
    g_vertex_color_dataA[i*18+17] = 0.0 ; 
   }else {
    g_vertex_color_dataA[i*18+6] = 0.4+(2*vecVals[i+1]/(vecVals[vecVals.size()-1]))/2; ; 
    g_vertex_color_dataA[i*18+7] = 0.0 ; 
    g_vertex_color_dataA[i*18+8] = 0.0 ; 
   
    g_vertex_color_dataA[i*18+12] = 0.4+(2*vecVals[i+1]/(vecVals[vecVals.size()-1]))/2; ; 
    g_vertex_color_dataA[i*18+13] = 0.0 ; 
    g_vertex_color_dataA[i*18+14] = 0.0 ; 
   
    g_vertex_color_dataA[i*18+15] = 0.4+(2*vecVals[i+1]/(vecVals[vecVals.size()-1]))/2; ; 
    g_vertex_color_dataA[i*18+16] = 0.0 ; 
    g_vertex_color_dataA[i*18+17] = 0.0 ; 
    } 
  
  
   //////////////////////////////////////////////////////////////////////
  ///////////// remplissage du buffer des normales  ////////////////////
 //////////////////////////////////////////////////////////////////////
    
    glm::vec3 a1 =  glm::vec3((-1+((i*2.0)/N))/1.75 ,0,  (2*vecVals[i]/(vecVals[vecVals.size()-1]))/2) ;
   
    glm::vec3  b1 = glm::vec3 ((-1+((i*2.0)/N))/1.75 , 
                               -(0.1*std::sqrt(vecVols[i])/std::sqrt(vecVols[vecVols.size()-1]))/1.5,
                               (2*vecVals[i]/(vecVals[vecVals.size()-1]))/2); 
    
    glm::vec3 c1 = glm::vec3((-1+(((i+1)*2.0)/N))/1.75,
                             -(0.1*std::sqrt(vecVols[i+1])/std::sqrt(vecVols[vecVols.size()-1]))/1.5,
                              (2*vecVals[i+1]/(vecVals[vecVals.size()-1]))/2);
     
    glm::vec3 res1 = computeNormal(a1,b1,c1) ;    
    
    g_vertex_normal_dataA[i*18+0] = res1.x ; 
    g_vertex_normal_dataA[i*18+1] = res1.y ; 
    g_vertex_normal_dataA[i*18+2] = res1.z ; 
   
    g_vertex_normal_dataA[i*18+3] = res1.x ; 
    g_vertex_normal_dataA[i*18+4] = res1.y ; 
    g_vertex_normal_dataA[i*18+5] = res1.z ; 
   
    g_vertex_normal_dataA[i*18+6] = res1.x ;
    g_vertex_normal_dataA[i*18+7] = res1.y ; 
    g_vertex_normal_dataA[i*18+8] = res1.z ; 
    
    glm::vec3 a2 = glm::vec3 ((-1+((i*2.0)/N))/1.75 ,0,(2*vecVals[i]/(vecVals[vecVals.size()-1]))/2 ) ;  
    glm::vec3 b2 = glm::vec3 ((-1+(((i+1)*2.0)/N))/1.75 ,0. , (2*vecVals[i+1]/(vecVals[vecVals.size()-1]))/2 ) ; 
    glm::vec3 c2 = glm::vec3 ((-1+(((i+1)*2.0)/N))/1.75 ,(0.1*std::sqrt(vecVols[i+1])/std::sqrt(vecVols[vecVols.size()-1]))/1.5 ,(2*vecVals[i+1]/(vecVals[vecVals.size()-1]))/2 ) ; 
     
    glm::vec3 res2 = computeNormal(a2,b2,c2) ;  
    g_vertex_normal_dataA[i*18+9]  = res2.x ; 
    g_vertex_normal_dataA[i*18+10] = res2.y ; 
    g_vertex_normal_dataA[i*18+11] = res2.z ; 
   
    g_vertex_normal_dataA[i*18+12] = res2.x ; 
    g_vertex_normal_dataA[i*18+13] = res2.y ; 
    g_vertex_normal_dataA[i*18+14] = res2.z ; 
   
    g_vertex_normal_dataA[i*18+15] = res2.x ;
    g_vertex_normal_dataA[i*18+16] = res2.y ; 
    g_vertex_normal_dataA[i*18+17] = res2.z ; 
      
}
  // on teste s'il ne reste pas notre valeur bizarre dans le tableau = on a pas oublie de cases !
  for(int i=0; i<18*(N-1); i++)
    if (g_vertex_buffer_dataA[i] > 0.654320 && g_vertex_buffer_dataA[i] < 0.654322)
      cout << i<<" EVIL IS IN THE DETAIL !" << endl ;

  glBindVertexArray(VertexArrayIDA);

  // This will identify our vertex buffer
  GLuint vertexbufferA;
  // Generate 1 buffer, put the resulting identifier in vertexbuffer
  glGenBuffers(1, &vertexbufferA);

  // The following commands will talk about our 'vertexbuffer' buffer
  glBindBuffer(GL_ARRAY_BUFFER, vertexbufferA);

    // Only allocate memory. Do not send yet our vertices to OpenGL.
    glBufferData(GL_ARRAY_BUFFER, sizeof(g_vertex_buffer_dataA)+sizeof(g_vertex_color_dataA)+sizeof(g_vertex_normal_dataA), 0, GL_STATIC_DRAW);

      // send vertices in the first part of the buffer
    glBufferSubData(GL_ARRAY_BUFFER, 0,                            sizeof(g_vertex_buffer_dataA), g_vertex_buffer_dataA);

    // send colors in the second part of the buffer
    glBufferSubData(GL_ARRAY_BUFFER, sizeof(g_vertex_buffer_dataA), sizeof(g_vertex_color_dataA), g_vertex_color_dataA);
    ///////// normales ////////////
   // glBufferSubData(GL_ARRAY_BUFFER, 0,                            sizeof(g_vertex_buffer_dataA), g_vertex_buffer_dataA);

    // send bormals in the third part of the buffer
   // glBufferSubData(GL_ARRAY_BUFFER, sizeof(g_vertex_buffer_dataA)+sizeof(g_vertex_color_dataA), sizeof(g_vertex_normal_dataA), g_vertex_normal_dataA);

    // ici les commandes stockees "une fois pour toute" dans le VAO
    glVertexAttribPointer(
       0,                  // attribute 0. No particular reason for 0, but must match the layout in the shader.
       3,                  // size
       GL_FLOAT,           // type
       GL_FALSE,           // normalized?
       0,                  // stride
       (void*)0            // array buffer offset
    );
    glEnableVertexAttribArray(0);

    glVertexAttribPointer( // same thing for the colors
      1,
      3,
      GL_FLOAT,
      GL_FALSE,
      0,
      (void*)sizeof(g_vertex_buffer_dataA));
    glEnableVertexAttribArray(1);

    glVertexAttribPointer( // same thing for the normals
      2,
      3,
      GL_FLOAT,
      GL_FALSE,
      0,
      (void*)(sizeof(g_vertex_buffer_dataA)+sizeof(g_vertex_color_dataA)));
    glEnableVertexAttribArray(2);


  glBindBuffer(GL_ARRAY_BUFFER, 0);

  // on desactive le VAO a la fin de l'initialisation
  glBindVertexArray (0);

  return sizeof(g_vertex_buffer_dataA)/(3*sizeof(float));
}



int loadModelB(vector<float> &vecVols, vector<float> &vecVals, GLuint VertexArrayIDB){
  GLfloat g_vertex_buffer_dataB[36*(vecVals.size()-1)]; // 4 triangles of 3 points with 3 coordinates each

  //cout<< "loadModelB " << (sizeof(g_vertex_buffer_dataB)/(sizeof(float)))<<" % "<<(3*3*4*(N))<<endl;
  for(int i=0; i<36*(vecVals.size()-1); i++)
    g_vertex_buffer_dataB[i] = 0.654321;

  // on rajoute des faces et de la hauteur a notre figure
  for (int i=0; i<N-1; i++){
    
      //////////////////////////////////
     ////////le premier triangle //////
    //////////////////////////////////
   
     ////////* 1er point *////////
     g_vertex_buffer_dataB[i*36+0] = (-1+((i*2.0)/N))/1.75; 
     g_vertex_buffer_dataB[i*36+2] =  (2*vecVals[i]/(vecVals[vecVals.size()-1]))/2;
     g_vertex_buffer_dataB[i*36+1] = -(0.1*std::sqrt(vecVols[i])/std::sqrt(vecVols[vecVols.size()-1]))/1.75;
     ////////* 2eme point *////////
     g_vertex_buffer_dataB[i*36+3] = (-1+((i*2.0)/N))/1.75; 
     g_vertex_buffer_dataB[i*36+5] = 0 ;
     g_vertex_buffer_dataB[i*36+4] = -(0.1*std::sqrt(vecVols[i])/std::sqrt(vecVols[vecVols.size()-1]))/1.5;
     ////////* 3eme point *////////
     g_vertex_buffer_dataB[i*36+6] = (-1+(((i+1)*2.0)/N))/1.75;
     g_vertex_buffer_dataB[i*36+8] = (2*vecVals[i+1]/(vecVals[vecVals.size()-1]))/2;
     g_vertex_buffer_dataB[i*36+7] = -(0.1*std::sqrt(vecVols[i+1])/std::sqrt(vecVols[vecVols.size()-1]))/1.5; 
      
      //////////////////////////////////
     ////////  2eme triangle //////////
    //////////////////////////////////

     ////////* 1er point *////////
     g_vertex_buffer_dataB[i*36+9] = (-1+(((i+1)*2.0)/N))/1.75;
     g_vertex_buffer_dataB[i*36+11] =(2*vecVals[i+1]/(vecVals[vecVals.size()-1]))/2;
     g_vertex_buffer_dataB[i*36+10] = -(0.1*std::sqrt(vecVols[i+1])/std::sqrt(vecVols[vecVols.size()-1]))/1.5;
    
     ////////* 2eme point *////////
     g_vertex_buffer_dataB[i*36+12] =(-1+((i*2.0)/N))/1.75; 
     g_vertex_buffer_dataB[i*36+14] = 0;
     g_vertex_buffer_dataB[i*36+13] = -(0.1*std::sqrt(vecVols[i])/std::sqrt(vecVols[vecVols.size()-1]))/1.5;
     
     ////////* 3eme point *////////
     g_vertex_buffer_dataB[i*36+15] = (-1+(((i+1)*2.0)/N))/1.75;
     g_vertex_buffer_dataB[i*36+17] = 0;
     g_vertex_buffer_dataB[i*36+16] = -(0.1*std::sqrt(vecVols[i+1])/std::sqrt(vecVols[vecVols.size()-1]))/1.5;
   
      //////////////////////////////////
     ////////  3eme triangle //////////
    //////////////////////////////////

     ////////* 1er point *////////
     g_vertex_buffer_dataB[i*36+18] = (-1+(((i+1)*2.0)/N))/1.75;
     g_vertex_buffer_dataB[i*36+20] =(2*vecVals[i+1]/(vecVals[vecVals.size()-1]))/2;
     g_vertex_buffer_dataB[i*36+19] = 0 ;
    
     ////////* 2eme point *////////
     g_vertex_buffer_dataB[i*36+21] =(-1+((i*2.0)/N))/1.75; 
     g_vertex_buffer_dataB[i*36+23] = 0;
     g_vertex_buffer_dataB[i*36+22] = 0 ;
     
     ////////* 3eme point *////////
     g_vertex_buffer_dataB[i*36+24] = (-1+(((i+1)*2.0)/N))/1.75;
     g_vertex_buffer_dataB[i*36+26] = 0;
     g_vertex_buffer_dataB[i*36+25] = 0;
      //////////////////////////////////
     ////////  4eme triangle //////////
    //////////////////////////////////

     ////////* 1er point *////////
     g_vertex_buffer_dataB[i*36+27] = (-1+(((i+1)*2.0)/N))/1.75;
     g_vertex_buffer_dataB[i*36+29] =(2*vecVals[i+1]/(vecVals[vecVals.size()-1]))/2;
     g_vertex_buffer_dataB[i*36+28] = 0 ;
     
     ////////* 2eme point *////////
     g_vertex_buffer_dataB[i*36+30] =(-1+((i*2.0)/N))/1.75;  
     g_vertex_buffer_dataB[i*36+32] =(2*vecVals[i]/(vecVals[vecVals.size()-1]))/2;
     g_vertex_buffer_dataB[i*36+31] = 0 ;
     
     ////////* 3eme point *////////
     g_vertex_buffer_dataB[i*36+33] = (-1+((i*2.0)/N))/1.75; 
     g_vertex_buffer_dataB[i*36+35] = 0 ;
     g_vertex_buffer_dataB[i*36+34] = 0 ;
}
      ////////////////////////////////////////////////////////// 
     ////// fermeture des bords par quatres triangles /////////
    //////////////////////////////////////////////////////////
     
     ////////////////////////////
    ////// 1er triangles ///////
   ////////////////////////////
 
     //////* 1er point *//////
     g_vertex_buffer_dataB[(N-1)*36+0] = (-1+((0*2.0)/N))/1.75; 
     g_vertex_buffer_dataB[(N-1)*36+2] =  0;
     g_vertex_buffer_dataB[(N-1)*36+1] = 0;
     
     //////* 2eme point *//////
     g_vertex_buffer_dataB[(N-1)*36+3] = (-1+((0*2.0)/N))/1.75; 
     g_vertex_buffer_dataB[(N-1)*36+5] = 0 ;
     g_vertex_buffer_dataB[(N-1)*36+4] = -(0.1*std::sqrt(vecVols[0])/std::sqrt(vecVols[vecVols.size()-1]))/1.5;
      
     //////* 3eme point *//////
     g_vertex_buffer_dataB[(N-1)*36+6] = (-1+((0*2.0)/N))/1.75;
     g_vertex_buffer_dataB[(N-1)*36+8] = (2*vecVals[0]/(vecVals[vecVals.size()-1]))/2;
     g_vertex_buffer_dataB[(N-1)*36+7] = 0; 
     
     ////////////////////////////
    ////// 2eme triangles //////
   //////////////////////////// 

     //////* 1er point *//////
     g_vertex_buffer_dataB[(N-1)*36+9] = (-1+((0*2.0)/N))/1.75;
     g_vertex_buffer_dataB[(N-1)*36+11] =(2*vecVals[0]/(vecVals[vecVals.size()-1]))/2;
     g_vertex_buffer_dataB[(N-1)*36+10] =0;
    
     //////* 2eme point *//////
     g_vertex_buffer_dataB[(N-1)*36+12] =(-1+((0*2.0)/N))/1.75; 
     g_vertex_buffer_dataB[(N-1)*36+14] = 0;
     g_vertex_buffer_dataB[(N-1)*36+13] = -(0.1*std::sqrt(vecVols[0])/std::sqrt(vecVols[vecVols.size()-1]))/1.5;
    
     //////* 3eme point *//////
     g_vertex_buffer_dataB[(N-1)*36+15] = (-1+((0*2.0)/N))/1.75;
     g_vertex_buffer_dataB[(N-1)*36+17] = (2*vecVals[0]/(vecVals[vecVals.size()-1]))/2;
     g_vertex_buffer_dataB[(N-1)*36+16] = -(0.1*std::sqrt(vecVols[0])/std::sqrt(vecVols[vecVols.size()-1]))/1.5;
     
     
     ////////////////////////////
    ////// 3eme triangles //////
   ////////////////////////////

     //////* 1er point *//////
     g_vertex_buffer_dataB[(N-1)*36+18] = (-1+(((N-1)*2.0)/N))/1.75;
     g_vertex_buffer_dataB[(N-1)*36+20] = 0 ;
     g_vertex_buffer_dataB[(N-1)*36+19] = 0 ;
   
     //////* 2eme point *//////
     g_vertex_buffer_dataB[(N-1)*36+21] =(-1+(((N-1)*2.0)/N))/1.75; 
     g_vertex_buffer_dataB[(N-1)*36+23] = 0;
     g_vertex_buffer_dataB[(N-1)*36+22] = -(0.1*std::sqrt(vecVols[N-2])/std::sqrt(vecVols[vecVols.size()-1]))/1.5;
   
     //////* 3eme point *//////
     g_vertex_buffer_dataB[(N-1)*36+24] = (-1+(((N-1)*2.0)/N))/1.75;
     g_vertex_buffer_dataB[(N-1)*36+26] = (2*vecVals[N-2]/(vecVals[vecVals.size()-1]))/2;
     g_vertex_buffer_dataB[(N-1)*36+25] = 0;
     
     ////////////////////////////
    ////// 4eme triangles //////
   ////////////////////////////

     //////* 1er point *//////
     g_vertex_buffer_dataB[(N-1)*36+27] = (-1+(((N-1)*2.0)/N))/1.75;
     g_vertex_buffer_dataB[(N-1)*36+29] =(2*vecVals[(N-2)+1]/(vecVals[vecVals.size()-1]))/2;
     g_vertex_buffer_dataB[(N-1)*36+28] = 0 ;
     
     //////* 2eme point *//////
     g_vertex_buffer_dataB[(N-1)*36+30] =(-1+(((N-1)*2.0)/N))/1.75; 
     g_vertex_buffer_dataB[(N-1)*36+32] =0;
     g_vertex_buffer_dataB[(N-1)*36+31] = -(0.1*std::sqrt(vecVols[N-2])/std::sqrt(vecVols[vecVols.size()-1]))/1.5; ;
     
     //////* 3eme point *//////
     g_vertex_buffer_dataB[(N-1)*36+33] = (-1+(((N-1)*2.0)/N))/1.75; 
     g_vertex_buffer_dataB[(N-1)*36+35] =  (2*vecVals[N-2]/(vecVals[vecVals.size()-1]))/2 ;
     g_vertex_buffer_dataB[(N-1)*36+34] = -(0.1*std::sqrt(vecVols[N-2])/std::sqrt(vecVols[vecVols.size()-1]))/1.5 ;
     
     
     
  

  for(int i=0; i<36*(N-1); i++)
    if (g_vertex_buffer_dataB[i] > 0.654320 && g_vertex_buffer_dataB[i] < 0.654322)
      cout << i<<" EVIL IS IN THE DETAIL !" << endl ;

  // This will identify our vertex buffer
  GLuint vertexbufferB;

  // on s'occupe du second VAO
  glBindVertexArray(VertexArrayIDB);
  glGenBuffers(1, &vertexbufferB);
  glBindBuffer(GL_ARRAY_BUFFER, vertexbufferB);
  glBufferData(GL_ARRAY_BUFFER, sizeof(g_vertex_buffer_dataB), 0, GL_STATIC_DRAW);
  glBufferSubData(GL_ARRAY_BUFFER, 0,                            sizeof(g_vertex_buffer_dataB), g_vertex_buffer_dataB);
  glVertexAttribPointer(
       0,                  // attribute 0. No particular reason for 0, but must match the layout in the shader.
       3,                  // size
       GL_FLOAT,           // type
       GL_FALSE,           // normalized?
       0,                  // stride
       (void*)0            // array buffer offset
    );
  glEnableVertexAttribArray(0);


  glBindBuffer(GL_ARRAY_BUFFER, 0);

  // on desactive le VAO a la fin de l'initialisation
  glBindVertexArray (0);

  return sizeof(g_vertex_buffer_dataB)/(3*sizeof(float));
}


int main(){
  // for later printing
  std::cout << std::setprecision(5) << std::fixed;
  /////////////////////////////////////////////////////////////
 ////// le tableau qui sert pour le lissage des valeurs //////
/////////////////////////////////////////////////////////////
      vector<float> kernel0 = computeGaussianKernel(SMOOTHING_VALS);
  
  /////////////////////////////////////////////////////////////
 ////// le tableau qui sert pour le lissage des volumes //////
/////////////////////////////////////////////////////////////
   
    vector<float> kernel01 = computeGaussianKernel(SMOOTHING_VOLS);

  for (int i = 0; i < kernel0.size(); i++) 
    cout << i<<": " << kernel0[i] << endl;
     //Initialise GLFW
  if( !glfwInit() ) {
      fprintf( stderr, "Failed to initialize GLFW\n" );
      return -1;
  }

  vector<float> vols10;
  vector<float> vals10;

  vector<float> vols20;
  vector<float> vals20;

  vector<float> vols30;
  vector<float> vals30;

  vector<float> vols40;
  vector<float> vals40;

  loadData("data/AAPL.csv",  vols10, vals10);
 // cout << "avvant " << vals10[100] << endl ; 

    cout << "apars " << vals10[100] << endl ; 
  loadData("data/GOOGL.csv", vols20, vals20);

  
  loadData("data/AMZN.csv",  vols30, vals30);
   

  loadData("data/MSFT.csv",  vols40, vals40);
 
  N = vals10.size()-3;

  glfwWindowHint(GLFW_SAMPLES, 4); // 4x antialiasing
  glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3); // On veut OpenGL 3.3
  glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
  glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE); // Pour rendre MacOS heureux ; ne devrait pas être nécessaire
  glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE); // On ne veut pas l'ancien OpenGL
  glfwWindowHint(GLFW_DEPTH_BITS, 24);

  // Ouvre une fenêtre et crée son contexte OpenGl
  GLFWwindow* window; // (Dans le code source qui accompagne, cette variable est globale)
  window = glfwCreateWindow( 1600, 768, "Projet 2018", NULL, NULL);
  if( window == NULL ){
      fprintf( stderr, "Failed to open GLFW window. If you have an Intel GPU, they are not 3.3 compatible. Try the 2.1 version of the tutorials.\n" );
      glfwTerminate();
      return -1;
  }

  glfwMakeContextCurrent(window); // Initialise GLEW
  glewExperimental=true; // Nécessaire dans le profil de base
  if (glewInit() != GLEW_OK) {
      fprintf(stderr, "Failed to initialize GLEW\n");
      return -1;
  }

  // Enable depth test
  glEnable(GL_DEPTH_TEST);

  // Accept fragment if it closer to the camera than the former one
  glDepthFunc(GL_LESS);
  glDepthRange(-1, 1);

  // Assure que l'on peut capturer la touche d'échappement enfoncée ci-dessous
  glfwSetInputMode(window, GLFW_STICKY_KEYS, GL_TRUE);

  // Bon maintenant on cree les IDs de VAO
  GLuint VertexArrayIDA1, VertexArrayIDB1;
  GLuint VertexArrayIDA2, VertexArrayIDB2;
  GLuint VertexArrayIDA3, VertexArrayIDB3;
  GLuint VertexArrayIDA4, VertexArrayIDB4;

  glGenVertexArrays(1, &VertexArrayIDA1);
  glGenVertexArrays(1, &VertexArrayIDB1);
  glGenVertexArrays(1, &VertexArrayIDA2);
  glGenVertexArrays(1, &VertexArrayIDB2);
  glGenVertexArrays(1, &VertexArrayIDA3);
  glGenVertexArrays(1, &VertexArrayIDB3);
  glGenVertexArrays(1, &VertexArrayIDA4);
  glGenVertexArrays(1, &VertexArrayIDB4);

  int m11 = loadModelA(vols10, vals10, VertexArrayIDA1);
   cout << m11 << endl ;
    
  int m12 = loadModelB(vols10, vals10, VertexArrayIDB1);

  int m21 = loadModelA(vols20, vals20, VertexArrayIDA2);
  int m22 = loadModelB(vols20, vals20, VertexArrayIDB2);

  int m31 = loadModelA(vols30, vals30, VertexArrayIDA3);
  int m32 = loadModelB(vols30, vals30, VertexArrayIDB3);

  int m41 = loadModelA(vols40, vals40, VertexArrayIDA4);
  int m42 = loadModelB(vols40, vals40, VertexArrayIDB4);

// identifiant de nos programmes de shaders
GLuint programA;
GLuint programB;

 GLuint ProgramA      =    LoadShaders( "projet2018A.vs", "projet2018A.fs" );
 GLint  uniform_modelA    = glGetUniformLocation(ProgramA, "modelMatrix");
 GLint	uniform_viewA    = glGetUniformLocation(ProgramA, "viewMatrix");
 GLint  uniform_projA    = glGetUniformLocation(ProgramA, "projectionMatrix");
 GLint  uniform_lightpos = glGetUniformLocation(ProgramA, "lightpos");
 GLint  uniform_viewpos = glGetUniformLocation(ProgramA, "viewpos");


  GLuint ProgramB        = LoadShaders( "projet2018B.vs", "projet2018B.fs" );
  GLint  uniform_modelB   = glGetUniformLocation(ProgramB, "modelMatrix");
  GLint	uniform_viewB   = glGetUniformLocation(ProgramB, "viewMatrix");
  GLint  uniform_projB   = glGetUniformLocation(ProgramB, "projectionMatrix"); 
  float angle = 1.57f;
  float decalge = 0 ; 
  do {
    // clear before every draw
  //  angle=angle+0.005;
    glClearColor( 1.0, 1.0, 1.0, 1.0);
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

    // Use our shader program
    glUseProgram(ProgramA);

    // onchange de matrice de projection : la projection orthogonale est plus propice a la visualization !
    //glm::mat4 projectionMatrix = glm::perspective(glm::radians(66.0f), 1024.0f / 768.0f, 0.1f, 200.0f);
    glm::mat4 projectionMatrix = glm::ortho( -1.0f, 1.0f, -1.0f, 1.0f, -6.f, 6.f );

    float camPos[3] = {1.2*cos(angle), 1.2*sin(angle), -0.5};
    glm::vec3 viewpos = glm::vec3 ( camPos[0],camPos[1],camPos[2] ) ; 
    glm::vec3 lightpos = glm::vec3 ((-1+((4*2.0)/N))/1.75,0,1.2) ; 
    
    glm::mat4 viewMatrix       = glm::lookAt(
                                  vec3(1.2*cos(angle), 1.2*sin(angle), -0.5), // where is the camara
                                  vec3(0, 0, 0.4), //where it looks
                                  vec3(0, 0, 2) // head is up
                                );
    mat4 modelMatrix     =  scale(glm::mat4(1.0f), glm::vec3(0.75f));
    modelMatrix          =  translate(modelMatrix, glm::vec3(decalge, 0.2f, 0.0f));

    glUniformMatrix4fv (uniform_modelA,  1, GL_FALSE, glm::value_ptr(modelMatrix)); 
    glUniformMatrix4fv (uniform_viewA,  1, GL_FALSE, glm::value_ptr(viewMatrix));
    glUniformMatrix4fv (uniform_projA,  1, GL_FALSE, glm::value_ptr(projectionMatrix));
    glUniform3fv       (uniform_lightpos , 1 , glm::value_ptr(lightpos)) ; 
    glUniform3fv       (uniform_viewpos , 1 , glm::value_ptr(viewpos)) ; 

    // on re-active les VAO avant d'envoyer les buffers
    
  //////////////////////////////////////////////////////////////////////////////////////
 /////////*********   GERER LES AFFICHAGES POUR LES Dessus  ***** /////////////////////
////////////////////////////////////////////////////////////////////////////////////// 

 ////////////**** AFFICHER LA COURBES DE AAPLE ****/////////////////////// 
    if (glfwGetKey(window, GLFW_KEY_P ) == GLFW_PRESS){
         lightpos = glm::vec3 ((-1+((vols10[vols10.size()-2]*2.0)/N))/1.75,0,1.2) ; 
         glUniform3fv       (uniform_lightpos , 1 , glm::value_ptr(lightpos)) ; 
         glBindVertexArray(VertexArrayIDA1);
         glDrawArrays(GL_TRIANGLES, 0, m11);
     } 

 ////////////**** AFFICHER LA COURBES DE GOOGLE ****///////////////////// 
     else if (glfwGetKey(window, GLFW_KEY_G ) == GLFW_PRESS){
         lightpos = glm::vec3 ((-1+((vols20[vols20.size()-2]*2.0)/N))/1.75,0,1.2) ; 
         glUniform3fv       (uniform_lightpos , 1 , glm::value_ptr(lightpos)) ; 
         glBindVertexArray(VertexArrayIDA2);
         glDrawArrays(GL_TRIANGLES, 0, m21);
     } 

 //////////***** AFFICHER LA COURBES DE AMAZON ****////////////////////
     else if (glfwGetKey(window, GLFW_KEY_Q ) == GLFW_PRESS){
         lightpos = glm::vec3 ((-1+((vols30[vols30.size()-2]*2.0)/N))/1.75,0,1.2) ; 
         glUniform3fv       (uniform_lightpos , 1 , glm::value_ptr(lightpos)) ; 
         glBindVertexArray(VertexArrayIDA3);
         glDrawArrays(GL_TRIANGLES, 0, m31);
     } 
//////////***** AFFICHER LA COURBES DE MICROSOFT ****////////////////////
     else if (glfwGetKey(window, GLFW_KEY_F ) == GLFW_PRESS){
         lightpos = glm::vec3 ((-1+((vols40[vols40.size()-2]*2.0)/N))/1.75,0,1.2) ; 
         glUniform3fv       (uniform_lightpos , 1 , glm::value_ptr(lightpos)) ; 
         glBindVertexArray(VertexArrayIDA4);
         glDrawArrays(GL_TRIANGLES, 0, m41);
    } 
    
/////////**** AFFICHER LES QUATRES COURBES AU MEME TEMPS ****//////////// 
    else{
        // cout<<" dlkvSDMFKSMDFSLMFD,ÙSfml " <<  vols10[vols10.size()-2] << " "  <<  vals10[vals10.size()-1]  << endl; 
         lightpos = glm::vec3 ((-1+((vols10[vols10.size()-2]*2.0)/N))/1.75,0,1.2) ; 
         glUniform3fv       (uniform_lightpos , 1 , glm::value_ptr(lightpos)) ; 
         glBindVertexArray(VertexArrayIDA1);
         glDrawArrays(GL_TRIANGLES, 0, m11);
     
    /*pour afficher les courbes cote a cote , on fait un translation pour modele matrix 
    et on l'envoie dans un UNIFORME ensuite on fait une translation inverse pour revenir 
    à la position initiale .
    */
        glBindVertexArray(VertexArrayIDA2);
        lightpos = glm::vec3 ((-1+((vols20[vols20.size()-2]*2.0)/N))/1.75,0,1.2) ; 
        glUniform3fv       (uniform_lightpos , 1 , glm::value_ptr(lightpos)) ; 
        modelMatrix          =  translate(modelMatrix, glm::vec3(decalge, 0.2f, 0.0f));
        glUniformMatrix4fv (uniform_modelA,  1, GL_FALSE, glm::value_ptr(modelMatrix)); 
        modelMatrix          =  translate(modelMatrix, glm::vec3(decalge, -0.2f, 0.0f));
        glDrawArrays(GL_TRIANGLES, 0, m21);

        glBindVertexArray(VertexArrayIDA3);
        lightpos = glm::vec3 ((-1+((vols30[vols30.size()-2]*2.0)/N))/1.75,0,1.2) ; 
        glUniform3fv       (uniform_lightpos , 1 , glm::value_ptr(lightpos)) ; 
        modelMatrix          =  translate(modelMatrix, glm::vec3(decalge, 0.4f, 0.0f));
        glUniformMatrix4fv (uniform_modelA,  1, GL_FALSE, glm::value_ptr(modelMatrix)); 
        modelMatrix          =  translate(modelMatrix, glm::vec3(decalge, -0.4f, 0.0f));
        glDrawArrays(GL_TRIANGLES, 0, m31);

        glBindVertexArray(VertexArrayIDA4);
        lightpos = glm::vec3 ((-1+((vols40[vols40.size()-2]*2.0)/N))/1.75,0,1.2) ; 
        glUniform3fv       (uniform_lightpos , 1 , glm::value_ptr(lightpos)) ; 
        modelMatrix          =  translate(modelMatrix, glm::vec3(decalge, 0.6f, 0.0f));
        glUniformMatrix4fv (uniform_modelA,  1, GL_FALSE, glm::value_ptr(modelMatrix)); 
        modelMatrix          =  translate(modelMatrix, glm::vec3(decalge, -0.6f, 0.0f));
        glDrawArrays(GL_TRIANGLES, 0, m41);
   
    }

  //////////////////////////////////////////////////////////////////////////////////////
 /////////*********   GERER LES AFFICHAGES POUR LES BOARDS  ***** /////////////////////
////////////////////////////////////////////////////////////////////////////////////// 
   
    glUseProgram(ProgramB);
    
    glUniformMatrix4fv (uniform_modelB,  1, GL_FALSE, glm::value_ptr(modelMatrix));   
    glUniformMatrix4fv (uniform_viewB,  1, GL_FALSE, glm::value_ptr(viewMatrix));
    glUniformMatrix4fv (uniform_projB,  1, GL_FALSE, glm::value_ptr(projectionMatrix));
     
 ////////////**** AFFICHER LA COURBES DE AAPLE ****/////////////////////// 
     if (glfwGetKey(window, GLFW_KEY_P ) == GLFW_PRESS){
        glBindVertexArray(VertexArrayIDB1);
        glDrawArrays(GL_TRIANGLES, 0, m12);
     }

////////////**** AFFICHER LA COURBES DE GOOGLE ****///////////////////// 
     else if (glfwGetKey(window, GLFW_KEY_G ) == GLFW_PRESS){
          glBindVertexArray(VertexArrayIDB2);
          glDrawArrays(GL_TRIANGLES, 0, m22);
     }

//////////***** AFFICHER LA COURBES DE AMAZON ****////////////////////
    else if (glfwGetKey(window, GLFW_KEY_Q ) == GLFW_PRESS){
         glBindVertexArray(VertexArrayIDB3);
         glDrawArrays(GL_TRIANGLES, 0, m32);
    } 

//////////***** AFFICHER LA COURBES DE MICROSOFT ****////////////////////
    else if (glfwGetKey(window, GLFW_KEY_F ) == GLFW_PRESS){
         glBindVertexArray(VertexArrayIDB4);
         glDrawArrays(GL_TRIANGLES, 0, m42);
    }

/////////**** AFFICHER LES QUATRES COURBES AU MEME TEMPS ****//////////// 
else{
    
    glBindVertexArray(VertexArrayIDB1);
    glDrawArrays(GL_TRIANGLES, 0, m12); // Starting from vertex 0 .. all the buffer
    
    /*pour afficher les courbes cote a cote , on fait un translation pour modele matrix 
    et on l'envoie dans un UNIFORME ensuite on fait une translation inverse pour revenir 
    à la position initiale .
    */
    glBindVertexArray(VertexArrayIDB2);
    modelMatrix          =  translate(modelMatrix, glm::vec3(decalge, 0.2f, 0.0f));
    glUniformMatrix4fv (uniform_modelB,  1, GL_FALSE, glm::value_ptr(modelMatrix)); 
    modelMatrix          =  translate(modelMatrix, glm::vec3(decalge, -0.2f, 0.0f));
    glDrawArrays(GL_TRIANGLES, 0, m22);
      
    glBindVertexArray(VertexArrayIDB3);
    modelMatrix          =  translate(modelMatrix, glm::vec3(decalge, 0.4f, 0.0f));
    glUniformMatrix4fv (uniform_modelB,  1, GL_FALSE, glm::value_ptr(modelMatrix)); 
    modelMatrix          =  translate(modelMatrix, glm::vec3(decalge, -0.4f, 0.0f));
    glDrawArrays(GL_TRIANGLES, 0, m32);

    glBindVertexArray(VertexArrayIDB4);
    modelMatrix          =  translate(modelMatrix, glm::vec3(decalge, 0.6f, 0.0f));
    glUniformMatrix4fv (uniform_modelB,  1, GL_FALSE, glm::value_ptr(modelMatrix)); 
    modelMatrix          =  translate(modelMatrix, glm::vec3(decalge, -0.6f, 0.0f));
    glDrawArrays(GL_TRIANGLES, 0, m42);
}
  //  on desactive le VAO a la fin du dessin
    glBindVertexArray (0);

    // on desactive les shaders
    glUseProgram(0);/**/

    // Swap buffers
    glfwSwapBuffers(window);
    glfwPollEvents();
  ///////////////////////////////////////////////////////////////////////////
 /////////////**        Gerer la Rotation       **//////////////////////////
///////////////////////////////////////////////////////////////////////////   
    float angleZ = 0;
    if (glfwGetKey(window, GLFW_KEY_E ) == GLFW_PRESS){
      angle=angle+0.05;
    }  
     
     if (glfwGetKey(window, GLFW_KEY_D ) == GLFW_PRESS){
       
       angle=angle-0.05;
    }
 
  ///////////////////////////////////////////////////////////////////////////
 //////////////////   LISSAGE DES VALEURS       ////////////////////////////
///////////////////////////////////////////////////////////////////////////

  //////////////*****  diminution du lissage   *****////////////////////////

  if ( glfwGetKey(window, GLFW_KEY_LEFT ) == GLFW_PRESS && SMOOTHING_VALS>1 ){
     
   SMOOTHING_VALS -- ; 
 
   kernel0 = computeGaussianKernel(SMOOTHING_VALS);
   kernel01 = computeGaussianKernel(SMOOTHING_VOLS);
  
   vector<float> vals10_smooth =  smoothData(vals10, kernel0)  ;   
   vector<float> vols10_smooth  = smoothData(vols10, kernel01)  ;
   
   vector<float> vals20_smooth =  smoothData(vals20, kernel0)  ;   
   vector<float> vols20_smooth  = smoothData(vols20, kernel01)  ;

   vector<float> vals30_smooth =  smoothData(vals30, kernel0)  ;   
   vector<float> vols30_smooth  = smoothData(vols30, kernel01)  ;
 
   vector<float> vals40_smooth =  smoothData(vals40, kernel0)  ;   
   vector<float> vols40_smooth  = smoothData(vols40, kernel01)  ;
   
   m11 = loadModelA(vols10_smooth, vals10_smooth, VertexArrayIDA1);
   m12 = loadModelB(vols10_smooth, vals10_smooth, VertexArrayIDB1);
   
   m21=loadModelA(vols20_smooth, vals20_smooth, VertexArrayIDA2);
   m22= loadModelB(vols20_smooth, vals20_smooth, VertexArrayIDB2);
        
   m31=loadModelA(vols30_smooth, vals30_smooth, VertexArrayIDA3);
   m32= loadModelB(vols30_smooth, vals30_smooth, VertexArrayIDB3);
        
   m41=loadModelA(vols40_smooth, vals40_smooth, VertexArrayIDA4);
   m42= loadModelB(vols40_smooth, vals40_smooth, VertexArrayIDB4);
   glBindVertexArray (0);
   

 }
 
 //////////////*****  Augmentation du lissage   *****////////////////////////
///////////// ((( j'ai utilisé la touche S car la touche RIGHT de mon PC ne fonctionne pas ))) ////////////

 if ((glfwGetKey(window, GLFW_KEY_RIGHT ) == GLFW_PRESS && SMOOTHING_VALS<64) or 
     (glfwGetKey(window, GLFW_KEY_S ) == GLFW_PRESS && SMOOTHING_VALS<64)){
  
   SMOOTHING_VALS ++ ; 
   
   kernel0 = computeGaussianKernel(SMOOTHING_VALS);
   kernel01 = computeGaussianKernel(SMOOTHING_VOLS);
   
   vector<float> vals10_smooth =  smoothData(vals10, kernel0)  ;   
   vector<float> vols10_smooth  = smoothData(vols10, kernel01)  ;
   
   vector<float> vals20_smooth =  smoothData(vals20, kernel0)  ;   
   vector<float> vols20_smooth  = smoothData(vols20, kernel01)  ;

   vector<float> vals30_smooth =  smoothData(vals30, kernel0)  ;   
   vector<float> vols30_smooth  = smoothData(vols30, kernel01)  ;

   vector<float> vals40_smooth =  smoothData(vals40, kernel0)  ;   
   vector<float> vols40_smooth  = smoothData(vols40, kernel01)  ;
   
   m11 = loadModelA(vols10_smooth, vals10_smooth, VertexArrayIDA1);
   m12 = loadModelB(vols10_smooth, vals10_smooth, VertexArrayIDB1);
   
   m21=loadModelA(vols20_smooth, vals20_smooth, VertexArrayIDA2);
   m22= loadModelB(vols20_smooth, vals20_smooth, VertexArrayIDB2);
        
   m31=loadModelA(vols30_smooth, vals30_smooth, VertexArrayIDA3);
   m32= loadModelB(vols30_smooth, vals30_smooth, VertexArrayIDB3);
        
   m41=loadModelA(vols40_smooth, vals40_smooth, VertexArrayIDA4);
   m42= loadModelB(vols40_smooth, vals40_smooth, VertexArrayIDB4);
        glBindVertexArray (0);
       glBindVertexArray (0);
}
 

  ///////////////////////////////////////////////////////////////////////////
 //////////////////   LISSAGE DES VALEURS       ////////////////////////////
///////////////////////////////////////////////////////////////////////////

  //////////////*****  diminution du lissage   *****////////////////////////
 if( glfwGetKey(window, GLFW_KEY_DOWN ) == GLFW_PRESS && SMOOTHING_VOLS>1){
     
   SMOOTHING_VOLS -- ; 
   
   kernel0 = computeGaussianKernel(SMOOTHING_VALS);
   kernel01 = computeGaussianKernel(SMOOTHING_VOLS);
  
   vector<float> vals10_smooth =  smoothData(vals10, kernel0)  ;   
   vector<float> vols10_smooth  = smoothData(vols10, kernel01)  ;
   
   vector<float> vals20_smooth =  smoothData(vals20, kernel0)  ;   
   vector<float> vols20_smooth  = smoothData(vols20, kernel01)  ;

   vector<float> vals30_smooth =  smoothData(vals30, kernel0)  ;   
   vector<float> vols30_smooth  = smoothData(vols30, kernel01)  ;

   vector<float> vals40_smooth =  smoothData(vals40, kernel0)  ;   
   vector<float> vols40_smooth  = smoothData(vols40, kernel01)  ;
   
   m11 = loadModelA(vols10_smooth, vals10_smooth, VertexArrayIDA1);
   m12 = loadModelB(vols10_smooth, vals10_smooth, VertexArrayIDB1);
   
   m21=loadModelA(vols20_smooth, vals20_smooth, VertexArrayIDA2);
   m22= loadModelB(vols20_smooth, vals20_smooth, VertexArrayIDB2);
        
   m31=loadModelA(vols30_smooth, vals30_smooth, VertexArrayIDA3);
   m32= loadModelB(vols30_smooth, vals30_smooth, VertexArrayIDB3);
        
   m41=loadModelA(vols40_smooth, vals40_smooth, VertexArrayIDA4);
   m42= loadModelB(vols40_smooth, vals40_smooth, VertexArrayIDB4);
   glBindVertexArray (0);
 } 

 //////////////*****  Augmentation du lissage   *****////////////////////////

if ( glfwGetKey(window, GLFW_KEY_UP ) == GLFW_PRESS && SMOOTHING_VOLS<64){
   
   SMOOTHING_VOLS ++ ; 
   
   kernel0 = computeGaussianKernel(SMOOTHING_VALS);
   kernel01 = computeGaussianKernel(SMOOTHING_VOLS);
   
   vector<float> vals10_smooth =  smoothData(vals10, kernel0)  ;   
   vector<float> vols10_smooth  = smoothData(vols10, kernel01)  ;
   
   vector<float> vals20_smooth =  smoothData(vals20, kernel0)  ;   
   vector<float> vols20_smooth  = smoothData(vols20, kernel01)  ;

   vector<float> vals30_smooth =  smoothData(vals30, kernel0)  ;   
   vector<float> vols30_smooth  = smoothData(vols30, kernel01)  ;

   vector<float> vals40_smooth =  smoothData(vals40, kernel0)  ;   
   vector<float> vols40_smooth  = smoothData(vols40, kernel01)  ;
   
   m11 = loadModelA(vols10_smooth, vals10_smooth, VertexArrayIDA1);
   m12 = loadModelB(vols10_smooth, vals10_smooth, VertexArrayIDB1);
   
   m21=loadModelA(vols20_smooth, vals20_smooth, VertexArrayIDA2);
   m22= loadModelB(vols20_smooth, vals20_smooth, VertexArrayIDB2);
        
   m31=loadModelA(vols30_smooth, vals30_smooth, VertexArrayIDA3);
   m32= loadModelB(vols30_smooth, vals30_smooth, VertexArrayIDB3);
        
   m41=loadModelA(vols40_smooth, vals40_smooth, VertexArrayIDA4);
   m42= loadModelB(vols40_smooth, vals40_smooth, VertexArrayIDB4);
   glBindVertexArray (0);
 }

  } // Vérifie si on a appuyé sur la touche échap (ESC) ou si la fenêtre a été fermée
  while( glfwGetKey(window, GLFW_KEY_ESCAPE ) != GLFW_PRESS && glfwWindowShouldClose(window) == 0 );

/////////////////////////////////////////////////
////////   FIN     *-* /////////////////////////
///////////////////////////////////////////////
}
