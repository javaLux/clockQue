package myjfxprojects.sciFiDigitalClock.common;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import myjfxprojects.sciFiDigitalClock.business.SettingsViewApp;
import myjfxprojects.sciFiDigitalClock.controller.DigitalClockFXMLcontroller;
import myjfxprojects.sciFiDigitalClock.controller.LocationListViewController;
import myjfxprojects.sciFiDigitalClock.controller.SettingsViewController;
import myjfxprojects.sciFiDigitalClock.customTooltip.TimeZoneTooltip;
import myjfxprojects.sciFiDigitalClock.database.DbHandling;
import myjfxprojects.sciFiDigitalClock.location.CustomComboBox;
import myjfxprojects.sciFiDigitalClock.location.LocationListViewApp;
import myjfxprojects.sciFiDigitalClock.location.LocationObject;
import myjfxprojects.sciFiDigitalClock.location.LocationObjectBuilder;

/**
 * Model Klasse -> hält intern die notwendigen Daten für die Anwendung 
 * 
 * Wichtigste Aufgaben des Model: -> speichert alle notwendigen Daten für die
 * Anwendung -> besitzt nur Getter- und Setter-Methoden für den Zugriff auf die
 * Daten
 * 
 * @author Christian
 *
 */
public class DataBean {
	
	// Thread safe singleton instance
	private static DataBean instance = null;
	
	// JOOQ API instance for database access
	private DbHandling jooqDbApi = null;
	
	private Stage primaryStage = null;
	
	private Stage listViewStage = null;
	
	private Stage settingsViewStage = null;
	
	// FXMLController instance
	private DigitalClockFXMLcontroller digitalClockFXMLcontroller = null;
	
	// Instance of LocationListViewApp to show and close
	private LocationListViewApp locationListViewApp = null;
	
	// instance of FXML controller class for the location list view
	private LocationListViewController locationListViewController = null;
	
	// Instance of SetingsView App
	private SettingsViewApp settingsViewApp = null;
	
	// Instance of SettingsView controller
	private SettingsViewController settingsViewController = null;
	
	// instance for custom combo-box
	private CustomComboBox customComboBox = null;
	
	// last activate location object
	private LocationObject lastActiveLocationObject = null;
	
	// instance of the Custom tool tip for the digital clock
	private TimeZoneTooltip timeZoneTooltip = null;
	
	// field to mark the state of the Internet connection -> default false
	public static BooleanProperty isInternetUp = new SimpleBooleanProperty(false);
	
	// final Image Views for the arrow buttons to resize the VBox middle
	private final ImageView imgViewArrowBtnLeft;
	private final ImageView imgViewArrowBtnRight;
	
	// field to declare if proxy is enabled or not -> value will changed by reading from database
	public static boolean isProxyEnabled = false;
	
	// field define if the user changed the application settings -> used in close Method from cross button
	public static boolean isAppSettingsChanged = false;
	
	// field to define if waiting symbol for weather data is showing or not
	public static boolean isWaitingSymbolShowing = false;
	
	// field to define if error symbol for fetching weather data is showing or not
	public static boolean isErrorSymbolShowing = false;
	
	// field to define state of the size of VBox Middle
	public static boolean isVBoxMiddleMinimize = false;
	
	// Application name
	public static final String APP_NAME = "ClockQue";
	
	// Map holds all available Country codes with the correct country names
	private Map<String, String> countries = new HashMap<String, String>();
	
	// start fields for API call
	// you have to specify your own API Key from openweathermap.org
	private final String API_KEY 			= "";
	private final String API_WEATHER_URL	= "https://api.openweathermap.org/data/2.5/onecall?";
	private final String API_LOCATION_URL	= "http://api.openweathermap.org/geo/1.0/direct?q=";
	
	// fields for Weather API call options
	private String language = "de";
	// field for temperature unit -> default is Metric (°C)
	private ETempUnits tempUnitFormat = ETempUnits.METRIC;
	
	// fields for the current GEO data to fetch weather data
	private String latitude = "";
	private String longitude = "";
	
	// excludes for the API call
	// -> forecast weather data possible values (current, minutely, hourly, daily, alerts) separates by comma
	private final String excludes = "minutely,hourly";
	
	// End fields for API call
	
	// current sunset, sunrise date time
	public static String dateTimeSunset = "";
	public static String dateTimeSunrise = "";
	
	// Long values for necessary UNIX time stamps
	public static long currentUnixTimestampInSeconds = 0;
	public static long currentDaySunriseUnixTimestampInSeconds = 0;
	public static long currentDaySunsetUnixTimestampInSeconds = 0;
	
	// current Time zone dependent on the current active location
	// (by default the time zone is Europe/Berlin, because the fall back location is Berlin)
	public static String currentTimeZone = "";
	
	// default time zone dependent on the default fallback location Berlin
	public static final String DefaultTimeZone = "Europe/Berlin";
	
	// define the width for main window and VBox middle to resize these if
	// arrow button will clicked
	public static final double defaultWidthMainWindow		= 360.0;
	public static final double minimizeWidthMainWindow 		= 230.0;
	public static final double defaultWidthVboxMiddle 		= 130.0;
	public static final double minimizeWidthVboxMiddle 		= 0.0;
	
	// current x and y position from main window if application is running
	// this values will change from InvalidationListener implements in class App
	public static double current_X_Pos_MainWindow = 0.0;
	public static double current_Y_Pos_MainWindow = 0.0;
	
	// fields for the proxy settings -> IMPORTANT to set right values by initialize the settings view in the text fields
	// after reading from database
	private String proxyName = "";
	private String proxyPort = "";
	
	// List holds all Location Objects the chosen by user
	private Set<LocationObject> userLocations = new HashSet<LocationObject>();
	
	// default location object
	private final LocationObject DEFAULT_LOCATION = LocationObjectBuilder.getInstance()
			.withFullLocationName("Berlin (Berlin) [Deutschland]")
			.withReducedLocationName("Berlin [DE]")
			.withLatitude("52.52437")
			.withLongitude("13.41053")
			.build();
	
	
	/**
	 * private constructor initialize the map for available country codes and country names
	 */
	private DataBean() {
		
		// set once the images for the arrow buttons to resize the VBox middle
		this.imgViewArrowBtnLeft = new ImageView(new Image("images/arrow_left.png"));
		this.imgViewArrowBtnRight = new ImageView(new Image("images/arrow_right.png"));
		
    	for(String iso : Locale.getISOCountries()) {
    		this.countries.put(iso, new Locale("de", iso).getDisplayCountry());
    	}
	}
	
	/**
	 * GETTER for an Thread-Safe Singleton of this class
	 * @return
	 */
	public static DataBean getInstance() {
		if(instance == null) {
			synchronized (DataBean.class) {
				if(instance == null) {
					instance = new DataBean();
				}
			}
		}
		
		return instance;
	}
	
	
	// GETTER and SETTER
	//
	public Stage getPrimaryStage() {
		return this.primaryStage;
	}
	
	/**
	 * @return the jooqDbApi
	 */
	public DbHandling getJooqDbApi() {
		return this.jooqDbApi;
	}

	/**
	 * @param jooqDbApi the jooqDbApi to set
	 */
	public void setJooqDbApi(DbHandling jooqDbApi) {
		this.jooqDbApi = jooqDbApi;
	}

	public void setPrimaryStage(Stage stage) {
		this.primaryStage = stage;
	}
	
	public Map<String, String> getCountryMap() {
		return this.countries;
	}

	/**
	 * @return the listViewStage
	 */
	public Stage getListViewStage() {
		return this.listViewStage;
	}

	/**
	 * @param listViewStage the listViewStage to set
	 */
	public void setListViewStage(Stage listViewStage) {
		this.listViewStage = listViewStage;
	}

	public DigitalClockFXMLcontroller getDigitalClockFXMLcontroller() {
		return this.digitalClockFXMLcontroller;
	}

	public void setDigitalClockFXMLcontroller(DigitalClockFXMLcontroller digitalClockFXMLcontroller) {
		this.digitalClockFXMLcontroller = digitalClockFXMLcontroller;
	}

	/**
	 * @return the locationListViewApp
	 */
	public LocationListViewApp getLocationListViewApp() {
		return this.locationListViewApp;
	}

	/**
	 * @param locationListViewApp the locationListViewApp to set
	 */
	public void setLocationListViewApp(LocationListViewApp locationListViewApp) {
		this.locationListViewApp = locationListViewApp;
	}

	/**
	 * @return the settingsViewStage
	 */
	public Stage getSettingsViewStage() {
		return this.settingsViewStage;
	}

	/**
	 * @param settingsViewStage the settingsViewStage to set
	 */
	public void setSettingsViewStage(Stage settingsViewStage) {
		this.settingsViewStage = settingsViewStage;
	}

	/**
	 * @return the settingsViewApp
	 */
	public SettingsViewApp getSettingsViewApp() {
		return this.settingsViewApp;
	}

	/**
	 * @param settingsViewApp the settingsViewApp to set
	 */
	public void setSettingsViewApp(SettingsViewApp settingsViewApp) {
		this.settingsViewApp = settingsViewApp;
	}

	/**
	 * @return the locationListViewController
	 */
	public LocationListViewController getLocationListViewController() {
		return this.locationListViewController;
	}

	/**
	 * @param locationListViewController the locationListViewController to set
	 */
	public void setLocationListViewController(LocationListViewController locationListViewController) {
		this.locationListViewController = locationListViewController;
	}

	/**
	 * @return the settingsViewController
	 */
	public SettingsViewController getSettingsViewController() {
		return this.settingsViewController;
	}

	/**
	 * @param settingsViewController the settingsViewController to set
	 */
	public void setSettingsViewController(SettingsViewController settingsViewController) {
		this.settingsViewController = settingsViewController;
	}

	/**
	 * @return the timeZoneTooltip
	 */
	public TimeZoneTooltip getTimeZoneTooltip() {
		return this.timeZoneTooltip;
	}

	/**
	 * @param timeZoneTooltip the timeZoneTooltip to set
	 */
	public void setTimeZoneTooltip(TimeZoneTooltip timeZoneTooltip) {
		this.timeZoneTooltip = timeZoneTooltip;
	}

	/**
	 * Method create the correct URL for the Weather data API call
	 * 
	 * @return	->	[String]	with the API call URL or an empty string if fields for geo data null or empty.
	 */
	public String getAPI_WEATHER_URL() {
		
		if((this.latitude != null) && !(this.latitude.isEmpty()) && (this.longitude != null) && !(this.longitude.isEmpty())) {
			return String.format(this.API_WEATHER_URL + "lat=" + "%s" + "&lon=" + "%s" + "&lang=" + "%s" + "&units=" + "%s" + "&exclude=" + "%s" + "&appid=" + "%s",
					this.latitude, this.longitude, this.language, this.tempUnitFormat.getUnitOfMeasurement(), this.excludes, this.API_KEY);
		}
		
		return "";
	}
	
	/**
	 * Method create the correct URL for the Location data API call
	 * 
	 * @param	->	location	[String] with the location to find
	 * @return	->	[String]	with the API call URL or an empty string if params null or empty.
	 */
	public String getAPI_LOCATION_URL(String location) {
		
		if((location != null) && !(location.isEmpty())) {
			return String.format(this.API_LOCATION_URL + location + "&limit=" + "%d" + "&appid=" + "%s", 5, this.API_KEY);
		}
		
		return "";
	}

	public String getLanguage() {
		return this.language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public void setCurrentTempUnit(ETempUnits tempUnit) {
		
		switch (tempUnit) {
		case METRIC:
			this.tempUnitFormat = ETempUnits.METRIC;
			break;

		case IMPERIAL:
			this.tempUnitFormat = ETempUnits.IMPERIAL;
		default:
			break;
		}
	}
	
	/**
	 * Method return the current set temperature unit.
	 * @return	-> [ETempUnits]	Metric(°)C or Imperial(°F)
	 */
	public ETempUnits getCurrentTempUnit() {
		return this.tempUnitFormat;
	}

	public CustomComboBox getCustomComboBox() {
		return this.customComboBox;
	}

	public void setCustomComboBox(CustomComboBox customComboBox) {
		this.customComboBox = customComboBox;
	}
	
	/**
	 * return Set of locations who choose from user
	 */
	public Set<LocationObject> getUserLocationsSet() {
		return this.userLocations;
	}

	/**
	 * @return the DEFAULT_LOCATION object
	 */
	public LocationObject getDefaultLocation() {
		return DEFAULT_LOCATION;
	}

	/**
	 * @param latidude the latitude to set
	 */
	public void setLatitude(String latidude) {
		this.latitude = latidude;
	}

	/**
	 * @param longitude the longitude to set
	 */
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	/**
	 * @return the lastActiveLocationObject
	 */
	public LocationObject getLastActiveLocationObject() {
		return this.lastActiveLocationObject;
	}

	/**
	 * @param lastActiveLocationObject the lastActiveLocationObject to set
	 */
	public void setLastActiveLocationObject(LocationObject lastActiveLocationObject) {
		this.lastActiveLocationObject = lastActiveLocationObject;
	}

	/**
	 * @return the proxyName
	 */
	public String getProxyName() {
		return this.proxyName;
	}

	/**
	 * @return the porxyPort
	 */
	public String getProxyPort() {
		return this.proxyPort;
	}

	/**
	 * @param proxyName the proxyName to set
	 */
	public void setProxyName(String proxyName) {
		this.proxyName = proxyName;
	}

	/**
	 * @param porxyPort the porxyPort to set
	 */
	public void setProxyPort(String proxyPort) {
		this.proxyPort = proxyPort;
	}

	/**
	 * @return the imgViewArrowBtnLeft
	 */
	public ImageView getImgViewArrowBtnLeft() {
		return this.imgViewArrowBtnLeft;
	}

	/**
	 * @return the imgViewArrowBtnRight
	 */
	public ImageView getImgViewArrowBtnRight() {
		return this.imgViewArrowBtnRight;
	}
	
}
