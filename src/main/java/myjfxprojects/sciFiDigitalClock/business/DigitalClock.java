package myjfxprojects.sciFiDigitalClock.business;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.TimeZone;

import myjfxprojects.sciFiDigitalClock.common.DataBean;
import myjfxprojects.sciFiDigitalClock.common.EHourFormat;

public class DigitalClock {
	
	private Calendar calender = null;
	
	//static instance
	private static DigitalClock instance = null;
	
	/**
	 * private constructor
	 */
	private DigitalClock() {}
	
	/**
	 * GETTER for an Thread-Safe Singleton of this class
	 * @return
	 */
	public static DigitalClock getInstance() {
		if(instance == null) {
			synchronized (DigitalClock.class) {
				if(instance == null) {
					instance = new DigitalClock();
				}
			}
		}
		
		return instance;
	}
	
	/**
	 * Get the current date time dependent on the current time zone in the given hour format.
	 * @return
	 */
	public String getDigitalTime(EHourFormat hourFormat) {
		
		// set clock time format, dependent on the given argument EHourFormat (by default 24h)
		DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern(hourFormat.getTimeFormat());
		
		// get always a new instance of the Calendar object to get current time
		Instant nowUTC = Instant.now();
		
		// create ZonID object with the current using time zone
		ZoneId currentTimeZoneID = ZoneId.of(DataBean.currentLocationTimeZone);
		
		// get date time for the current using time zone
		ZonedDateTime currentDateTime = ZonedDateTime.ofInstant(nowUTC, currentTimeZoneID);
		
		// return date time in the given time format
		return currentDateTime.format(timeFormat);
	}

	/**
	 * Method give the current hours dependent on the given time zone in 12h time format. This is IMPORTANT, because
	 * this method managed the hour hand and hour hand track. The hour hand and hour hand track
	 * is used in 12h format.
	 * @return the hours
	 */
	public int getHours() {
		
		// create ZonID object with the current using time zone
		ZoneId currentTimeZoneID = ZoneId.of(DataBean.currentLocationTimeZone);
		
		// get always a new instance of the Calendar object to get new time
		this.calender = Calendar.getInstance();
		
		this.calender.setTimeZone(TimeZone.getTimeZone(currentTimeZoneID));
		
		// return hours for the current time zone in 12 hours format
		return this.calender.get(Calendar.HOUR);
	}

	/**
	 * Method give the current minutes dependent on the given time zone.
	 * @return the minutes
	 */
	public int getMinutes() {

		// get always a new instance of the Calendar object to get current time
		Instant nowUTC = Instant.now();
		
		// create ZonID object with the current using time zone
		ZoneId currentTimeZoneID = ZoneId.of(DataBean.currentLocationTimeZone);
		
		// get date time for the current using time zone
		ZonedDateTime currentDateTime = ZonedDateTime.ofInstant(nowUTC, currentTimeZoneID);
		
		return currentDateTime.getMinute();
	}
	
	/**
	 * Method calculate the time shift in hours between the system time (from JVM) and the current active
	 * location time zone.
	 * 
	 * @return	->	[long]	difference in hours, value can be negative
	 */
	public long getCurrentTimeShiftInHours() {
		
		// create ZonID object with the current using time zone
		ZoneId currentTimeZoneID = ZoneId.of(DataBean.currentLocationTimeZone);
		
		// get current system time (from JVM)
		LocalDateTime currentSystemTime = LocalDateTime.now();
		// get current time from active location, dependent on the time zone
		LocalDateTime activeLocationTime = LocalDateTime.now(currentTimeZoneID);
		
		return Duration.between(currentSystemTime, activeLocationTime).toHours();
	}
}
