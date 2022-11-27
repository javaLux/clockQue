package myjfxprojects.sciFiDigitalClock.weatherConditions;

/**
 * @author Christian Enum for the possible Atmosphere weather conditions from open
 *         weather API. Class holds the path to icon for this weather condition.
 */
public enum EAtmosphere {
	
	ATMOSPHERE("images/weather/normal/atmosphere.png");

	private final String pathToIcon;

	private EAtmosphere(String path)  {
		this.pathToIcon = path;
	}

	public String getPathToIcon() {
		return this.pathToIcon;
	}
}
