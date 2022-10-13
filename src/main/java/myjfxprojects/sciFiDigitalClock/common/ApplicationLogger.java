package myjfxprojects.sciFiDigitalClock.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 
 * @author Christian
 * This class initialize an slf4j (simple logging facade 4 Java) Logger which will use intern as Log4j component.
 * @see maven dependency log4j-slf4j-impl
 */
public class ApplicationLogger {
	
	public static Logger LOGGER = null;
	
	/**
	 * Getter for a thread safe singleton application logger.
	 * @return
	 */
	public static Logger getAppLogger() {
		if(LOGGER == null) {
			synchronized (ApplicationLogger.class) {
				if(LOGGER == null) {
					LOGGER = LoggerFactory.getLogger(ApplicationLogger.class.getSimpleName());
				}
			}
		}
		
		return LOGGER;
	}
}
