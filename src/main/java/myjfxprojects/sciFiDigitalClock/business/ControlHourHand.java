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
 * Class managed the hour hand. 
 */
public class ControlHourHand {
	
	// DataBean
	private DataBean dataBean = DataBean.getInstance();
	
	// members
	private int hourStartAngle;
	private int hourExtentAngle;	
	private Point hourHandTipPoint;
	private Timeline timeLineHourHandArc;
	private Timeline timeLineHourHandTip;
	
	/**
	 * Constructor initialize members
	 */
	public ControlHourHand() {
		
		this.hourStartAngle = 0;
		this.hourExtentAngle = 0;
		this.hourHandTipPoint = new Point();
		this.timeLineHourHandArc = new Timeline();
		this.timeLineHourHandTip = new Timeline();
	}
	
	/**
	 * Starts the Animation of hour hand (set the hour hand on the current hour).
	 */
	public void startHourHandAnimation() {
		
		this.timeLineHourHandArc.setCycleCount(Timeline.INDEFINITE);
		this.timeLineHourHandTip.setCycleCount(Timeline.INDEFINITE);
		
		// calculate the hour hand arc
		this.timeLineHourHandArc.getKeyFrames().add(new KeyFrame(Duration.seconds(1.0),
        		event -> {
        			
        			hourStartAngle = CalculateHourHandArc.startAngleHour.apply(DigitalClock.getInstance().getHours());
        			hourExtentAngle =  CalculateHourHandArc.extentAngleHour.apply(DigitalClock.getInstance().getHours());
        			dataBean.getDigitalClockFXMLcontroller().getHourHandArc().setStartAngle(hourStartAngle);
        			dataBean.getDigitalClockFXMLcontroller().getHourHandArc().setLength(hourExtentAngle);
        			
        		}));
    	
		// calculate the hour hand tip
    	this.timeLineHourHandTip.getKeyFrames().add(new KeyFrame(Duration.seconds(1.0), event -> {
    		
    		hourHandTipPoint = CalculateHourHandTip.hourHandTipPoint.apply(hourStartAngle, 34.0);
    		dataBean.getDigitalClockFXMLcontroller().getHourHandTip().setTranslateX(hourHandTipPoint.getX());
    		/*
    		 * It's important to multiplied the Y-Coordinate by -1.
    		 * This is to convert to the screen coordinate system where the Y coordinate going in a southerly direction are positive values.
    		 */
    		dataBean.getDigitalClockFXMLcontroller().getHourHandTip().setTranslateY(hourHandTipPoint.getY() * -1);
    	}));
    	
    	this.timeLineHourHandArc.play();
    	this.timeLineHourHandTip.play();
	}
	
	/**
	 * Set once the hour hand on the current hour (no time line playing)
	 */
	public void setHourHandOnStartUp() {
		// set hour hand 
		this.hourStartAngle = CalculateHourHandArc.startAngleHour.apply(DigitalClock.getInstance().getHours());
		this.hourExtentAngle =  CalculateHourHandArc.extentAngleHour.apply(DigitalClock.getInstance().getHours());
		this.dataBean.getDigitalClockFXMLcontroller().getHourHandArc().setStartAngle(this.hourStartAngle);
		this.dataBean.getDigitalClockFXMLcontroller().getHourHandArc().setLength(this.hourExtentAngle);
		// set hour hand tip
		this.hourHandTipPoint = CalculateHourHandTip.hourHandTipPoint.apply(this.hourStartAngle, 34.0);
		this.dataBean.getDigitalClockFXMLcontroller().getHourHandTip().setTranslateX(this.hourHandTipPoint.getX());
		this.dataBean.getDigitalClockFXMLcontroller().getHourHandTip().setTranslateY(this.hourHandTipPoint.getY() * -1);
	}
}
