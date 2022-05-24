package fr.ul.miage.Application_Reseau;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.glassfish.tyrus.client.ClientManager;

import javax.websocket.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;

@javax.websocket.ClientEndpoint
public class Application_ReseauController {

	private static Session ses;
	private static String receipt;
	private int indiceSubscribeTopic = 1;

	//Champs FXML
	@FXML
	private ComboBox combo_listeTopic;
	@FXML
	private Button btn_Actualisation;
	@FXML
	private Button btn_aboDesabo;
	@FXML
	private Button btn_EnvoiMessage;
	@FXML
	private TextArea sai_message;
	@FXML
	private TabPane tabPane_Topics;
	@FXML
	private TextArea sai_InfoExec;
	@FXML
	private TextField sai_NomTopic;
	
	private HashMap<String, Boolean> topicsSub;
	private HashMap<Integer, String> idSubscribeTopics;
	
	/*
	 Constructeur du controller
	 Renseigne l'uri, le port et initie la connexion au serveur
	 */
	public Application_ReseauController() throws URISyntaxException, DeploymentException {
		ClientManager client = ClientManager.createClient();
		URI uri = new URI("ws://127.0.0.1:9999/");
		client.connectToServer(this, uri);
	}

	
	/*
	 Méthode initialize, exécutée après le constructeur au moment du chargement du fichier fxml
	 On initialise les Map passée en attributs.
	 On affiche aussi le résultat de la requête de connexion au serveur. Si une erreur est survenue, on le verra au lancement
	 */
	@FXML
	private void initialize() {
		topicsSub = new HashMap<>();
		idSubscribeTopics = new HashMap<>();

		sai_InfoExec.appendText(receipt + "\n");
	}
	
	/*
	 Méthode actualiserTopics,
	 cette méthode est appelée par le bouton "Actualiser", elle envoie un message au serveur
	 
	 On utilise ici la frame FUNCTION et on passe en attribut le nom de méthode "getAllTopics",
	 le fait d'envoyer cette frame déclenchera le traitement "OnMessage" de notre serveur
	 */
	@FXML
	public void actualiserTopics() throws IOException, InterruptedException {
		ses.getBasicRemote().sendText("FUNCTION\ngetAllTopics\n^@");
	}

	/*
	 Méthode updateListeTopic,
	 cette méthode est appelée lorsque le serveur recoit la frame FUNCTION envoyée par le bouton "Actualiser"
	 
	 On met à jour la liste déroulante
	 */
	public void updateListeTopic(String[] receipt_lines){
		for(String str : receipt_lines) {
			if(!str.equals("^@")&&!str.equals("RECEIPT")&&!str.equals("getAllTopics")){
				if(!topicsSub.containsKey(str)){
					topicsSub.put(str, false);
					combo_listeTopic.getItems().add(str);
				}
			}
		}

	}
	
	/*
	 Méthode updateTopics
	 cette méthode met à jour le contenu des onglet associé à chaque topic auquelles l'utilisateur est abonné
	 */
	public void updateTopics(String destination, String content){
		int i = 0;
		for(Tab t : tabPane_Topics.getTabs()){
			if(t.getText().equals(destination)){
				Label label = (Label) t.getContent();
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						label.setText(content + "\n" + label.getText());
					}
				});
				break;
			}
		}
	}
	
	@FXML
	public void subscribeHandler() throws IOException {
		subscribe(null);
	}

	/*
	 Méthode subscribe,
	 appelée par le bouton "S'abonner", elle permet à l'utilisateur de s'abonner ou de se désabonner selon le fait qu'il le soit déjà ou non
	 
	 La méthode envoi un message au serveur avec l'entête SUBSCRIBE ou UNSUBSCRIBE
	 Le traitement se fait ensuite dans le OnMessage du serveur
	 L'abonnement/Désabonnement met à jour les onglet disponibles en ajoutant ou retirant la topic concernée par le traitement
	 */
	@FXML
	public void subscribe(String[] receipt_lines) throws IOException {
		String destination = (String) combo_listeTopic.getValue();
		if(receipt_lines == null){
			if(destination != null){
				if(topicsSub.get(destination)){ // Si je suis abonné alors ...
					int idSuppr = -1;
					for(int indice : idSubscribeTopics.keySet()){
						if(idSubscribeTopics.get(indice).equals(destination)){
							idSuppr = indice;
							break;
						}
					}
					ses.getBasicRemote().sendText(
							"UNSUBSCRIBE\n" +
									"id:"+ idSuppr + "\n" +
									"^@");
					idSubscribeTopics.remove(idSuppr);
					topicsSub.put(destination, false);
					for(Tab t : tabPane_Topics.getTabs()){
						if(t.getText().equals(destination)){
							tabPane_Topics.getTabs().remove(t);
							break;
						}
					}
					btn_aboDesabo.setText("S'abonner");
				} else {
					ses.getBasicRemote().sendText(
							"SUBSCRIBE\n" +
									"id:"+ indiceSubscribeTopic + "\n" +
									"destination:" + destination + "\n" +
									"ack:client\n" +
									"^@");
				}
			}
		} else { // On a reçu un message pour subscribe
			if(receipt_lines[0].equals("ERROR") && receipt_lines[2].equals("message:id already exist")){
				indiceSubscribeTopic++;
				ses.getBasicRemote().sendText(
						"SUBSCRIBE\n" +
								"id:"+ indiceSubscribeTopic + "\n" +
								"destination:" + destination + "\n" +
								"ack:client\n" +
								"^@");
			}
			else if(receipt_lines[0].equals("RECEIPT") && receipt_lines[1].startsWith("receipt-id:subscribe")){
				idSubscribeTopics.put(indiceSubscribeTopic, destination);
				topicsSub.put(destination, true);
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						tabPane_Topics.getTabs().add(new Tab(destination, new Label("Début de la conversation...")));
						btn_aboDesabo.setText("Se désabonner");
					}
				});
			}
		}
	}
	
	/*
	 Méthode send,
	 appelée par le bouton "envoyer message"
	 
	 Cette méthode envoi un message au serveur avec l'entête SEND
	 Le traitement sera fait dans le onMessage du serveur
	 */
	@FXML
	public void send() throws IOException {
		if(!sai_message.getText().equals("") && !sai_NomTopic.getText().equals("")){
			ses.getBasicRemote().sendText("SEND\n" +
					"destination:"+ sai_NomTopic.getText()  + "\n" +
					"content-type:text/plain\n" +
					sai_message.getText() + "\n" +
					"^@");
			if(!combo_listeTopic.getItems().contains(sai_NomTopic.getText())) {
				sai_InfoExec.appendText("La topic à été créée, pensez à rafraichir la liste déroulante afin qu'elle apparaisse.\n");
			}
		}else {
			sai_InfoExec.appendText("Erreur : Merci de renseigner un message et un nom de queue avant d'envoyer.\n");
		}
	}

	
	/*
	 Méthode changeBtnSubscribeLabel,
	 cette méthode change le message du bouton s'abonner de "S'abonner" à "Se désabonner"
	 
	 Cette méthode est appelé à la sélection d'un élément de la liste déroulante
	 */
	@FXML
	public void changeBtnSubscribeLabel(){
		if((String) combo_listeTopic.getValue() != null){
			if(topicsSub.get(combo_listeTopic.getValue())){ // Si je suis abonné alors ...
				btn_aboDesabo.setText("Se désabonner");
			} else {
				btn_aboDesabo.setText("S'abonner");
			}
			sai_NomTopic.setText((String) combo_listeTopic.getValue());
		}
	}


	/*
	 Traitement OnOpen, s'exécute à l'ouverture de la connexion
	 
	 Lors de l'ouverture, une frame de connexion est envoyée
	 */
	@OnOpen
	public void onOpen(Session session) throws IOException {
		session.getBasicRemote().sendText("CONNECT\n" +
				"accept-version:1.2\n" +
				"host:127.0.0.1:9999\n" +
				"^@");
		ses = session;
	}

	/*
	 Traitement OnMessage, s'exécute quand on reçoit un message
	 
	 Lorsqu'un message est reçu, on récupère le résultat et on exécute la méthode adaptée
	 */
	@OnMessage
	public void onMessage(String message, Session session) throws IOException {
		BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
		receipt = message;
		String[] receipt_lines = receipt.split("\r?\n|\r");
		if(receipt_lines[0].equals("MESSAGE")){
			String destination = receipt_lines[3].substring(12);
			String content = receipt_lines[5];
			updateTopics(destination, content);
		}
		else if(receipt_lines[0].equals("RECEIPT") && receipt_lines[1].equals("getAllTopics")){
			updateListeTopic(receipt_lines);
		}
		else if(receipt_lines[0].equals("ERROR") && receipt_lines[2].equals("message:id already exist")){
			subscribe(receipt_lines);
		}
		else if(receipt_lines[0].equals("RECEIPT") && receipt_lines[1].startsWith("receipt-id:subscribe")){
			subscribe(receipt_lines);
		}
	}

	/*
	 Traitement OnClose, s'exécute à la fermeture de la connexion
	 
	 A la fermenure, on affiche un message
	 */
	@OnClose
	public void onClose(Session session, CloseReason closeReason) {
		System.out.println("--- Session: " + session.getId());
		System.out.println("--- Closing because: " + closeReason);
	}


}
