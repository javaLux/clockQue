package myjfxprojects.sciFiDigitalClock.customTooltip;

import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.util.Duration;
import myjfxprojects.sciFiDigitalClock.common.DataBean;


/**
 * @author Christian
 * 
 * Class to create a custom Tool tip for the to show the lowest and highest temperature 
 * of the day with two vertical layout images.
 */
public class CustomTempTooltip extends Tooltip {
	
	// Thread safe singleton instance of dataBean
	private DataBean dataBean = DataBean.getInstance();

	// font family for the tool tip
	private final Font fontTooltip = new Font("Calibri Bold", 13.0);

	// sunrise and sunset images
	private final ImageView imageViewMinTemp = new ImageView(new Image("/images/weather/minTemp.png"));
	private final ImageView imageViewMaxTemp = new ImageView(new Image("/images/weather/maxTemp.png"));

	private String minTempValue = "";
	private String maxTempValue = "";

	public CustomTempTooltip() {
			
			// set tool tip font
			this.setFont(this.fontTooltip);
			
			// set show delay of tool tip smaller
			this.setShowDelay(Duration.seconds(0.4));
			
			// Layout for the images
			VBox vBox = new VBox(2.0);
			vBox.getChildren().addAll(this.imageViewMinTemp, this.imageViewMaxTemp);
			
			this.imageViewMaxTemp.setTranslateX(this.imageViewMaxTemp.getX() + 1);
			this.setGraphic(vBox);
			
			this.setGraphicTextGap(8.0);
		}

	/**
	 * Method set the current lowest and highest temperature of the day and the correct unit
	 * @param tempMin
	 * @param tempMax
	 */
	public void setMinMaxTempValue(String tempMin, String tempMax) {
		
		this.minTempValue = tempMin;
		this.maxTempValue = tempMax;
		
		String tooltipText = String.format("%s\t%s\n%s\t%s", this.minTempValue, this.dataBean.getCurrentTempUnit().getTempUnit(),
				this.maxTempValue, this.dataBean.getCurrentTempUnit().getTempUnit());
				
		this.setText(tooltipText);
		
	}
}
