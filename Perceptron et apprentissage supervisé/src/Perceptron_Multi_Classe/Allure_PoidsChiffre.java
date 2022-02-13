package Perceptron_Multi_Classe;

import mnisttools.MnistReader;

public class Allure_PoidsChiffre {
	
	// la taille de l'image de l'allure
		public static final int J= 28 ; 
		
	/*la fonction qui binarise une image par rapport à un seuil passé en parametre  
	*/
	public static double [][] Binariser (double[][]img, double seuil){
		//int[][]res = img ; ;
		for ( int i = 0 ; i < img.length; i++){
		    for(int j =0 ; j < img[0].length;j++){
		    	if (img[i][j]<seuil){
		    		 img[i][j]=0;
		    	}else{
		    		img[i][j]=1;
		    	}
		    }
	}
		return img ; 
	}	
	
	/*fonction d'affichage d'une image binaire.
	 */
	public static void afficheBinaire (double[][]img ){
		   for ( int i = 0 ; i < img.length; i++){
			    for(int j =0 ; j < img[0].length;j++){
			    	 if(img[i][j]==0){
			    		 System.out.print(" ");
			         }else{
			        	 System.out.print("@");
			         }
			    }
			    System.out.println(" ");
		   }
	}

	/* Les donnees */
	public static String path="/home/tp-home010/tnaitla/Bureau/";
	public static String labelDB=path+"train-labels-idx1-ubyte";
	public static String imageDB=path+"train-images-idx3-ubyte";
 
	/* Parametres */
	// les N premiers exemples pour l'apprentissage
	public static final int N = 1000; 
	// les T derniers exemples  pour l'evaluation
	public static final int T = 500; 
	// le nombre de classes
	public static final int K= 10 ; 
	// Nombre d'epoque max
	public final static int EPOCHMAX=40;
	// Classe positive (le reste sera considere comme des ex. negatifs):
	public static int  classe = 5 ;
	
 
 
 
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.err.println("# Load the database !");
		/* Lecteur d'image */ 
		MnistReader db = new MnistReader(labelDB, imageDB);
		/* Taille des images et donc de l'espace de representation */
		final int SIZEW = ImageConverter.image2VecteurReel_withB(db.getImage(1)).length;
 
         //////////////////////////
		// Creation des donnees //
	   //////////////////////////
		
		System.err.println("# Build train for "+ classe);
		double[][] trainData = new double[N][SIZEW];
		double[][]VecRefs=new double[N][K];
		int cptPos=0;
		int cpt=0;
		System.out.println(SIZEW);
		 ///////////////////////////////
		/// Donnees d'apprentissage ///
	   ///////////////////////////////
		for (int i = 1; i <= N; i++) {
			cpt++;
			trainData[i-1]=ImageConverter.image2VecteurReel_withB(db.getImage(i));
			int label = db.getLabel(i);
			//////////////////////////////////////////////////////////
		   ////initialisation du tableau des etiquettes de données///
		  //////////////////////////////////////////////////////////
			for (int j = 0 ; j < VecRefs[i-1].length;j++){
				if(j==label){
				  VecRefs[i-1][j]=1;
				}else{
				VecRefs[i-1][j]=0;
				}
			}
		}	
 
		System.err.println("Train set with "+cptPos*100.0/cpt+ " % positives /"+cpt);
         ///////////////////////////
		////* Donnees de test *////
	   ///////////////////////////
		System.err.println("# Build test for "+ classe);
		cptPos=0;
		cpt=0;
		final int TOTAL = db.getTotalImages();
		if (N+T >= TOTAL){
			System.out.println("N+T > Total");
			throw new RuntimeException();
		}
		double[][] testData = new double[T][SIZEW];
		double[][] VecRefstest = new double[T][K];
		for (int i = 0; i < T; i++) {
			cpt++;
			testData[i]=ImageConverter.image2VecteurReel_withB(db.getImage(TOTAL-i));
			int label = db.getLabel(TOTAL-i);
                ///////////////////////////////////////////////////////////////////
               ////initialisation du tableau des etiquettes de données de test ///
              ///////////////////////////////////////////////////////////////////
			for (int j = 0 ; j < VecRefstest[i].length;j++){
			if(j==label){
			  VecRefstest[i][j]=1;
			}else{
			VecRefstest[i][j]=0;
			}
		}
			
		}
		System.err.println("Test set with "+cptPos*100.0/cpt+ " % positives /"+cpt);
		 
		 /////////////////////////////////////////////////////////////////////////////////////
		/////////// on récupere les poids de chaque perceptro en appellant la  //////////////
	   ///////////         la fonction Perceptron_multi                       //////////////
	  /////////////////////////////////////////////////////////////////////////////////////
	     double[][]w =  Algorithme_Code.Preceptron_Multi (trainData , VecRefs ,0.1,2) ;
	     System.out.println(w[0].length);
	     /////////////////////////////////////////////////////////////////////////////////
	    ////////  convertir les poids d'un perceptron en une image 28x28 ////////////////
	   /////////////////////////////////////////////////////////////////////////////////
	     double[][][]image = new double[K][J][J];
	     for(int m = 0;m<K;m++){
	    	 int t= 0 ; 
	    	 for(int i = 0 ; i < J ; i++) {
	    		for (int j = 0 ; j < J ; j++){
	    			image[m][i][j]= w[m][j+t+1];
	
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
	    	 afficheBinaire( Binariser (image[i] ,0));
	     }
	}
}
