package myjfxprojects.sciFiDigitalClock.business;

import java.util.function.Function;

public class CalculateHourHandArc {

	/**
	 * Start Angle of arc to draw the hour hand (start).
	 */
	public static Function<Integer, Integer> startAngleHour = (hours) -> {
		// 360 ÷ 12 = 30 degrees for each hours tick on the clock, because the hourHandArc represents
		// a analog clock and for this reason we must calculate with 12 hours
	    int degrees = (12 - hours) * 30;
	    // add 90 degrees to position start at the 12'o clock position.
	    // JavaFX arc goes counter clockwise starting zero degrees at the 3 o'clock
	    return (degrees + 90) % 360;
	};
	
	/**
	 * Extent angle of the arc to draw the hour hand (end)
	 */
	public static Function<Integer, Integer> extentAngleHour = ( hours ) -> {
	    // 360 ÷ 12 = 30 degrees for each hours tick on the clock, because the hourHandArc represents
		// a analog clock and for this reason we must calculate with 12 hours
		int degrees = (12 - hours) * 30;
	    // make the extent angle counter clockwise to the 12'o clock position
		// we give 390° to draw the arc full add 12'o clock
	    return (360 - degrees) % 390;
	};
}
