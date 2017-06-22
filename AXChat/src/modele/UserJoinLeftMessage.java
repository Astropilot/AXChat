package modele;

import java.io.Serializable;

public class UserJoinLeftMessage extends Message implements Serializable{

	private long action;
	private String username;
	
	public static final long ACTION_JOIN = 0;
	public static final long ACTION_LEFT = 1;
	
	public UserJoinLeftMessage() {
		super(Message.ID_USERJOINLEFT_MESSAGE);
	}

	public UserJoinLeftMessage(long action, String username) {
		super(Message.ID_USERJOINLEFT_MESSAGE);
		this.action = action;
		this.username = username;
	}

	public long getAction() {
		return action;
	}

	public void setAction(long action) {
		this.action = action;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
	
}
