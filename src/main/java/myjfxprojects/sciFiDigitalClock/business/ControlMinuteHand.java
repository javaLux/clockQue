/**
 * 
 */
package myjfxprojects.sciFiDigitalClock.business;

import java.awt.Point;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import myjfxprojects.sciFiDigitalClock.common.DataBean;

/**
 * @author Christian
 *
 * Class managed the minute hand. 
 */
public class ControlMinuteHand {
	
	// DataBean
	private DataBean dataBean = DataBean.getInstance();
	
	// members
	private int minuteStartAngle;
	private int minuteExtentAngle;
	private Point minuteHandTipPoint;
	private Timeline timeLineMinuteHandArc;
	private Timeline timeLineMinuteHandTip;
	
	/**
	 * Constructor initialize members
	 */
	public ControlMinuteHand() {
		
		this.minuteStartAngle = 0;
		this.minuteExtentAngle = 0;
		this.minuteHandTipPoint = new Point();
		this.timeLineMinuteHandArc = new Timeline();
		this.timeLineMinuteHandTip = new Timeline();
	}
	
	/**
	 * Starts the animation of minute hand
	 */
	public void startMinuteHandAnimation() {
		this.timeLineMinuteHandArc.setCycleCount(Timeline.INDEFINITE);
		this.timeLineMinuteHandTip.setCycleCount(Timeline.INDEFINITE);
		
		// calculate minute hand arc
		this.timeLineMinuteHandArc.getKeyFrames().add(new KeyFrame(Duration.seconds(1.0),
        		event -> {

        			minuteStartAngle = CalculateMinuteHandArc.startAngleMinute.apply(DigitalClock.getInstance().getMinutes());
        			minuteExtentAngle =  CalculateMinuteHandArc.extentAngleMinute.apply(DigitalClock.getInstance().getMinutes());
        			dataBean.getDigitalClockFXMLcontroller().getMinuteHandArc().setStartAngle(minuteStartAngle);
        			dataBean.getDigitalClockFXMLcontroller().getMinuteHandArc().setLength(minuteExtentAngle);
        			
        		}));
		
		//calculate minute hand tip
		this.timeLineMinuteHandTip.getKeyFrames().add(new KeyFrame(Duration.seconds(1.0), event -> {
    		minuteHandTipPoint = CalculateMinuteHandTip.minuteHandTipPoint.apply(minuteStartAngle, 49.0);
    		dataBean.getDigitalClockFXMLcontroller().getMinuteHandTip().setTranslateX(minuteHandTipPoint.getX());
    		/*
    		 * It's important to multiplied the Y-Coordinate by -1.
    		 * This is to convert to the screen coordinate system where the Y coordinate going in a southerly direction are positive values.
    		 */
    		dataBean.getDigitalClockFXMLcontroller().getMinuteHandTip().setTranslateY(minuteHandTipPoint.getY() * -1);
    	}));
		
		this.timeLineMinuteHandArc.play();
		this.timeLineMinuteHandTip.play();
	}
	
	/**
	 * Set once the minute hand on the current minute (no time line playing)
	 */
	public void setMinuteHandOnStartUp() {
		// set minute hand
		this.minuteStartAngle = CalculateMinuteHandArc.startAngleMinute.apply(DigitalClock.getInstance().getMinutes());
		this.minuteExtentAngle =  CalculateMinuteHandArc.extentAngleMinute.apply(DigitalClock.getInstance().getMinutes());
		this.dataBean.getDigitalClockFXMLcontroller().getMinuteHandArc().setStartAngle(this.minuteStartAngle);
		this.dataBean.getDigitalClockFXMLcontroller().getMinuteHandArc().setLength(this.minuteExtentAngle);
		
		// set minute hand tip
		this.minuteHandTipPoint = CalculateMinuteHandTip.minuteHandTipPoint.apply(this.minuteStartAngle, 49.0);
		this.dataBean.getDigitalClockFXMLcontroller().getMinuteHandTip().setTranslateX(this.minuteHandTipPoint.getX());
		this.dataBean.getDigitalClockFXMLcontroller().getMinuteHandTip().setTranslateY(this.minuteHandTipPoint.getY() * -1);
	}
	
}

