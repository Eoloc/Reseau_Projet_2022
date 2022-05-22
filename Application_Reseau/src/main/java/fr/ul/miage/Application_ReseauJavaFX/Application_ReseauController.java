package fr.ul.miage.Application_ReseauJavaFX;

import fr.ul.miage.Application_Reseau.ClientEndpoint;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;

import javax.websocket.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;


public class Application_ReseauController {

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
	private HashMap<String, Boolean> topicsSub;
	private HashMap<Integer, String> idSubscribeTopics;
	
	
	public Application_ReseauController() {
		tabPane_Topics = new TabPane();
		topicsSub = new HashMap<>();
		idSubscribeTopics = new HashMap<>();
	}

	
	@FXML
	private void initialize() {
		
		
		/*exemple ajout TabPane
		Tab tab1 = new Tab("Planes", new Label("Show all planes available"));
        Tab tab2 = new Tab("Cars"  , new Label("Show all cars available"));
        Tab tab3 = new Tab("Boats" , new Label("Show all boats available"));

        tabPane_Topics.getTabs().add(tab1);
        tabPane_Topics.getTabs().add(tab2);
        tabPane_Topics.getTabs().add(tab3);
        */
		sai_InfoExec.appendText(ClientEndpoint.getReceipt() + "\n");
	}
	
	@FXML
	public void actualiserTopics() throws IOException, InterruptedException {
		ClientEndpoint.getSes().getBasicRemote().sendText("FUNCTION\ngetAllTopics\n^@");
		String old_receipt = ClientEndpoint.getReceipt();
		int i = 0;
		while (ClientEndpoint.getReceipt().equals(old_receipt) && i < 1000){

			old_receipt = ClientEndpoint.getReceipt();
			System.out.println(ClientEndpoint.getReceipt() + " : " + i);
			i++;
		}
		System.out.println();
		System.out.println("ON A CHANGE DE RECEIPT");
		System.out.println(ClientEndpoint.getReceipt());
		String receipt = ClientEndpoint.getReceipt().substring(7);
		String[] listeTopic = receipt.split("\r?\n|\r");
		System.out.println("LISTE DE TOPIC :");
		for(String str : listeTopic) {
			if(!str.equals("^@")&&!str.equals("")){
				if(!topicsSub.containsKey(str)){
					topicsSub.put(str, false);
					System.out.println("Nouvelle topic : " + str);
					combo_listeTopic.getItems().add(str);
				}
			}

		}
		System.out.println("FIN LISTE");

	}

	@FXML
	public void subscribe() throws IOException {
		//subscribe ou unsubscribe
		String destination = (String) combo_listeTopic.getValue();
		if(destination != null){
			if(topicsSub.get(destination)){ // Si je suis abonné alors ...
				int idSuppr = -1;
				for(int indice : idSubscribeTopics.keySet()){
					if(idSubscribeTopics.get(indice).equals(destination)){
						idSuppr = indice;
						break;
					}
				}
				ClientEndpoint.getSes().getBasicRemote().sendText(
						"UNSUBSCRIBE\n" +
								"id:"+ idSuppr + "\n" +
								"^@");
				String old_receipt = ClientEndpoint.getReceipt();
				int i = 0;
				while (ClientEndpoint.getReceipt().equals(old_receipt) && i < 1000){
					old_receipt = ClientEndpoint.getReceipt();
					System.out.println(ClientEndpoint.getReceipt() + " : " + i);
					i++;
				}
				System.out.println();
				System.out.println("ON A CHANGE DE RECEIPT");
				System.out.println(ClientEndpoint.getReceipt());
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
				int indiceSubscribeTopic = 1;
				boolean isNotSub = true;
				while (isNotSub){
					ClientEndpoint.getSes().getBasicRemote().sendText(
							"SUBSCRIBE\n" +
									"id:"+ indiceSubscribeTopic + "\n" +
									"destination:" + destination + "\n" +
									"ack:client\n" +
									"^@");
					String old_receipt = ClientEndpoint.getReceipt();
					int i = 0;
					while (ClientEndpoint.getReceipt().equals(old_receipt) && i < 1000){
						old_receipt = ClientEndpoint.getReceipt();
						System.out.println(ClientEndpoint.getReceipt() + " : " + i);
						i++;
					}
					System.out.println();
					System.out.println("ON A CHANGE DE RECEIPT");
					System.out.println(ClientEndpoint.getReceipt());
					String[] receipt = ClientEndpoint.getReceipt().split("\r?\n|\r");
					if(receipt[0].equals("ERROR")){
						if(receipt[2].equals("message:id already exist")){
							indiceSubscribeTopic = indiceSubscribeTopic + 1;
						}
					} else {
						isNotSub = false;
						idSubscribeTopics.put(indiceSubscribeTopic, destination);
						topicsSub.put(destination, true);
						tabPane_Topics.getTabs().add(new Tab(destination, new Label("Début de la conversation...")));
						btn_aboDesabo.setText("Se désabonner");
					}
				}
			}
		}
	}
	
	@FXML
	public void send() {
		
	}

	@FXML
	public void changeBtnSubscribeLabel(){
		if((String) combo_listeTopic.getValue() != null){
			if(topicsSub.get(combo_listeTopic.getValue())){ // Si je suis abonné alors ...
				btn_aboDesabo.setText("Se désabonner");
			} else {
				btn_aboDesabo.setText("S'abonner");
			}
		}
	}
	
}
