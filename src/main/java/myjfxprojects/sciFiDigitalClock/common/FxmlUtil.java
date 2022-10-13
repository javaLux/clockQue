package myjfxprojects.sciFiDigitalClock.common;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.image.Image;

public final class FxmlUtil {
	
	// icon for errorBox
	private static final Image iconForErrorBox = new Image("images/wheelChair.png");
	
	/**
	 * Method load an given fxml file 
	 * @param fxmlPath	-> String [path to fxml file]
	 * @return			->	Parent object with loaded fxml file
	 */
	public Parent loadFxmlFile(String fxmlPath) {
		
		// Parent layout container for fxml file
		Parent root = null;
		
		// Error text for msgBox
		final String errorMsg = String.format("Failed to load fxml file: %s", fxmlPath);
		
		if(fxmlPath != null) {
			/*
			 *  getClass.getResources() works with relativ paths
			 *  that means if an file not in the same class as the
			 *  called java class than we must give the path from
			 *  "src" folder as root to the file we will use
			 *  For example:
			 *  /src/package1/package2/file_we_will_use.txt
			 */
			
			try {
				// load fxml file
				root = FXMLLoader.load(this.getClass().getResource(fxmlPath));
				
			} catch (IOException ex) {
				// show info box with exception
				// show info box with exception
				ErrorBox.show(ex, "SciFi-Clock", "An error occurent",
						errorMsg, iconForErrorBox);				
				
			} catch (NullPointerException ex) {
				// show info box with exception
				// show info box with exception
				ErrorBox.show(ex, "SciFi-Clock", "An error occurent",
						errorMsg, iconForErrorBox);				
			}
		}
		
		return root;
	}
}
