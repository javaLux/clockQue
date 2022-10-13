package myjfxprojects.sciFiDigitalClock.weatherConditions;

/**
 * @author Christian Enum for the possible Drizzle weather conditions from open
 *         weather API. Class holds the path to icon for this weather condition.
 */
public enum EDrizzle {

	DRIZZLE("images/weather/normal/lightRain.png");

	private EDrizzle(String path)  {
		this.pathToIcon = path;
	}

	private String pathToIcon;

	public String getPathToIcon() {
		return this.pathToIcon;
	}
}
