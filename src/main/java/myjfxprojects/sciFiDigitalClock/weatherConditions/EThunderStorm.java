
package myjfxprojects.sciFiDigitalClock.weatherConditions;

/**
 * @author Christian
 * Enum for the possible Thunderstorm weather conditions from open weather API.
 * Class holds the path to icon for this weather condition.
 */
public enum EThunderStorm {
	
	THUNDERSTORM("images/weather/normal/lightning.png");
	
	EThunderStorm(String path) {
		this.pathToIcon = path;
	}
	
	private String pathToIcon;
	
	public String getPathToIcon() {
		return this.pathToIcon;
	}
}
