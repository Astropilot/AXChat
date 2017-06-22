package modele;

import java.io.Serializable;

public class ReqConnMessage extends Message implements Serializable {

	private String username;
	private String password;
	
	public ReqConnMessage() {
		super(Message.ID_CONNEXION_REQ);
	}
	
	public ReqConnMessage(String username, String password) {
		super(Message.ID_CONNEXION_REQ);
		this.username = username;
		this.password = password;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	
}
