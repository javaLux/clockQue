package myjfxprojects.sciFiDigitalClock.business;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.Locale;

import myjfxprojects.sciFiDigitalClock.common.DataBean;

/**
 * Class managed the current date informations.
 * 
 * @author Christian
 *
 */
public class ControlDateInfos {
	
	// Thread safe instance of this class
	private static ControlDateInfos instance = null;
	
	/**
	 * Constructor initialized members
	 */
	private ControlDateInfos() {}
	
	/**
	 * GETTER for an Thread-Safe Singleton of this class
	 * @return
	 */
	public static ControlDateInfos getInstance() {
		if(instance == null) {
			synchronized (ControlDateInfos.class) {
				if(instance == null) {
					instance = new ControlDateInfos();
				}
			}
		}
		
		return instance;
	}
	
	public String getCurrentDayOfWeek() {
		
		// create ZonID object with the current using time zone
		ZoneId currentTimeZoneID = ZoneId.of(DataBean.currentTimeZone);
		
		// use current locale from JVM to display day of week in current language
		return LocalDate.now(currentTimeZoneID).getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault());
	}
	
	public String getCurrentMonth() {
		
		// create ZonID object with the current using time zone
		ZoneId currentTimeZoneID = ZoneId.of(DataBean.currentTimeZone);
		
		// use current locale from JVM to display month in current language
		return LocalDate.now(currentTimeZoneID).getMonth().getDisplayName(TextStyle.FULL, Locale.getDefault());
	}
	
	public int getCurrentDayOfMonth() {
		
		// create ZonID object with the current using time zone
		ZoneId currentTimeZoneID = ZoneId.of(DataBean.currentTimeZone);
		return LocalDate.now(currentTimeZoneID).getDayOfMonth();
	}
}
