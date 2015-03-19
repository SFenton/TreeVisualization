package gui.application;
import gui.application.controllers.TreeToolViewController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * JavaFX Application Class.
 * Loads FXML Views, Controllers, and starts the application.
 * 
 * @author Dustin Thompson
 */
public class TreeToolApplication extends Application{

	@Override
	public void start(Stage primaryStage) throws Exception {
		// Setup fxml loader to start on TreeToolView
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/tree_tool_view.fxml"));
    
		// Setup the primary stage
        primaryStage.setTitle("Tree Tool");
        primaryStage.setScene(
        		new Scene(
        				(Parent) loader.load(), 500, 500
        				));
        
        // Initialize controller for fxml
        TreeToolViewController controller= loader.<TreeToolViewController>getController();
        controller.initData(primaryStage);
        
        primaryStage.show();
	}

}
