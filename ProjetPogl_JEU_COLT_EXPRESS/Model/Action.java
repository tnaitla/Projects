package Model;


public class Action{

		public enum Direction{
			AVANT, ARRIERE, HAUT, BAS;
		}

		public enum Tir{
			AVANT, ARRIERE, HAUT, BAS;
		}

		private Direction Bouge;
		private Tir Vise;
		private boolean Voleur;


		public Action(String commande) {
			String[] actions = commande.split(" ");
			switch(actions[0]) {

			case "Direction" : 
				switch (actions[1]) {
				case "Avant":
					this.Bouge = Direction.AVANT;
					break;
				case "Arriere":
					this.Bouge = Direction.ARRIERE;
					break;
				case "Haut":
					this.Bouge = Direction.HAUT;
					break;
				case "Bas":
					this.Bouge = Direction.BAS;
					break;
				}
				this.Vise= null;
				this.Voleur = false;
				break;

			case "Tir":
				switch (actions[1]) {
				case "Avant":
					this.Vise= Tir.AVANT;
					break;
				case "Arriere":
					this.Vise= Tir.ARRIERE;
					break;
				case "Haut":
					this.Vise= Tir.HAUT;
					break;
				case "Bas":
					this.Vise= Tir.BAS;
					break;
				}
				this.Voleur = false;
				this.Bouge = null;
				break;

			case "Braque" :
				this.Voleur = true;
				this.Bouge = null;
				this.Vise= null;
				break;
			}
		}
		
		public boolean estVoleur() {
			return this.Voleur;
		}
		
		public Direction Bouge (){
			return this.Bouge;
		}
		
		public Tir Vise () {
			return this.Vise;
		}

}
