package Bellman;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import Jama.*;
import Jama.util.Maths;

public class GridWorld_sql {

	private boolean[][] grid;
	private double[][] reward;
	private int size_x;
	private int size_y;
	int nbStates;
	private double gamma = 0.9;
	private Random rdmnum;
	private long seed = 124;
	private int MAX_REWARD = 20;
	private HashMap<Integer,HashMap<String,Double>> action;
	private HashMap<String,HashMap<Integer,ArrayList<double[]>>> pi;
	private ArrayList<String> dir;

	GridWorld_sql(int size_x, int size_y, int n_rew) {
		this.rdmnum = new Random(this.seed);

		this.grid = new boolean[size_x][size_y];
		this.reward = new double[size_x][size_y];
		this.size_x = size_x;
		this.size_y = size_y;
		this.nbStates = size_x*size_y;

		// list of actions
		this.dir = new ArrayList<String>();
		this.dir.add("left");
		this.dir.add("up");
		this.dir.add("right");
		this.dir.add("down");
		this.dir.add("stay");

		for(int i=0; i<size_x; i++) {
			for(int j=0; j<size_y; j++)
				grid[i][j] = false;
		}

		//this.ChooseRdmState();
		// put n_rew reward randomly
		this.PutRdmReward(n_rew);
		// initialize the random policy
		this.InitRdmPol();
		// initialize the transition matrices
		this.InitTransitionMat();
	}

	// choose a random coordinate in the grid
	private void ChooseRdmState() {
		int i = rdmnum.nextInt(size_x);
		int j = rdmnum.nextInt(size_y);
		grid[i][j] = true;
	}

	// add a reward randomly on the grid
	private void PutRdmReward(int n_rew) {
		int n = 0;
		while(n<n_rew) {
			int i = rdmnum.nextInt(size_x);
			int j = rdmnum.nextInt(size_y);
			if(reward[i][j] == 0) {
				reward[i][j] = rdmnum.nextInt(MAX_REWARD);
				n++;
			}
		}
		/*for(int i = 0 ; i < this.size_x ; i ++ ) {
			 for (int j = 0 ; j < this.size_y ; j++ ){
				 this.reward [i][j] = 0 ; 
				 if( i== 0 && j == 1) {
					 this.reward [i][j] = 10 ; 
				 }
				 if(  i== 0 && j == 3){
					 this.reward [i][j] = 5 ; 
				 }*/


	}


	// return a state given a coordinate on the grid
	private int GridToState(int i, int j) {
		return i+size_x*j;
	}

	// return the coordinate on the gris given the state
	private int[] StateToGrid(int s) {
		int[] index = new int[2];
		index[1] = (int) s/size_x;
		index[0] = s-index[1]*size_x;
		return index;
	}

	// add the possible actions for all states
	void InitRdmPol() {
		action = new HashMap<Integer,HashMap<String,Double>>();
		HashMap<String, Double> proba = new HashMap<String,Double>() ; 
		for(int i = 0 ; i < this.size_x ; i ++ ){
			for(int j = 0  ; j < this.size_y ; j++ ){
				int state =  GridToState(i , j ) ; 
				double norm = 0.0;
				double alea = 0 ;
				for(String A : this.dir){
					alea = this.rdmnum.nextDouble() ;
					norm +=alea ; 
					proba.put(A, alea) ; 
				} 
				/// normalisation 
				for(String A : this.dir){
					double valeur = proba.get(A)/norm ; 
					proba.put(A,valeur) ;
				}
				action.put(state, proba);
			}
		}
	}


	// return the direction (on the grid) for a given action
	private int[] getDirNeighbor(String act) {
		int[] d = new int[2];

		if(act.equals("left")) d[0]=-1;
		if(act.equals("right")) d[0]=1;
		if(act.equals("up")) d[1]=1;
		if(act.equals("down")) d[1]=-1;

		return d;
	}

	// To each state, give the reachable states given an action
	private HashMap<Integer,ArrayList<double[]>> computeTrans(String act) {
		HashMap<Integer,ArrayList<double[]>> trans = new HashMap<Integer,ArrayList<double[]>>();

		for ( int i = 0 ; i < this.nbStates ; i++){
			ArrayList <double[]> proba = new ArrayList<> () ; 
			int [] newCoord  = getDirNeighbor( act) ; 
			int [] actualCoord = StateToGrid (i) ; 
			newCoord[0]= newCoord[0] + actualCoord[0] ;
			newCoord[1]= newCoord[1] + actualCoord[1] ;
			int newState = 0  ; 
			if(newCoord[0] < 0 || newCoord[0]  >= this.size_x || 
					newCoord[1] <0 || newCoord[1] >= this.size_y){
				newState = i ; 
			}else{
				newState = GridToState (newCoord[0],newCoord[1]);
			}
			double []temp = new double [2] ; 
			temp [0] = newState ; 
			temp[1] = 1 ; 
			proba.add(temp);
			trans.put(i, proba) ; 
		}
		return trans;
	}

	// initiate values of P
	private void InitTransitionMat() {
		pi = new HashMap<String,HashMap<Integer,ArrayList<double[]>>>();
		for(String act : this.dir) {
			pi.put(act,computeTrans(act));
		}
	}

	// compute the vector r
	private double[] computeVecR() {
		double[] R = new double[nbStates];
		for(int s=0; s<nbStates; s++) {
			double sum = 0;
			HashMap<String,Double> a = action.get(s);
			// compute the reward obtained fomo state s, by doing all potential action a
			for(String act : this.dir) {
				double prob_action = a.get(act) ; 
				int [] newstate = StateToGrid ((int) pi.get(act).get(s).get(0)[0]); 
				sum += prob_action * this.reward[newstate[0]][newstate[1]];
				//System.out.println( "EtatActual " + s + " " +act +" " + pi.get(act).get(s).get(0)[0]+ "  : " +  sum );
			}
			R[s] = sum;
		}
		return R;
	}

	private double[][] computeMatP() {
		double[][] P = new double[nbStates][nbStates];

		for(int s=0; s<nbStates; s++) {
			// from state s, compute P^{\pi}(s,s')
			for(String act : this.dir) {
				double prob_action = this.action.get(s).get(act) ;
				for (int s2 = 0 ; s2 < nbStates ;s2++){
					if(s2 == pi.get(act).get(s).get(0)[0]){
						P[s][s2] += prob_action ; 
					}
				}
			}
		}
		for (int  i= 0 ; i <  1 ; i ++ ){
			for (int j =0 ; j <  this.nbStates ; j ++ ){
				//System.out.print(P[i][j]+ " ");
			}
			//System.out.println("");
		}
		return P;
	}

	// converting to matrix for the inverse
	private Matrix BuildMatA() {
		double[][] f_A = new double[nbStates][nbStates];
		double[][] P = computeMatP();
		for(int s=0; s<nbStates; s++) {
			f_A[s][s] = 1;
			for(int sp=0; sp<nbStates; sp++) {
				f_A[s][sp] -= this.gamma*P[s][sp];
			}
		}

		Matrix matP = new Matrix(f_A);
		return new Matrix(f_A);
	}

	// converting to matrix for the inverse
	private Matrix BuildMatb() {
		double[] vec_b = computeVecR();
		double[][] b = new double[vec_b.length][1];
		for(int i=0; i<vec_b.length; i++) {
			b[i][0] = vec_b[i];
		}
		return new Matrix(b);
	}

	// solving the linear system
	double[][] SolvingP() {
		Matrix x = BuildMatA().solve(BuildMatb());
		return x.getArray();
	}

	private void showGrid() {
		for(int i=0; i<size_x; i++) {
			for(int j=0; j<size_y; j++)
				System.out.print((this.grid[i][j]?1:0));
			System.out.println();
		}
	}

	void showRewGrid() {
		/*for(int i=0; i<size_x; i++) {
			for(int j=0; j<size_y; j++)
				System.out.print(this.reward[i][j]+" ");
			System.out.println();
		}*/
		for (int s =0 ; s < this.nbStates ; s++){
			int [] temp = StateToGrid (s) ; 
			if(s%5==0) System.out.println();
			//System.out.print( V[i][0]+" ");
			System.out.print(this.reward [temp[0]][temp[1]] + " ");
		}
		System.out.println();
	}

	// improve the policy by looking at the best_a Q(s,a)
	void ImprovePolicy(double[][] V) {
		boolean pol_stable = false ; 
		HashMap<String,Double> temp =new HashMap<String,Double>() ; 
		//action = new HashMap<Integer,HashMap<String,Double>>();
		int State  = 0 ; 
		while(!pol_stable){
			pol_stable = true ; 
			for(int i=0; i<size_x; i++) {
				for(int j=0; j<size_y; j++) {
					State = GridToState(i,j);
					temp = this.action.get(State);
					String bestAction = "";
					double bestProb = 0 ; 
					for(String act : this.dir){
						int nouveauState = (int) this.pi.get(act).get(State).get(0)[0] ; 
						int []newState = StateToGrid((int) this.pi.get(act).get(State).get(0)[0]);
						double newProb = this.reward[newState[0]][newState[1]]+this.gamma*V[nouveauState][0];
						if(bestProb<=newProb){
							bestAction = act ;
							bestProb = newProb;
						}
					}
					for(String act : this.dir){
						if(act.equals(bestAction)){
							this.action.get(State).put(act, (double) 1 );
							this.action.put(State,this.action.get(State) );

						}else
							this.action.get(State).put(act, (double) 0 );
						this.action.put(State,this.action.get(State) );	
					}

				}
				if(!action.get(State).equals(temp)){
					pol_stable = false ; 
				}
			}
		}
	}

	double[] Iterative (double Teta ){
		double [] res = new double [this.nbStates] ; 
		double delta =  (0.9+ 1) ;  
		while (delta> Teta){
			delta = 0 ; 
			for (int s = 0 ; s < this.nbStates ; s++ ){
				double temp = res [s] ; 
				double sum = 0 ; 
				for(String act : this.dir) {
					double prob_action = this.action.get(s).get(act) ; 
					for (int s2 = 0 ; s2 < nbStates ;s2++){
						if(s2 == pi.get(act).get(s).get(0)[0]){
							int [] newstate = StateToGrid(s2);
							sum += prob_action*(this.reward[newstate[0]][newstate[1]]+(this.gamma*res[s2])); 
						}
					}
				}
				res[s] = sum ; 
				double condition = (temp - res[s]);
				delta = Math.max(delta,Math.abs(condition));
			}
		}
		for (int i = 0 ; i < this.nbStates ; i ++ ){
			if(i%5==0) System.out.println();
			System.out.print( res[i]+" ");
		}
		return res ; 
	}


	public static void main(String[] args) {
		System.out.println("");
		System.out.println("*************Tableau des recompences *********************");
		GridWorld_sql gd = new GridWorld_sql(5,5,2);
		gd.showRewGrid();
		double[][] V = gd.SolvingP();
		// show V
		System.out.println("");
		System.out.println("*************MEthodes Exacte *********************");
		for(int i=0; i<gd.nbStates; i++) {
			if(i%5==0) System.out.println();
			System.out.print( V[i][0]+" ");			
			//  System.out.println("etat" + i + " " + V[i][0]+" ");			
		}
		System.out.println("\n");
		System.out.println("*************MEthodes Iterative *********************");
		gd.Iterative(0.000000065);
		System.out.println("\n");
		// Improve the policy !
		System.out.println("\n");
		gd.ImprovePolicy(V);

	}
}
