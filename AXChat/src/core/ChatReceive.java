package core;

import java.io.IOException;
import java.io.ObjectInputStream;

import javafx.application.Platform;
import modele.ChatMessage;
import modele.Message;
import modele.UserJoinLeftMessage;
import vue.FenetrePrincipale;
import vue.MessageChatVue;


public class ChatReceive implements Runnable{

	ObjectInputStream fluxIn;
	FenetrePrincipale mainFenetre;
	
	public ChatReceive(ObjectInputStream in, FenetrePrincipale mainFenetre) {
		this.fluxIn = in;
		this.mainFenetre = mainFenetre;
	}
	
	public void run() {
		
		try {
			
			while(true) {
				
				Message messageRecu = (Message) fluxIn.readObject();
				if(messageRecu.getmID() == Message.ID_CHAT_MESSAGE) {
					ChatMessage msg = (ChatMessage) messageRecu;
					Platform.runLater(() -> {
						mainFenetre.ecrireMessageChat(new MessageChatVue(msg.getDate(), msg.getAuteur(), msg.getMessage()));
					});
				}
				else if(messageRecu.getmID() == Message.ID_USERJOINLEFT_MESSAGE) {
					UserJoinLeftMessage msg = (UserJoinLeftMessage) messageRecu;
					if(msg.getAction() == UserJoinLeftMessage.ACTION_JOIN) {
						mainFenetre.listClients.add(msg.getUsername());
						Platform.runLater(() -> {
							mainFenetre.ecrireMessageInfo(msg.getUsername() + " viens de se connecter !", MessageChatVue.TYPE_VALIDE);
						});
					}
					else if(msg.getAction() == UserJoinLeftMessage.ACTION_LEFT) {
						mainFenetre.listClients.remove(msg.getUsername());
						Platform.runLater(() -> {
							mainFenetre.ecrireMessageInfo(msg.getUsername() + " viens de se déconnecter !", MessageChatVue.TYPE_ERROR);
						});
					}
				}
				
				Platform.runLater(() -> {
					mainFenetre.updateListClient();
				});
			}
			
		} catch (ClassNotFoundException | IOException e) {
			
		}
		
	}

}
