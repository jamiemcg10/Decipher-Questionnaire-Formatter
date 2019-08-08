// loads and displays GUI


import java.io.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class QstConverter extends Application {

	public static void main(String[] args) throws IOException {
		// create a SurveyConverter object and call its start method
		launch(args);
	}
	
	
	@Override
	public void start(Stage stage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("QuestionnaireFormatterGUI.fxml"));
		
		Scene scene = new Scene(root);  // attach scene graph to scene
		stage.setTitle("Survey converter");  // displayed in window's title bar
		stage.setScene(scene);  // attach scene to stage
		stage.show();
	}
	

}
