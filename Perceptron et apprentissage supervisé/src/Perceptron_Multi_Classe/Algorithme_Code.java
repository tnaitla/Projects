package Perceptron_Multi_Classe;
import mnisttools.MnistReader;

public class Algorithme_Code {
	

/** une fonction qui calcule le produit scalaire entre deux vecteur.  
 * @param double[]a : c'est un tableau de double de dim=1 qui represente le premier vecteur
 * @param double[]b : c'est un tableau de double de dim=1 qui represente le deuxieme vecteur
 * return double : le produit scalaire entre les deux vecteurs
 **/
	public static double Produit_Scal (double[]a , double[]b){ 
	    double scal = 0 ;  
	    	if (a.length!=b.length){
	    		System.out.println("erreur........................");
	    	}else{
	    		for (int i =0;i<a.length;i++){
	    			 scal = scal + a[i]*b[i];
	    		}
	    	}
	    return scal ;
	}

	
	
/** une fonction qui donne l'indice de la plus grande valeur d'un tabeau .  
* @param double[]a : c'est un tableau de double de dim=1 
**/
	public static int indiceMax (double[]a){
        double max=0 ;
        int indice=0 ; 
			for(int i=0 ; i<a.length;i++){
				if(max<a[i]){
					indice = i;
					max=a[i];
				}
			}
		return indice ; 
	}

	
	
	
/** une fonction qui donne la somme des probabilités d'une seul donnée en passant l'ensemble des perceptron    
* @param double[]D : c'est un tableau de double de dim=1 qui represente l'ensemble de coordonnées de la donnée
* @param double[][]W : c'est un tableau de double de dim=2 qui represente les poids de chaque perceptron
* return double : la somme des probabilité
**/
	public static double sommeProba (double[]D ,double[][]W){
		double Sproba = 0 ; 
			for ( int i =0 ; i<W.length;i++){
				Sproba= Sproba +  Math.exp(Produit_Scal(D,W[i]));
			}
		return Sproba ; 
	}
	

	
/** une fonction qui donne un tableau de probabilité d'appartenir à une classe pour une donnée   
* @param double[]D : c'est un tableau de double de dim=1 qui represente l'ensemble de coordonnées de la donnée
* @param double[][]W : c'est un tableau de double de dim=2 qui represente les poids de chaque perceptron
* @param double SommePb :  la somme des probabilités de cette donnée en passant par l'ensemble des perceptron 
* return double[] : un tableau de taille egal au nombre de classe, ou chaque case represente la probabilité d'appartenir à une classe  
**/
	public static double[] tableau_proba (	double[]Don , double SommePb,double[][]W){
		double Proba= 0;
		double []TabProba = new double[W.length]; 
			for ( int i =0 ; i<W.length;i++){
				Proba = Math.exp(Produit_Scal(Don,W[i]));
				TabProba[i]=Proba/SommePb;
			}
		return TabProba ;
		 
	}
	
	
	
	
/** une fonction qui initialise les poids de chaque perceptron avec 1 sur le premier poids (biais)
* @param int nbClase : le nombre de classe 
* @param int dim_donne : la dimmension de la donées(nombre de coordonées)
* return double[][]: le tableau des poids de chaque perceptron  
**/
	public static double[][] initialise_poids(int nbClasse , int dim_donne){
		 	double res[][]= new double[nbClasse][dim_donne];
			for(int i = 0 ;i<res.length ; i++){
		 		for(int j =0;j<res[i].length;j++){
		 			if(j==0){
		 				res[i][j]=1;
		 			}
		 			else{
		 				res[i][j]=1/10000;
		 			}
		 		}
		 	}
		return res;
	}

	
/** une fonction qui mis a jour les poids de chaque de perceptron 
*@param double[][]data : le tableau qui represente l'ensemble des données 
*@param double[][]dataRffs : le tableau qui represente les étiquettes pour l'ensemble des donnée
*@param double taux_appre : le taux d'apprentissage
**@param int nbepoque : le nombre d'epoque
* return double[][]: le tableau des poids de l'ensemble perceptron mis à jour 
**/
	public static double[][] Preceptron_Multi (double[][]data , double[][]dataRefs , double taux_appre,int nbepoque){
		double[][]w = initialise_poids(dataRefs[0].length,data[0].length);
        double MAJ = 0 ;
		double Sproba = 0 ;
		double[]TabProba ;
		for(int t = 0 ; t < nbepoque;t++){
			for(int m = 0 ; m<data.length;m++){
					//on calcule la somme des probabilité pour la donnée numero m 
					Sproba = sommeProba (data[m] , w);
					//on cherche le tableau de probabilité pour m_ieme donnée
					TabProba = tableau_proba (data[m] , Sproba  ,w);
					for(int p = 0 ; p < w.length ; p++){
							MAJ = taux_appre*(dataRefs[m][p]-TabProba[p]);
							for(int i = 0 ; i <data[m].length;i++){
									w[p][i]	=w[p][i]+MAJ*data[m][i];
							}
					}
			}
	}
        return w ; 
	}
					
				
}
