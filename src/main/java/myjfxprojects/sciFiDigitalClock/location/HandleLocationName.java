
package myjfxprojects.sciFiDigitalClock.location;

import java.util.Map.Entry;
import java.util.regex.Pattern;

import org.slf4j.Logger;

import myjfxprojects.sciFiDigitalClock.common.ApplicationLogger;
import myjfxprojects.sciFiDigitalClock.common.DataBean;

/**
 * @author Christian
 * 
 * This class managed the reducing of the full qualified location name.
 * To make them shorter for showing in ComboBox Head and ComboBox list view!!!
 */
public class HandleLocationName {
	
	// logger instance
	private final static Logger LOGGER = ApplicationLogger.getAppLogger();

	// Data-bean
	private static final DataBean dataBean = DataBean.getInstance();

	// RegEx who look at the state name in the full qualified location name
	private static final Pattern stateNamePattern = Pattern.compile("\\((.+)\\)");
	// RegEx who find all brackets like [ or ] in a string to get the country name
	private static final Pattern squareBrackets = Pattern.compile("[\\[\\]]");
	
	// private constructor
	private HandleLocationName() {}

	// always add dots to the end of the city name
	public static String reduceLocationName(String longLocationName) {

		return cutLocationName(longLocationName, true);
	}

	// manage to add dots to the end of the reduced city name
	public static String reduceLocationName(String longLocationName, boolean addDots) {

		return cutLocationName(longLocationName, addDots);
	}

	/**
	 * Method reduced the complete location to a shorter name dependent on the city name
	 * and the country code.
	 * For example:
	 * From long location name 'Frankfurt am Main (Hessen) [DE]' -> will shorter one like 'Frankfur...[DE]'
	 * This is important to display the location with user-friendly cuts in the combo box head.
	 *
	 * @param longLocationName	->	[String]	the complete location name
	 * @param  addDots			->	[boolean]	manage cut city name by three dots
	 * @return					->	[String]	the reduce location name or an empty string
	 * 											if argument is null
	 */
	private static String cutLocationName(String longLocationName, boolean addDots) {

		if(longLocationName != null) {

			final int maxLengthCityName = 8;

			String cityName = "";
			String completeCountryName = "";
			String countryCode = "";

			// split complete location name on the state name like (New York), to only city name and country code
			String[] unitsOfLocationName = longLocationName.split(stateNamePattern.toString());

			// first:	 extract city and country name
			if(unitsOfLocationName.length > 1) {
				cityName = unitsOfLocationName[0].trim();
				completeCountryName = unitsOfLocationName[unitsOfLocationName.length -1];

			} else {

				// split location name on whitespace (e.g. Hamburg [Deutschland])
				unitsOfLocationName = longLocationName.split(" ");

				if(unitsOfLocationName.length > 1) {
					cityName = unitsOfLocationName[0].trim();
					completeCountryName = unitsOfLocationName[unitsOfLocationName.length -1];

				} else {

					//  default behavior is to return given argument as reduced location name
					return longLocationName;
				}
			}

			// second:	cut a too long city name at the end with three dots
			if(addDots && cityName.length() >= maxLengthCityName) {

				// reduce the city name (i.g. Frankfurt am Main [DE]-> Frankfurt...[DE]
				cityName = cityName.substring(0, maxLengthCityName) + "...";
			}

			// third:	remove all [] brackets from country name, to find this as key in map
			completeCountryName = completeCountryName.replaceAll(squareBrackets.toString(), "").trim();

			// fourth:	convert the complete country name in shorter country code
			if(dataBean.getCountryMap().containsValue(completeCountryName)) {

				// iterate over Map and find the key (country code) assigned to this country name
				for(Entry<String, String> entry : dataBean.getCountryMap().entrySet()) {

					if(entry.getValue().equals(completeCountryName)) {
						countryCode = entry.getKey();
					}
				}

				// return the reduced location name with converted country code
				return cityName + " [" + countryCode.trim() + "]";

			} else {
				// return city name and the first two upper case chars of the complete country name
				return cityName + " [" + completeCountryName.trim().substring(0, 3).toUpperCase() + "]";
			}

		} else {
			LOGGER.warn("Can not reduce location name, because argument 'longLocationName' is null ");
		}
		return "No location";
	}
}
