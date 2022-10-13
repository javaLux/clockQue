package myjfxprojects.sciFiDigitalClock.weatherConditions;

/**
 * @author Christian Enum for the possible Atmosphere weather conditions from open
 *         weather API. Class holds the path to icon for this weather condition.
 */
public enum EAtmosphere {
	
	ATMOS("images/weather/normal/atmosphere.png");

	private EAtmosphere(String path)  {
		this.pathToIcon = path;
	}

	private String pathToIcon;

	public String getPathToIcon() {
		return this.pathToIcon;
	}
}
