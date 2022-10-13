package myjfxprojects.sciFiDigitalClock.weatherConditions;

/**
 * @author Christian Enum for the possible Clear weather conditions from open
 *         weather API. Class holds the path to icon for this weather condition.
 */
public enum EClear {
	
	CLEAR_DAY("images/weather/day/clearSun.png"),
	CLEAR_NIGHT("images/weather/night/clearMoon.png");
	
	private EClear(String path)  {
		this.pathToIcon = path;
	}

	private String pathToIcon;

	public String getPathToIcon() {
		return this.pathToIcon;
	}
}
