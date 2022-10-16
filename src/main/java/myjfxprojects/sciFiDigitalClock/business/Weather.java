package myjfxprojects.sciFiDigitalClock.business;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HttpsURLConnection;

import org.slf4j.Logger;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.util.Duration;
import myjfxprojects.sciFiDigitalClock.common.ApplicationLogger;
import myjfxprojects.sciFiDigitalClock.common.DataBean;
import myjfxprojects.sciFiDigitalClock.common.OwnDateTimeFormatter;
import myjfxprojects.sciFiDigitalClock.common.OwnJsonConverter;
import myjfxprojects.sciFiDigitalClock.customTooltip.CustomTempTooltip;
import myjfxprojects.sciFiDigitalClock.customTooltip.CustomWeatherToolTip;
import myjfxprojects.sciFiDigitalClock.location.LocationObject;
import myjfxprojects.sciFiDigitalClock.weatherConditions.WeatherCondition;

public class Weather {

	// thread safe instance field
	private static Weather instance = null;

	// logger instance
	private final static Logger LOGGER = ApplicationLogger.getAppLogger();

	// thread safe instance of data bean
	private DataBean dataBean = DataBean.getInstance();

	// thread safe instance of weather conditions class
	private WeatherCondition weatherCondition = null;

	// Time line to fetch periodically weather data
	private Timeline timelineWeatherData = null;

	// Map holds the weather data from API call
	private Map<String, Object> mapWeatherData = null;

	// define weather data time line duration to four minutes
	// IMPORTANT: because only 1000 API calls per day are allowed
	private final double durationWeatherTimeline = 4.0;

	// weather data currently not available
	private final String dataNotAvailableText = "Wetterdaten aktuell nicht verfügbar...";

	// error by query the current weather data from server
	private final String errorByFetchingWeatherDataText = "Fehler beim abrufen der aktuellen Wetterdaten.\nDer Server antwortet nicht.";

	// map keys to find weather data from map
	private final String TIME_ZONE_KEY 				= "timezone";
	private final String CURRENT_KEY 				= "current";
	private final String DAILY_KEY 					= "daily";
	private final String CURRENT_TIMESTAMP 			= "dt";
	private final String SUNRISE_TIMESTAMP 			= "sunrise";
	private final String SUNSET_TIMESTAMP 			= "sunset";
	private final String WEATHER_KEY 				= "weather";
	private final String TEMP_KEY 					= "temp";
	private final String TEMP_MIN_KEY 				= "min";
	private final String TEMP_MAX_KEY 				= "max";
	private final String WEATHER_CON_ID_KEY 		= "id";
	private final String WEATHER_DESCRIPTION_KEY 	= "description";

	// fields for temperatureCurrent, weather condition id and weather description
	private String weatherConIconPath 	= "";
	private String weatherDescription 	= "";
	private String temperatureCurrent 	= "";
	private String temperatureLowest 	= "";
	private String temperatureHighest 	= "";
	private String dateTimeSunrise 		= "";
	private String dateTimeSunSet	 	= "";

	// customize tool tips
	private CustomWeatherToolTip weatherIconToolTip = null;
	private CustomTempTooltip tempIconToolTip = null;

	/**
	 * private Constructor initialize members
	 */
	private Weather() {

		this.weatherCondition = WeatherCondition.getInstance();
		this.timelineWeatherData = new Timeline();
		this.mapWeatherData = new HashMap<String, Object>();
		this.weatherIconToolTip = new CustomWeatherToolTip();
		this.tempIconToolTip = new CustomTempTooltip();

		// Assign custom tool tips to nodes from FXML
		this.dataBean.getDigitalClockFXMLcontroller().getLblHelperTooltip().setTooltip(this.weatherIconToolTip);
		this.dataBean.getDigitalClockFXMLcontroller().getLblCurrentTemp().setTooltip(this.tempIconToolTip);

		/*
		 * add ChangeListener to the BooleanProperty value which says if Internet
		 * connection is up or down if this value changed from false to true -> than
		 * start once a new time line to fetch weather data again IMPORTANT because if
		 * the application started only the periodically time line and these is fetching
		 * the weather data every minute -> this means the weather data can not updated
		 * one minute long
		 */
		DataBean.isInternetUp.addListener((observable, oldValue, newValue) -> {

			if (oldValue.booleanValue() == false) {

				// the default value from variable "isInternetUp" is false, IMPORTANT if
				// Application starts and the scheduled service to check the
				// Internet connectivity will execute -> than will this value changed to true if
				// Internet is available and fetching weather data begins or
				// value stays at false -> fetching weather data will nor´t execute and so on
				this.startOnceTimelineToFetchWeatherData();
			}
		});
	}

	/**
	 * GETTER for an Thread-Safe Singleton of this class
	 * 
	 * @return
	 */
	public static Weather getInstance() {
		if (instance == null) {
			synchronized (Weather.class) {
				if (instance == null) {
					instance = new Weather();
				}
			}
		}

		return instance;
	}

	/**
	 * Method starts a time line who periodically fetch the current weather data
	 * (dependent on the duration value).
	 */
	public void startPeriodicallyFetchWeatherData() {
		this.timelineWeatherData.setCycleCount(Timeline.INDEFINITE);

		this.timelineWeatherData.getKeyFrames()
				.add(new KeyFrame(Duration.minutes(this.durationWeatherTimeline), event -> {

					this.getWeatherData();

				}));

		this.timelineWeatherData.play();
	}

	/**
	 * Method execute a new time line once to fetch weather data.
	 */
	public void startOnceTimelineToFetchWeatherData() {
		Timeline timeline = new Timeline();
		// time line executes once
		timeline.setCycleCount(1);
		timeline.getKeyFrames().add(new KeyFrame(Duration.millis(100), event -> {

			this.getWeatherData();

		}));

		timeline.play();
	}

	/**
	 * Method fetch weather data once and save the current time zone for the given
	 * location object in database. Farther the tool tip from digital clock will be
	 * updated with new time zone info and the date info's will be updated. Last but
	 * not least, the current time shift in hours will be add to the digital clock
	 * tool tip.
	 * 
	 * @param location -> [LocationObject] the current location
	 */
	public void fetchWeatherDataAndSetTimeZone(LocationObject location) {
		Timeline timeline = new Timeline();
		// time line executes only one time
		timeline.setCycleCount(1);
		timeline.getKeyFrames().add(new KeyFrame(Duration.millis(100), event -> {

			this.getWeatherData();

		}));

		// event handler for the time line finished event
		timeline.setOnFinished(event -> {
			// if time line finished, than save time zone for the given location object in
			// database
			dataBean.getJooqDbApi().setTimeZoneForThisLocation(location, DataBean.currentLocationTimeZone);
			// set new date info's dependent on the current active time zone
			dataBean.getDigitalClockFXMLcontroller()
					.setLblWeekDay(ControlDateInfos.getInstance().getCurrentDayOfWeek());
			dataBean.getDigitalClockFXMLcontroller().setLblMonth(ControlDateInfos.getInstance().getCurrentMonth());
			dataBean.getDigitalClockFXMLcontroller()
					.setLblMonthDay(ControlDateInfos.getInstance().getCurrentDayOfMonth());

			// assigned the current time zone to the right label in the custom tool tip of
			// the digital clock
			dataBean.getTimeZoneTooltip().setTimeZoneName(DataBean.currentLocationTimeZone);

			// assigned the current time shift value to the right label in the custom tool
			// tip of the digital clock
			dataBean.getTimeZoneTooltip().setTimeShiftValue(DigitalClock.getInstance().getCurrentTimeShiftInHours());
		});

		timeline.play();
	}

	/**
	 * Method fetch weather data from open weather API
	 */
	private void getWeatherData() {

		// StringBuffer to hold API response
		StringBuffer jsonResponseString = new StringBuffer();

		HttpsURLConnection connection = null;

		// START ONLY the weather API call if Internet is up
		if (DataBean.isInternetUp.get()) {

			// make API call
			try {
				URL url = new URL(this.dataBean.getAPI_WEATHER_URL());

				connection = (HttpsURLConnection) url.openConnection();

			} catch (IOException ex) {
				
				LOGGER.error("Failed to established a connection to:\n'" + this.dataBean.getAPI_WEATHER_URL() + "'\n", ex);
				this.errorOccursAtFetchingWeatherData();
				
				return;
			}

			// store incoming weather data in BufferReader, read this data and
			// use try with resources to close stream automatically after using
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
				// temp container for reading data
				String line = "";
				// read response line by line
				while ((line = reader.readLine()) != null) {
					jsonResponseString.append(line);
				}
				
				// couldn't read data from API response
			} catch (IOException ex) {
				
				LOGGER.error("Failed to get weather data from API.\nPossible reason:\n- wrong or empty API key\n"
						+ "API URL: '" + this.dataBean.getAPI_WEATHER_URL() + "'\n" 
						+ "API response string:\n" + jsonResponseString, ex);
				
				this.errorOccursAtFetchingWeatherData();
				
				return;
			}
			
			// convert JSON to map
			this.mapWeatherData = OwnJsonConverter.convertWeatherJsonToMap(jsonResponseString.toString());

			// show weather data only if these are available
			if (! this.mapWeatherData.isEmpty()) {

				// IMPORTANT: If Weather icon with associated tool tip and the temperature with
				// associated tool tip
				// are showing will managed in class "ControlDigitalClock" for detail in the
				// Time line of
				// the digital clock. These time line will check every second the current state
				// of the
				// Internet connectivity and if waiting or error symbol are showing. Is not that
				// case, the
				// the weather icon and temperature will showing.
				DataBean.isWaitingSymbolShowing = false;
				DataBean.isErrorSymbolShowing = false;

				// save periodically the current time zone in data bean to have access
				// from outside to get the right date time
				DataBean.currentLocationTimeZone = this.fetchLocationTimeZone();
				this.temperatureCurrent = this.fetchCurrentTemp();
				this.temperatureLowest = this.fetchDailyMinTemp();
				this.temperatureHighest = this.fetchDailyMaxTemp();
				this.dateTimeSunrise = this.fetchSunriseDateTime();
				this.dateTimeSunSet = this.fetchSunsetDateTime();

				// save periodically current sunset and sunrise date time in dataBean
				DataBean.dateTimeSunset = this.dateTimeSunSet;
				DataBean.dateTimeSunrise = this.dateTimeSunrise;

				// save periodically necessary UNIX time stamps in seconds
				DataBean.currentUnixTimestampInSeconds = this.fetchCurrentDayTimeStamp(CURRENT_TIMESTAMP);
				DataBean.currentDaySunriseUnixTimestampInSeconds = this.fetchCurrentDayTimeStamp(SUNRISE_TIMESTAMP);
				DataBean.currentDaySunsetUnixTimestampInSeconds = this.fetchCurrentDayTimeStamp(SUNSET_TIMESTAMP);

				this.weatherDescription = this.fetchWeatherDescription();
				// get matching weather icon
				this.weatherConIconPath = this.weatherCondition.getWeatherIcon(this.fetchWeatherConID());

				// set correct weather data, e.g. temperatureCurrent, temperature unit and
				// weather description and so on in GUI
				this.dataBean.getDigitalClockFXMLcontroller().getLblCurrentTemp().setText(this.temperatureCurrent);
				this.dataBean.getDigitalClockFXMLcontroller().getTextTempUnit()
						.setText(this.dataBean.getCurrentTempUnit().getTempUnit());

				// check if path to weather icon not empty
				if (!this.weatherConIconPath.isEmpty()) {
					// if weather icon valid -> than change image view with current weather icon
					this.dataBean.getDigitalClockFXMLcontroller()
							.setImageViewWeather(new Image(this.weatherConIconPath));
					// set tool tips for weather icon
					this.weatherIconToolTip.setWeatherText(this.weatherDescription, this.dateTimeSunrise,
							this.dateTimeSunSet);
				} else {
					// if no path to icon available -> show waiting icon
					this.dataBean.getDigitalClockFXMLcontroller().setImageViewWeather(new Image("images/waiting.png"));
					this.weatherIconToolTip.setWaitingText(this.dataNotAvailableText);
				}

				// set values for current highest and lowest temperature tool tip
				this.tempIconToolTip.setMinMaxTempValue(this.temperatureLowest, this.temperatureHighest);
			}

			// if no weather data available
			else {

				this.currentlyNoWeahterDataAvailable();
				// make log entry
				LOGGER.warn("Currently no weather data available.\nJson response string: " + jsonResponseString);
			}
		}
	}

	/**
	 * Method hide the text elements for temperatureCurrent and temp unit
	 */
	private void hideWeatherData() {
		this.dataBean.getDigitalClockFXMLcontroller().getLblCurrentTemp().setVisible(false);
		this.dataBean.getDigitalClockFXMLcontroller().getTextTempUnit().setVisible(false);
	}

	/**
	 * Method fetch the time zone for the current location to convert sunrise and
	 * sunset times correctly.
	 * 
	 * @return -> [String] with the current time zone (for e.g. Europe/Berlin) or an
	 *         empty String if no time zone was found.
	 */
	private String fetchLocationTimeZone() {

		String currentTimeZoneForLocation = "";

		if (this.mapWeatherData.containsKey(TIME_ZONE_KEY)) {

			currentTimeZoneForLocation = this.mapWeatherData.get(TIME_ZONE_KEY).toString();
		} else {
			LOGGER.warn("Can not fetch time zone, because no key named 'timezone' available from map:\n"
					+ this.mapWeatherData);
		}

		return currentTimeZoneForLocation;
	}

	/**
	 * Method search for the temp data in map.
	 * 
	 * @return -> an empty string if no temp data available or the temp as string.
	 */
	private String fetchCurrentTemp() {
		String tempAsString = "";

		if (this.mapWeatherData.containsKey(CURRENT_KEY)) {
			Map<String, Object> tempMap = (Map<String, Object>) this.mapWeatherData.get(CURRENT_KEY);

			if (tempMap.containsKey(TEMP_KEY)) {

				// here we must use type long, because in case that we must rounded the value
				// from
				// map -> method round returns a long value
				Long tempValue = Long.valueOf(0);

				try {
					// found a floating point number -> than rounded
					if (tempMap.get(TEMP_KEY).toString().contains(".")) {

						tempValue = Math.round((double) tempMap.get(TEMP_KEY));
					} else {

						tempValue = Long.parseLong(tempMap.get(TEMP_KEY).toString());
					}

					tempAsString = tempValue.toString();

				} catch (Exception ex) {
					LOGGER.error("Can not parse current temperatur as integer from ' "
							+ tempMap.get(TEMP_KEY).toString() + " '", ex);
				}
			}
		}

		return tempAsString;
	}

	private String fetchDailyMinTemp() {
		String tempMinAsString = "";

		if (this.mapWeatherData.containsKey(DAILY_KEY)) {
			List<Map<String, Object>> dailyForcastValueList = (List<Map<String, Object>>) this.mapWeatherData
					.get(DAILY_KEY);

			if (!dailyForcastValueList.isEmpty()) {
				// get the current day from daily list -> this is always the first element in
				// the list
				Map<String, Object> dailyValueMap = (Map<String, Object>) dailyForcastValueList.get(0);

				if (dailyValueMap.containsKey(TEMP_KEY)) {
					Map<String, Object> tempMap = (Map<String, Object>) dailyValueMap.get(TEMP_KEY);

					try {
						Long tempMinValue = Long.valueOf(0);
						// found a floating point number -> than rounded
						if (tempMap.get(TEMP_MIN_KEY).toString().contains(".")) {

							tempMinValue = Math.round((double) tempMap.get(TEMP_MIN_KEY));
						} else {

							tempMinValue = Long.parseLong(tempMap.get(TEMP_MIN_KEY).toString());
						}

						tempMinAsString = tempMinValue.toString();

					} catch (Exception ex) {
						LOGGER.error("Can not parse min temperatur as integer from ' "
								+ tempMap.get(TEMP_MIN_KEY).toString() + " '", ex);
					}
				}
			}
		}

		return tempMinAsString;
	}

	private String fetchDailyMaxTemp() {

		String tempMaxAsString = "";

		if (this.mapWeatherData.containsKey(DAILY_KEY)) {
			List<Map<String, Object>> dailyForcastValueList = (List<Map<String, Object>>) this.mapWeatherData
					.get(DAILY_KEY);

			if (!dailyForcastValueList.isEmpty()) {
				// get the current day from daily list -> this is always the first element in
				// the list
				Map<String, Object> dailyValueMap = (Map<String, Object>) dailyForcastValueList.get(0);

				if (dailyValueMap.containsKey(TEMP_KEY)) {
					Map<String, Object> tempMap = (Map<String, Object>) dailyValueMap.get(TEMP_KEY);

					try {
						Long tempMinValue = Long.valueOf(0);
						// found a floating point number -> than rounded
						if (tempMap.get(TEMP_MAX_KEY).toString().contains(".")) {

							tempMinValue = Math.round((double) tempMap.get(TEMP_MAX_KEY));
						} else {

							tempMinValue = Long.parseLong(tempMap.get(TEMP_MAX_KEY).toString());
						}

						tempMaxAsString = tempMinValue.toString();

					} catch (Exception ex) {
						LOGGER.error("Can not parse min temperatur as integer from ' "
								+ tempMap.get(TEMP_MAX_KEY).toString() + " '", ex);
					}
				}
			}
		}

		return tempMaxAsString;
	}

	/**
	 * Method search for the weather condition id.
	 * 
	 * @return -> an integer with the weather condition id or the value 0 if no
	 *         weather condition id available.
	 * 
	 * @throws -> throws an NumberFormatException if available id can not parse to
	 *            an integer.
	 */
	private Integer fetchWeatherConID() {

		Integer weatherConId = 0;

		if (this.mapWeatherData.containsKey(CURRENT_KEY)) {
			Map<String, Object> tempMapCurrent = (Map<String, Object>) this.mapWeatherData.get(CURRENT_KEY);

			if (tempMapCurrent.containsKey(WEATHER_KEY)) {
				ArrayList<Map<String, Object>> tempList = (ArrayList<Map<String, Object>>) tempMapCurrent
						.get(WEATHER_KEY);

				if (!tempList.isEmpty()) {

					Map<String, Object> tempMapWeatherData = tempList.get(0);
					// get data of weather conditions
					if (tempMapWeatherData.containsKey(WEATHER_CON_ID_KEY)) {

						try {
							weatherConId = Integer.parseInt(tempMapWeatherData.get(WEATHER_CON_ID_KEY).toString());

						} catch (Exception ex) {
							LOGGER.error("Can not parse weather condition id: ' "
									+ tempMapWeatherData.get(WEATHER_CON_ID_KEY).toString() + " '" + " to integer", ex);
						}
					}
				}
			}
		}

		return weatherConId;
	}

	/**
	 * Method search for the weather description data in map.
	 * 
	 * @return -> an empty string if no weather description available or the weather
	 *         description as string.
	 */
	private String fetchWeatherDescription() {
		String weatherDescription = "";

		if (this.mapWeatherData.containsKey(CURRENT_KEY)) {
			Map<String, Object> tempMapCurrent = (Map<String, Object>) this.mapWeatherData.get(CURRENT_KEY);

			if (tempMapCurrent.containsKey(WEATHER_KEY)) {
				ArrayList<Map<String, Object>> tempList = (ArrayList<Map<String, Object>>) tempMapCurrent
						.get(WEATHER_KEY);

				if (!tempList.isEmpty()) {

					Map<String, Object> tempMapWeatherData = tempList.get(0);
					// get data of weather conditions
					if (tempMapWeatherData.containsKey(WEATHER_DESCRIPTION_KEY)) {

						weatherDescription = tempMapWeatherData.get(WEATHER_DESCRIPTION_KEY).toString();
					}
				}
			}
		}

		// return an string UTF-8 encoded string
		return new String(weatherDescription.getBytes(), StandardCharsets.UTF_8);
	}

	/**
	 * Method fetch the current date time of sunrise.
	 * 
	 * @return -> [String] with the date time string of current sunrise.
	 */
	private String fetchSunriseDateTime() {
		String dateTimeOfSunrise = "";

		if (this.mapWeatherData.containsKey(CURRENT_KEY)) {
			Map<String, Object> tempMapCurrent = (Map<String, Object>) this.mapWeatherData.get(CURRENT_KEY);

			if (tempMapCurrent.containsKey(SUNRISE_TIMESTAMP)) {
				try {
					// convert map entry from string to long value
					Long longValue = Long.parseLong(tempMapCurrent.get(SUNRISE_TIMESTAMP).toString());

					// we must convert seconds in milliseconds to get the correct time
					dateTimeOfSunrise = OwnDateTimeFormatter.formatTime(TimeUnit.SECONDS.toMillis(longValue),
							DataBean.currentLocationTimeZone);

				} catch (Exception ex) {
					LOGGER.error("Failed to convert the current date time of sunrise.\nTime stamp: '"
							+ tempMapCurrent.get(SUNRISE_TIMESTAMP) + " '", ex);
				}
			}
		}

		return dateTimeOfSunrise;
	}

	/**
	 * Method fetch the current date time of sunset.
	 * 
	 * @return -> [String] with the date time string of current sunset.
	 */
	private String fetchSunsetDateTime() {
		String dateTimeOfSunset = "";

		if (this.mapWeatherData.containsKey(CURRENT_KEY)) {
			Map<String, Object> tempMapCurrent = (Map<String, Object>) this.mapWeatherData.get(CURRENT_KEY);

			if (tempMapCurrent.containsKey(SUNSET_TIMESTAMP)) {
				try {
					// convert map entry from string to long value
					Long longValue = Long.parseLong(tempMapCurrent.get(SUNSET_TIMESTAMP).toString());

					// we must convert seconds in milliseconds to get the correct time
					dateTimeOfSunset = OwnDateTimeFormatter.formatTime(TimeUnit.SECONDS.toMillis(longValue),
							DataBean.currentLocationTimeZone);

				} catch (Exception ex) {
					LOGGER.error("Failed to convert the current date time of sunset.\nTime stamp: '"
							+ tempMapCurrent.get(SUNSET_TIMESTAMP) + " '", ex);
				}
			}
		}

		return dateTimeOfSunset;
	}

	/**
	 * Method fetch UNIX time stamps from weather map, for the current day.
	 * Dependent on the given time stamp key.
	 * 
	 * @return -> [long] the UNIX time stamp in seconds.
	 */
	private long fetchCurrentDayTimeStamp(String timeStampKey) {
		long currentTimeStamp = 0L;

		if (this.mapWeatherData.containsKey(CURRENT_KEY)) {
			Map<String, Object> tempMapCurrent = (Map<String, Object>) this.mapWeatherData.get(CURRENT_KEY);

			if (tempMapCurrent.containsKey(timeStampKey)) {
				try {
					// convert map entry from string to long value
					currentTimeStamp = Long.parseLong(tempMapCurrent.get(timeStampKey).toString());

				} catch (Exception ex) {
					// TODO rework log message
					LOGGER.error(
							"Failed to convert time stamp of the current day, from string to long.\nArgs: 'timeStampKey': "
									+ timeStampKey + " Time stamp: '" + tempMapCurrent.get(timeStampKey) + " '",
							ex);
				}
			}
		}

		return currentTimeStamp;
	}

	/**
	 * Method will used if no weather data at current query interval available.
	 * Shows the waiting Symbol instead of the weather icon and changed the tool
	 * tip.
	 */
	private void currentlyNoWeahterDataAvailable() {

		DataBean.isWaitingSymbolShowing = true;
		// hide weather data in application and shows waiting symbol
		this.hideWeatherData();
		// hide progress indicator
		this.dataBean.getDigitalClockFXMLcontroller().getProgressIndicator().setVisible(false);

		// show imageView and label with tooltip
		this.dataBean.getDigitalClockFXMLcontroller().getImageViewWeather().setVisible(true);
		this.dataBean.getDigitalClockFXMLcontroller().getLblHelperTooltip().setVisible(true);
		// show waiting symbol instead of weather icon
		this.dataBean.getDigitalClockFXMLcontroller().setImageViewWeather(new Image("images/waiting.png"));
		this.weatherIconToolTip.setWaitingText(this.dataNotAvailableText);
	}

	/**
	 * Method is used when an exception will thrown by querying the current weather
	 * data from OpenWeather API. Shows the error Symbol instead of the weather icon
	 * and changed the tool tip.
	 */
	private void errorOccursAtFetchingWeatherData() {

		DataBean.isErrorSymbolShowing = true;
		// hide weather data in application and shows error symbol
		this.hideWeatherData();
		// hide progress indicator
		this.dataBean.getDigitalClockFXMLcontroller().getProgressIndicator().setVisible(false);

		// show imageView and label with tooltip
		this.dataBean.getDigitalClockFXMLcontroller().getImageViewWeather().setVisible(true);
		this.dataBean.getDigitalClockFXMLcontroller().getLblHelperTooltip().setVisible(true);
		// show waiting symbol instead of weather icon
		this.dataBean.getDigitalClockFXMLcontroller().setImageViewWeather(new Image("images/error.png"));
		this.weatherIconToolTip.setWaitingText(this.errorByFetchingWeatherDataText);
	}
}
