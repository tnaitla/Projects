package Model;

import java.util.ArrayList;

import Vue.VueModel;

public class Train {
	// le nombre de train 
	public final static int NB_WAGONS = 4;
	public final static int NB_BANDIT = VueModel.nbPlayers; 
	public final static float NERVOSITE_MARSHALL = 0.3f;
	private  ArrayList<Wagon> CompoTrain ;
	public int PosSherif;
	private ArrayList<Bandit> Joueur;



	public Train (ArrayList<String> namePlayers){
		this.CompoTrain = new ArrayList ();
		for(int i = 0 ; i < NB_WAGONS ; i++){
			this.CompoTrain.add(new Wagon(this, i));
		}
		this.createPlayers(namePlayers);
		this.PosSherif = 0 ; 

	}

	

	/* Une méthode qui crée les joueurs */ 
	public void createPlayers(ArrayList<String> namePlayers){
		ArrayList<Bandit> players = new ArrayList<Bandit>();
		this.Joueur = new ArrayList();
		for (int i = 0; i < namePlayers.size(); i++) {
			if (namePlayers.get(i).isEmpty())
				Joueur.add(new Bandit("Joueur" + (i+1), this));
			else
				Joueur.add(new Bandit (namePlayers.get(i), this));
		}
	
	//	return players;
	}
	
	
	

	public void BougeMarchal (){
		double proba = Math.random();
		if (proba < this.NERVOSITE_MARSHALL){
			int act = 	(int)( Math.random()*2);
			if(act ==0 && this.PosSherif>0){
				this.PosSherif -- ; 
			}else if(act==1 && this.PosSherif <this.NB_WAGONS-1){
				this.PosSherif++ ; 
			}
		}
	}

	public ArrayList<Wagon> getTrain () {
		return this.CompoTrain ; 
	}

	public Wagon getCellule (int i ){
		return this.CompoTrain.get(i);
	}


  public ArrayList<Bandit> getJoueur(){
	  return this.Joueur;
  }













}
