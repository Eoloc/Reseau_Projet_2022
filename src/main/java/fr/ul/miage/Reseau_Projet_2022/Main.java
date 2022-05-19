package fr.ul.miage.Reseau_Projet_2022;

import fr.ul.miage.Reseau_Projet_2022.Models.WebSocketServer;
import org.glassfish.tyrus.server.Server;

import javax.websocket.DeploymentException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class Main {
    public static void main(String[] args) {
        runServer();
    }

    private static void runServer() {
        Server webSocketServer = null;
        try {
            webSocketServer = new Server("127.0.0.1", 9999, "",  WebSocketServer.class);

            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            try {
                webSocketServer.start();
            } catch (DeploymentException e) {
                e.printStackTrace();
            }
            System.out.println("Please press a key to stop the server.");
            reader.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (webSocketServer != null) {
                webSocketServer.stop();
            }
        }
    }
}