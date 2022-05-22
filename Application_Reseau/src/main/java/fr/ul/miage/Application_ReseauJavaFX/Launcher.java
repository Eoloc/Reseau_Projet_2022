package fr.ul.miage.Application_ReseauJavaFX;

import fr.ul.miage.Application_Reseau.ClientEndpoint;
import org.glassfish.tyrus.client.ClientManager;

import javax.websocket.DeploymentException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.CountDownLatch;

public class Launcher {
	
	public static void main(String[] args) {

		ClientManager client = ClientManager.createClient();
		try {
			URI uri = new URI("ws://127.0.0.1:9999/");
			client.connectToServer(ClientEndpoint.class, uri);
			App.main(args);
		} catch (DeploymentException | URISyntaxException e) {
			e.printStackTrace();
		}

	}
}
