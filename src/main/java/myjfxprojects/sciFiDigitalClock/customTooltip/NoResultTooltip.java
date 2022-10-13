package myjfxprojects.sciFiDigitalClock.customTooltip;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.OverrunStyle;
import javafx.scene.control.Tooltip;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import myjfxprojects.sciFiDigitalClock.common.DataBean;

/**
 * Class for the NoResultTooltip
 * This Tool tip will automatically hide if main windows will moved with mouse on desktop!!!
 * Managed in UndecoratedWindow class!!!
 * @author Christian
 *
 */
public class NoResultTooltip extends Tooltip{
	
	// Singleton instance of this tool tip
	private static NoResultTooltip instance = null;
	
	// Thread safe instance of data bean
	private DataBean dataBean = DataBean.getInstance();
	
	
	/**
	 * Constructor initialize tool tip
	 */
	private NoResultTooltip() {
		
		this.setText("Keine Ergebnisse gefunden");
		this.setTextOverrun(OverrunStyle.ELLIPSIS);
		this.setFont(Font.font("Calibri", FontWeight.BOLD, 13.0));
	}
	
	/**
	 * GETTER for an Singleton of this class
	 * @return
	 */
	public static NoResultTooltip getInstance() {
		if(instance == null) {
	
			instance = new NoResultTooltip();	
		}
		
		return instance;
	}
	
	/**
	 * Method make no result tool tip visible and hide them if new input on text field will detected.
	 */
	public void showNoResultTooltip() {
		
		// x and y coordinates to set tool tip on the right place over the text field
		double xPosToShowTooltip = this.dataBean.getDigitalClockFXMLcontroller().getTxtFieldLocation().getScene().getWindow().getX() + 
				this.dataBean.getDigitalClockFXMLcontroller().getTxtFieldLocation().getLayoutX() + this.dataBean.getDigitalClockFXMLcontroller().getTxtFieldLocation().getWidth() - 25.0;
		
		double yPosToShowTooltip = this.dataBean.getDigitalClockFXMLcontroller().getTxtFieldLocation().getScene().getWindow().getY() +
				this.dataBean.getDigitalClockFXMLcontroller().getTxtFieldLocation().getLayoutY() + this.dataBean.getDigitalClockFXMLcontroller().getTxtFieldLocation().getHeight() - 42.0;
		
		this.show(this.dataBean.getDigitalClockFXMLcontroller().getTxtFieldLocation(), xPosToShowTooltip, yPosToShowTooltip);
		
		// register new change listener on text field(for searching new location)
		this.dataBean.getDigitalClockFXMLcontroller().getTxtFieldLocation().textProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue,
					String newValue) {
				
				// if value of text field for search location changed -> hide tool tip
				NoResultTooltip.this.hide();
			}
		});
		
		// Hide this tool tip also if main window will be minimize or lost the focus!!!
		this.dataBean.getPrimaryStage().focusedProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				
				NoResultTooltip.this.hide();			
			}
		});
	}
}
