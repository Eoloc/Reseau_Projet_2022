package fr.ul.miage.Application_ReseauJavaFX;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;

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
	
	
	public Application_ReseauController() {
		tabPane_Topics = new TabPane();
		
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
	}
	
	@FXML
	public void actualiserTopics() {
		
	}

	@FXML
	public void subscribe() {
		//subscribe ou unsubscribe
	}
	
	@FXML
	public void send() {
		
	}
	
}
