/**
 * 
 */
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

	// Databean
	private static DataBean dataBean = DataBean.getInstance();
	
	// private constructor
	private HandleLocationName() {}
	
	/**
	 * Method reduced the complete location to a shorter name dependent on the city name
	 * and the country code.
	 * For example:
	 * From long location name 'Frankfurt am Main (Hessen) [DE]' -> will shorter one like 'Frankfurt am Main [DE]'
	 * 
	 * This method don't use dots to cut a long city name, instead of the "normal" reduceLocationName Method below!!!
	 * 
	 * @param longLocationName	->	[String]	the complete location name
	 * @return					->	[String]	the reduce location name or an empty string
	 * 											if argument is null
	 */
	public static String reduceLocationNameforComboBoxCells(String longLocationName) {
		
		if(longLocationName != null) {
			
			// RegEx who look at the state name in the full qualified location name
			Pattern stateNamePattern = Pattern.compile("\\((.+)\\)");
			// RegEx who find all brackets like [ or ] in a string
			Pattern squareBrackets = Pattern.compile("[\\[\\]]");
			// split complete location name on the state name like (Thuringia), to get city name and country code
			String[] unitsOfLocationName = longLocationName.split(stateNamePattern.toString());
			
			if(unitsOfLocationName.length > 1) {
				// first:	 get the city name from list
				String cityName = unitsOfLocationName[0];
				
				// third:	remove all [] brackets from country name, to find this as key in map
				String completeCountryName = unitsOfLocationName[unitsOfLocationName.length -1].replaceAll(squareBrackets.toString(), "").trim();				
				
				// fourth:	convert the complete country name in shorter country code
				if(dataBean.getCountryMap().containsValue(completeCountryName)) {
					
					String countryCode = "";
					// iterate over Map and find the key (country code) assigned to this country name
					for(Entry<String, String> entry : dataBean.getCountryMap().entrySet()) {
						
						if(entry.getValue().equals(completeCountryName)) {
							countryCode = entry.getKey();
						}
					}
					
					// return the reduced location name with converted country code
					return cityName + "[" + countryCode.trim() + "]";
					
				} else {
					// return city name and the complete country name
					return cityName + "[" + completeCountryName.trim() + "]";
				}			
			}
			// if no state name was found -> return given argument as reduced location name
			else {
				return longLocationName;
			}
		}
		// if argument null -> make LOGGER entry and return an empty string
		else {
			LOGGER.warn("Can not reduce location name, because argument 'longLocationName' is null " + longLocationName);	
		}	
		return "";
	}
	
	/**
	 * Method reduced the complete location to a shorter name dependent on the city name
	 * and the country code.
	 * For example:
	 * From long location name 'Frankfurt am Main (Hessen) [DE]' -> will shorter one like 'Frankfur...[DE]'
	 * 
	 * This is important to display the location with user friendly cuts cuts in the combo box head.
	 * 
	 * @param longLocationName	->	[String]	the complete location name
	 * @return					->	[String]	the reduce location name or an empty string
	 * 											if argument is null
	 */
	public static String reduceLocationName(String longLocationName) {
		
		final int maxLengthCityName = 9;
		
		if(longLocationName != null) {
			
			// RegEx who look at the state name in the full qualified location name
			Pattern stateNamePattern = Pattern.compile("\\((.+)\\)");
			// RegEx who find all brackets like [ or ] in a string
			Pattern squareBrackets = Pattern.compile("[\\[\\]]");
			// split complete location name on the state name like (Thuringia), to get city name and country code
			String[] unitsOfLocationName = longLocationName.split(stateNamePattern.toString());
			
			if(unitsOfLocationName.length > 1) {
				// first:	 get the city name from list
				String cityName = unitsOfLocationName[0].trim();
				
				// second:	check length of city name
				if(cityName.length() >= maxLengthCityName) {
					
					// reduce the city name (i.g. Frankfurt am Main [DE]-> Frankfur...[DE]
					cityName = cityName.substring(0, maxLengthCityName) + "...";
				}
				
				// third:	remove all [] brackets from country name, to find this as key in map
				String completeCountryName = unitsOfLocationName[unitsOfLocationName.length -1].replaceAll(squareBrackets.toString(), "").trim();				
				
				// fourth:	convert the complete country name in shorter country code
				if(dataBean.getCountryMap().containsValue(completeCountryName)) {
					
					String countryCode = "";
					// iterate over Map and find the key (country code) assigned to this country name
					for(Entry<String, String> entry : dataBean.getCountryMap().entrySet()) {
						
						if(entry.getValue().equals(completeCountryName)) {
							countryCode = entry.getKey();
						}
					}
					
					// return the reduced location name with converted country code
					return cityName + " [" + countryCode.trim() + "]";
					
				} else {
					// return city name and the complete country name
					return cityName + " [" + completeCountryName.trim() + "]";
				}			
			}
			// if no state name was found -> return given argument as reduced location name
			else {
				return longLocationName;
			}
		}
		// if argument null -> make LOGGER entry and return an empty string
		else {
			LOGGER.warn("Can not reduce location name, because argument 'longLocationName' is null " + longLocationName);	
		}	
		return "";
	}
}
