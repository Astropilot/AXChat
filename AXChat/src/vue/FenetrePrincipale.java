package vue;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import core.ChatReceive;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import modele.AckConnMessage;
import modele.ChatMessage;
import modele.Message;
import modele.ReqConnMessage;
import modele.UserListMessage;

public class FenetrePrincipale extends Application{
	
	// Composants graphiques
	
	Button btn_connexion = new Button("Connexion");
	Button btn_deconnexion = new Button("Deconnexion");
    Button btn_envoisMessage = new Button("Envoyer");
    
    TextField txt_pseudo = new TextField();
    PasswordField txt_password = new PasswordField();
    TextField txt_message = new TextField();
    
    //TextArea txta_messages = new TextArea();
    VBox box_messages = new VBox();
    //TextArea txta_clients = new TextArea();
    ListView<String> list_clients = new ListView<String>();
    
    // Client (socket + thread + stream)
    
    private static Socket socket = null;
    private static Thread t1;
    private static final int PORT = 1555;
    private static ObjectOutputStream fluxOut;
    private static ObjectInputStream fluxIn;
    
    public List<String> listClients = new ArrayList<String>();
    ObservableList<String> items;
	
	public static void main(String[] args) {
        Application.launch(FenetrePrincipale.class, args);
    }

    public void start(Stage primaryStage) {

        primaryStage.setTitle("AXChat - Client");

        Group root = new Group();

        Scene scene = new Scene(root, 900, 650, Color.LIGHTGRAY);
        
        BorderPane borderPane = new BorderPane();
        
        btn_deconnexion.setDisable(true);
        btn_envoisMessage.setDisable(true);
        
        //txta_messages.setEditable(false);
        box_messages.setSpacing(5);
        txt_message.setPrefWidth(600);
        //txta_clients.setPrefWidth(200);
        list_clients.setPrefWidth(200);
        
        final HBox hbox = new HBox();
        hbox.setSpacing(5);
        hbox.getChildren().addAll(txt_pseudo, txt_password, btn_connexion, btn_deconnexion);
        
        final HBox hbox2 = new HBox();
        hbox2.setSpacing(5);
        hbox2.getChildren().addAll(txt_message, btn_envoisMessage);
        
        ScrollPane sp = new ScrollPane();
        sp.setContent(box_messages);
        sp.vvalueProperty().bind(box_messages.heightProperty());
        sp.setHbarPolicy(ScrollBarPolicy.NEVER);
        sp.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);

        borderPane.prefHeightProperty().bind(scene.heightProperty());
        borderPane.prefWidthProperty().bind(scene.widthProperty());
        
        borderPane.setPadding(new Insets(10, 10, 10, 10));
        //borderPane.setMargin(txta_messages, new Insets(10, 10, 10, 0));
        borderPane.setMargin(sp, new Insets(10, 10, 10, 0));
        
        borderPane.setTop(hbox);
        //borderPane.setCenter(txta_messages);
        borderPane.setCenter(sp);
        //borderPane.setRight(txta_clients);
        borderPane.setRight(list_clients);
        borderPane.setBottom(hbox2);
        
        root.getChildren().add(borderPane);     

        primaryStage.setScene(scene);

        primaryStage.show();
        
        primaryStage.setOnCloseRequest(event -> {
        	stopClient();
        	Platform.exit();
        	System.exit(0);
        });
        
        btn_connexion.setOnAction(this::handleConnexion);
        btn_envoisMessage.setOnAction(this::handleEnvoisMessage);
        btn_deconnexion.setOnAction(this::handleDeconnexion);
        
        txt_message.setOnKeyPressed(event -> {
        	if(event.getCode().equals(KeyCode.ENTER))
        		handleEnvoisMessage(null);
        });

    }

    private void startClient() {
    	try {
    		ecrireMessageInfo("Connexion au serveur...", MessageChatVue.TYPE_NORMAL);
			socket = new Socket("127.0.0.1",PORT);
			ecrireMessageInfo("Connexion réussie !", MessageChatVue.TYPE_VALIDE);
			
			fluxOut = new ObjectOutputStream(socket.getOutputStream());
			fluxIn = new ObjectInputStream(socket.getInputStream());
			
			// 1. Envois du pseudo
	        
			Message message = new ReqConnMessage(txt_pseudo.getText(), txt_password.getText());
			
	        fluxOut.writeObject(message);
	        fluxOut.flush();
	        
	        // Réception de la réponse de connexion
	        
	        message = (Message) fluxIn.readObject();
	        if(message.getmID() != Message.ID_CONNEXION_ACK) {
	        	stopClient();
	        	return;
	        }
	        AckConnMessage connMessage = (AckConnMessage) message;
	        if(connMessage.getResultType() == AckConnMessage.RESULT_BAD_ID) {
	        	ecrireMessageInfo("Erreur, les identifiants sont incorrects !", MessageChatVue.TYPE_ERROR);
	        	stopClient();
	        	return;
	        }
	        else if(connMessage.getResultType() == AckConnMessage.RESULT_INTERNAL_ERROR) {
	        	ecrireMessageInfo("Erreur, une erreur interne au serveur est survenue !", MessageChatVue.TYPE_ERROR);
	        	stopClient();
	        	return;
	        }
	        
	        // 2. Reception de la liste des dialogeurs
	        
	        message = (Message) fluxIn.readObject();
	        if(message.getmID() != Message.ID_USER_LIST) {
	        	ecrireMessageInfo("Erreur pendant la réception de la liste des connectées !", MessageChatVue.TYPE_ERROR);
	        	stopClient();
	        	return;
	        }
	        UserListMessage listMessage = (UserListMessage) message;
	        listClients = listMessage.getDialogeurs();
	        
	        updateListClient();
	        
	        // 3. Réception des messages
	        
	        t1 = new Thread(new ChatReceive(fluxIn, this));
	        t1.start();
			
		} catch (IOException e) {
			ecrireMessageInfo("Impossible de se connecter au serveur !", MessageChatVue.TYPE_ERROR);
			btn_connexion.setDisable(false);
	    	btn_deconnexion.setDisable(true);
		} catch (ClassNotFoundException e) {
			ecrireMessageInfo("Erreur dans la réception des données du serveur !", MessageChatVue.TYPE_ERROR);
			btn_connexion.setDisable(false);
	    	btn_deconnexion.setDisable(true);
		}
    }
    
    private void stopClient() {
    	btn_connexion.setDisable(false);
    	btn_deconnexion.setDisable(true);
    	btn_envoisMessage.setDisable(true);
    	try {
    		if(t1 != null)
    			t1.stop();
    		if(fluxOut != null)
    			fluxOut.close();
    		if(fluxIn != null)
    			fluxIn.close();
    		if(socket != null)
    			socket.close();
    		listClients.clear();
		} catch (IOException e) {
		}
    	updateListClient();
    	ecrireMessageInfo("Vous êtes bien déconnecté !", MessageChatVue.TYPE_ERROR);
    }
    
    public void handleConnexion(ActionEvent event) {
    	btn_connexion.setDisable(true);
    	btn_deconnexion.setDisable(false);
    	btn_envoisMessage.setDisable(false);
    	startClient();
    }
    
    public void handleEnvoisMessage(ActionEvent event) {
    	if(txt_message.getText().length() < 1 || onlySpace(txt_message.getText()))
    		return;
    	
    	String messageConsole = txt_message.getText();
    	DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    	Date dated = new Date();
    	String date = dateFormat.format(dated);
    	
    	Message message = new ChatMessage(date, txt_pseudo.getText(), messageConsole);
    	
    	try {
			fluxOut.writeObject(message);
			fluxOut.flush();
			txt_message.clear();
		} catch (IOException e) {
			ecrireMessageInfo("Votre message n'a pas pu être envoyé !", MessageChatVue.TYPE_ERROR);
		}
    }
    
    private boolean onlySpace(String str) {
    	for(char s : str.toCharArray()) {
    		if(s != ' ')
    			return false;
    	}
    	return true;
    }
    
    public void handleDeconnexion(ActionEvent event) {
    	stopClient();
    }
    
    public void ecrireMessageChat(MessageChatVue message) {
    	box_messages.getChildren().add(message);
    	//txta_messages.appendText(message + "\n");
    }
    
    public void ecrireMessageInfo(String message, String typeMessage) {
    	box_messages.getChildren().add(new MessageChatVue(message, typeMessage));
    }
    
    public void updateListClient() {
    	items =FXCollections.observableArrayList (listClients);
    	list_clients.setItems(items);
    }
}
