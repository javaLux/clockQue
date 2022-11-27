package myjfxprojects.sciFiDigitalClock.weatherConditions;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.TimeZone;

import org.slf4j.Logger;

import myjfxprojects.sciFiDigitalClock.common.ApplicationLogger;
import myjfxprojects.sciFiDigitalClock.common.DataBean;

/**
 * @author Christian
 * 
 * Class to check the current weather conditions by id and assigned the correct weather icon to imageView.
 */
public class WeatherCondition {
	// logger instance
	private final static Logger LOGGER = ApplicationLogger.getAppLogger();
	
	// thread safe singleton
	private static WeatherCondition instance = null;

	// fields hold the possible id for each weather condition
	private final List<Integer> thunderStormIDList = Arrays.asList(200, 201, 202, 210, 211, 212, 221, 230, 231, 232);
	private final List<Integer> drizzleIDList = Arrays.asList(300, 301, 302, 310, 311, 312, 313, 314, 321);
	private final List<Integer> lightRainIDList = Arrays.asList(500, 501, 502, 503, 504);
	private final List<Integer> showerRainIDList = Arrays.asList(520, 521, 522, 531);
	private final List<Integer> freezingRainIDList = Arrays.asList(511);
	private final List<Integer> snowIDList = Arrays.asList(600, 601, 602, 611, 612, 613, 615, 616, 620, 621, 622);
	private final List<Integer> atmosphereIDList = Arrays.asList(701, 711, 721, 731, 741, 751, 761, 762, 771, 781);
	private final List<Integer> clearIDList = Arrays.asList(800);
	private final List<Integer> fewCloudsIDList = Arrays.asList(801, 802);
	private final List<Integer> overCastCloudsIDList = Arrays.asList(803, 804);
	
	/**
	 * private constructor 
	 */
	private WeatherCondition() {}
	
	/**
	 * GETTER for a Thread-Safe Singleton instance
	 * @return	-> this
	 */
	public static WeatherCondition getInstance() {
		if(instance == null) {
			synchronized (WeatherCondition.class) {
				if(instance == null) {
					instance = new WeatherCondition();
				}
			}
		}
		
		return instance;
	}
	
	/**
	 * Method to get the right matching weather icon for each weather condition.
	 * 
	 * @param id	->	[Integer]	ID for the weather condition
	 * @return		->	[String]	Path to the matching weather icon
	 */
	public String getWeatherIcon(Integer id) {
		
		if((id != null) && (id > 0)) {
			
			if(this.thunderStormIDList.contains(id)) {
				return EThunderStorm.THUNDERSTORM.getPathToIcon();
			}
			else if(this.drizzleIDList.contains(id)) {
				return EDrizzle.DRIZZLE.getPathToIcon();
			}
			else if(this.lightRainIDList.contains(id)) {	
				return ERain.LIGHT_RAIN.getPathToIcon();
			}
			else if(this.showerRainIDList.contains(id)) {
				return ERain.SHOWER_RAIN.getPathToIcon();
			}
			else if(this.freezingRainIDList.contains(id)) {
				return ERain.FREEZING_RAIN.getPathToIcon();
			}
			else if(this.snowIDList.contains(id)) {
				return ESnow.SNOW.getPathToIcon();
			}
			else if(this.atmosphereIDList.contains(id)) {
				return EAtmosphere.ATMOSPHERE.getPathToIcon();
			}
			
			else if(this.clearIDList.contains(id)) {
				
				try {
					if(this.isNight()) {
						return EClear.CLEAR_NIGHT.getPathToIcon();
					}
					else {
						return EClear.CLEAR_DAY.getPathToIcon();
					}
				} catch (Exception ex) {
					
					LOGGER.error("Failed to check if night or day.", ex);
				}
			}
			
			else if(this.fewCloudsIDList.contains(id)) {
							
				try {
					if(this.isNight()) {
						return EClouds.FEW_CLOUDS_NIGHT.getPathToIcon();
					}
					else {
						return EClouds.FEW_CLOUDS_DAY.getPathToIcon();
					}
				} catch (Exception ex) {
					
					LOGGER.error("Failed to check if night or day.", ex);
				}
			}
			else if(this.overCastCloudsIDList.contains(id)) {
				
				return EClouds.OVERCAST_CLOUDS.getPathToIcon();
			}
		}
		
		return "";
	}
	
	/**
	 * Method calculate if it's night or day, to set weather icons correctly.
	 */
	private boolean isNight() throws Exception {
		
		// time until complete sunrise and sunset
		int fifteenMinutes = 15;
		
		// convert each necessary time stamps to a LocalDateTime object to make compare easier
		LocalDateTime currentDateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(DataBean.currentUnixTimestampInSeconds),
				TimeZone.getTimeZone(DataBean.currentLocationTimeZone).toZoneId());
		
		LocalDateTime currentDaySunriseDateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(DataBean.currentDaySunriseUnixTimestampInSeconds),
				TimeZone.getTimeZone(DataBean.currentLocationTimeZone).toZoneId()).plusMinutes(fifteenMinutes);
		
		LocalDateTime currentDaySunsetDateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(DataBean.currentDaySunsetUnixTimestampInSeconds),
				TimeZone.getTimeZone(DataBean.currentLocationTimeZone).toZoneId()).plusMinutes(fifteenMinutes);
		
		// compare the date time objects
		int beforeSunrise = currentDateTime.compareTo(currentDaySunriseDateTime);
		int afterSunset = currentDateTime.compareTo(currentDaySunsetDateTime);
 		
		if(beforeSunrise < 0 || afterSunset > 0) {
			
			return true;
		}
	
		return false;
	}
}
