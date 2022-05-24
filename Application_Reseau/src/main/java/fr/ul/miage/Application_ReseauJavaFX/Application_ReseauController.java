package fr.ul.miage.Application_ReseauJavaFX;

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
	
	
	public Application_ReseauController() throws URISyntaxException, DeploymentException {
		ClientManager client = ClientManager.createClient();
		URI uri = new URI("ws://127.0.0.1:9999/");
		client.connectToServer(this, uri);
	}

	
	@FXML
	private void initialize() {
		topicsSub = new HashMap<>();
		idSubscribeTopics = new HashMap<>();

		sai_InfoExec.appendText(receipt + "\n");
	}
	
	@FXML
	public void actualiserTopics() throws IOException, InterruptedException {
		ses.getBasicRemote().sendText("FUNCTION\ngetAllTopics\n^@");
	}


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

	@FXML
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
	
	@FXML
	public void send() throws IOException {
		if(!sai_message.getText().equals("")){
			ses.getBasicRemote().sendText("SEND\n" +
					"destination:"+ sai_NomTopic.getText()  + "\n" +
					"content-type:text/plain\n" +
					sai_message.getText() + "\n" +
					"^@");
		}
	}

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



	@OnOpen
	public void onOpen(Session session) throws IOException {
		session.getBasicRemote().sendText("CONNECT\n" +
				"accept-version:1.2\n" +
				"host:127.0.0.1:9999\n" +
				"^@");
		ses = session;
	}

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

	@OnClose
	public void onClose(Session session, CloseReason closeReason) {
		System.out.println("--- Session: " + session.getId());
		System.out.println("--- Closing because: " + closeReason);
	}


}
