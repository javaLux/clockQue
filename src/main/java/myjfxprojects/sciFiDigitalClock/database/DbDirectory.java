/**
 * 
 */
package myjfxprojects.sciFiDigitalClock.database;

import java.io.File;

import org.slf4j.Logger;

import myjfxprojects.sciFiDigitalClock.common.ApplicationLogger;

/**
 * @author Christian
 *
 * This class is only intended to create the directory for the SQLite database in the project folder
 */
public class DbDirectory {
	
	// logger instance
	private final static Logger LOGGER = ApplicationLogger.getAppLogger();
	
	public static final File DB_DIR = new File("data");
	private static String absolutePathDbDir = null;

	// private constructor
	private DbDirectory() {}
	
	/**
	 * Method create the database directory 'data' in the current folder of the JAR file.
	 * 
	 * @return	->	[boolean]	true if and only if the directory was created, false otherwise
	 */
	public static boolean createDbDirectory() {
		
		// check if currently not exists
		if(! DB_DIR.exists()) {
			try {
				boolean successfulCreated = DB_DIR.mkdir();
				
				if(successfulCreated) {
					absolutePathDbDir = DB_DIR.getAbsolutePath();
					return true;
				}
			} catch (SecurityException ex) {
				// if a Security Manager was called
				LOGGER.error("Can not create database directory '" + DB_DIR.getAbsolutePath() + "'", ex);
			}
			
		}
		else {
			absolutePathDbDir = DB_DIR.getAbsolutePath();
			return true;
		}
		
		return false;
	}
	
	// GETTER for the absolute path of database directory
	public static String getDbDirectoryPath() {
		return absolutePathDbDir;
	}
}
