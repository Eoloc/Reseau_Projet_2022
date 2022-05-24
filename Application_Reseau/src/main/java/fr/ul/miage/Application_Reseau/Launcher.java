package fr.ul.miage.Application_Reseau;

import javax.websocket.DeploymentException;
import java.net.URISyntaxException;

public class Launcher {
	
	/*
	 Méthode main, instancie le controller javafx et le passe en paramètre de la classe méthode main de la classe App
	 */
	public static void main(String[] args) {

		try {
			Application_ReseauController controller = new Application_ReseauController();
			App.main(args, controller);
		} catch (URISyntaxException | DeploymentException e) {
			e.printStackTrace();
		}

	}
}
