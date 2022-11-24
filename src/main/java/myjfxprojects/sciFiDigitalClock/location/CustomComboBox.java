/**
 * 
 */
package myjfxprojects.sciFiDigitalClock.location;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.skin.ComboBoxListViewSkin;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import myjfxprojects.sciFiDigitalClock.business.Weather;
import myjfxprojects.sciFiDigitalClock.common.DataBean;

/**
 * @author Christian
 *
 * Class to optimize the default combo-box for changing the current location.
 */
public class CustomComboBox {

	private ObservableList<LocationObject> listOfLocations = null;
	private final DataBean dataBean = DataBean.getInstance();
	private ChangeListener<LocationObject> comboBoxListener = null;
	
	// Add location text
	private final String addLocationText = "Add location";
	
	// Add new location object
	private LocationObject addLocationObject = null;
	
	// helper field to set the last selected location object after aborting search new location process correctly
	private LocationObject helperLastSelectedLocation = null;
	
	/**
	 * constructor initialize members
	 */
	public CustomComboBox() {
		
		// First:	create a new pseudo location object as "addLocationItem" to store them in the combo box
		this.addLocationObject = LocationObjectBuilder.getInstance()
				.withFullLocationName("")
				.withReducedLocationName(addLocationText)
				.withLatitude("").withLongitude("")
				.build();
		
		// initialize combo box with the add location value
		this.listOfLocations = FXCollections.observableArrayList(this.addLocationObject);
		
		// after this add all possible location from data bean (read from database)
		this.dataBean.getUserLocationsSet().forEach(location -> {
			this.listOfLocations.add(location);
		});
		
		// add locations list to combo box, to make the available location visible
		this.dataBean.getDigitalClockFXMLcontroller().getComboBoxLocations().setItems(this.listOfLocations);
		
		// initialize ChangeListener
		this.comboBoxListener = new ComboBoxListener();
		// add ChangeListener to the combo box to listen if values changed or will be selected
		this.dataBean.getDigitalClockFXMLcontroller().getComboBoxLocations().getSelectionModel().selectedItemProperty().addListener(this.comboBoxListener);
		
		// We have to initialize and assigned a new skin to the combo box,
		// without this option the ComboBox disappears before the click on the cross Button is registered.
    	ComboBoxListViewSkin<LocationObject> newSkin = new ComboBoxListViewSkin<LocationObject>(this.dataBean.getDigitalClockFXMLcontroller().getComboBoxLocations());
    	// set hiding on false -> IMPORTANT: without this setting the combo box is closed before the click event on the cross button was registry !!!
    	newSkin.setHideOnClick(false);

    	// set either the last active location object or the default location on combo box head
    	if(this.dataBean.getLastActiveLocationObject() != null) {
    		this.dataBean.getDigitalClockFXMLcontroller().getComboBoxLocations().valueProperty().set(this.dataBean.getLastActiveLocationObject());
    	}
    	else {
    		this.dataBean.getDigitalClockFXMLcontroller().getComboBoxLocations().valueProperty().set(this.dataBean.getDefaultLocation());
    	}
    	
    	// set full location name of current selected location object to combo box tool tip
    	this.dataBean.getDigitalClockFXMLcontroller().getTooltipComboBox()
    		.setText(this.dataBean.getDigitalClockFXMLcontroller().getComboBoxLocations().getSelectionModel().getSelectedItem().getFullLocationName());
    	
    	// add cross button to each location object, except the add location item
    	this.initCustomComboBox();
	}
	
	/**
	 * Method makes a customized list view skin of the combo box.
	 */
	public void initCustomComboBox() {
		
		this.dataBean.getDigitalClockFXMLcontroller().getComboBoxLocations().setCellFactory(listView -> 
			
			// create a new customize skin for each list cell, except the add location item
			new CustomListCell()
		);		
	}

	// GETTER
	//
	public ObservableList<LocationObject> getLocationList() {
		return this.listOfLocations;
	}
	
	public LocationObject getTheLastSelectedLocation() {
		return this.helperLastSelectedLocation;
	}
	
	/*
	 * Event handling Combo-Box
	 */
	class ComboBoxListener implements ChangeListener<LocationObject> {

		@Override
		public void changed(ObservableValue<? extends LocationObject> observable, LocationObject oldLocation,
				LocationObject newLocation) {
			
			// hide combo box and open add location text field with
			// associated buttons by clicking on add location object
			if(newLocation.equals(addLocationObject)) {
				dataBean.getDigitalClockFXMLcontroller().getComboBoxLocations().setVisible(false);
				dataBean.getDigitalClockFXMLcontroller().getTxtFieldLocation().setVisible(true);
				dataBean.getDigitalClockFXMLcontroller().getTxtfieldButtons().setVisible(true);
				
				if(oldLocation != null) {
					// save the last selected location object from combo box
					// IMPORTANT:
					// to select this location when user press the cross button to abort the search new location process
					// otherwise the add location object is visible 
					helperLastSelectedLocation = oldLocation;
				}
			}
			else {
				// first:	change the combo box tool tip to the current selected location full name
				dataBean.getDigitalClockFXMLcontroller().getTooltipComboBox().setText(newLocation.getFullLocationName());
				
				// second:	save new geo data for selected location object in data bean
				dataBean.setLatitude(newLocation.getLatitude());
				dataBean.setLongitude(newLocation.getLongitude());
				
				// third:	fetch new weather data and set time zone for the selected location object
				//			and update tool tip from digital clock with current time zone info's and
				//			the date info's will be updated.
				//			Last but not least, calculate current time shift in hours.
				//			IMPORTANT:	This step will execute in a separate timeline, because
				//						time zone for this new location object is only available after the weather data
				//						API call was successfully done.
				Weather.getInstance().fetchWeatherDataAndSetTimeZone(newLocation);
				
				// fourth:	mark the current selected location item in database to find out at the next application start
				//			which location was selected at last by user
				//			At first set all location items to false (isLastSelected Column)
				dataBean.getJooqDbApi().setAllLocationsAsNotLastSelected();
				//			Then mark the current location item as "isLastSelected" in database
				dataBean.getJooqDbApi().setThisLocationAsLastSelected(newLocation);
				
			}
		}
	}
	
	// customize each list cell (expect the add location item) with a cross button to remove location from
	// combo box
	class CustomListCell extends ListCell<LocationObject> {
		
		// This is the node that will display the text and the cross button 
        private HBox graphic;
        // cross btn
        private Button crossBtn;
        // Label for the text
        private Label lblText;
        
        // constructor initialize members and set button and label for each list cell
        public CustomListCell() {
        	// Label for the text (reduce location name from Location Object)         	
        	lblText = new Label();
        	
            // make the text fill white
        	lblText.setTextFill(Color.WHITE);
            // Set max width to infinity so the cross is all the way to the right. 
        	lblText.setMaxWidth(Double.POSITIVE_INFINITY);
            // We have to modify the hiding behavior of the ComboBox to allow clicking on the Button, 
            // so we need to hide the ComboBox when the label is clicked (item selected). 
        	lblText.setOnMouseClicked(event -> dataBean.getDigitalClockFXMLcontroller().getComboBoxLocations().hide());
            
            // create button with cross icon
        	crossBtn = new Button("", new ImageView(new Image("images/cross.png")));
            crossBtn.setCursor(Cursor.HAND);
            crossBtn.setStyle("-fx-background-color: transparent");
            crossBtn.setOnMouseClicked(event -> {
            	// Since the ListView reuses cells, we need to get the item first, before making changes.  
                LocationObject item = getItem();
                
                // ONLY if the location that will be removed is currently not selected -> allow user to remove this
                // to remove a selected item from list view -> will be thrown a NullPointerException
                if (! isSelected()) {
                    // remove the location object from SET (DataBean)
                	if(dataBean.getUserLocationsSet().contains(item)) {
                		dataBean.getUserLocationsSet().remove(item);
                	}
                	// remove location object from list view of the combo box
                    dataBean.getDigitalClockFXMLcontroller().getComboBoxLocations().getItems().remove(item);
                    
                    // DELETE the location from database
                    dataBean.getJooqDbApi().deleteExistingLocation(item.getFullLocationName(), item.getLatitude(), item.getLongitude());
                }
            });
            
            // Arrange controls in a HBox, and set display to graphic only (the text is included in the graphic in this implementation).
            graphic = new HBox(lblText, crossBtn);
            graphic.setAlignment(Pos.CENTER_LEFT);
            HBox.setHgrow(lblText, Priority.ALWAYS);
            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            
            // IMPORTANT: check on Mouse entered event if the item where mouse entered is currently selected!!!
            // if this true -> disable cross button, because it is not allowed to remove a selected item
            graphic.setOnMouseEntered(event -> {
            	if(isSelected()) {
            		crossBtn.setDisable(true);
            	} else {
            		crossBtn.setDisable(false);
            	}
            });
        }
        
        /**
         * This Method will be updated all items they are stored in the list view of the combo box.
         */
        @Override
        protected void updateItem(LocationObject item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setGraphic(null);
                
            } else if(item.getReducedLocationName() == addLocationText) {
            	// give only the location items a cross button, not the add location item
                setGraphic(new Label(item.getReducedLocationName()));
                
            } else {
            	// IMPORTANT: 	Update each item in combo box list view with the reduced location name, to give the user
            	//				more details over the location objects which stored in the combo box
            	lblText.setText(HandleLocationName.reduceLocationName(item.getFullLocationName(), false));
            	setGraphic(graphic);
            }          
        }
	}
}
