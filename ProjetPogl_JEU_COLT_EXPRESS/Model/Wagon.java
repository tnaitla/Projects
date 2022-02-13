package Model;

import java.util.ArrayList;

public class Wagon {
	public static int MaxButin = 5 ; 
	private Train myTrain ; 
	private ArrayList <Bandit> Wbandit ; 
	private ArrayList <Butin> Richesse ; 
	private String Identite ; 
	private int NumWagon ; 
	private int NbBijoux ; 
	private int NbBourse ; 
	private int NbMargot ; 
	
	public Wagon (Train t , int NumWagon ){
		this.myTrain =t ; 
		this.NumWagon = NumWagon ; 
		this.Richesse = new ArrayList();
		this.Wbandit = new ArrayList<Bandit>();
		InitButin(NumWagon);
		if (NumWagon == 3){
			this.Identite = " du dernier wagon";
		}
		else if(NumWagon == 0){
			this.Identite =" du locomotive";
		}
		else {
			this.Identite= " du wagon numéro " + NumWagon;
		}
	
	}
		

	private void InitButin (int pos){
		if (pos == 0 ){
			this.Richesse.add(new Butin (Butin.Margot));
			this.NbMargot++ ; 
			}
		else {
			for (int i = 0 ; i < MaxButin ; i++){
				int val = (int) (Math.random()*2);
				if (val == 0 ){
					this.Richesse.add(new Butin (Butin.Bijoux));
					this.NbBijoux ++ ; 
				}else {
					this.Richesse.add(new Butin (Butin.Bourse));
					this.NbBourse ++ ; 
				}
				
			}
			
			
		}
		
	}
	
	
	
	
	
	/**** les getters */////
	public int getNumWagon(){
		return this.NumWagon;
	}
	
	public int getNbBijoux(){
		return this.NbBijoux ; 	}
	
	public int getNbBourse (){
		return this.NbBourse;
	}
	public int getNbMargot(){
		return this.NbMargot;
	}
	
	public int addNbBijoux () { 
		return this.NbBijoux++;
	}
	
	public int addNbBourse (){
		return this.NbBourse++;
	}
	public int addNbMargot(){
		return this.NbMargot++;
	}
	public int removeNbBijoux () { 
		if (this.NbBijoux>0)
		return this.NbBijoux--;
		return this.NbBijoux;
	}
	
	public int removeNbBourse (){
		if (this.NbBourse>0)
		return this.NbBourse--;
	 return this.NbBourse;
	}
	public int removeNbMargot(){
		if (this.NbMargot>0)
		return this.NbMargot--;
		return this.NbMargot;
	}
	
	public Train getMyTrain (){
		return this.myTrain;
	}
	
	public ArrayList<Bandit> getBandit (){
	
		return this.Wbandit ; 
	}
	
	public ArrayList<Butin> getRichesse (){
		return this.Richesse ; 
	}
    
	
	public String getIdentite () {
		return this.Identite ; 
	}
}
