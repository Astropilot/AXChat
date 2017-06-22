package modele;

import java.io.Serializable;

public class ChatMessage extends Message implements Serializable{
	
	private String date;
	private String auteur;
	private String message;
	
	public ChatMessage(String date, String auteur, String message) {
		super(Message.ID_CHAT_MESSAGE);
		this.date = date;
		this.auteur = auteur;
		this.message = message;
	}

	public String getDate() {
		return date;
	}

	public String getAuteur() {
		return auteur;
	}

	public String getMessage() {
		return message;
	}

	public String toString() {
		return "["+date+"] "+auteur+": "+message;
	}
}
