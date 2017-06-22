package modele;

import java.io.Serializable;

public class AckConnMessage extends Message implements Serializable{

	private long resultType;
	
	public static final long RESULT_OK = 0;
	public static final long RESULT_BAD_ID = 1;
	public static final long RESULT_INTERNAL_ERROR = 2;
	
	public AckConnMessage() {
		super(Message.ID_CONNEXION_ACK);
	}
	
	public AckConnMessage(long resultType) {
		super(Message.ID_CONNEXION_ACK);
		this.resultType = resultType;
	}

	public long getResultType() {
		return resultType;
	}

	public void setResultType(long resultType) {
		this.resultType = resultType;
	}
}
