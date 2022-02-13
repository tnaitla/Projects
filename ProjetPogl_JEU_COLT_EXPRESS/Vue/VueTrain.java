package Vue;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import Model.Action;
import Model.Bandit;
import Model.Butin;
import observer.Observer;
import Model.Train;
import Model.Wagon;

public class VueTrain extends JPanel {
	private Train model ; 
	private final static int TAILLE = 42;
	//JLabel logo = new JLabel(new ImageIcon("/home/takfarinas/Bureau/POGL/projet/src/coloriage-transport-en-ligne-gatuit-dessins-transport-a-inspiration-de-wagon-coloriage-de-wagon-coloriage.jpg"));
	Image ImgWagon = new ImageIcon("/home/takfarinas/Bureau/POGL/leila/src/fini2.png").getImage();
	Image ImgWestern = new ImageIcon("/home/takfarinas/Bureau/POGL/leila/src/cowboy.png").getImage();
	Image ImgDiament = new ImageIcon("/home/takfarinas/Bureau/POGL/leila/src/diamant.png").getImage();
	Image ImgArgent = new ImageIcon("/home/takfarinas/Bureau/POGL/leila/src/argent.png").getImage();
	Image ImgSherrif = new ImageIcon("/home/takfarinas/Bureau/POGL/leila/src/sherif.png").getImage();
	Image ImgTresor = new ImageIcon("/home/takfarinas/Bureau/POGL/leila/src/tresor.png").getImage();
	Image ImgFond = new ImageIcon("/home/takfarinas/Bureau/POGL/leila/src/sahar.png").getImage();
	Image ImgLoco = new ImageIcon("/home/takfarinas/Bureau/POGL/leila/src/fini1.png").getImage();

	public VueTrain (Train t){
		this.model = t ; 

		Dimension dim =  new Dimension(1200 , 500) ; 
		this.setPreferredSize(dim);
	}

	public void update() { repaint(); }

	public void paintComponent(Graphics g) {
		update();
		// faire le degradé de l'arriere plan 
		/*Graphics2D g2 = (Graphics2D)g;
		GradientPaint grad = new GradientPaint(0, 0, Color.blue, 0, 3*getHeight()/4, Color.green);
		g2.setPaint(grad);
		g2.fillRect(0, 0, getWidth(), getHeight());*/
	
		// dessiner le soleil 
		g.setColor(Color.YELLOW);
		g.fillOval(WIDTH-60, -60, 150, 150);

		g.setColor(Color.WHITE);
		

		

		// dessiner les railles ...
		g.setColor(Color.LIGHT_GRAY);
		g.drawImage(ImgFond, 0,0, this);
		g.fillRect(0, 485 , getWidth(),5);
		g.fillRect(0, 470 , getWidth(),5);
		for (int i = 0 ; i < getWidth() ; i=i+20){
			g.fillRect(i, 470 ,2,15);
		}

		//// dessin Marchal ; 
		g.setColor(Color.blue);
			g.drawImage(ImgSherrif,((this.model.PosSherif+1) * 200), 360,this);
			g.drawString("Marchal",(this.model.PosSherif+1) * 200 , 362);
			
			
	//	g.drawImage(ImgSherrif,model.PosSherif*(300+160) , 240,this);
			g.setFont(new Font("TimesRoman",Font.BOLD , 12)); 
			g.setColor(Color.white);
			g.drawString(Bandit.Info, 700, 50);
		/** Pour chaque cellule... */
		for(int i=0; i< 4 ; i++) {
			for(int j=1; j<= 1; j++) {
				/**
				 * ... Appeler une fonction d'affichage auxiliaire.
				 * On lui fournit les informations de dessin [g] et les
				 * coordonnées du coin en haut à gauche.
				 */
				paint(g, model.getCellule(i),i);
			}
		}

	}
	int k = 0 ; 

	private void paint(Graphics g, Wagon c, int x) {
	//	System.out.println("ceeeeeeeeeeee");
//		/** Sélection d'une couleur. 
		g.setColor(Color.blue);
	
		int long_W2 = 280 ;
		int entre_W = 0;
		entre_W = entre_W + entre_W/Train.NB_WAGONS-1;
		if (x==0){
		//	System.out.println(x+"rrrrrrrrrrrrrr");
			g.drawImage(ImgLoco,40+x*(long_W2), 298,this);
		}
		else if ( x==Train.NB_WAGONS) {
			g.setColor(Color.orange);
			g.drawImage(ImgWagon,40+x*(long_W2+entre_W)-(c.getNumWagon()*7) , 310,this);
		}
		else  {
			g.drawImage(ImgWagon,40+x*(long_W2+entre_W) -(c.getNumWagon()*7), 310,this);
		}
		g.setColor(Color.BLACK);
		g.setFont(new Font("TimesRoman",Font.ITALIC , 10)); 
		for ( int i = 0 ; i < c.getBandit().size() ; i++){
               Bandit b = c.getBandit().get(i);
			if(c.getNumWagon()==0){
               if (b.getPos().get_ordonne()==1){
				g.drawImage(ImgWestern,x*(long_W2+entre_W)+70+i*40 , 245,this);
				g.drawString(b.getNom(), x*(long_W2+entre_W)+70+i*40, 240);
			}else {
				g.drawImage(ImgWestern,x*(long_W2+entre_W)+70 +i*40 , 370,this);
				g.drawString(b.getNom(), x*(long_W2+entre_W)+70+i*40, 365);
			}
			}else{
				   if (b.getPos().get_ordonne()==1){
						g.drawImage(ImgWestern,(x*(long_W2+entre_W)+50+i*40)+15 , 257,this);
						g.drawString(b.getNom(), (x*(long_W2+entre_W)+50+i*40)+15, 255);
					}else {
						g.drawImage(ImgWestern,(x*(long_W2+entre_W)+50 +i*40)+15 , 370,this);
						g.drawString(b.getNom(), x*(long_W2+entre_W)+50+ i*40, 365);
					}
			}
		}
		
		for(int i = 0 ; i<model.getJoueur().size();i++){
		Bandit	b = model.getJoueur().get(i);
		g.setFont(new Font("TimesRoman",Font.CENTER_BASELINE , 13));
			g.drawString(b.getNom() , i*200, 15);
			g.setFont(new Font(" : TimesRoman",Font.CENTER_BASELINE , 10));
			g.drawString(" : NBBALLE "+ b.getBalle(), i*200+55, 15);
			g.drawString(" : Richesse"+ b.TotalGain(), i*200+55, 25);
			
			
		}
		
		//	
		g.setFont(new Font("TimesRoman", Font.BOLD , 18)); 
		
		g.drawImage(ImgArgent,x*(long_W2+entre_W)+50,90,this)
		;
		g.setColor(Color.BLACK);
		g.drawString(" X   ( "+ c.getNbBourse()+ " )  "  ,x*(long_W2+entre_W)+120 , 120  ) ; 
		g.drawImage(ImgDiament,x*(long_W2+entre_W)+50,130,this);
		g.drawString(" X   ( "+ c.getNbBijoux()+ " )  "  ,x*(long_W2+entre_W)+120 , 160  ) ;
		g.drawImage(ImgTresor,x*(long_W2+entre_W)+50,180,this);
		g.drawString(" X   ( "+ c.getNbMargot()+ " )  "  ,x*(long_W2+entre_W)+120 , 210  ) ;
	}





}
