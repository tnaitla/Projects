package Perceptron_Multi_Classe;

import java.io.FileNotFoundException;
import java.io.IOException;

import caltech.CalTech101;

public class Application_Caltech101 {
	
	// le nombre de Classe 
		public static final int K= 101 ; 
		/// le nombre d'image de l'ensemble d'apprentissage
		public static final int N= 4100 ; 
		/// le nombre d'image de l'ensemble d'apprentissage
			public static final int T= 2200 ; 
		/// le nombre de coordonnée d'une donnée
			public static final int SIZEW= 28*28+1  ; 
			
			
			
		public static void main(String[] args) throws FileNotFoundException, IOException {
			System.out.println("Test load images");
	                String path="/home/tp-home010/tnaitla/Bureau/"; //  indiquer le path correct
	                String CaltechCheminFichier=path+"caltech101.mat";
			CalTech101 CT = new CalTech101(CaltechCheminFichier);
	         ////////////////////////////////
			///////// charge une image /////
		   ////////////////////////////////	
			double[] im = CT.getTrainImage(0); // charge l'image numero 0
			for(int i=0; i<28; i++) {          // affiche cette image, pour voir
				for(int j=0; j<28; j++) {
					System.out.print((int) im[i+j*28] + " ");
				}
				System.out.println();
			}
			int lab = CT.getTrainLabel(0);
			System.out.println("Label "+lab); // print le label de cette image la
	 
	 
			 ///////////////////////////////
			/// Donnees d'apprentissage ///
		   ///////////////////////////////
			double[][] trainIm = new double[N][SIZEW];
			double[][] AvecbiaistrainIM = new double[N][SIZEW];
			double[][] trainRefs = new double[N][K];// reference, adaptation au perceptron multiclasses 
			  //////////////////////////////////////////////////////////
			 ////initialisation du tableau des etiquettes de données///
		    //////////////////////////////////////////////////////////
			for(int i=0; i<N; i++) {
				trainIm[i] = CT.getTrainImage(i);
				int label = CT.getTrainLabel(i);
				for(int j =  0; j < K ; j++){
					if(j+1==label){
						trainRefs[i][j]=1;
						System.out.println(label);
					}else{
						trainRefs[i][j]=0;
					}
				}
			}
			for(int i=0; i<N; i++) {
				for(int j =  0; j < SIZEW ; j++){
					if(j==0){
						AvecbiaistrainIM[i][j]=1;
					}else{
						AvecbiaistrainIM[i][j]=trainIm[i][j-1];
					}
				}
			}
			 ///////////////////////////////
			/// Donnees d'apprentissage ///
		   ///////////////////////////////
			double[][] testIm = new double[T][SIZEW];
			double[][] AvecbiaistestIm = new double[T][SIZEW];
			double[][] testRefs = new double[T][K];// reference, adaptation au perceptron multiclasses 
			  //////////////////////////////////////////////////////////
			 ////initialisation du tableau des etiquettes de données///
		    //////////////////////////////////////////////////////////
			for(int i=0; i<T; i++) {
				testIm[i] = CT.getTestImage(i);
				int label = CT.getTestLabel(i);
				for(int j = 0 ; j < K ; j++){
					if(j+1==label){
						testRefs[i][j]=1;
					}else{
						testRefs[i][j]=0;
					}
				}
			}
			for(int i=0; i<T; i++) {
				for(int j =  0; j < SIZEW ; j++){
					if(j==0){
						AvecbiaistestIm[i][j]=1;
					}else{
						AvecbiaistestIm[i][j]=testIm[i][j-1];
					}
				}
			}
			// Pour les donnees de test, utiliser  CT.getTestLabel  et  CT.getTestImage
			
			 /////////////////////////////////////////////////////////////////////////////////////
			/////////// on récupere les poids de chaque perceptro en appellant la  //////////////
		   ///////////         la fonction Perceptron_multi                       //////////////
		  /////////////////////////////////////////////////////////////////////////////////////
			double[][]w =  Algorithme_Code.Preceptron_Multi (AvecbiaistrainIM, trainRefs ,0.007,10) ; 
		    // System.out.println(w[0].length);
		     System.out.println(AvecbiaistrainIM[0].length);
		   
		     
		     ////////////////////////////////////////////////////////////////////////////////////////
			 /////////////// on fait le test sur l'ensemble de test < testData >  ///////////////////
			///////////////    et afficher le nombre de donnée mal Classées       //////////////////
		   ////////////////////////////////////////////////////////////////////////////////////////  
			int nberr = 0 ;
			double scal = 0;
			for (int i = 0 ; i < testIm.length;i++){
				/// cherche la classe de i-emme donnée 
				int Classe = Algorithme_Code.indiceMax(testRefs[i]);
				double[]proba = Algorithme_Code.tableau_proba(AvecbiaistestIm[i],Algorithme_Code.sommeProba (AvecbiaistestIm[i],w),w);
				int classePerceptron =  Algorithme_Code.indiceMax(proba);
				//scal = Algorithme_Code.Produit_Scal(w[Classe], AvecbiaistestIm[i]);
				//System.out.println(Classe+" "+classePerceptron);
				if(Classe!=classePerceptron){
					nberr++;
					//System.out.println("la donnée numéro "+i+" est mal classé et elle represente la classe "+Classe);
				}
			}
			
			int nberr1=0;
			for (int i = 0 ; i < AvecbiaistrainIM.length;i++){
				/// cherche la classe de i-emme donnée 
				int Classe = Algorithme_Code.indiceMax(trainRefs[i]);
				double[]proba = Algorithme_Code.tableau_proba(AvecbiaistrainIM[i],Algorithme_Code.sommeProba (AvecbiaistrainIM[i],w),w);
				int classePerceptron =  Algorithme_Code.indiceMax(proba);
				//scal = Algorithme_Code.Produit_Scal(w[Classe], AvecbiaistestIm[i]);
				if(Classe!=classePerceptron){
					nberr1++;
					//System.out.println("la donnée numéro "+i+" est mal classé et elle represente la classe "+Classe);
				}
			}
			System.out.println("le nombre de données mal Classé est "+nberr+" avec un pourcentage de "+(float)nberr*100/T+"%");
			System.out.println("le nombre de données mal Classé train est "+nberr1+" avec un pourcentage de "+(float)nberr1*100/N+"%");
		}

}
