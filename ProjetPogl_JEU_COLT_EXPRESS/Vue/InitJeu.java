package Vue;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import Model.Action;
import observer.Observable;
import observer.Observer;
import Vue.VueModel;;

public class InitJeu  extends JFrame implements ActionListener, Observable{
	private static final long serialVersionUID = 1L;
	
	public static ArrayList<JButton> SelectNBPlayers= new ArrayList<JButton>();
	private static ArrayList<JTextField> NomPlayers;
	Image ImgWagon = new ImageIcon("/home/takfarinas/Bureau/POGL/leila/src/fini2.png").getImage();
	ImageIcon icone =  new ImageIcon("/home/takfarinas/Bureau/POGL/leila/src/Couverture.png");

	private JLabel Message;
	private JPanel  PNBJoueur;
	private JPanel  PNOM;
	private JLabel Couver ; 
	private JButton START;
	private int nbPlayers;
	private ArrayList<String> PlayerNames;
	
	public InitJeu (){
		/*this.NomPlayers = new ArrayList<JTextField>();
		this.PlayerNames = new ArrayList<String>();*/
		
		this.PNBJoueur = new JPanel();
		this.PNOM = new JPanel () ; 
		this.NomPlayers = new ArrayList<JTextField>();
		this.PlayerNames = new ArrayList<String>();
		this.setTitle("Initialisation du Conway");
		this.setSize(400, 500);
		this.setLocation(400, 100);
		START = new JButton("START");
		START.setBackground(Color.GREEN);
		
		this.setAlwaysOnTop(true);
		this.setVisible(true);
		
		///////////////////////////////////////////////////////////////////
		//////// Initialisation des Boutton de choix de NBP //////////////
		/////////////////////////////////////////////////////////////////
		
		SelectNBPlayers.add(new JButton("1"));
		SelectNBPlayers.add(new JButton("2"));
		SelectNBPlayers.add(new JButton("3"));
		SelectNBPlayers.add(new JButton("4"));
		SelectNBPlayers.add(new JButton("5"));
		SelectNBPlayers.add(new JButton("6"));
	
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		
		GridLayout gl = new GridLayout(2, 2);
		gl.setHgap(5); //Cinq pixels d'espace entre les colonnes (H comme Horizontal)
		gl.setVgap(5); //Cinq pixels d'espace entre les lignes (V comme Vertical)
		this.PNBJoueur.setLayout(gl);
		this.Couver = new JLabel(icone);
		this.add(Couver, BorderLayout.SOUTH);
		this.Message = new JLabel("Veuillez choisir le nombre de joueurs");
		this.add(this.Message, BorderLayout.NORTH);
		
		
		for (int i =0 ; i < SelectNBPlayers.size() ; i++){
			this.SelectNBPlayers.get(i).addActionListener(this);
		
			this.SelectNBPlayers.get(i).setBackground(Color.pink);
			this.PNBJoueur.add(this.SelectNBPlayers.get(i));
		}
		this.add(this.PNBJoueur, BorderLayout.CENTER);
		
		

		
	}
	
	
	@Override
	public void addObserveres(Observer obs) {
		Observable.observers.add(obs);		
	}


	public void notifyObservers() {
		for(Observer obs : Observable.observers) {
			obs.update(this.PlayerNames);
		}
	}

	@Override
	public void setChange(Action action) {
		// */
		
	}
	

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(this.START)){
			for (int i = 0 ;i < this.nbPlayers ; i++){
				this.PlayerNames.add(this.NomPlayers.get(i).getText());
			}
			this.notifyObservers();
			this.setVisible(false);
			this.dispose();
		}else {
			for (int i = 0; i < this.SelectNBPlayers.size(); i++) {
				if(e.getSource() == this.SelectNBPlayers.get(i)) { 
					this.nbPlayers = i + 1;	
					this.setVisible(false);
				}		
			}
		
			this.InitNames();
			this.remove(this.PNBJoueur);
			this.add(this.PNOM, BorderLayout.CENTER);
			this.setVisible(true);
		
		}
	}

	private void InitNames (){
		this.PNOM.setLayout(new GridLayout(10,4));
		this.Message.setText("Veuillez choisir des noms pour les joueurs");
		for(int i = 0 ; i < this.nbPlayers ; i++){
			this.PNOM.add(new JLabel("Prenom du Joueur"+ (i+1)));
			this.NomPlayers.add(new JTextField(""));
			this.PNOM.add(this.NomPlayers.get(i));
		}
		START.addActionListener(this);
		this.PNOM.add(START);
		
		
	}
	public ArrayList<String> getNames() {
		VueModel.nbPlayers = this.PlayerNames.size();
		return this.PlayerNames;
	}


	@Override
	public void setChange(javax.swing.Action action) {
		// TODO Auto-generated method stub
		
	}
	
	
	
	
	
	
	
	
	
	
	
}
