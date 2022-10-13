/**
 * 
 */
package myjfxprojects.sciFiDigitalClock.business;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import myjfxprojects.sciFiDigitalClock.common.DataBean;
import myjfxprojects.sciFiDigitalClock.common.ETempUnits;
import myjfxprojects.sciFiDigitalClock.common.FxmlUtil;
import myjfxprojects.sciFiDigitalClock.common.Numeric;

/**
 * @author Christian
 *
 */
public class SettingsViewApp {

	// Databean
	private DataBean dataBean = DataBean.getInstance();
	
	// Stage for location list view
	private Stage settingsViewStage = null;
	
	// root layout container
	private Parent root = null;
	
	// Toggle group radio buttons
	private ToggleGroup toggleGroup = null;
	
	/**
	 * Constructor load the responsible controller class
	 * and initialize necessary conditions to show and close this stage.
	 */
	public SettingsViewApp() {
		
		// initialize own FXML loader
		FxmlUtil fxmlUtil = new FxmlUtil();
		
		// load FXML file to root layout container with self made
    	// FxmlUtil class 
    	this.root = fxmlUtil.loadFxmlFile("/fxml/SettingsView.fxml");
    	
    	if(this.root != null) {
    		
    		this.settingsViewStage = new Stage();
    		
    		// save settings view stage in dataBean
    		this.dataBean.setSettingsViewStage(this.settingsViewStage);
    		
    		Scene scene = new Scene(this.root);
    		
    		// load specific Style sheet file
    		scene.getStylesheets().add(this.getClass().getResource("/css/settingsView.css").toExternalForm());
    		
    		this.settingsViewStage.setScene(scene);
    		
    		// set the undecorated stage -> this means without frame
    		this.settingsViewStage.initStyle(StageStyle.TRANSPARENT);
        	scene.setFill(Color.TRANSPARENT);
        	
        	// set icon for dock and main window
        	this.settingsViewStage.getIcons().add(new Image("images/clock.png"));
        	// set window name
        	this.settingsViewStage.setTitle("Einstellungen");
        	
        	// set user data to each radio button -> that means assigned the correct enum to the radio  button
        	this.dataBean.getSettingsViewController().getRadioBtnCelcius().setUserData(ETempUnits.METRIC);
        	this.dataBean.getSettingsViewController().getRadioBtnFahrenheit().setUserData(ETempUnits.IMPERIAL);
        	
        	// add both radio buttons to a toggle group -> to be selected only once
        	this.toggleGroup = new ToggleGroup();
        	this.dataBean.getSettingsViewController().getRadioBtnCelcius().setToggleGroup(toggleGroup);
        	this.dataBean.getSettingsViewController().getRadioBtnFahrenheit().setToggleGroup(toggleGroup);
        	
        	// set value of show delay from tool tip
        	this.dataBean.getSettingsViewController().getTooltipBtnClose().setShowDelay(Duration.millis(150.0));
        	
        	// select the Radio Button for the temperature unit which was the last selection from user
        	switch (this.dataBean.getCurrentTempUnit()) {
			case METRIC:
				this.toggleGroup.selectToggle(this.dataBean.getSettingsViewController().getRadioBtnCelcius());
				break;

			case IMPERIAL:
				this.toggleGroup.selectToggle(this.dataBean.getSettingsViewController().getRadioBtnFahrenheit());
				break;
			}
        	
        	// set proxy settings values (reading from database) in the related text fields
        	this.dataBean.getSettingsViewController().getTxtFieldProxyName().setText(this.dataBean.getProxyName());
        	this.dataBean.getSettingsViewController().getTxtFieldProxyPort().setText(this.dataBean.getProxyPort());
        	
        	// Add event filter to handle close event for this stage
        	// IMPORTANT: to enable button for the setting view if this stage
        	// will close in a another way as to use the close button
        	this.settingsViewStage.addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, event -> {
        		
        		this.dataBean.getDigitalClockFXMLcontroller().getBtnSettings().setDisable(false);
        	});
        	
        	// Add Event Handling check box
        	this.dataBean.getSettingsViewController().getProxyCheckBox().selectedProperty().addListener(new CheckBoxChangeListener());
        	
        	// Add Event Handling toggle group
        	this.toggleGroup.selectedToggleProperty().addListener(new ToggleGroupChangeListener());
        	
        	// Add Event Handling to text field proxy name to mark changes on the proxy settings
        	this.dataBean.getSettingsViewController().getTxtFieldProxyName().textProperty().addListener(new TxtFieldProyNameChangeListener());
        	
        	// Add Event Handling for text field Proxy Port, to ensure that ONLY numeric values are allowed
        	this.dataBean.getSettingsViewController().getTxtFieldProxyPort().textProperty().addListener(new TxtFieldProyPortChangeListener());
        	
        	//IMPORTANT that this line of code placed after the Event handling to enable or disable check box and text fields 
        	// -> dependent on the value from database       	
        	this.dataBean.getSettingsViewController().getProxyCheckBox().setSelected(DataBean.isProxyEnabled);
    	}
	}
	
	/**
	 * Show this stage and set the position under the main window.
	 */
	public void show() {
		// set this stage under the primary stage
		this.settingsViewStage.getScene().getWindow().setX(DataBean.current_X_Pos_MainWindow);
		this.settingsViewStage.getScene().getWindow().setY(DataBean.current_Y_Pos_MainWindow + this.dataBean.getPrimaryStage().getHeight());
		
    	this.settingsViewStage.show();       	
    	this.settingsViewStage.setResizable(false);
    	
    	// disable btn to open settings
    	if((this.settingsViewStage != null) && (this.settingsViewStage.isShowing())) {
    		
    		this.dataBean.getDigitalClockFXMLcontroller().getBtnSettings().setDisable(true);
    	}
	}
	
	/**
	 * Close this stage and enable btn search, btn cross and the text field.
	 */
	public void close() {
		
		this.settingsViewStage.close();
		
		if((this.settingsViewStage != null) && !(this.settingsViewStage.isShowing())) {
    		
			this.dataBean.getDigitalClockFXMLcontroller().getBtnSettings().setDisable(false);
    	}
	}
	
	// GETTER
	public ToggleGroup getToggleGroup() {
		return this.toggleGroup;
	}
	
	//
	// Event handling
	//
	//
	// Listener for the check box to use Proxy
	class CheckBoxChangeListener implements ChangeListener<Boolean> {

		@Override
		public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
			
			// mark the changed settings event in data bean
			DataBean.isAppSettingsChanged = true;
			
			// dependent on check box state(is selected or not) -> enable or disable text fields for proxy settings
			// and set state in data bean
			if(observable.getValue()) {
				dataBean.getSettingsViewController().getTxtFieldProxyName().setDisable(false);
				dataBean.getSettingsViewController().getTxtFieldProxyPort().setDisable(false);
				DataBean.isProxyEnabled = true;
			}
			else {
				dataBean.getSettingsViewController().getTxtFieldProxyName().setDisable(true);
				dataBean.getSettingsViewController().getTxtFieldProxyPort().setDisable(true);
				DataBean.isProxyEnabled = false;
			}
		}
		
	}
	
	// Listener for the toggle group of radio buttons to select the temperature unit like Celcius or Fahrenheit
	class ToggleGroupChangeListener implements ChangeListener<Toggle> {

		@Override
		public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {

			// Only if selected radio button will changed -> set new temp unit value in data bean
			dataBean.setCurrentTempUnit((ETempUnits) toggleGroup.getSelectedToggle().getUserData());
			// mark the changed settings event in data bean
			DataBean.isAppSettingsChanged = true;
		}
		
	}
	
	// Change Listener for the Proxy Port text field, to check on non digit chars and mark if user changed the proxy settings 
	class TxtFieldProyPortChangeListener implements ChangeListener<String> {

		@Override
		public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
			
			// mark the changed settings event in data bean
			DataBean.isAppSettingsChanged = true;
			
			// check if new value is not null or empty
			if((newValue != null) && (! newValue.isEmpty())) {
				
				// replace all non digit chars from Proxy Port text field
				dataBean.getSettingsViewController().getTxtFieldProxyPort().setText(Numeric.removeAllNotDigitChars(newValue));
			}			
		}
	}
	
	// Change Listener for the Proxy Name text field, to mark if user changed the proxy settings
	class TxtFieldProyNameChangeListener implements ChangeListener<String> {

		@Override
		public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
			
			// mark the changed settings event in data bean
			DataBean.isAppSettingsChanged = true;
		}
	}
	
}
