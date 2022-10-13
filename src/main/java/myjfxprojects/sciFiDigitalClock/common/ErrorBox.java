package myjfxprojects.sciFiDigitalClock.common;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Optional;

import javafx.geometry.NodeOrientation;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

public class ErrorBox {
	
	
	/**
	 * Method shows a messagebox if an error ocurend and tell users
	 * details
	 * 
	 * @param infoMessage   -> message
	 * @param titleBar      -> title form msgbox
	 * @param headerMessage -> header message
	 * @param img           -> Image for infobox
	 * 
	 * @return if ok button was clicked -> true, else -> false
	 * 
	 */
	public static boolean show(Exception currentEx, String titleBar, String headerMessage,
			String contentMessage, Image img) {

		Alert errorBox = new Alert(AlertType.ERROR);
		
		if (titleBar != null) {
			errorBox.setTitle(titleBar);
		}
		
		if (headerMessage != null) {
			errorBox.setHeaderText(headerMessage);
		}
		
		if (contentMessage != null) {
			errorBox.setContentText(contentMessage);
		}
		
		// shows stacktrace of exception in textarea
		if (currentEx != null) {
			// Create expandable Exception.
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			// gives the current Stacktrace from Exception
			currentEx.printStackTrace(pw);
			// formats current Stacktrace to String object
			String exceptionText = sw.toString();
			
			// Text Area for the stacktrace
			TextArea textArea = new TextArea(exceptionText);
			// text is not editable
			textArea.setEditable(false);
			// text can copied
			textArea.setWrapText(true);
			
			// set orientation of text inside of text area
			textArea.setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);
			
			// set max width and height for the text area
			textArea.setMaxWidth(Double.MAX_VALUE); 
			textArea.setMaxHeight(Double.MAX_VALUE);
			// Abstand zwischen den Elementen in der Text Area festlegen
			GridPane.setVgrow(textArea, Priority.ALWAYS);
			GridPane.setHgrow(textArea, Priority.ALWAYS);
			
			// set stacktrace (from exception) into msgBox
			errorBox.getDialogPane().setExpandableContent(textArea);
		}
		
			
		Stage stage = (Stage) errorBox.getDialogPane().getScene().getWindow();
		if (img != null) {
			stage.getIcons().add(img);
		}
		
		stage.setAlwaysOnTop(true);

		// get the value if ok button is pressed
		Optional<ButtonType> result = errorBox.showAndWait();
		ButtonType button = result.orElse(ButtonType.OK);
		// if ok button was clicked -> return true
		if(button == ButtonType.OK) {
			return true;
		}
		
		return false;
	}

}
