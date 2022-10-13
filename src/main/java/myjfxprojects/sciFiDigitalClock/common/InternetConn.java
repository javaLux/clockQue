package myjfxprojects.sciFiDigitalClock.common;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.Map.Entry;

import javax.net.ssl.HttpsURLConnection;

import org.slf4j.Logger;

import javafx.concurrent.ScheduledService;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import myjfxprojects.sciFiDigitalClock.business.Weather;

/**
 * Class to check in the current internet connection at run time.
 * This is for updating weather data important.
 * 
 * @author csd
 *
 */
public class InternetConn {
	
	
	// field for connection time out 13s -> 13000ms
	private static final int CONNECT_TIMEOUT = 13000;

	// Test URL for testing Internet connection
	private static final String TEST_URL = "https://www.google.com";
	
	// logger instance
	private final static Logger LOGGER = ApplicationLogger.getAppLogger();
	
	// field to save thrown exception
	private static Exception connEx = null;
	
	// field to define if Internet connection is up or down write once in log file
	private static boolean isWrittenException = false;
	
	// fields to set the environments system variables for HTTP and HTTPS proxy settings
	private static final String HTTP_PROXY_HOST_PROPERTY	= 	"http.proxyHost";
	private static final String HTTP_PROXY_PORT_PROPERTY	= 	"http.proxyPort";
	private static final String HTTPS_PROXY_HOST_PROPERTY	=	"https.proxyHost";
	private static final String HTTPS_PROXY_PORT_PROPERTY	= 	"https.proxyPort";
	
	// Scheduled JavaFX service to check Internet connection every duration (set in INIT-Method class App up to 10 s)
	public static ScheduledService<Void> SCHEDULED_SERVICE_INTERNET_CONN = new ScheduledService<Void>() {

		@Override
		protected Task<Void> createTask() {
			
			Task<Void> task = new Task<Void>() {

				@Override
				protected Void call() throws Exception {
		
					checkInternetConnection();
					
					return null;
				}
			};

			return task;
		}
	};
	
	// Background JavaFX Task to check once is Internet Up or Down 
	private static Service<Void> ONCE_SERVICE_INTERNET_CONN = new Service<Void>() {
		
		/**
		 * Constructor of class Service!!!
		 */
		{
			// add Event handling for the succeeded event of this service
			this.setOnSucceeded( event -> {
				// ONLY if this service succeeded make a new weather data API Call
				Weather.getInstance().startOnceTimelineToFetchWeatherData();
			});
		}

		@Override
		protected Task<Void> createTask() {
			
			Task<Void> task = new Task<Void>() {

				@Override
				protected Void call() throws Exception {
					
					// check Internet connection state
					checkInternetConnection();
					
					return null;
				}
			};
			
			return task;
		}
		
	};
	
	/**
	 * Method checks one time the current state of the Internet connection.
	 */
	public static void startOnceServiceToCheckInternet() {
		
		try {
			// IMPORTANT: use the "restart" Method instead to the normal "start" method, because with the "start" method
			// will thrown a Exception when this service start again and it is not in state READY
			ONCE_SERVICE_INTERNET_CONN.restart();
			
		} catch (Exception ex) {
			// handle exception from executing this service
			LOGGER.error("Failed to execute service 'ONCE_SERVICE_INTERNET_CONN' ", ex);
		}	
	}
	
	/**
	 * Method checks if currently an Internet connection is up or down.
	 * This information will be logged in the log file.
	 */
	private static void checkInternetConnection() {
		
		try {
			URL url = new URL(TEST_URL);
			HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
			connection.setConnectTimeout(CONNECT_TIMEOUT);
			connection.connect();
			DataBean.isInternetUp.set(true);
			
			// write log info for reconnected Internet connection
			if(isWrittenException) {
				LOGGER.warn("Internet connection was established again.");
				connEx = null;
				isWrittenException = false;
			}
			
		}
		catch (MalformedURLException ex) {
			DataBean.isInternetUp.set(false);
			
			// save exception for writing in log file
			connEx = ex;
		}
		catch (SocketTimeoutException ex) {
			DataBean.isInternetUp.set(false);
			
			connEx = ex;
		}
		catch (IOException ex) {
			DataBean.isInternetUp.set(false);
			
			connEx = ex;
		}
		
		// write only once the error for the Internet connection in log file
		if((connEx != null) && ! (isWrittenException)) {
			LOGGER.error("Internet connection down", connEx);
			isWrittenException = true;
		}
	}
	
	/**
	 * Enable and set the environments fields to use proxy settings for HTTPS and HTTPS in the JVM.
	 */
	public static void enableProxySettings(String proxyServer, String proxyPort) {
		
		if((proxyServer != null) && (proxyPort != null)) {
			try {
				// check if currently the env variables are set or not
				if((System.getProperty(HTTP_PROXY_HOST_PROPERTY) == null) && (System.getProperty(HTTP_PROXY_PORT_PROPERTY) == null)
						&& (System.getProperty(HTTPS_PROXY_HOST_PROPERTY) == null) && (System.getProperty(HTTPS_PROXY_PORT_PROPERTY) == null)) {
					
					// set new properties for HTTP and HTTPS proxy settings
					System.setProperty(HTTP_PROXY_HOST_PROPERTY, proxyServer);
					System.setProperty(HTTP_PROXY_PORT_PROPERTY, proxyPort);
					System.setProperty(HTTPS_PROXY_HOST_PROPERTY, proxyServer);
					System.setProperty(HTTPS_PROXY_PORT_PROPERTY, proxyPort);
				}
				else {
					// change existing proxy settings for HTTP and HTTPS
					for(Entry<Object, Object> property : System.getProperties().entrySet()) {
						
						if(property.getKey() == HTTP_PROXY_HOST_PROPERTY) {
							property.setValue(proxyServer);
						}
						if(property.getKey() == HTTP_PROXY_PORT_PROPERTY) {
							property.setValue(proxyPort);
						}
						if(property.getKey() == HTTPS_PROXY_HOST_PROPERTY) {
							property.setValue(proxyServer);
						}
						if(property.getKey() == HTTPS_PROXY_PORT_PROPERTY) {
							property.setValue(proxyPort);
						}
					}
				}
				
				// Logging the new proxy settings
				LOGGER.warn("Proxy Settings have been succesfully changed by user.\nCurrent Proxy Settings for HTTP and HTTPS: Proxy-Server: '"
				+ proxyServer + "' Proxy-Port: '" + proxyPort + "'");
				
			} catch (Exception ex) {
				// write in LOGGER
				LOGGER.error("Failed to set or enable proxy settings. Given params 'proxyServer': " + proxyServer + ", 'proxyPort': " + proxyPort, ex);
			}
			
		}
		else {
			LOGGER.warn("Can not set or enable proxy settings, because one or more arguments are null.\nParams: proxyServer = '" + proxyServer + "' proxyPort = '" + proxyPort + "'");
		}		
	}
	
	/**
	 * Disable and remove proxy HTTP and HTTPS settings from JVM.
	 */
	public static void disableProxySettings() {
		
		try {
			
			if((System.getProperty(HTTP_PROXY_HOST_PROPERTY) != null) && (System.getProperty(HTTP_PROXY_PORT_PROPERTY) != null)
					&& (System.getProperty(HTTPS_PROXY_HOST_PROPERTY) != null) && (System.getProperty(HTTPS_PROXY_PORT_PROPERTY) != null)) {
						
				System.clearProperty(HTTP_PROXY_HOST_PROPERTY);
				System.clearProperty(HTTP_PROXY_PORT_PROPERTY);
				System.clearProperty(HTTPS_PROXY_HOST_PROPERTY);
				System.clearProperty(HTTPS_PROXY_PORT_PROPERTY);
			}
			
		} catch (Exception ex) {
			// write in LOGGER
			LOGGER.error("Failed to clear current proxy settings.", ex);
		}
		
	}
}
