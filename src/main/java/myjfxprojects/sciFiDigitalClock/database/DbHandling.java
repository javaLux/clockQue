/**
 * 
 */
package myjfxprojects.sciFiDigitalClock.database;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;
import static org.jooq.impl.DSL.using;

import java.io.File;
import java.nio.file.FileSystems;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.swing.ImageIcon;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.impl.SQLDataType;
import org.slf4j.Logger;

import myjfxprojects.sciFiDigitalClock.common.ApplicationLogger;
import myjfxprojects.sciFiDigitalClock.common.DataBean;
import myjfxprojects.sciFiDigitalClock.common.DisplayDimension;
import myjfxprojects.sciFiDigitalClock.common.ETempUnits;
import myjfxprojects.sciFiDigitalClock.common.ErrorBoxSwing;
import myjfxprojects.sciFiDigitalClock.common.InternetConn;
import myjfxprojects.sciFiDigitalClock.location.HandleLocationName;
import myjfxprojects.sciFiDigitalClock.location.LocationObject;
import myjfxprojects.sciFiDigitalClock.location.LocationObjectBuilder;

/**
 * @author Christian
 *
 *         Class handles all the database access with the JOOQ Framework. This
 *         Framework is for manage and execute SQL commands in an easy way and
 *         it wrapped the SQL commands so that these can be used like a good old
 *         java object.
 */
public class DbHandling {
	
	// get a Thread safe instance of application logger
	private static Logger LOGGER = ApplicationLogger.getAppLogger();
	
	// Thread safe instance of data bean
	DataBean dataBean = DataBean.getInstance();
	
	// fields for database connection
	private final String DB_NAME;
	private final String DB_CONNECTION_URL;

	// fields for table names
	private final String TABLE_LOCATIONS;
	private final String TABLE_SETTINGS;

	// Column names for Table locations
	private final String COLUMN_CITY_NAME;
	private final String COLUMN_LATITUDE;
	private final String COLUMN_LONGITUDE;
	private final String COLUMN_TIMEZONE;
	private final String COLUMN_CURRENT_ACTIVE;

	// Columns names for Table settings
	private final String COLUMN_PROXY_ENABLED;
	private final String COLUMN_VBOX_MIDDLE_STATE;
	private final String COLUMN_PROXY_NAME;
	private final String COLUMN_PROXY_PORT;
	private final String COLUMN_TEMP_UNIT;
	private final String COLUMN_MAIN_X_POS;
	private final String COLUMN_MAIN_Y_POS;
	
	// field for database connection
	private Connection dbConnection = null;
	
	// field for the JOOQ SQL executer
	private DSLContext sqlContext = null;
	
	// boolean the specify a location as last selected
	private final boolean markAsCurrentActive = true;
	
	
	// constructor initialize members
	public DbHandling() {
		
		// use system property to deactivate the JOOQ Logo
		// without this flag, it will print at each application start the JOOQ Logo on the console and write
		// them in the Log-File
		System.getProperties().setProperty("org.jooq.no-logo", "true");
		
		this.DB_NAME 					= "appData.db";
		this.DB_CONNECTION_URL 			= "jdbc:sqlite:" + DbDirectory.getDbDirectoryPath() + FileSystems.getDefault().getSeparator() + DB_NAME;
		this.TABLE_LOCATIONS 			= "locations";
		this.TABLE_SETTINGS 			= "settings";
		this.COLUMN_CITY_NAME			= "cityName";
		this.COLUMN_LATITUDE			= "latitude";
		this.COLUMN_LONGITUDE			= "longitude";
		this.COLUMN_TIMEZONE			= "timezone";
		this.COLUMN_CURRENT_ACTIVE		= "isCurrentlyActive";
		this.COLUMN_PROXY_ENABLED		= "proxyEnabled";
		this.COLUMN_VBOX_MIDDLE_STATE	= "vBoxMiddleMinimize";
		this.COLUMN_PROXY_NAME			= "proxyName";
		this.COLUMN_PROXY_PORT			= "proxyPort";
		this.COLUMN_TEMP_UNIT			= "tempUnit";
		this.COLUMN_MAIN_X_POS			= "xPos";
		this.COLUMN_MAIN_Y_POS			= "yPos";
	}
	
	/**
	 * Method check if currently the database exists or not.
	 * 
	 * @return	->	[boolean]	true if exists, otherwise false.
	 */
	private boolean existsDatabase() {
		
		File db = new File(DbDirectory.getDbDirectoryPath() + FileSystems.getDefault().getSeparator() + DB_NAME);
		
		return db.exists();
	}
	
	/**
	 * Method checks if currently the database exists, if the database exists than creates ONLY a connection to the SQlite database.
	 * In the case the database is not existing -> Method write the default settings values in the database.
	 *
	 * @return	->	[boolean] true if and only if the database connection was successfully established.
	 */
	public boolean establishDbConnection() {
		
		try {
			
			// if database currently not exists
			if(! existsDatabase()) {
				
				// Establishing the database connection
				dbConnection = DriverManager.getConnection(this.DB_CONNECTION_URL);
				
				// create an SQL executor to managed and execute the SQL commands like a good old Java object
	        	this.sqlContext = using(dbConnection, SQLDialect.SQLITE);
	        	
	        	// create necessary tables
	        	this.createTables();
	        	
	        	// IMPORTANT: save the default/Fallback location "Berlin" also in database
    			this.createNewLocation(this.dataBean.getDefaultLocation(), markAsCurrentActive);
    			// assigned to the fallback location Berlin the right time zone (default value is Europe/Berlin)
    			this.setTimeZoneForThisLocation(this.dataBean.getDefaultLocation(), DataBean.DefaultTimeZone);
    			
				// write once the default settings values
				this.createOnceTheDefaultSettingsValues();
			}
			// otherwise -> don't write default values in database!!!
			else {
				
				// Establishing the database connection
				dbConnection = DriverManager.getConnection(this.DB_CONNECTION_URL);
				
				// create an SQL executor to managed and execute the SQL commands like a good old Java object
	        	this.sqlContext = using(dbConnection, SQLDialect.SQLITE);
			}
			
			// find and the last activate location and the current active time zone and store them in data bean
			this.getLastSelectedLocation();
					
			return true;
			
		} catch (SQLException ex) {
			// Log Exception in log file
			LOGGER.error("Failed to established a connection to the SQLite database.", ex);
			
			ErrorBoxSwing.showErrorMessage(DataBean.APP_NAME + " - Launch failed",
					"Failed to established a connection to the database\n" + "Application will closed. For more informations, see the log file.",
					new ImageIcon("src/main/resources/images/wheelChair.png"));
			
			System.exit(1);
		}
		
		return false;
	}
	
	/**
	 * Method close the current database connection.
	 */
	public void closeDbConnection() {
		try {
			this.dbConnection.close();
			
		} catch (SQLException ex) {
			// Log Exception in log file
			LOGGER.error("Failed to established a connection to the SQLite database.", ex);
			System.exit(1);
		}
	}
	
	/**
	 * Method creates the necessary tables (if these not exists) in the database.
	 */
	private void createTables() {
		
		if(this.sqlContext != null) {
			
			// IMPORTANT: The method "unique()" tells the table "locations" that it is not allowed
			// more than one location object with the same full location name!!!
			this.sqlContext.createTableIfNotExists(TABLE_LOCATIONS)
        			.column(COLUMN_CITY_NAME, SQLDataType.CHAR)
        			.column(COLUMN_LATITUDE, SQLDataType.CHAR)
        			.column(COLUMN_LONGITUDE, SQLDataType.CHAR)
        			.column(COLUMN_TIMEZONE, SQLDataType.CHAR)
        			.column(COLUMN_CURRENT_ACTIVE, SQLDataType.BOOLEAN)
        			.unique(COLUMN_CITY_NAME)
        			.execute();
			
			this.sqlContext.createTableIfNotExists(TABLE_SETTINGS)
					.column(COLUMN_PROXY_ENABLED, SQLDataType.BOOLEAN)
					.column(COLUMN_VBOX_MIDDLE_STATE, SQLDataType.BOOLEAN)
					.column(COLUMN_PROXY_NAME, SQLDataType.CHAR)
					.column(COLUMN_PROXY_PORT, SQLDataType.CHAR)
					.column(COLUMN_TEMP_UNIT, SQLDataType.CHAR)
					.column(COLUMN_MAIN_X_POS, SQLDataType.FLOAT)
					.column(COLUMN_MAIN_Y_POS, SQLDataType.FLOAT)
					.execute();
		}
	}
	
	// *********************************************************
	// CRUD Methods following
	// CREATE, READ, UPDATE and DELETE sources from database
	// *********************************************************
	
	/**
	 * Method add ONLY a new location to the database if these currently not exists.
	 * 
	 * @param location	->	[LocationObject]	the new location
	 */
	public void createNewLocation(LocationObject location, boolean isCurrentActive) {
		
		if(location != null) {
			
			if(this.sqlContext != null) {
				
				// insert a new location in the table for the locations, the option "onDuplicateKeyIgnore" make that only
	        	// be values stored in the table which currently not exists
				this.sqlContext.insertInto(table(TABLE_LOCATIONS), field(COLUMN_CITY_NAME), field(COLUMN_LATITUDE),
	        			field(COLUMN_LONGITUDE), field(COLUMN_CURRENT_ACTIVE))
	        			.values(location.getFullLocationName(), location.getLatitude(), location.getLongitude(), isCurrentActive)
	        			.onDuplicateKeyIgnore()
	        			.execute();
			}
		}
		else {
			LOGGER.warn("Failed to write new location in SQLite database."
						+ "\nBecause argument 'location' are null. Given arg ->" + "'location: '" + location);
		}
	}
	
	/**
	 * Method write once the default settings values in database.
	 */
	public void createOnceTheDefaultSettingsValues() {
		
		if(this.sqlContext != null) {
			
			boolean proxyDisabled  		= false;
			boolean vBoxMidleMinimize 	= false;
			String proxyNameEmpty  		= "";
			String proxyPortEmpty  		= "";
			String defaultTempUnit 		= "METRIC";
			
			// use default values (from current display dimension) to center main window on screen
			double defaultXPosMainWindow = (DisplayDimension.getScreenWidth() / 2) - 120;
			double defaultYPosMainWindow = (DisplayDimension.getScreenHeight() / 2) - 60;
			
			this.sqlContext.insertInto(table(TABLE_SETTINGS), field(COLUMN_PROXY_ENABLED), field(COLUMN_VBOX_MIDDLE_STATE), field(COLUMN_PROXY_NAME),
					field(COLUMN_PROXY_PORT), field(COLUMN_TEMP_UNIT), field(COLUMN_MAIN_X_POS), field(COLUMN_MAIN_Y_POS))
					.values(proxyDisabled, vBoxMidleMinimize, proxyNameEmpty, proxyPortEmpty, defaultTempUnit, defaultXPosMainWindow, defaultYPosMainWindow)
					.execute();
		}
	}
	
	/**
	 * Method give all available location items from database and store them in the SET for the user locations.
	 */
	public void getAvailableLocations() {
		
		if(this.sqlContext != null) {
			
			// Explicitly selects all available items from table locations
			Result<?> result = this.sqlContext.select().from(TABLE_LOCATIONS).fetch();
			
			if(result.isNotEmpty()) {
				
				result.forEach(locationItem -> {
					
					// build with infos's from database a new location object
					LocationObject locationObject = LocationObjectBuilder.getInstance()
											.withFullLocationName(locationItem.get(COLUMN_CITY_NAME, String.class))
											.withLatitude(locationItem.get(COLUMN_LATITUDE, String.class))
											.withLongitude(locationItem.get(COLUMN_LONGITUDE, String.class))
											.withReducedLocationName(HandleLocationName.reduceLocationName(locationItem.get(COLUMN_CITY_NAME, String.class)))
											.build();
					
					// store each location object in the SET
					this.dataBean.getUserLocationsSet().add(locationObject);
				});
			}
			else {
				
				LOGGER.warn("No locations available from SQlite database. Because result is empty.\nResult: " + result);
			}
		}	
	}
	
	/**
	 * Method find the last selected location item in database and store them in data bean
	 * as last selected location object. Farther the method set the time zone of the last selected location
	 * in data bean.
	 * @throws SQLException 
	 */
	private void getLastSelectedLocation() {
		
		if(this.sqlContext != null) {
			
			Result<?> result = this.sqlContext.select()
					.from(TABLE_LOCATIONS)
					.where(field(COLUMN_CURRENT_ACTIVE).isTrue())
					.fetch();
			
			if(result.isNotEmpty()) {
				
				// get the one location item from database which is mark as last selected
				Record locationItem = result.get(0);
				
				// build a location object
				LocationObject locationObject = LocationObjectBuilder.getInstance()
						.withFullLocationName(locationItem.get(COLUMN_CITY_NAME, String.class))
						.withLatitude(locationItem.get(COLUMN_LATITUDE, String.class))
						.withLongitude(locationItem.get(COLUMN_LONGITUDE, String.class))
						.withReducedLocationName(HandleLocationName.reduceLocationName(locationItem.get(COLUMN_CITY_NAME, String.class)))
						.build();
				
				// store them in data bean as last selected location object
				this.dataBean.setLastActiveLocationObject(locationObject);
				
				// set time zone of the last selected location to get the right date time
				DataBean.currentTimeZone = locationItem.get(COLUMN_TIMEZONE, String.class);
			}
		}
	}
	
	/**
	 * Method read the application settings from SQlite database.
	 */
	public void getApplicationSettings() {
		
		if(this.sqlContext != null) {
			
			// select the one item from table application settings
			Result<?> result = this.sqlContext.select().from(TABLE_SETTINGS).fetch();
			
			if(result.isNotEmpty()) {
				
				// get the item for the application settings
				Record appSettings = result.get(0);
				
				// first:	set the right temperature unit (like METRIC= °C or IMPERIAL= °F)
				String currentTempUnit = appSettings.get(COLUMN_TEMP_UNIT, String.class);
				
				switch (currentTempUnit) {
				case "METRIC":
					this.dataBean.setCurrentTempUnit(ETempUnits.METRIC);
					break;
					
				case "IMPERIAL":
					this.dataBean.setCurrentTempUnit(ETempUnits.IMPERIAL);
					break;

					// if no value from database matched -> default is METRIC(°C)
				default:
					this.dataBean.setCurrentTempUnit(ETempUnits.METRIC);
					break;
				}
				
				// second:	set the state of VBox Middle is minimize or not
				DataBean.isVBoxMiddleMinimize = appSettings.get(COLUMN_VBOX_MIDDLE_STATE, Boolean.class);
				
				// third:	set the right value for proxy using -> this value decides whenever the check box is activated or not
				DataBean.isProxyEnabled = appSettings.get(COLUMN_PROXY_ENABLED, Boolean.class);
				
				// fourth:	get proxy settings from database
				String proxyName = appSettings.get(COLUMN_PROXY_NAME, String.class).trim();
				String proxyPort = appSettings.get(COLUMN_PROXY_PORT, String.class).trim();
				
				// fifth:	set the proxy settings to JVM environments variables if proxy using is enabled
				if(DataBean.isProxyEnabled) {
				
					if( (proxyName != null && ! proxyName.isEmpty()) && (proxyPort != null && ! proxyPort.isEmpty())) {
						// ONLY if reading proxy settings are correct -> changed the proxy settings of JVM
						InternetConn.enableProxySettings(proxyName, proxyPort);							
					}
					
				}
				else {
					
					InternetConn.disableProxySettings();
				}
				
				// sixth:	save the reading values in data bean to get them and use it by initialized the settings view
				//			and put each value in the right text field
				this.dataBean.setProxyName(proxyName);
				this.dataBean.setProxyPort(proxyPort);
				
				// seventh:	save the x and y position for the main window in the data bean
				DataBean.current_X_Pos_MainWindow = appSettings.get(COLUMN_MAIN_X_POS, Float.class);
				DataBean.current_Y_Pos_MainWindow = appSettings.get(COLUMN_MAIN_Y_POS, Float.class);
			}
			else {
				
				LOGGER.warn("No application settings available from SQlite database. Because result is empty.\nResult: " + result);
			}
		}
	}
	
	/**
	 * Method updates all Location items at column "lastSelected" to value false in SQlite database. 
	 */
	public void setAllLocationsAsNotLastSelected() {
		
		if(this.sqlContext != null) {
			
			this.sqlContext.update(table(TABLE_LOCATIONS)).set(field(COLUMN_CURRENT_ACTIVE), false).execute();
		}
	}
	
	/**
	 * Method update a specific location object as "lastSelected" item in database, dependent on the given arguments.
	 * 
	 * @param location	->	[LocationObject]	the new location
	 */
	public void setThisLocationAsLastSelected(LocationObject location) {
		
		if(location != null) {
			
			if(this.sqlContext != null) {
				
				this.sqlContext.update(table(TABLE_LOCATIONS)).set(field(COLUMN_CURRENT_ACTIVE), markAsCurrentActive)
						.where(field(COLUMN_CITY_NAME).equalIgnoreCase(location.getFullLocationName()))
						.and(field(COLUMN_LATITUDE).eq(location.getLatitude()))
						.and(field(COLUMN_LONGITUDE).eq(location.getLongitude()))
						.execute();
			}
		}
		else {
			LOGGER.warn("Failed to update given location in SQLite database."
					+ "\nBecause argument 'location' are null. Given arg ->" + "'location: '" + location);
		}
	}
	
	/**
	 * Method update the field for the time zone of the given location object.
	 * 
	 * @param location	->	[LocationObject]	the current location
	 * @param timezone	->	[String]			the current time zone
	 */
	public void setTimeZoneForThisLocation(LocationObject location, String timezone) {
		
		if( (location != null) && (timezone != null) ) {
			
			if(this.sqlContext != null) {
				
				this.sqlContext.update(table(TABLE_LOCATIONS)).set(field(COLUMN_TIMEZONE), timezone)
						.where(field(COLUMN_CITY_NAME).equalIgnoreCase(location.getFullLocationName()))
						.and(field(COLUMN_LATITUDE).eq(location.getLatitude()))
						.and(field(COLUMN_LONGITUDE).eq(location.getLongitude()))
						.execute();
			}
		}
		else {
			LOGGER.warn("Failed to update current time zone for the given location in SQLite database."
					+ "\nBecause one or more arguments are null. Given args -> " + " 'location: '" + location + ", 'timezone: '" + timezone);
		}
	}
	
	/**
	 * Method write the current application settings in SQlite database.
	 */
	public void setApplicationSettings() {
		
		if(this.sqlContext != null) {
			
			this.sqlContext.update(table(TABLE_SETTINGS))
					.set(field(COLUMN_PROXY_ENABLED), DataBean.isProxyEnabled)
					.set(field(COLUMN_VBOX_MIDDLE_STATE), DataBean.isVBoxMiddleMinimize)
					.set(field(COLUMN_PROXY_NAME), this.dataBean.getSettingsViewController().getTxtFieldProxyName().getText())
					.set(field(COLUMN_PROXY_PORT), this.dataBean.getSettingsViewController().getTxtFieldProxyPort().getText())
					.set(field(COLUMN_TEMP_UNIT), this.dataBean.getSettingsViewApp().getToggleGroup().getSelectedToggle().getUserData().toString())
					.set(field(COLUMN_MAIN_X_POS), DataBean.current_X_Pos_MainWindow)
					.set(field(COLUMN_MAIN_Y_POS), DataBean.current_Y_Pos_MainWindow)
					.execute();
		}
	}
	
	/**
	 * Method delete an existing location item from database, dependent on the given arguments.
	 * 
	 * @param name	->	[String]	the city name
	 * @param lat	->	[String]	the latitude value
	 * @param lon	->	[String]	the longitude value
	 */
	public void deleteExistingLocation(String name, String lat, String lon) {
		
		if((name != null) && (lat != null) && (lon != null) ) {
			
			if(this.sqlContext != null) {
				
				this.sqlContext.delete(table(TABLE_LOCATIONS))
				.where(field(COLUMN_CITY_NAME).equalIgnoreCase(name))
				.and(field(COLUMN_LATITUDE).eq(lat))
				.and(field(COLUMN_LONGITUDE).eq(lon))
				.execute();
			}
		}
	}
}