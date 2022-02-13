package Vue;



import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JSplitPane;

import Controle.Commande;
import Model.Action;
import Model.Train;
import Model.Bandit;
import observer.Observer;

import Vue.VueTrain;

public class VueModel extends JFrame implements Observer{
	
	private static final long serialVersionUID = 1L;
    public static int Role ; 
	public static final int NB_WAGONS = 4;
	public static int nbPlayers = 0;
	public static ArrayList<JButton> nbP = new ArrayList<JButton>();
	//public static int Role = 0;
	private static ArrayList<Bandit> players;

	
	private JSplitPane panel;
	  private Train train;
	  
	  private Commande Com ; 
	  private InitJeu Info ; 
	    
	    
	  public VueModel (){
		
	   this.Info = new InitJeu();
	   this.Com = new Commande();
	   this.setTitle("ConWay");
		this.setSize(1200, 800);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setResizable(false);
		this.setLayout(new BorderLayout());
		
		this.Info.addObserveres(this);
	//	this.Com.addObserveres(this);
		this.setVisible(false);

	  }

	  public static ArrayList<Bandit> getPlayers(){
		  return players ; 
	  }
		@Override
		public void update(Action action) {
			if (Role==0){
				this.train.BougeMarchal();
			}
			

			this.players.get(Role).act(action);
			Role++;
			
			  if(Com.StratGame==false){
			        System.exit(0);
			        this.setVisible(false);
			    }
			this.getContentPane().remove(this.panel);
			this.panel = new JSplitPane(JSplitPane.VERTICAL_SPLIT, Com, new VueTrain(train));
			this.panel.setDividerLocation(150);
			this.panel.setEnabled(false);
			this.getContentPane().add(this.panel);
			this.setVisible(true);
		
			
		}

		@Override
		public void update(ArrayList<String> names) {
		
		  
			this.train= new Train(names);
			this.nbPlayers = names.size();
			this.players = train.getJoueur();	
			this.setVisible(true);
			this.panel = new JSplitPane(JSplitPane.VERTICAL_SPLIT, Com, new VueTrain(train));
			this.panel.setDividerLocation(150);
			this.panel.setEnabled(false);
			//Int�grer le panneau
			this.getContentPane().add(this.panel);
			
			
			
			
		}
		}
