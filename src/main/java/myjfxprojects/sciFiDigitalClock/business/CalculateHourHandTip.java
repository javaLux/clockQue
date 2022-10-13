package myjfxprojects.sciFiDigitalClock.business;

import java.awt.Point;
import java.util.function.BiFunction;

public class CalculateHourHandTip {

	public static BiFunction<Integer, Double, Point> hourHandTipPoint = (angleDegrees, radius) -> {
		Point tipPoint = new Point();
		tipPoint.x = (int) (Math.cos(Math.toRadians(angleDegrees)) * radius);
		tipPoint.y = (int) (Math.sin(Math.toRadians(angleDegrees)) * radius);
		
		return tipPoint;
	};
}
