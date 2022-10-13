/**
 * 
 */
package myjfxprojects.sciFiDigitalClock.controller;


import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import myjfxprojects.sciFiDigitalClock.common.DataBean;


/**
 * @author Christian
 *
 * FXML Controller class for the locations list view.
 */
public class LocationListViewController implements Initializable {
	
	// DataBean
	private DataBean dataBean = DataBean.getInstance();

	@FXML
    private Button btnCloseListView;
	
    @FXML
    private Label lblChooseLocation;

    @FXML
    private ListView<String> listViewLocations;
    
    @FXML
    private VBox vBoxRootListView;
    
    
    @Override
	public void initialize(URL location, ResourceBundle resources) {
    	// save this controller instance in data bean
    	this.dataBean.setLocationListViewController(this);
	}
    
    /**
     * Handle the event if mouse clicked on btn close
     * @param event
     */
    @FXML
    void btnCloseMousePressed(MouseEvent event) {
    	
    	this.dataBean.getLocationListViewApp().close();
    }

    
    // GETTER
    public ListView<String> getListViewOfLocations() {
    	return this.listViewLocations;
    }

    public VBox getRootVBox() {
    	return this.vBoxRootListView;
    }
}

