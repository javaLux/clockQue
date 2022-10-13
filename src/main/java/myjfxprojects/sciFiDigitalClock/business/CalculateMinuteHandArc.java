package myjfxprojects.sciFiDigitalClock.business;

import java.util.function.Function;

public class CalculateMinuteHandArc {
	/**
	 * Start Angle of arc to draw the minute hand (start).
	 */
	public static Function<Integer, Integer> startAngleMinute = (minutes) -> {
		// 360 ÷ 60 = 6 degrees for each minute tick on the clock
		int degrees = (60 - minutes) * 6;
	    // add 90 degrees to position start at the 12'o clock position.
	    // JavaFX arc goes counter clockwise starting zero degrees at the 3 o'clock
	    return (degrees + 90) % 360;
	};
	
	/**
	 * Extent angle of the arc to draw the minute hand (end)
	 */
	public static Function<Integer, Integer> extentAngleMinute = ( minutes ) -> {
		// 360 ÷ 60 = 6 degrees for each minute tick on the clock
		int degrees = (60 - minutes) * 6;
	    // make the extent angle counter clockwise to the 12'o clock position
		// we give 366° to draw the arc full add 12'o clock
	    return (360 - degrees) % 366;
	};
}
