import gui.application.TreeToolApplication;
import javafx.application.Application;

/**
 * The Driver to start the Tree Tool
 * 
 * @author Dustin Thompson
 */
public class TreeToolDriver {

	public static void main(String[] args) {
		// Launch the tool on the javafx application thread
		Application.launch(TreeToolApplication.class, args);
	}

}
