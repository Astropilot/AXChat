package modele;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class UserListMessage extends Message implements Serializable {

	private List<String> dialogeurs;
	
	public UserListMessage() {
		super(Message.ID_USER_LIST);
	}
	
	public UserListMessage(Collection<String> listUsers) {
		super(Message.ID_USER_LIST);
		dialogeurs = new ArrayList<String>();
		for(String valeur : listUsers) {
			dialogeurs.add(valeur);
		}
	}

	public List<String> getDialogeurs() {
		return dialogeurs;
	}

	public void setDialogeurs(List<String> dialogeurs) {
		this.dialogeurs = dialogeurs;
	}

	
}
