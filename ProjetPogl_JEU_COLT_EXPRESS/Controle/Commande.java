

package Controle;


import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import Model.Action;
import Vue.VueModel;
import observer.Observable;
import observer.Observer;
//import vue.VueJeu;



public class Commande  extends JPanel implements ActionListener, Observable{

	private static final long serialVersionUID = 1L;
	private static ArrayList<Action> AllActs = new ArrayList<Action>();

	private JPanel PBouger;
	private JPanel PTirer;
	private JPanel PInformation;
	private JPanel Gestion;
	private ArrayList<JButton> Bouge;
	private ArrayList<JButton> Tire;

	private JButton Voler;
	private JButton Action;
	private JButton Efface;
	private JButton Start ;
	private JButton STop ; 
	private JButton Valide ; 
	public static boolean  StratGame =true ; 
	/// pour permettre au joueur de changer les action Avant de valder si il le souhaite 
	private ArrayList<Action> TempoActs = new ArrayList();
	private JLabel message = new JLabel("Welcome, presse START to begin ");
	private int TourA = 0 ; 
	public int Role = 0 ; 


	public Commande () {
	
		this.setLayout(new GridLayout(2, 2));
		this.AllActs = new ArrayList<Action>();



		////////////////////////////////////////////////////////////////
		/////// INITIALISATION du Panneau de bouton Direction /////////
		///////////////////////////////////////////////////////////////
		this.PBouger = new  JPanel () ;
		JButton text = new JButton("Bouger");
		text.setBackground(Color.orange);
		text.setEnabled(false);

		this.Bouge = new ArrayList<JButton>() ;
		this.PBouger.setLayout(new BorderLayout());
		this.PBouger.add(text, BorderLayout.CENTER);

		this.Bouge.add(new JButton ("Avant")) ; 
		this.Bouge.get(0).addActionListener(this);
		this.PBouger.add(this.Bouge.get(0), BorderLayout.WEST);

		this.Bouge.add(new JButton ("Arriere")) ; 
		this.Bouge.get(1).addActionListener(this);
		this.PBouger.add(this.Bouge.get(1), BorderLayout.EAST);

		this.Bouge.add(new JButton ("Haut")) ; 
		this.Bouge.get(2).addActionListener(this);
		this.PBouger.add(this.Bouge.get(2), BorderLayout.NORTH);

		this.Bouge.add(new JButton ("Bas")) ; 
		this.Bouge.get(3).addActionListener(this);
		this.PBouger.add(this.Bouge.get(3), BorderLayout.SOUTH);

		for (JButton b : this.Bouge) {
			b.setBackground(Color.cyan);
		}

		/////////////////////////////////////////////////////////////
		//////// Initilisation des Tires                ////////////
		///////////////////////////////////////////////////////////

		this.PTirer = new JPanel () ;
		JButton text1 = new JButton("TIRER");
		text1.setBackground(Color.orange);
		text1.setEnabled(false);
		this.Tire = new ArrayList<JButton>() ;
		this.PTirer.setLayout(new BorderLayout());
		this.PTirer.add(text1, BorderLayout.CENTER);

		this.Tire.add(new JButton ("TAvant")) ; 
		this.Tire.add(new JButton ("TArriere")) ; 
		this.Tire.add(new JButton ("THaut")) ; 
		this.Tire.add(new JButton ("TBas")) ; 

		this.Tire.get(0).addActionListener(this);
		this.Tire.get(1).addActionListener(this);
		this.Tire.get(2).addActionListener(this);
		this.Tire.get(3).addActionListener(this);

		this.PTirer.add(this.Tire.get(0), BorderLayout.WEST);
		this.PTirer.add(this.Tire.get(1), BorderLayout.EAST);
		this.PTirer.add(this.Tire.get(2), BorderLayout.NORTH);
		this.PTirer.add(this.Tire.get(3), BorderLayout.SOUTH);

		for (JButton b : this.Tire) {
			b.setBackground(Color.cyan);
		}

		////////////////////////////////////////////////////////////
		//////// Initialisation du panneau d'affichage /////////////
		////////////////////////////////////////////////////////////
		this.PInformation = new JPanel () ; 
		Font Ecriture = new Font("Tahoma", Font.CENTER_BASELINE, 18);  
		message.setFont(Ecriture);  
		message.setForeground(Color.BLUE);  
		message.setHorizontalAlignment(JLabel.CENTER);
		this.setBackground(Color.LIGHT_GRAY);
		this.PInformation.add(message, BorderLayout.CENTER);

		////////////////////////////////////////////////////////////////
		///////////  Initialisation des Bouton de gestion /////////////
		//////////////////////////////////////////////////////////////

		this.Gestion = new JPanel () ; 
		this.Gestion.setLayout(new GridLayout(3, 1));
		Voler = new JButton("Braque");
		this.Voler.setBackground(Color.gray);
		this.Voler.addActionListener(this);
		this.Gestion.add(Voler);

		this.Action= new JButton("action");
		this.Action.setBackground(Color.PINK);
		this.Action.addActionListener(this);
		this.Gestion.add(Action);


		this.STop= new JButton ("Stop");
		this.STop.setBackground(Color.red);
		this.STop.addActionListener(this);
		this.Gestion.add(STop);
		
	

		this.Start = new JButton ("Start");
		this.Start.setBackground(Color.green);
		this.Start.addActionListener(this);
		this.Gestion.add(Start);


		this.Efface = new JButton("Modifier");
		this.Efface.addActionListener(this);
		this.Gestion.add(Efface);

		this.Valide= new JButton ("Valide");
		this.Valide.setBackground(Color.blue);
		this.Valide.addActionListener(this);
		this.Gestion.add(Valide);
		//this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


		this.add(this.PBouger);

		this.add(this.PTirer);
		this.add(this.PInformation);
		this.add(this.Gestion);


	}

	public void updateMessage() {
		
		
		if(this.AllActs.size()/3 == VueModel.nbPlayers) {
			if (this.TourA == 3) {
				message.setText("Action Termine , Recommencer ou Cliquer STOP pour Sortir");
			}	else{
				message.setText(VueModel.getPlayers().get(VueModel.Role).getNom() + " fait son action numero : " + (this.TourA+1));
			}
		}
		else  {
			if (this.TempoActs.size()/3 == 1)
				message.setText("Vous pouvez validez ou modifez");
			else
				message.setText(VueModel.getPlayers().get((int)this.AllActs.size()/3).getNom() + " action numero : " + (this.TempoActs.size()%3 + 1));
		}
	}
	@Override
	public void addObserveres(Observer obs) {
		// TODO Auto-generated method stub

	}
	@Override

	public void notifyObservers() {
		// TODO Auto-generated method stub

	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == Efface) 
		TempoActs = new ArrayList<Action>();
		if(e.getSource() == STop){
		//	VueModel.setVisible(false); 
			StratGame = false ; 
			this.setChange(new Action ("Direction Avant"));
			}
			//this.dispose();
		
		if (e.getSource() == Action ||e.getSource() == Valide ) {
			if(TempoActs.size()/3 == 1 || TempoActs.size() == 0) {
			
				if (this.AllActs.size()/3 == VueModel.nbPlayers) {
					this.desactiveButtons();
					if (TourA < 3) {
						this.setChange(AllActs.get(VueModel.Role * 3 + TourA));
						if (VueModel.Role == VueModel.nbPlayers) {
							TourA++;
							VueModel.Role = 0;
						}
					}
					else {
						TourA = 0;
						this.AllActs = new ArrayList<Action>();
					this.activeButtons();
					}
				}
				else {

					this.AllActs.addAll(TempoActs);
					TempoActs = new ArrayList<Action>();
				}
			}
		}
		else if(TempoActs.size()/3 < 1){
			if (Bouge.contains(e.getSource())) {
				for (JButton b : Bouge) {
					if(b == e.getSource())
						TempoActs.add(new Action("Direction " + b.getText()));
					
				}
			}
			if(Tire.contains(e.getSource())) {
				for(JButton b : Tire) {
					if(b == e.getSource()){
						System.out.println("             " + b.getText()+" ++++++++++++");
						TempoActs.add(new Action("Tir " + b.getText()));
					}
				}
			}
			if(e.getSource() == Voler) {
				TempoActs.add(new Action("Braque"));
			}
		}
		this.updateMessage();
	}

	public void desactiveButtons() {
		for (int i = 0; i < 4; i++) {
			this.Bouge.get(i).setEnabled(false);
			this.Tire.get(i).setEnabled(false);
		}
		this.Efface.setEnabled(false);
		this.Voler.setEnabled(false);
	}

	public void activeButtons() {
		for (int i = 0; i < 4; i++) {
			this.Bouge.get(i).setEnabled(true);
			this.Tire.get(i).setEnabled(true);
		}
		this.Efface.setEnabled(true);
		this.Voler.setEnabled(true);
	}

	/*@Override
	public void addObserveres(Observer obs) {

	}*/

	@Override
	public void setChange(Action action) {
		for(Observer obs : Observable.observers) {
			obs.update(action);
		}
	}

	@Override
	public void setChange(javax.swing.Action action) {
		// TODO Auto-generated method stub
		
	}


	

}
