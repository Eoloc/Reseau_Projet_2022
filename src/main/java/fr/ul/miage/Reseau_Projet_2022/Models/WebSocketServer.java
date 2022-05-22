package fr.ul.miage.Reseau_Projet_2022.Models;

import fr.ul.miage.Reseau_Projet_2022.Controllers.ServerController;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@ServerEndpoint(
        value="/",
        decoders = MessageDecoder.class,
        encoders = MessageEncoder.class )
public class WebSocketServer {
    private Session session;
    private static Set<WebSocketServer> webSocketServer = new CopyOnWriteArraySet<>();
    private static HashMap<String, Boolean> users = new HashMap<>();
    private ServerController serverController = new ServerController();
    private static HashMap<String, ArrayList<String>> topics = new HashMap<>();
    private static HashMap<String, ArrayList<Session>> subscribers = new HashMap<>();
    private static HashMap<Integer, CoupleDestinationSession> historiqueSubscribers = new HashMap<>();

    /*public WebSocketServer(){
        if(topics == null) topics = new HashMap<>();
        if(subscribers == null) subscribers = new HashMap<>();
        if(historiqueSubscribers == null) historiqueSubscribers = new HashMap<>();
    }*/

    @OnOpen // Quand un client arrive
    public void onOpen(Session session) throws IOException, EncodeException {
        this.session = session;
        webSocketServer.add(this);
        users.put(session.getId(), false);
    }

    @OnMessage
    public void onMessage(Session session, String str) throws IOException, EncodeException {
        String[] strSendWithSpace = str.split("\r?\n|\r");
        String[] strSend = new String[strSendWithSpace.length];
        int index = 0;
        for (String s : strSendWithSpace) {
            if (!s.isEmpty()) {
                strSend[index] = s;
                index++;
            }
        }



        if(strSend[0].equals("CONNECT")){
            // TODO DEMANDE DE CONNEXION + ENVOYER QU'IL EST BIEN CONNECTE
            users = serverController.connect(users, session, strSend[1], strSend[2]);
        }

        if(users.get(session.getId())){ // On regarde si il a bien fait la connexion avant autre chose
            if(strSend[0].equals("SEND")){
                ArrayList<HashMap> listeMaps = serverController.send(topics, subscribers, session, strSend[1],strSend[2],strSend[3]);
                this.topics = listeMaps.get(0);
                this.subscribers = listeMaps.get(1);
            }
            if(strSend[0].equals("SUBSCRIBE")){
                ArrayList<HashMap> listeMaps = serverController.subscribe(subscribers, historiqueSubscribers, session, strSend[1], strSend[2], strSend[3]);
                subscribers = listeMaps.get(0);
                historiqueSubscribers = listeMaps.get(1);
            }
            if(strSend[0].equals("UNSUBSCRIBE")){
                ArrayList<HashMap> listeMaps = serverController.unsubscribe(subscribers, historiqueSubscribers, session, strSend[1]);
                subscribers = listeMaps.get(0);
                historiqueSubscribers = listeMaps.get(1);
            }
            if(strSend[0].equals("DISCONNECT")){
                serverController.disconnect(users, session, strSend[1]);
            }
            if(strSend[0].equals("FUNCTION")){
                if(strSend[1].equals("getAllTopics")){
                    String allTopics = serverController.getAllTopics(topics);
                    session.getBasicRemote().sendText(allTopics);
                }
            }
        }
    }

    @OnClose // Quand un client part
    public void onClose(Session session) throws IOException, EncodeException {
        users.remove(session.getId());
        webSocketServer.remove(this);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        // Do error handling here
        // Useless ?????
    }

    public ServerController getServerController() {
        return serverController;
    }

    public void setServerController(ServerController serverController) {
        this.serverController = serverController;
    }

    public Session getSession() {
        return session;
    }
}
