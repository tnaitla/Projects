package Bellman;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.OptionalDouble;
import java.util.Random;

import Jama.Matrix;

public class GridWorld {

	private boolean[][] grid;
	private double[][] reward;
	private int size_x;
	private int size_y;
	private int nbStates;
	private double gamma = 0.5;
	private Random rdmnum;
	private long seed = 124;
	private int MAX_REWARD = 20;
	private HashMap<Integer,HashMap<String,Double>> action;
	private HashMap<String,HashMap<Integer,ArrayList<double[]>>> pi;
	private ArrayList<String> dir;

	GridWorld(int num_g) {
		this.rdmnum = new Random(this.seed);
		this.dir = new ArrayList<String>();
		this.dir.add("left");
		this.dir.add("up");
		this.dir.add("right");
		this.dir.add("down");
		this.dir.add("stay");

		CreateGrid(num_g);
		InitRdmPol();
		WallCst();
		InitTransitionMat();

	}
	private void WallCst() {
		for(int i=0; i<size_x; i++) {
			for(int j=0; j<size_y; j++) {				
				if(!grid[i][j]) {
					HashMap<String,Double> a = new HashMap<String,Double>();
					a.put("left", 0.0);
					a.put("up", 0.0);
					a.put("right", 0.0);
					a.put("down", 0.0);
					a.put("stay", 1.0);
					action.put(GridToState(i,j),a);
				}
			}
		}
	}

	private void CreateGrid(int g) {
		switch(g) {
		case 0:
			this.size_x = 8;
			this.size_y = 5;
			this.grid = new boolean[size_x][size_y];
			this.reward = new double[size_x][size_y];
			this.nbStates = size_x*size_y;
			for(int i=0; i<size_x; i++) {
				for(int j=0; j<size_y; j++)
				{
					grid[i][j] = true;
					reward[i][j] = -1;
				}
			}
			// put some walls
			reward[2][2] = -1000;
			reward[3][2] = -1000;
			reward[4][2] = -1000;
			reward[5][2] = -1000;
			grid[2][2] = false;
			grid[3][2] = false;
			grid[4][2] = false;
			grid[5][2] = false;

			// put a strong reward somewhere
			reward[0][0] = 20;
			break;
		case 1:
			this.size_x = 6;
			this.size_y = 6;
			this.grid = new boolean[size_x][size_y];
			this.reward = new double[size_x][size_y];
			this.nbStates = size_x*size_y;
			for(int i=0; i<size_x; i++) {
				for(int j=0; j<size_y; j++)
				{
					grid[i][j] = true;
					reward[i][j] = -1;
				}
			}
			reward[0][1] = 100;
			reward[0][2] = -1000;
			reward[0][3] = -1000;
			reward[0][4] = -1000;
			reward[2][0] = -1000;
			reward[2][1] = -1000;
			reward[2][3] = -1000;
			reward[2][4] = -1000;
			reward[3][4] = -1000;
			reward[3][5] = -1000;
			reward[4][1] = -1000;
			reward[4][2] = -1000;
			reward[4][3] = -1000;
			reward[4][5] = -1000;
			reward[5][5] = -1000;

			grid[0][2] = false;
			grid[0][3] = false;
			grid[0][4] = false;
			grid[2][0] = false;
			grid[2][1] = false;
			grid[2][3] = false;
			grid[2][4] = false;
			grid[3][4] = false;
			grid[3][5] = false;
			grid[4][1] = false;
			grid[4][2] = false;
			grid[4][3] = false;
			grid[4][5] = false;
			grid[5][5] = false;
			break;				
		default:
			System.out.println("Erreur choix grille!");
			System.exit(-1);
			break;
		}
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
	void InitRdmPol() {
		action = new HashMap<Integer,HashMap<String,Double>>();
		HashMap<String, Double> proba = new HashMap<String,Double>() ; 
		for(int i = 0 ; i < this.size_x ; i ++ ){
			for(int j = 0  ; j < this.size_y ; j++ ){
				int state =  GridToState(i , j ) ; 
				double norm = 0.0;
				for(String A : this.dir){
					double alea = this.rdmnum.nextDouble() ;
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
					//System.out.println(("qsdlkjfklsqm  " + pi.get(act).get(s).get(0)[0]));
					if(s2 == pi.get(act).get(s).get(0)[0]){
						P[s][s2] += prob_action ; 
					}
				}
			}
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
	private double[][] SolvingP() {
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

	private void showRewGrid() {
		for(int i=0; i<size_x; i++) {
			for(int j=0; j<size_y; j++)
				System.out.print(this.reward[i][j]+" ");
			System.out.println();
		}
	}

	/*
	 * cette fonction permet de trouver une politique deterministe ou pour chaque etat 
	 * on met une probabilité de 1 sur la meilleurs action et zero sur le reste des action .
	 */
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
			if(i%6==0) System.out.println();
			System.out.print( res[i]+" ");
		}
		return res ; 
	}


	public static void main(String[] args) {
		GridWorld gd = new GridWorld(1);

		gd.showRewGrid();
		double[][] V = gd.SolvingP();
		// show V
		System.out.println("");
		System.out.println("*************MEthodes Exacte *********************");
		for(int i=0; i<gd.nbStates; i++) {
			if(i%6==0) System.out.println();
			System.out.print( V[i][0]+" ");			

		}
		System.out.println("\n");
		System.out.println("*************MEthodes Iterative *********************");
		gd.Iterative(0.00000005);

		System.out.println("\n");
		// Improve the policy !
		gd.ImprovePolicy(V);

	}
}
