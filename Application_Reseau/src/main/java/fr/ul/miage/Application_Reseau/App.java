package fr.ul.miage.Application_Reseau;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

	private static Application_ReseauController controller;

	/*
	 Méthode start
	 Cette méthode charge la fenêtre fxml créée et l'affiche à l'écran
	 */
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

	
	/*
	 Méthode main de la classe App
	 Paramètres :
	 	Application_ReseauController : controller fxml
	 	
	 La méthode lance la méthode start de la même classe
	 */
	public static void main(String[] args, Application_ReseauController cll) {
		controller = cll;
		launch(args);
	}
}
