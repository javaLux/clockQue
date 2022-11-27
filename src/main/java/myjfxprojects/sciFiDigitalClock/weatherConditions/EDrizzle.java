package myjfxprojects.sciFiDigitalClock.weatherConditions;

/**
 * @author Christian Enum for the possible Drizzle weather conditions from open
 *         weather API. Class holds the path to icon for this weather condition.
 */
public enum EDrizzle {

	DRIZZLE("images/weather/normal/lightRain.png");

	private final String pathToIcon;

	private EDrizzle(String path)  {
		this.pathToIcon = path;
	}

	public String getPathToIcon() {
		return this.pathToIcon;
	}
}
