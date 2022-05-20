package fr.ul.miage.Reseau_Projet_2022.Controllers;

import fr.ul.miage.Reseau_Projet_2022.Models.Message;
import fr.ul.miage.Reseau_Projet_2022.Models.WebSocketServer;

import javax.websocket.EncodeException;
import javax.websocket.Session;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class ServerController {
    public ServerController(){ }

    public HashMap<String, Boolean> connect(HashMap<String, Boolean> users,Session session, String version, String host){
        /*
            Ce que le client va envoyer :

            CONNECT
            accept-version:1.2
            host:stomp.github.org
            ^@

            Si la frame est correct on envoie au client le message :

            CONNECTED
            version:1.2
            ^@

            Grace a la fonction : session.getBasicRemote().sendText("Message");
            Puis passer dans le hashmap le boolean a true

            Si erreur, ne rien faire
         */
        return users;
    }

    public ArrayList<HashMap> send(HashMap<String, ArrayList<String>> topics, HashMap<String, ArrayList<Session>> subscribers, Session session, String strDestination, String strContentType, String strMessage) throws IOException {
        
    	
    	String destination = strDestination.substring(12);
    	String contentType = strContentType.substring(13);
    	String message = 
    			"MESSAGE\n"+
                "subscription:0\n"+
                "message-id:"+topics.get(destination).size()+1+"\n"+
                "destination:/queue/a\n"+
                "content-type:text/plain\n"+
                "hello queue a^@";
    	
    	String receipt = 
    			"RECEIPT\n"+
                "receipt-id:message-007\n"+

                "^@";
    	
    	String errorStructure =
    			"ERROR\n"+
                "receipt-id:"+topics.get(destination).size()+1+"\n"+
                "content-type:"+contentType+"\n"+
                "content-length:"+strMessage.length()+"\n"+
                "message:malformed frame received\n"+

                "The message:\n"+
                "-----\n"+
                "MESSAGE\n"+
                "destined:"+strMessage+"\n"+
                "receipt:"+topics.get(destination).size()+1+"\n"+

                "Hello queue a!\n"+
                "-----\n"+
                "Did not contain a destination header, which is REQUIRED\n"+
                "for message propagation.\n"+
                "^@";
    	
    	String errorMessage =
    			"ERROR\n"+
                "receipt-id:"+topics.get(destination).size()+1+"\n"+
                "content-type:"+contentType+"\n"+
                "content-length:"+strMessage.length()+"\n"+
                "message:message content empty\n"+

                "The message:\n"+
                "-----\n"+
                "MESSAGE\n"+
                "destined:"+destination+"\n"+
                "receipt:"+topics.get(destination).size()+1+"\n"+

                strMessage+"\n"+
                "-----\n"+
                "Did not contain a message, which is REQUIRED\n"+
                "for message propagation.\n"+
                "^@";
    	
    	ArrayList<HashMap> listeMaps = new ArrayList<HashMap>();
    	
    	if(topics.keySet().contains(destination)) {
    		if(contentType.equals("text/plain")) {
    			topics.get(destination).add(strMessage);
				session.getBasicRemote().sendText(receipt);
				for(Session s:subscribers.get(destination)) {
					s.getBasicRemote().sendText(message);
				}
    		} else {
    			session.getBasicRemote().sendText(errorStructure);
    		}
    	}else {
    		if(contentType.equals("text/plain")) {
        		ArrayList<String> messages = new ArrayList<String>();
        		messages.add(strMessage);
        		topics.put(destination, messages);
        		subscribers.put(destination,new ArrayList<Session>());
        		session.getBasicRemote().sendText(receipt);
    		} else {
    			session.getBasicRemote().sendText(errorStructure);
    		}
    	}
    	
    	listeMaps.add(topics);
    	listeMaps.add(subscribers);
    	return listeMaps;
    	
    	
    	/*
            Ce que le client va envoyer :

            SEND
            destination:/queue/a
            content-type:text/plain

            hello queue a

            ^@

            Notes :
            This sends a message to a destination named /queue/a.
            Note that STOMP treats this destination as an opaque string and
            no delivery semantics are assumed by the name of a destination.
            You should consult your STOMP server's documentation to find out
            how to construct a destination name which gives you the delivery
            semantics that your application needs.

            Si la frame est correct on envoie à la topic le message :

            MESSAGE
            subscription:0
            message-id:007  (Le 7ième message envoyé dans la topic ?)
            destination:/queue/a
            content-type:text/plain

            hello queue a^@

            Notes :
            this destination header SHOULD be identical to the one used in
            the corresponding SEND frame.
            The MESSAGE frame MUST contain a message-id header with a unique
            identifier for that message and a subscription header matching
            the identifier of the subscription that is receiving the message.

            Et ce message au client qui a envoyé le SEND :

            RECEIPT
            receipt-id:message-007

            ^@

            Notes :
            A RECEIPT frame MUST include the header receipt-id, where the value
            is the value of the receipt header in the frame which this is a
            receipt for.

            On envoie au client grâce à la fonction :
            session.getBasicRemote().sendText("Message");

            On peut aussi avoir plusieurs type d'erreur dont le message
            (à modifier) est :

            ERROR
            receipt-id:message-007
            content-type:text/plain
            content-length:170
            message:malformed frame received

            The message:
            -----
            MESSAGE
            destined:/queue/a
            receipt:message-007

            Hello queue a!
            -----
            Did not contain a destination header, which is REQUIRED
            for message propagation.
            ^@
         */
    }

    public void subscribe(HashMap<String, ArrayList<Session>> subscribers, Session session, String strId, String strDestination, String strAck) {
        /*
            Ce que le client va envoyer :

            SUBSCRIBE
            id:0
            destination:/queue/foo
            ack:client              ?????? On garde le ack ??????
            ^@

            Si la frame est correct on retourne au client le message :

            RECEIPT
            receipt-id:subscribe-0
            ^@

            Notes :
            subscription header matching the identifier of the subscription that
            is receiving the message.

            Si erreur, on doit retourner un message d'erreur (voir methode send)
         */

    }

    public void unsubscribe(HashMap<String, ArrayList<Session>> subscribers, Session session) {
        /*
            Ce que le client va envoyer :

            UNSUBSCRIBE
            id:0
            ^@

            Notes :
            id header MUST be included in the frame to uniquely identify
            the subscription to remove.

            Si la frame est correct on retourne au client le message :

            RECEIPT
            receipt-id:unsubscribe-0
            ^@

            Grace a la fonction : session.getBasicRemote().sendText("Message");

            Si erreur, on doit retourner un message d'erreur (voir methode send)
         */
    }

    public void disconnect(HashMap<String, Boolean> users, Session session) {
        /*
            Ce que le client va envoyer :

            DISCONNECT
            receipt:77
            ^@

            Si la frame est correct on retourne au client le message :

            RECEIPT
            receipt-id:77
            ^@

            Grace a la fonction : session.getBasicRemote().sendText("Message");

            Si erreur, on doit retourner un message d'erreur (voir methode send)
         */
    }

    // Envoie à tous les endpoints le message
    public static void broadcast(Set<WebSocketServer> webSocketServer, Message message) throws IOException, EncodeException {

        webSocketServer.forEach(endpoint -> {
            synchronized (endpoint) {
                try {
                    endpoint.getSession().getBasicRemote().sendObject(message);
                } catch (IOException | EncodeException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}