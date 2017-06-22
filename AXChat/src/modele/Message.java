package modele;

import java.io.Serializable;

public class Message implements Serializable{

	private long mID;
	
	public static final long ID_CHAT_MESSAGE = 1;
	public static final long ID_USERJOINLEFT_MESSAGE = 2;
	public static final long ID_CONNEXION_REQ = 3;
	public static final long ID_CONNEXION_ACK = 4;
	public static final long ID_USER_LIST = 5;
	
	public Message(long id) {
		this.mID = id;
	}

	public long getmID() {
		return mID;
	}

	public void setmID(long mID) {
		this.mID = mID;
	}
	
	
}
