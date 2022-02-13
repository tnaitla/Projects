

package Model;

public class Butin {
	public static final int Bijoux = 0 ; 
	public static final int Bourse = 1 ; 
	public static final int Margot= 2 ;  
	private int type ; 
	private int Valeur ; 
	
	
	
	public Butin ( int t){
		this.type = t ; 
		if(t==0){
			this.Valeur = 750 ; 
			
		}else if (t==1){
			this.Valeur = (int)(Math.random()*501);
		}
		
	}
	
	public int getType () {
		return this.type ; 
	}
	
	public int getValeur() {
		return this.Valeur ; 
	}


}
