package rl;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Random;

public class Qlearn {
	public double epsilon = 0.2; // parametre epsilon pour \epsilon-greedy
	public double alpha = 0.2; // taux d'apprentissage
	public double gamma = 0.9; // parametre gamma des eq. de Bellman/

	// valeur aleatoire 
	public Random RDM;
	private long seed = 124;
	// Suggestions
	public int actions[];
	public Hashtable< Tuple<Integer,Integer>, Double> q;


	// Constructeurs
	public Qlearn(int[] actions) {
		this.actions = actions;
		q = new Hashtable< Tuple<Integer,Integer>, Double>();
		this.RDM = new Random(this.seed);
	}

	public Qlearn(int[] actions, double epsilon, double alpha, double gamma) {
		this.actions = actions;
		this.epsilon = epsilon;
		this.alpha = alpha;
		this.gamma = gamma;
		q = new Hashtable< Tuple<Integer,Integer>, Double>();
		this.RDM = new Random(this.seed);
	}

	/*
	 * la fonction argmax qui permet de calculer la meilleurs action depuis l'etat S 
	 */
	public int argmax (Hashtable< Tuple<Integer,Integer>, Double> h, int State){
		double max = -1 ; 
		int bestAction = -5 ; 
		for(int i = 0 ; i <= 7 ; i++){
			Tuple<Integer,Integer> temp = new Tuple(State , i );
			double proba = h.get(temp);
			if(max <= proba){
				max = proba ; 
				bestAction = i ; 
			}
		}
		return bestAction ; 

	}
	/*une fonction d'initialisation qui nous permet d'initialiser les Q(S,A) 
	 * pour les etats S qu'on a pas encore rencontrer dans l'environnement 
	 */
	public void initialize (int State){
		for(int i = 0 ; i <= 7 ; i++){
			Tuple<Integer,Integer> temp = new Tuple(State , i );
			if(!this.q.contains(temp)){
				this.q.put(temp, (double) 0 );
			}
		}
	}


	public void Qlearning (int prevstate , int newstate , int action , double reward ){
		////** initialisation de Q(S) **/////
		Tuple tuple1 = new Tuple(prevstate,action);
		if(!this.q.contains(tuple1)){
			initialize (prevstate);
		}
		////** initilaisation de Q(Sprime) **//////
		Tuple tuple2 = new Tuple(newstate,1);
		if(!this.q.contains(tuple2)){
			initialize (newstate);
		}

		double Qt = this.q.get(tuple1);
		////recherche de la meilleure action depuis l'etat Sprime
		int action2 = argmax (this.q , newstate);

		tuple2 = new Tuple(newstate,action2);
		double Qt1= this.q.get(tuple2);
		/// Mise A jour ///
		Qt = Qt + this.alpha * (reward + this.gamma*Qt1 - Qt) ; 		 
		this.q.put(tuple1,Qt);
	}


	public void Sarsa (int prevstate , int newstate , int action , double reward ){
		////** initialisation de Q(S) **/////
		Tuple tuple1 = new Tuple(prevstate,action);
		if(!this.q.contains(tuple1)){
			initialize (prevstate);
		}else{
			System.out.println(prevstate);
		}
		////** initilaisation de Q(Sprime) **//////
		Tuple tuple2 = new Tuple(newstate,1);
		if(!this.q.contains(tuple2)){
			initialize (newstate);
		}
		double Qt = this.q.get(tuple1);

		//*/// choix de l'action depuis l'etat Sprime
		int action2 = (int)( this.RDM.nextDouble() * 8);
		tuple2 = new Tuple(newstate,action2);
		double Qt1= this.q.get(tuple2);
		/// Mise A jour ///
		Qt = Qt + this.alpha * (reward + this.gamma*Qt1 - Qt) ; 
		this.q.replace(tuple1,Qt);

	}






}
