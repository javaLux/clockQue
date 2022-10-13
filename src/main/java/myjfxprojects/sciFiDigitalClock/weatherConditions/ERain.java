package myjfxprojects.sciFiDigitalClock.weatherConditions;

/**
 * @author Christian Enum for the possible Rain weather conditions from open
 *         weather API. Class holds the path to icon for this weather condition.
 */
public enum ERain {
	
	LIGHT_RAIN("images/weather/normal/lightRain.png"),
	SHOWER_RAIN("images/weather/normal/showerRain.png"),
	FREEZING_RAIN("images/weather/normal/freezingRain.png");

	private ERain(String path)  {
		this.pathToIcon = path;
	}

	private String pathToIcon;

	public String getPathToIcon() {
		return this.pathToIcon;
	}
}
