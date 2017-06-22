package vue;

import java.awt.Color;

import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class MessageChatVue extends Group{

	public static final String TYPE_NORMAL = "black";
	public static final String TYPE_ERROR = "red";
	public static final String TYPE_VALIDE = "green";
	
	private static final double WIDTH_SEPARATOR = 650;
	
	public MessageChatVue(String date, String auteur, String message) {
		
		Label labelAuteur = new Label(auteur);
		Label labelDate = new Label(date);
		Label labelMessage = new Label(message);
		
		labelAuteur.setFont(Font.font("Verdana", FontWeight.BOLD, 14));
		labelDate.setFont(Font.font("Verdana", FontWeight.NORMAL, 10));
		labelDate.setAlignment(Pos.BOTTOM_CENTER);
		labelDate.setContentDisplay(ContentDisplay.BOTTOM);
		labelDate.setStyle("-fx-text-fill: gray;");
		labelMessage.setFont(Font.font("Verdana", FontWeight.NORMAL, 14));
		
		HBox hbox = new HBox();
		hbox.setSpacing(5);
		hbox.getChildren().addAll(labelAuteur, labelDate);
		
		Separator separator1 = new Separator();
		separator1.setValignment(VPos.CENTER);
		separator1.setPrefWidth(WIDTH_SEPARATOR);
		
		VBox vbox = new VBox();
		vbox.setSpacing(5);
		vbox.getChildren().addAll(hbox, labelMessage, separator1);
		
		//separator1.setMaxWidth(200);
		
		this.getChildren().add(vbox);
	}
	
	public MessageChatVue(String message, String couleur) {
		
		Label labelMessage = new Label(message);
		
		labelMessage.setStyle("-fx-text-fill: "+couleur+";");
		
		Separator separator1 = new Separator();
		separator1.setValignment(VPos.CENTER);
		separator1.setPrefWidth(WIDTH_SEPARATOR);
		
		VBox vbox = new VBox();
		vbox.setSpacing(5);
		vbox.getChildren().addAll(labelMessage, separator1);
		
		this.getChildren().add(vbox);
	}
}
