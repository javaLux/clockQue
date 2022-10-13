/**
 * 
 */
package myjfxprojects.sciFiDigitalClock.customTooltip;

import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.util.Duration;

/**
 * @author Christian
 * 
 * Class to create a custom Tool tip for the weather icon with two vertical layout images for
 * sunrise and sunset and the weather description below the images.
 */
public class CustomWeatherToolTip extends Tooltip {

	// font family for the tool tip
	private final Font fontTooltip = new Font("Calibri Bold", 13.0);
	
	// sunrise and sunset images
	private final ImageView imageViewSunrise = new ImageView(new Image("/images/weather/sunrise.png"));
	private final ImageView imageViewSunset = new ImageView(new Image("/images/weather/sunset.png"));
	
	private String weatherDescription = "";
	private String sunriseDateTime = "";
	private String sunsetDateTime = "";
	
	public CustomWeatherToolTip() {
		
		// set tool tip font
		this.setFont(this.fontTooltip);
		
		// Layout for the images
		VBox vBox = new VBox(2.0);
		vBox.getChildren().addAll(imageViewSunrise, imageViewSunset);
		
		// set show delay of tool tip smaller
		this.setShowDelay(Duration.seconds(0.4));
		
		this.setGraphic(vBox);
		// set vbox with images in right position to the text
		this.getGraphic().setTranslateY(-12.0);
		this.setGraphicTextGap(8.0);
	}
	
	public void setWeatherText(String description, String sunrise, String sunset) {
		this.weatherDescription = description;
		this.sunriseDateTime = sunrise;
		this.sunsetDateTime = sunset;
		
		String tooltipText = String.format("%s\n%s\n%s",
				this.sunriseDateTime, this.sunsetDateTime, this.weatherDescription);
		
		this.setText(tooltipText);
		// show icons for sunrise and sunset + text on the left side of tool tip
		this.setContentDisplay(ContentDisplay.LEFT);
	}
	
	public void setWaitingText(String description) {
		this.setText(description);
		// show only the text in tool tip
		this.setContentDisplay(ContentDisplay.TEXT_ONLY);
	}
}
