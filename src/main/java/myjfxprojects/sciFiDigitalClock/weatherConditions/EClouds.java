package myjfxprojects.sciFiDigitalClock.weatherConditions;

/**
 * @author Christian Enum for the possible Clear weather conditions from open
 *         weather API. Class holds the path to icon for this weather condition.
 */
public enum EClouds {

	FEW_CLOUDS_DAY("images/weather/day/fewCloudsDay.png"),
	FEW_CLOUDS_NIGHT("images/weather/night/fewCloudsNight.png"),
	OVERCAST_CLOUDS("images/weather/normal/overcastClouds.png");

	private final String pathToIcon;
	
	private EClouds(String path)  {
		this.pathToIcon = path;
	}
	
	public String getPathToIcon() {
		return this.pathToIcon;
	}	
}
