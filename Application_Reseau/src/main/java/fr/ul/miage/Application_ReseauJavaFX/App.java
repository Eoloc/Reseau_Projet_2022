package fr.ul.miage.Application_ReseauJavaFX;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

	private static Application_ReseauController controller;

	@Override
	public void start(Stage primaryStage) {
		primaryStage.setTitle("Application STOMP");
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setController(controller);
			Parent root = FXMLLoader.load(getClass().getResource("fenetre.fxml"));
			Scene scene = new Scene(root);
			primaryStage.setScene(scene);
			primaryStage.show();	
		}catch(IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args, Application_ReseauController cll) {
		controller = cll;
		launch(args);
	}
}
