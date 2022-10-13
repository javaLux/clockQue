package myjfxprojects.sciFiDigitalClock.common;

/**
 * Enum holds the possible instance of the time format.
 * 
 * @author Christian
 *
 */
public enum EHourFormat {
	HOURS_FORMAT_24("HH:mm"),
	HOURS_FORMAT_12("hh:mm");
	
	// field for the time format value (like HH:mm)
	private String timeFormat;
	
	private EHourFormat(String format) {
		this.timeFormat = format;
	}
	
	// GETTER for the time format value
	public String getTimeFormat() {
		return this.timeFormat;
	}
}
