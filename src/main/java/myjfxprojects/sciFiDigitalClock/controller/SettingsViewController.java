package myjfxprojects.sciFiDigitalClock.controller;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import myjfxprojects.sciFiDigitalClock.common.DataBean;
import myjfxprojects.sciFiDigitalClock.common.InternetConn;

public class SettingsViewController implements Initializable {
	
	// Thread safe instance of data bean
	private DataBean dataBean = DataBean.getInstance();
	
	@FXML
    private GridPane rootGridPane;

    @FXML
    private Button btnCloseSettingsView;

    @FXML
    private CheckBox checkBoxProxy;

    @FXML
    private Label lblTempUnits;

    @FXML
    private RadioButton radioBtnCelcius;

    @FXML
    private RadioButton radioBtnFahrenheit;

    @FXML
    private TextField textfieldProxyName;

    @FXML
    private TextField textfieldProxyPort;
    
    @FXML
    private Tooltip tooltipCloseBtn;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// save this controller instance in data bean
		this.dataBean.setSettingsViewController(this);
	}
	
	// close the settings view stage and apply the settings
	@FXML
    void btnCloseOnMouseClicked(MouseEvent event) {
		
		// Make ONLY a new weather data request if really changed application settings by user
		if(DataBean.isAppSettingsChanged) {
			
			// Apply the current proxy settings if check box is selected
			if(this.checkBoxProxy.isSelected()) {
				
				// only if text fields not empty -> apply proxy settings
				if(! (this.textfieldProxyName.getText().trim().isEmpty()) && ! (this.textfieldProxyPort.getText().trim().isEmpty())) {
					
					InternetConn.enableProxySettings(this.textfieldProxyName.getText(), this.textfieldProxyPort.getText());
				}
			}
			else {
				InternetConn.disableProxySettings();
			}
			
			// after changing settings, it's IMPORTANT to check current state of the Internet connectivity again
			InternetConn.startOnceServiceToCheckInternet();
			
			// IMPORTANT: set value that indicates the changing event of settings to false
			DataBean.isAppSettingsChanged = false;
		}
		
		// close the settings view
		this.dataBean.getSettingsViewApp().close();
		
    }
	
	@FXML
    void proxyNameTextfieldOnMouseClicked(MouseEvent event) {
		// select complete text in text field
		this.textfieldProxyName.selectAll();
    }

    @FXML
    void proxyPortTextfieldOnMouseClicked(MouseEvent event) {
    	// select complete text in text field
    	this.textfieldProxyPort.selectAll();
    }
    
    
	// GETTER
	//
	public RadioButton getRadioBtnCelcius() {
		return this.radioBtnCelcius;
	}
	
	public RadioButton getRadioBtnFahrenheit() {
		return this.radioBtnFahrenheit;
	}
	
	public CheckBox getProxyCheckBox() {
		return this.checkBoxProxy;
	}
	
	public TextField getTxtFieldProxyName() {
		return this.textfieldProxyName;
	}
	
	public TextField getTxtFieldProxyPort() {
		return this.textfieldProxyPort;
	}
	
	public Tooltip getTooltipBtnClose() {
		return this.tooltipCloseBtn;
	}
}
