package myjfxprojects.sciFiDigitalClock.weatherConditions;

/**
 * @author Christian Enum for the possible Snow weather conditions from open
 *         weather API. Class holds the path to icon for this weather condition.
 */
public enum ESnow {
	
	SNOW("images/weather/normal/snow.png");

	private final String pathToIcon;

	private ESnow(String path)  {
		this.pathToIcon = path;
	}

	public String getPathToIcon() {
		return this.pathToIcon;
	}
}
