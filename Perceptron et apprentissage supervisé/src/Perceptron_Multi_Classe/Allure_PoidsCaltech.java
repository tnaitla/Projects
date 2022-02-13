package Perceptron_Multi_Classe;

import java.io.FileNotFoundException;
import java.io.IOException;
 
import caltech.CalTech101;

public class Allure_PoidsCaltech {
	// le nombre de Classe 
	public static final int K= 101 ; 
	/// le nombre d'image de l'ensemble d'apprentissage
	public static final int N= 4100 ; 
	/// le nombre d'image de l'ensemble d'apprentissage
		public static final int T= 2307  ; 
	/// la taille de l'image representant l'allure des poids du perceptron 
	   public static final int J = 28 ; 
		
	public static void main(String[] args) throws FileNotFoundException, IOException {
		System.out.println("Test load images");
        String path="/home/tp-home010/tnaitla/Bureau/"; //  indiquer le path correct
        String CaltechCheminFichier=path+"caltech101.mat";
		CalTech101 CT = new CalTech101(CaltechCheminFichier);
         ////////////////////////////////
		///////// charge une image /////
	   ////////////////////////////////	
		 final int SIZEW=   CT.getTrainImage(0).length +1; 
		double[] im = CT.getTrainImage(0); // charge l'image numero 0
		for(int i=0; i<28; i++) {          // affiche cette image, pour voir
			for(int j=0; j<28; j++) {
				System.out.print((int) im[i+j*28] + " ");
			}
			System.out.println();
		}
		int lab = CT.getTrainLabel(0);
		//System.out.println("Label "+lab); // print le label de cette image la
 
 
		 ///////////////////////////////
		/// Donnees d'apprentissage ///
	   ///////////////////////////////
		double[][] trainIm = new double[N][SIZEW];
		double[][] trainImbis = new double[N][SIZEW];
		//System.out.println("tes conard " +trainIm[0].length);
		double[][] trainRefs = new double[N][K];// reference, adaptation au perceptron multiclasses 
		  //////////////////////////////////////////////////////////
		 ////initialisation du tableau des etiquettes de données///
	    //////////////////////////////////////////////////////////
		for(int i=0; i<N; i++) {
			trainIm[i][0]=1;
			trainImbis[i] = CT.getTrainImage(i);
			for(int x=1;x<SIZEW;x++){
				trainIm[i][x]=trainImbis[i][x-1];
			}
			for(int j = 0 ; j < K ; j++){
				if(j==CT.getTrainLabel(i)){
					trainRefs[i][j-1]=1;
				}else{
					trainRefs[i][j]=0;
				}
			}
		}
		 ///////////////////////////////
		/// Donnees d'apprentissage ///
	   ///////////////////////////////
		double[][] testIm = new double[T][28*28+1];
		double[][] testRefs = new double[T][K];// reference, adaptation au perceptron multiclasses 
		  //////////////////////////////////////////////////////////
		 ////initialisation du tableau des etiquettes de données///
	    //////////////////////////////////////////////////////////
		for(int i=0; i<T; i++) {
			testIm[i] = CT.getTestImage(i);
			for(int j = 0 ; j < K ; j++){
				if(j==CT.getTestLabel(i)){
					testRefs[i][j-1]=1;
				}else{
					testRefs[i][j]=0;
				}
			}
		}
		// Pour les donnees de test, utiliser  CT.getTestLabel  et  CT.getTestImage
		
		 /////////////////////////////////////////////////////////////////////////////////////
		/////////// on récupere les poids de chaque perceptro en appellant la  //////////////
	   ///////////         la fonction Perceptron_multi                       //////////////
	  /////////////////////////////////////////////////////////////////////////////////////
	     double[][]w =  Algorithme_Code.Preceptron_Multi (trainIm , trainRefs ,0.01,2) ;   
	     
	     /////////////////////////////////////////////////////////////////////////////////
		    ////////  convertir les poids d'un perceptron en une image 28x28 ////////////////
		   /////////////////////////////////////////////////////////////////////////////////
		     double[][][]image = new double[K][J][J];
		     for(int m = 0;m<K;m++){
		    	 int t= 0 ; 
		    	 for(int i = 0 ; i < J ; i++) {
		    		for (int j = 0 ; j < J ; j++){
		    			image[m][i][j]= w[m][j+1+t];
		    			//System.out.println(j+t);
		
		    		 }
		    		t=t+J;
		    	 }
		     }
		     
		
		     
		     //////////////////////////////////////////////////////////////////////////////////////
		    ///////////////// afficher l'allure des poids de chaque perceptron ///////////////////
		   //////////////////////////////////////////////////////////////////////////////////////
		     
		     for(int i = 0 ; i < K ; i++){
		    	 System.out.println("");
		    	 System.out.println("l'allure des poids du perceptron de la classe "+i +" est "); 
		    	 Allure_PoidsChiffre.afficheBinaire( Allure_PoidsChiffre.Binariser (image[i] ,-0.002));
		     }
	     
	}

}
