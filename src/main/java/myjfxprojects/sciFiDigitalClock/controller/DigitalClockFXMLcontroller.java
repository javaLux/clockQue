package myjfxprojects.sciFiDigitalClock.controller;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Arc;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import myjfxprojects.sciFiDigitalClock.business.ManageVBoxMiddle;
import myjfxprojects.sciFiDigitalClock.common.DataBean;
import myjfxprojects.sciFiDigitalClock.location.LocationObject;
import myjfxprojects.sciFiDigitalClock.location.SearchLocation;

public class DigitalClockFXMLcontroller implements Initializable {
	
	private DataBean dataBean = DataBean.getInstance();
	
	@FXML
    private Arc arcHourHand;

    @FXML
    private Arc arcMinuteHand;

    @FXML
    private Button btnClose;

    @FXML
    private Button btnMinimize;
    
    @FXML
    private Button btnResizeVboxMiddle;
    
    @FXML
    private Button btnSettings;
    
    @FXML
    private Button btnCrossTextfield;
    
    @FXML
    private Button btnSearchTextfield;

    @FXML
    private HBox buttonsHBox;
    
    @FXML
    private HBox hBoxTxtfieldButtons;

    @FXML
    private StackPane centerStackPane;

    @FXML
    private Circle circleHourHandTip;

    @FXML
    private Circle circleHourHandTrack;

    @FXML
    private Circle circleMinuteHandTip;

    @FXML
    private Circle circleMinuteHandTrack;

    @FXML
    private Circle circleNotVisible;
    
    @FXML
    private ComboBox<LocationObject> comboBoxLocations;

    @FXML
    private ImageView imageViewWeather;
    
    @FXML
    private Label lblDigitalClock;
    
    @FXML
    private Label lblHelperTooltip;
    
    @FXML
    private Label lblCurrentTemp;

    @FXML
    private Label lblMonth;

    @FXML
    private Label lblMonthDay;

    @FXML
    private Label lblWeekDay;
    
    @FXML
    private VBox vBoxMiddle;

    @FXML
    private HBox rootHBox;

    @FXML
    private Text textTempUnit;
    
    @FXML
    private TextField textfieldAddLocation;

    @FXML
    private VBox weatherInfoVBox;
    
    @FXML
    private ProgressIndicator progressIndicator;
    
    @FXML
    private Tooltip tooltipComboBox;
    
    @FXML
    private Tooltip tooltipProgress;
    
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		// save this controller instance in model
		this.dataBean.setDigitalClockFXMLcontroller(this);
	}
	
	// start Event Handling methods
	//
	
	// close application
	@FXML
    void btnCloseMousePressed(MouseEvent event) {
		Platform.exit();
    }

	// go back from add location text field to combo box 
    @FXML
    void btnCrossMousePressed(MouseEvent event) {
    	// hide add location text field and associated buttons
    	this.textfieldAddLocation.setVisible(false);
    	this.hBoxTxtfieldButtons.setVisible(false);
    	
    	// select the last location object before combo box is visible again
		 if(! this.comboBoxLocations.isVisible()) {
			 this.comboBoxLocations.valueProperty().set(this.dataBean.getCustomComboBox().getTheLastSelectedLocation());
			 this.comboBoxLocations.setVisible(true);
		 }
    }

    // minimize application 
    @FXML
    void btnMinimizeMousePressed(MouseEvent event) {
    	this.dataBean.getPrimaryStage().setIconified(true);
    	// check if list view window showing -> if this true than minimize this window too
    	if(this.dataBean.getListViewStage().isShowing()) {
    		this.dataBean.getListViewStage().setIconified(true);
    	}
    	if(this.dataBean.getSettingsViewStage().isShowing()) {
    		this.dataBean.getSettingsViewStage().setIconified(true);
    	}
    }

	// open settings
    @FXML
    void btnSettingsMouseClicked(MouseEvent event) {
    	
    	// show settings view stage
    	this.dataBean.getSettingsViewApp().show();
    }
    
    // resize the VBox middle
    @FXML
    void btnResizeVboxMiddleMouseClicked(MouseEvent event) {
    	
    	// is the VBox middle currently minimized -> than can user maximize the VBox middle
		if (DataBean.isVBoxMiddleMinimize) {
	
			ManageVBoxMiddle.maximizeVBoxMiddle();				
		}		
		// otherwise the VBox middle currently the normal size -> than can user minimize the VBox middle
		else {
			
			ManageVBoxMiddle.minimizeVBoxMiddle();
		}
    }

    // start search for possible location
    @FXML
    void btnSearchMousePressed(MouseEvent event) {
    	SearchLocation searchLocation = SearchLocation.getInstance();
    	searchLocation.searchLocation(this.textfieldAddLocation.getText().trim());	
    }
	
    // select all text when user clicked on text field
    @FXML
    void searchTextfieldOnMouseClicked(MouseEvent event) {
    	this.textfieldAddLocation.selectAll();
    }
    
    // start search for possible location also from Key Press Event on the ENTER Key
	@FXML
    void searchTextfieldOnKeyPressed(KeyEvent event) {
		if(event.getCode() == KeyCode.ENTER) {
			SearchLocation searchLocation = SearchLocation.getInstance();
	    	searchLocation.searchLocation(this.textfieldAddLocation.getText().trim());
		}
    }
	
	/*
	 * GETTER and SETTER Methods
	 */
	public HBox getRootHBox() {
		return this.rootHBox;
	}
	
	public VBox getVBoxMiddle() {
		return this.vBoxMiddle;
	}
	
	public Label getLblDigitalClock() {
		return this.lblDigitalClock;
	}
    
	public Arc getHourHandArc() {
		return this.arcHourHand;
	}
	
	public Circle getHourHandTip() {
		return this.circleHourHandTip;
	}
	
	public Arc getMinuteHandArc() {
		return this.arcMinuteHand;
	}
	
	public Circle getMinuteHandTip() {
		return this.circleMinuteHandTip;
	}
	
	public Button getBtnSettings() {
		return this.btnSettings;
	}
	
	public Button getBtnResizeVboxMiddle() {
		return this.btnResizeVboxMiddle;
	}
	
	public Button getBtnSearchTextfield() {
		return this.btnSearchTextfield;
	}
	
	public Button getBtnCrossTextfield() {
		return this.btnCrossTextfield;
	}
	
	public HBox getTxtfieldButtons() {
		return this.hBoxTxtfieldButtons;
	}
	
	public ComboBox<LocationObject> getComboBoxLocations() {
		return this.comboBoxLocations;
	}

	public Tooltip getTooltipComboBox() {
		return this.tooltipComboBox;
	}

	public ImageView getImageViewWeather() {
		return this.imageViewWeather;
	}

	public void setImageViewWeather(Image weatherImage) {
		this.imageViewWeather.setImage(weatherImage);
	}
	
	public Label getLblHelperTooltip() {
		return this.lblHelperTooltip;
	}

	public Label getLblMonth() {
		return this.lblMonth;
	}

	public void setLblMonth(String month) {
		this.lblMonth.setText(month);
	}

	public Label getLblMonthDay() {
		return this.lblMonthDay;
	}

	public void setLblMonthDay(Integer monthDay) {
		this.lblMonthDay.setText(monthDay.toString());
	}

	public Label getLblWeekDay() {
		return this.lblWeekDay;
	}

	public void setLblWeekDay(String weekDay) {
		this.lblWeekDay.setText(weekDay);
	}
	
	public Text getTextTempUnit() {
		return this.textTempUnit;
	}

	public Label getLblCurrentTemp() {
		return this.lblCurrentTemp;
	}
	
	public ProgressIndicator getProgressIndicator() {
		return this.progressIndicator;
	}
	
	public TextField getTxtFieldLocation() {
		return this.textfieldAddLocation;
	}
	
}
