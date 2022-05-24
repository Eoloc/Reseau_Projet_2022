package fr.ul.miage.Application_ReseauJavaFX;

import javax.websocket.DeploymentException;
import java.net.URISyntaxException;

public class Launcher {
	
	public static void main(String[] args) {



		try {
			Application_ReseauController controller = new Application_ReseauController();
			App.main(args, controller);
		} catch (URISyntaxException | DeploymentException e) {
			e.printStackTrace();
		}

	}
}
