package Model;

import java.util.ArrayList;

import Model.Action.Direction;

public class Bandit {
	protected ArrayList<Butin> butins;
	protected Train trainBandit;
	protected Position pos ; 
	protected String NomBandit;
	private int balles;
	public static String Info = ""; 


	public Bandit (String nom , Train t){
		this.NomBandit = nom ; 
		this.trainBandit = t ; 
		int x = (int) (Math.random()*this.trainBandit.getTrain().size()) +1 ;
		if(x == this.trainBandit.NB_WAGONS) x--
		;
		this.pos = new Position (x,1);
		t.getCellule(x).getBandit().add(this);
		this.balles = 11 ; 
		this.butins = new ArrayList<Butin>();

	}


	public void AvanceBandit (){
		String Msg ="";
		if (this.pos.get_ordonne() == 0){
			Msg = "à l'interieur de " ; 
		}else {
			Msg = "sur le toit de ";
		}
		if (this.pos.get_abcisse() > 0 ){


			int x = this.pos.get_abcisse() - 1 ; 
			int y = this.pos.get_ordonne() ;  
			this.pos = new Position (x,y) ; 
			this.trainBandit.getCellule(x).getBandit().add(this);
			this.trainBandit.getCellule(x+1).getBandit().remove(this);
			//this.trainBandit
			Info = this.NomBandit + " a avance pour etre "+ Msg +   this.trainBandit.getCellule(x).getIdentite();
			System.out.println(this.NomBandit + " a avance pour etre "+ Msg +   this.trainBandit.getCellule(x).getIdentite());
		}else {
			Info = this.NomBandit + " est deja "+ Msg +   this.trainBandit.getCellule(this.pos.get_abcisse()).getIdentite();
			System.out.println(this.NomBandit + " a avance pour etre "+ Msg +   this.trainBandit.getCellule(this.pos.get_abcisse()).getIdentite());
		}

	}

	public void ReculeBandit (){

		String Msg ="";
		if (this.pos.get_ordonne()==0){
			Msg = " a l'interieur" ; 
		}else {
			Msg = " sur le toit ";
		}
		if (this.pos.get_abcisse() <= this.trainBandit.NB_WAGONS- 2  ){

			int x = this.pos.get_abcisse() + 1 ; 
			int y = this.pos.get_ordonne() ;  
			this.pos = new Position (x,y) ; 
			this.trainBandit.getCellule(x).getBandit().add(this);
			this.trainBandit.getCellule(x-1).getBandit().remove(this);
			Info =this.NomBandit + " a reculer pour etre "+ Msg +   this.trainBandit.getCellule(x).getIdentite();
			System.out.println(this.NomBandit + " a reculer our etre "+ Msg +   this.trainBandit.getCellule(x).getIdentite());
		}else {
			Info = this.NomBandit + " est deja "+ Msg +   this.trainBandit.getCellule(this.pos.get_abcisse()).getIdentite();
			System.out.println(this.NomBandit + " a avance pour etre "+ Msg +   this.trainBandit.getCellule(this.pos.get_abcisse()).getIdentite());

		}

	}

	public void MonteBandit (){
		String Msg ="";
		if (this.pos.get_ordonne()==1){
			Msg = "est deja sur le toit";
		}else {
			Msg = "est monte sur le toit ";
		}
		if (this.pos.get_ordonne() == 0 ){
			this.pos = new Position (this.pos.get_abcisse(), 1 ) ; 

			System.out.println(this.NomBandit + Msg + this.trainBandit.getCellule(this.pos.get_abcisse()).getIdentite());
		}else{
			System.out.println(this.NomBandit + Msg + this.trainBandit.getCellule(this.pos.get_abcisse()).getIdentite());
		}
		Info = this.NomBandit + Msg + this.trainBandit.getCellule(this.pos.get_abcisse()).getIdentite();

	}

	public void RentreBandit (){
		String Msg ="";
		if (this.pos.get_ordonne()==0){
			Msg = "est deja à l'interieur";
		}else {
			Msg = "est rentre en interieur ";
		}
		if (this.pos.get_ordonne() == 1 ){
			this.pos = new Position (this.pos.get_abcisse(), 0 ) ; 
			System.out.println(this.NomBandit + Msg + this.trainBandit.getCellule(this.pos.get_abcisse()).getIdentite());
		}else{
			System.out.println(this.NomBandit + Msg + this.trainBandit.getCellule(this.pos.get_abcisse()).getIdentite());


		}
		Info = this.NomBandit + Msg + this.trainBandit.getCellule(this.pos.get_abcisse()).getIdentite();
	}


	public void act (Action a){

		if(a.estVoleur()){
			this.GagneButin();
		}

		if (a.Bouge() != null) {
			switch (a.Bouge()) {
			case AVANT :
				this.AvanceBandit();;
				break;

			case ARRIERE :
				this.ReculeBandit();;
				break;

			case HAUT :
				this.MonteBandit();;
				break;

			case BAS : 
				this.RentreBandit();;
				break;				
			}
			this.Marchal_est_ici();
		}
		if  (a.Vise()!=null){
			switch (a.Vise()) {
			case AVANT :
				this.TireAvant();
				break;

			case ARRIERE :
				this.TireArriere();
				break;

			case HAUT :
				this.tireHaut();
				break;

			case BAS : 
				this.TirBas();
				break;				
			}
			this.Marchal_est_ici();
		}

	}


	public void TireArriere (){
		int x = this.pos.get_abcisse();
		int y = this.pos.get_ordonne();
		ArrayList<Integer> tmp = new ArrayList<>();
		if (this.balles > 0 && x == trainBandit.NB_WAGONS){
			this.balles--;
			for(int i = x+1 ; i < this.trainBandit.NB_WAGONS; i++){
				if(this.trainBandit.getCellule(i).getBandit().size()!=0){
					if(this.trainBandit.getCellule(i).getBandit().size()!=0){
						for (int j = 0 ; j < this.trainBandit.getCellule(i).getBandit().size() ; j++ ) {
							Bandit b = this.trainBandit.getCellule(i).getBandit().get(j);
							if (b.pos.get_ordonne() == y ){
								tmp.add(j);
							}

						}
					}
					if(tmp.size()!=0){
						int rand =(int)( Math.random() *tmp.size());
						int indice = tmp.get(rand);
						
						this.trainBandit.getCellule(x-(i+1)).getBandit().get(indice).LacheButin();
					//	this.trainBandit.getCellule(x-(i+i)).getRichesse().add(i)
						
					}
					i = i+100;/// pour sortir de la boucle for ;;



				}
			}
		}
		if (this.balles > 0){
			Bandit.Info = this.NomBandit + " tu es deja dans la locomotive, dit moi comment tu tire vers le bas ";
		}else {
			Bandit.Info = this.NomBandit + " tu as plus de balles pour tirer";	
		}

	}

	public void TireAvant (){
		int x = this.pos.get_abcisse();
		int y = this.pos.get_ordonne();
		ArrayList<Integer> tmp = new ArrayList<>();
		if (this.balles > 0 & x!=0){
			this.balles--;
			Bandit.Info = this.NomBandit + " a tiré vers l'avant dans l'esperence de blesser quelqu'un";
			for (int i = 0 ; i < x-1 ; i++ ){
				if(this.trainBandit.getCellule(x-(i+1)).getBandit().size()!=0){
					if(this.trainBandit.getCellule(x-(i+1)).getBandit().size()!=0){
						for (int j = 0 ; j < this.trainBandit.getCellule(x-(i+1)).getBandit().size() ; j++ ) {
							Bandit b = this.trainBandit.getCellule(x-(i+1)).getBandit().get(j);
							if (b.pos.get_ordonne() == y ){
								tmp.add(j);
							}

						}
					}
					if(tmp.size()!=0){
						int rand =(int)( Math.random() *tmp.size());
						int indice = tmp.get(rand);
						this.trainBandit.getCellule(x-(i+1)).getBandit().get(indice).LacheButin();
						
					}
					i = i+100;/// pour sortir de la boucle for ;;


				}
			}
		}
		if (this.balles > 0){
			Bandit.Info = this.NomBandit + " tu es deja dans la locomotive, dit moi comment tu tire vers le bas ";
		}else {
			Bandit.Info = this.NomBandit + " tu as plus de balles pour tirer";	
		}
	}

	public void TirBas(){
		int x = this.pos.get_abcisse();
		int y = this.pos.get_ordonne();
		ArrayList<Integer> tmp = new ArrayList<>();
		if (this.balles > 0 && y >0){
			this.balles--;
			Bandit.Info = this.NomBandit + " a tiré vers le bas dans l'esperence de blesser quelqu'un";
			if(this.trainBandit.getCellule(x).getBandit().size()!=0){
				for (int i = 0 ; i < this.trainBandit.getCellule(x).getBandit().size() ; i++ ) {
					Bandit b = this.trainBandit.getCellule(x).getBandit().get(i);
					if (b.pos.get_ordonne() ==0){
						tmp.add(i);
					}

				}
			}
			if(tmp.size()!=0){
				int rand =(int)( Math.random() *tmp.size());
				int indice = tmp.get(rand);
				this.trainBandit.getCellule(x).getBandit().get(indice).LacheButin();
			}

		}
		if (this.balles > 0){
			Bandit.Info = this.NomBandit + " tu es deja en bas, dit moi comment tu tire vers le bas ";
		}else {
			Bandit.Info = this.NomBandit + " tu as plus de balles pour tirer";	
		}


	}


	public void tireHaut () {

		int x = this.pos.get_abcisse();
		int y = this.pos.get_ordonne();
		ArrayList<Integer> tmp = new ArrayList<>();
		if (this.balles > 0 && y == 0){
			this.balles--;
			Bandit.Info = this.NomBandit + " a tiré vers le bas dans l'esperence de blesser quelqu'un";
			if(this.trainBandit.getCellule(x).getBandit().size()!=0){
				for (int i = 0 ; i < this.trainBandit.getCellule(x).getBandit().size() ; i++ ) {
					Bandit b = this.trainBandit.getCellule(x).getBandit().get(i);
					if (b.pos.get_ordonne() ==1){
						tmp.add(i);
					}

				}
			}
			if(tmp.size()!=0){
				int rand =(int)( Math.random() *tmp.size());
				int indice = tmp.get(rand);
				this.trainBandit.getCellule(x).getBandit().get(indice).LacheButin();
			}

		}
		if (this.balles > 0){
			Bandit.Info = this.NomBandit + " tu es deja en haut, dit moi comment tu tire vers le haut ";
		}else {
			Bandit.Info = this.NomBandit + " tu as plus de balles pour tirer";	
		}


	}






	public void Marchal_est_ici () {
		int x = this.pos.get_abcisse();
		if(this.trainBandit.PosSherif==x && this.pos.get_ordonne()==0){
			LacheButin() ; 
			this.act(new Action("Direction Haut"));
			System.out.println(this.NomBandit + " est chassé par le marchal");
			Info = this.NomBandit + " est chassé par le marchal" ; 
		}

	}

	public void LacheButin(){
		int x = this.pos.get_abcisse();
		if(this.butins.size() != 0){
			int rand = (int)(Math.random() *this.butins.size());
			Butin B = this.butins.get(rand);
			this.butins.remove(B);
			this.trainBandit.getCellule(x).getRichesse().add(B);
			if (B.getType()==Butin.Bijoux){
				this.trainBandit.getCellule(x).addNbBijoux();
			}else if(B.getType()==Butin.Bourse){
				this.trainBandit.getCellule(x).addNbBourse();
			}else{
				this.trainBandit.getCellule(x).addNbMargot();
			}
			Bandit.Info = this.NomBandit+ " a perdu un butin de type " + B.getType() + " valant " + B.getValeur()+ "$ " ; 
			System.out.println(this.NomBandit+ " a perdu un butin de type " + B.getType() + " valant " + B.getValeur()+ "$ ");

		}

	}

	public int TotalGain () {
		int res = 0 ; 
		for(Butin b : this.butins)	{
			res = res +b.getValeur() ; 
		}
		return res ; 
	}

	public void GagneButin (){
		int x = this.pos.get_abcisse();
		if(this.pos.get_ordonne()==0){
			int rand = (int)(Math.random()*trainBandit.getCellule(x).getRichesse().size());
			Butin B = this.trainBandit.getCellule(x).getRichesse().get(rand);

			this.trainBandit.getCellule(x).getRichesse().remove(B);
			if(B.getType()==B.Bijoux){
				this.trainBandit.getCellule(x).removeNbBijoux();
			}
			if(B.getType()==B.Bourse)
				this.trainBandit.getCellule(x).removeNbBourse();

			else this.trainBandit.getCellule(x).removeNbMargot();
			//this.trainBandit.getCellule(x).

			this.butins.add(B);
			System.out.println(this.NomBandit+ " a Gagne un butin de type " + B.getType() + " valant " + B.getValeur()+ "$ ");
			Bandit.Info = this.NomBandit+ " a Gagne un butin de type " + B.getType() + " valant " + B.getValeur()+ "$ " ; 
		}else {
			System.out.println(this.NomBandit + "vous devez prendre le risque de rentrer pour esperer ganger de l'argent ");
			Bandit.Info = this.NomBandit + "vous devez prendre le risque de rentrer pour esperer ganger de l'argent " ; 
		}
	}


	public int getBalle(){
		return this.balles ;
	}



	public Position getPos(){
		return this.pos;
	}
	public String getNom (){
		return this.NomBandit;
	}


















}
