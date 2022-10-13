/**
 * 
 */
package myjfxprojects.sciFiDigitalClock.customTooltip;

import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Duration;
import myjfxprojects.sciFiDigitalClock.common.DataBean;

/**
 * @author Christian
 * 
 * Custom Tool tip to show current time zone and time shift.
 * This tool tip is assigned to digital clock label.
 */
public class TimeZoneTooltip extends Tooltip{

	// Thread safe singleton instance of dataBean
	private DataBean dataBean = DataBean.getInstance();
	
	// font family for description labels
	private final Font fontDescription = new Font("Calibri Bold Italic", 13.0);
	// font family for value labels
	private final Font fontValues = new Font("Calibri Bold", 13.0);
	
	// Strings for labels in the tool tip
	private final String timeZoneText = "Zeitzone:";
	private final String timeShiftText = "Zeitverschiebung:";
	
	// Labels for this tool tip
	private Label lblTimeZoneText = new Label(timeZoneText);
	private Label lblTimeShiftText = new Label(timeShiftText);
	private Label lblTimeZoneValue = new Label();
	private Label lblTimeShiftValue = new Label();
		
	public TimeZoneTooltip() {
		
		// optimize the show delay, to show this tool tip in a faster way
		this.setShowDelay(Duration.millis(500.0));
		
		// set font to the labels
		this.lblTimeZoneText.setFont(this.fontDescription);
		this.lblTimeShiftText.setFont(this.fontDescription);
		this.lblTimeZoneValue.setFont(this.fontValues);
		this.lblTimeShiftValue.setFont(this.fontValues);
		
		// set custom color to the description labels
		this.lblTimeZoneText.setTextFill(Color.web("#9f9ea8"));
		this.lblTimeShiftText.setTextFill(Color.web("#9f9ea8"));
		
		// root layout manager
		VBox root = new VBox(5.0);
		
		// HBox for time zone info's
		HBox hBoxTimeZoneInfo = new HBox(5.0);
		
		// HBox for time shift info's
		HBox hBoxTimeShiftInfo = new HBox(5.0);
		
		hBoxTimeZoneInfo.getChildren().addAll(this.lblTimeZoneText, this.lblTimeZoneValue);
		hBoxTimeShiftInfo.getChildren().addAll(this.lblTimeShiftText, this.lblTimeShiftValue);
		
		// add to root
		root.getChildren().addAll(hBoxTimeZoneInfo, hBoxTimeShiftInfo);
		
		this.setGraphic(root);
		
		// save this tool tip instance in data bean
		this.dataBean.setTimeZoneTooltip(this);
		
		// assigned this tool tip to digital clock label
		this.dataBean.getDigitalClockFXMLcontroller().getLblDigitalClock().setTooltip(this);
	}
	
	/**
	 * Method assigned current time zone to the label.
	 * 
	 * @param timeZone	->	[String]	the time zone name
	 */
	public void setTimeZoneName(String timeZone) {
		this.lblTimeZoneValue.setText(timeZone);
	}
	
	/**
	 * Method assigned the current time shift value to the label.
	 * 
	 * @param timeShift	->	[long]	the time zone value
	 */
	public void setTimeShiftValue(long timeShiftValue) {
		
		String timeShiftString = "";
		
		// assigned a plus to the time shift value, if this is positive
		if(timeShiftValue > 0) {
			timeShiftString += "+" + timeShiftValue + "h";
		}
		else {
			timeShiftString += timeShiftValue + "h";
		}
	
		this.lblTimeShiftValue.setText(timeShiftString);
	}
}
