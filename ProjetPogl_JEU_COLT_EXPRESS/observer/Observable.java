package observer;

import java.util.ArrayList;
import Model.Action;


public interface Observable {
    
	public static ArrayList<Observer> observers = new ArrayList<Observer>();
    
	public void addObserveres(Observer obs);

	public void notifyObservers();
	
	public void setChange(Action action);

	void setChange(javax.swing.Action action);

}
