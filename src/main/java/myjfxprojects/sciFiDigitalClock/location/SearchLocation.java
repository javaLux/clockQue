/**
 * 
 */
package myjfxprojects.sciFiDigitalClock.location;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

import org.slf4j.Logger;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import myjfxprojects.sciFiDigitalClock.common.ApplicationLogger;
import myjfxprojects.sciFiDigitalClock.common.DataBean;
import myjfxprojects.sciFiDigitalClock.common.OwnJsonConverter;
import myjfxprojects.sciFiDigitalClock.customTooltip.NoResultTooltip;

/**
 * @author CSD
 * 
 * Class managed the search for given location (Text field input) in list of possible locations.
 */
public class SearchLocation {

	// logger instance
	private final static Logger LOGGER = ApplicationLogger.getAppLogger();
	
	// Data bean
	private DataBean dataBean = DataBean.getInstance();
	
	// simple boolean property to managed showing of the noResultTooltip
	private SimpleBooleanProperty noResultBoolean = new SimpleBooleanProperty(false);
	
	// Thread safe instance
	private static SearchLocation instance = null;
	
	// Keys for Map with location data
	private final String keyCityName		=	"name";
	private final String keyLocalNames		=	"local_names";
	private final String keyCountry			= 	"country";
	private final String keyState			=	"state";
	private final String keyCoordLon		= 	"lon";
	private final String keyCoordLat		= 	"lat";
	
	// get the current country code based on current running JVM 
	private final String keyCurrentCountryCode = Locale.getDefault().getCountry().toLowerCase();
	
	// define bad url characters !*'();:@&=+$,/?#[]
	// these characters not allowed for city or location names, because it's reserved URL characters
	private final Pattern badUrlCharPattern = Pattern.compile("[!\\*'\\(\\);:@&=\\+\\$,\\/\\?#\\[\\]]+");
	
	// Map holds founded geo data to each possible location (e.g. city name: [lon, lat])
	private Map<String, Map<String, String>> mapGeoData = new HashMap<String, Map<String, String>>();
		
	// list holds the possible location after searching
	private ObservableList<String> listOfPossibleLocations = FXCollections.observableArrayList();
	private ObservableSet<String> setOfPossibleLocations = FXCollections.observableSet();
	
	/**
	 * Private constructor initialize the change listener for the boolean property to monitor,
	 * if the noResultTooltip must be showing or not.
	 */
	private SearchLocation() {
		this.noResultBoolean.addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				// if new value set to true -> show noResultTooltip
				if(newValue.booleanValue()) {
					NoResultTooltip.getInstance().showNoResultTooltip();
				}
			}
		});
	}
	
	/**
	 * GETTER for an Thread-Safe Singleton of this class
	 * @return
	 */
	public static SearchLocation getInstance() {
		if(instance == null) {
			synchronized (SearchLocation.class) {
				if(instance == null) {
					instance = new SearchLocation();
				}
			}
		}
		
		return instance;
	}
	
	/**
	 * Method search with regular expression in the possible location list for the given location
	 * from user input to the textfield.
	 * 
	 * @param locationToFind
	 */
	public void searchLocation(String locationToFind) {
		
		StringBuffer jsonResponseString = new StringBuffer();
		
		// set always the boolean property value, to managed showing noResultTooltip, on false
		// because if user search new location the tool tip must present
		this.noResultBoolean.set(false);
	
		// Start searching for new location ONLY if text field not empty and Internet connection is UP
		if((locationToFind != null) && !(locationToFind.isEmpty()) && (DataBean.isInternetUp.get())) {
			
			// first clear list and SET for possible location before a new API call starts
			this.listOfPossibleLocations.clear();
			this.setOfPossibleLocations.clear();
			
			// filtering on bad characters in given location and replace them with an empty string
			String filteredBadCharacters = locationToFind.replaceAll(this.badUrlCharPattern.toString(), "");
			
			// only if the sting not is empty, after filtering bad characters from user input call API
			if(! filteredBadCharacters.isEmpty()) {
				
				// get right url to make an API call for geo data
				String urlString = this.dataBean.getAPI_LOCATION_URL(filteredBadCharacters);
				
				try {
					
					URL url = new URL(urlString);
					// connect to GeoCoding API
					HttpURLConnection connection = (HttpURLConnection) url.openConnection();
					
					try(BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
						// temp container for reading data
						String line = "";
						// read response line by line
						while((line = reader.readLine()) != null) {
							jsonResponseString.append(line);
						}
					}
					
					// check if given location exists and it's geo data available?
					this.filterLocationDatafromJsonResponse(jsonResponseString.toString());
					
					// only if the boolean property value to managed noResultTooltip false -> show list view
					if(! this.noResultBoolean.get()) {

						// iterate over SET and add all available location to Observable list
						this.setOfPossibleLocations.stream().forEach(entry -> {
							
							this.listOfPossibleLocations.add(entry);
						});
						// add locations list to list view
						this.dataBean.getLocationListViewController().getListViewOfLocations().setItems(this.listOfPossibleLocations);
						// show the location list view stage with possible locations
				    	this.dataBean.getLocationListViewApp().show();
					}
	
				}
				catch (MalformedURLException ex) {
					LOGGER.error("Can not get geo data for: ' " + locationToFind + " '\n" + "Complete URL: " + this.dataBean.getAPI_LOCATION_URL(locationToFind), ex);
				}
				catch (SocketTimeoutException ex) {
					LOGGER.error("Can not get geo data for: ' " + locationToFind + " '\n" + "Complete URL: " + this.dataBean.getAPI_LOCATION_URL(locationToFind), ex);
				}
				catch (IOException ex) {
					LOGGER.error("Can not get geo data for: ' " + locationToFind + " '\n" + "Complete URL: " + this.dataBean.getAPI_LOCATION_URL(locationToFind), ex);
				}
				
				// if filtered string is empty, tell user no results found for this location
			} else {
				
				this.noResultBoolean.set(true);
			}
		}		
	}
	
	/**
	 * Method filtered all necessary data for the location to find from the json response string.
	 * 
	 * @param jsonResponse	[String]	Json response from Open Weather API
	 */
	private void filterLocationDatafromJsonResponse(String jsonResponse) {
		
		// String for city name + state name and country code
		String completeLocationInfo = "";
		
		String cityName 			= "";
		String stateName 			= "";
		String countryName 			= "";
		
		if((jsonResponse != null) && ! (jsonResponse.isEmpty())) {
			
			List<Map<String, Object>> listOfFoundedLocations = OwnJsonConverter.convertCitiesJsonToMap(jsonResponse);
			
			// check if location data for current location is available
			if(! listOfFoundedLocations.isEmpty()) {
				// iterate over all locations and filter data
				for(Map<String, Object> entry : listOfFoundedLocations) {
					
					// check all necessary entry in map of location info's
					if(entry.containsKey(this.keyLocalNames)) {
						// get the right city name depend on the current Locale (Language) from JVM
						Map<String, Object> localeNames = (Map<String, Object>) entry.get(this.keyLocalNames);
						
						if(localeNames.containsKey(this.keyCurrentCountryCode)) {
							// get the specific city name in current language
							cityName = localeNames.get(this.keyCurrentCountryCode).toString() + " ";
						}
						else {
							// FALLBACK is always the default city name if a specific locale name is not present
							if(entry.containsKey(this.keyCityName)) {
								cityName = entry.get(this.keyCityName).toString() + " ";
							}
						}
					}
					// if no locale names for the city available
					else {
						if(entry.containsKey(this.keyCityName)) {
							cityName = entry.get(this.keyCityName).toString() + " ";
						}
					}
					
					// get state name
					if(entry.containsKey(this.keyState)) {
						stateName = "(" + entry.get(this.keyState) + ") ";
					}
					//get country code -> that means the country code e.g. DE or EN
					if(entry.containsKey(this.keyCountry)) {
						// convert the current country code in the country name
						if(this.dataBean.getCountryMap().containsKey(entry.get(this.keyCountry))) {
							countryName = "[" + this.dataBean.getCountryMap().get(entry.get(this.keyCountry)) + "]";
							
						} else {
							// if the current country code not in map, use the country code as name
							countryName = "[" + entry.get(this.keyCountry) + "]";
						}
						
					}
					
					// build the full qualified location name
					completeLocationInfo = cityName + stateName + countryName;
					
					// add location string with all important info's to observable list
					this.setOfPossibleLocations.add(completeLocationInfo);
					
					// safe geo data if they are present for each location in map
					if(entry.containsKey(this.keyCoordLat) && entry.containsKey(this.keyCoordLon)) {
						// Temporally map for latitude and longitude value
						Map<String, String> mapForLatAndLon = new HashMap<String, String>();
						
						mapForLatAndLon.put(this.keyCoordLat, entry.get(this.keyCoordLat).toString());
						mapForLatAndLon.put(this.keyCoordLon, entry.get(this.keyCoordLon).toString());
						
						// assigned geo data to right city name
						this.mapGeoData.put(completeLocationInfo, mapForLatAndLon);
						
						completeLocationInfo += " lat: " + entry.get(this.keyCoordLat).toString() + " lot: " + entry.get(this.keyCoordLon).toString();
					}
					else {
						LOGGER.warn("No geo data for latidude or longitude available for location: '" + completeLocationInfo + "'\nCurrent map:" + entry.toString());
					}
					
					// clear string with complete location info's
					completeLocationInfo = "";
				}
			}
			else {
				// if no location data in list -> show noResultTooltip
				this.noResultBoolean.set(true);				
			}
		
		}
		else {
			LOGGER.warn("Json response string for GeoCoding is null or empty.\nGiven json string: " + jsonResponse);
		}
	}
	
	/**
	 * Method get the latitude value for the given location.
	 * @param location	->	[String]	location to find latitude
	 * @return			->	[String]	latitude value or an empty string if no value found.
	 */
	public String getLatitudeForLocation(String location) {
		
		// return the latitude for given city
		if(this.mapGeoData.containsKey(location)) {
			// temporally map contains the geo data for this city
			Map<String, String> geoData = this.mapGeoData.get(location);
			return geoData.get(this.keyCoordLat);
		}
		else {
			LOGGER.warn("Can not find latidude value in geo data map for given location: ' " + location + " '");
		}
		
		return "";
	}
	
	/**
	 * Method get the longitude value for the given location.
	 * @param location	->	[String]	location to find longitude
	 * @return			->	[String]	longitude value or an empty string if no value found.
	 */
	public String getLongitudeForLocation(String location) {
		
		// return the longitude for given city		
		if(this.mapGeoData.containsKey(location)) {
			// temporally map contains the geo data for this city
			Map<String, String> geoData = this.mapGeoData.get(location);
			return geoData.get(this.keyCoordLon);
		}
		else {
			LOGGER.warn("Can not find longitude value in geo data map for given location: ' " + location + " '");
		}
		
		return "";
	}
	
}
