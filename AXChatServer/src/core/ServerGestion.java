package core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import modele.AckConnMessage;
import modele.Message;
import modele.ReqConnMessage;
import modele.UserJoinLeftMessage;
import modele.UserListMessage;

public class ServerGestion implements Runnable {
	
	public Socket socket;
	private List<Message> listMessages;
	private HashMap<ServerGestion, String> listClients;
	
	public String pseudo;
	
	public ObjectInputStream fluxIn;
	public ObjectOutputStream fluxOut;
	
	public ServerGestion(Socket s, List<Message> listMessages, HashMap<ServerGestion, String> listClients) {
		this.socket = s;
		this.listMessages = listMessages;
		this.listClients = listClients;
	}

	public void run() {
		Message messageLu;
		
		/*
		C->S: Ask connexion
		C->S: Send username + password
		S->C: Send response (OK, BAD, ERROR)
		S->C: Send userList if OK
		*/
		
		try {
			fluxIn = new ObjectInputStream(socket.getInputStream());
			fluxOut = new ObjectOutputStream(socket.getOutputStream());
			
			// 1. Le serveur recoit le pseudo
			
			Message msg = (Message)fluxIn.readObject();
			ReqConnMessage messageConnexion = new ReqConnMessage();
			if(msg.getmID() == Message.ID_CONNEXION_REQ)
				messageConnexion = (ReqConnMessage) msg;
			
			// TODO: Checker username + password avec base de données SQL
			
			pseudo = messageConnexion.getUsername();
			
			boolean compteExiste = false;
			if(isValid(pseudo, messageConnexion.getPassword()))
				compteExiste = true;
			
			if(!compteExiste) {
				Message message = new AckConnMessage(AckConnMessage.RESULT_BAD_ID);
				fluxOut.writeObject(message);
				try {
					fluxIn.close();
					fluxOut.close();
					socket.close();
				} catch (IOException e) {
				}
				return;
			}
			else {
				Message message = new AckConnMessage(AckConnMessage.RESULT_OK);
				fluxOut.writeObject(message);
			}
			
			listClients.put(this, pseudo);
			System.out.println(pseudo+" est connecté !");
			
			// Broadcast message pour le nouveau venu
			
			for(ServerGestion client : listClients.keySet()) {
				if(!client.pseudo.equals(pseudo)) {
					ObjectOutputStream tempfluxOut = client.fluxOut;
					Message message = new UserJoinLeftMessage(UserJoinLeftMessage.ACTION_JOIN, pseudo);
					tempfluxOut.writeObject(message);
				}
			}

			// 2. Le serveur envois la liste des personnes connectées
			
			Message messageList = new UserListMessage(listClients.values());

			fluxOut.writeObject(messageList);
			fluxOut.flush();
			
			// 3. Le serveur envois les messages recus
			
			while(true) {
			
				messageLu = (Message)fluxIn.readObject();
				//System.out.println(messageLu.toString());
				if(messageLu.getmID() == Message.ID_CHAT_MESSAGE) {
					for(ServerGestion client : listClients.keySet()) {
						ObjectOutputStream tempfluxOut = client.fluxOut;
						tempfluxOut.writeObject(messageLu);
					}
				}
			
				//System.out.println(messageLu);
			}
			
		} catch (ClassNotFoundException parException) {
			System.err.println(parException.toString());
			System.exit(1);
		} catch (IOException parException) {
			System.out.println(pseudo+" s'est déconnecté !");
			listClients.remove(this);
			
			try {
				fluxIn.close();
				fluxOut.close();
				socket.close();
			} catch (IOException e) {
			}
			
			deconnect();
		}
	}
	
private static boolean isValid(String login, String pass) {

        boolean connexion = false;
        try {
            Scanner sc = new Scanner(new File("accounts.txt"));
            while(sc.hasNext()){
                if(sc.nextLine().equals(login+" "+pass)){
                  connexion=true;
                  break;
                }
             }
        } catch (FileNotFoundException e) {
            System.err.println("Le fichier n'existe pas !");
        }
    return connexion;
    }
	
	public void deconnect() {
		try {
		for(ServerGestion client : listClients.keySet()) {
				ObjectOutputStream tempfluxOut = client.fluxOut;
				Message message = new UserJoinLeftMessage(UserJoinLeftMessage.ACTION_LEFT, pseudo);
				tempfluxOut.writeObject(message);
		}
		} catch(IOException e) {
			
		}
	}
	
}
