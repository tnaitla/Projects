package observer;

import java.util.ArrayList;
import Model.Action;

// Utiliser le pattern observer afin de faire communiquer notre mod�le avec d'autres objets
public interface Observer {
	
	public void update(Action action);

	public void update(ArrayList<String> names);

}