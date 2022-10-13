package myjfxprojects.sciFiDigitalClock.common;

import java.text.SimpleDateFormat;
import java.util.TimeZone;


public class OwnDateTimeFormatter {
	
	public static EHourFormat CustomTimeFormat = null;
	
	private static final EHourFormat DefaultTimeFormat = EHourFormat.HOURS_FORMAT_24;
	
	
	/**
	 * private constructor
	 */
	private OwnDateTimeFormatter() {}
	
	/**
	 * Method converts an given time stamp (milliseconds) in a specific date time format.
	 * Date time format pattern can changed in field CustomTimeFormat.
	 * 
	 * Date time default pattern:	HH:mm:ss
	 * 								-> return a date time in 24 hours format e,g, 20:30:09
	 * 
	 * @param timeStamp		->	[Long]		represents the Unix time stamp in milliseconds
	 * @return				->	[String] 	returns an empty string when format the date time failed
	 * 										otherwise the converted date time string in given format and time zone
	 */
	public static String formatTime(Long timeStamp, String timezone) {
		if(timeStamp != null && timezone != null) {
			SimpleDateFormat timeFormat = new SimpleDateFormat(CustomTimeFormat != null ? CustomTimeFormat.getTimeFormat() : DefaultTimeFormat.getTimeFormat());
			
			// use the given time zone to convert time stamp correctly in date time
			timeFormat.setTimeZone(TimeZone.getTimeZone(timezone));
	
		    return timeFormat.format(timeStamp);
		}
	    
		return "";
	 }
}
