/**
 * 
 */
package myjfxprojects.sciFiDigitalClock.business;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import myjfxprojects.sciFiDigitalClock.common.DataBean;
import myjfxprojects.sciFiDigitalClock.common.EHourFormat;

/**
 * @author Christian
 *
 * Class managed the digital clock and the Visibility of
 * the weather image and temperature text dependent on state of Internet connectivity. 
 */
public class ControlDigitalClock {
	
	// DataBean
	private DataBean dataBean = DataBean.getInstance();
	
	// Digital clock time line
	private Timeline timeLineClock;
	
	// day of month at app start
	private int dayOfMonthAtAppStart;
	
	/**
	 * Constructor initialize members
	 */
	public ControlDigitalClock() {
		this.dayOfMonthAtAppStart = ControlDateInfos.getInstance().getCurrentDayOfMonth();
		this.timeLineClock = new Timeline();
	}
	
	/**
	 * Starts the animation of digital clock, check the date info's at run time and
	 * checks every seconds the state of Internet connection.
	 */
	public void startDigitalClockAnimation() {
		
		this.timeLineClock.setCycleCount(Timeline.INDEFINITE);
		this.timeLineClock.getKeyFrames().add(new KeyFrame(Duration.seconds(1.0),
        		event -> {
        			// make weather image view and temperature label visible when Internet connection is up
        			// no waiting or error symbol are showing
        			if((DataBean.isInternetUp.get()) && ! (DataBean.isWaitingSymbolShowing) && ! (DataBean.isErrorSymbolShowing)) {
        				// hide progress indicator
        				dataBean.getDigitalClockFXMLcontroller().getProgressIndicator().setVisible(false);
        				dataBean.getDigitalClockFXMLcontroller().getImageViewWeather().setVisible(true);
        				dataBean.getDigitalClockFXMLcontroller().getLblCurrentTemp().setVisible(true);
        				dataBean.getDigitalClockFXMLcontroller().getTextTempUnit().setVisible(true);
        				// show label for tooltip weather icon to activate tooltip
        				dataBean.getDigitalClockFXMLcontroller().getLblHelperTooltip().setVisible(true);
        			}
        			// if currently no Internet connection available -> show progress indicator and the associated tool tip
        			else if(! DataBean.isInternetUp.get()){
        				dataBean.getDigitalClockFXMLcontroller().getProgressIndicator().setVisible(true);
        				dataBean.getDigitalClockFXMLcontroller().getImageViewWeather().setVisible(false);
        				dataBean.getDigitalClockFXMLcontroller().getLblCurrentTemp().setVisible(false);
        				dataBean.getDigitalClockFXMLcontroller().getTextTempUnit().setVisible(false);
        				// hide label for tool tip weather icon to deactivate tool tip
        				dataBean.getDigitalClockFXMLcontroller().getLblHelperTooltip().setVisible(false);
        			}
        			
        			// set text for the digital clock
        			dataBean.getDigitalClockFXMLcontroller().getLblDigitalClock()
        			.setText(DigitalClock.getInstance().getDigitalTime(EHourFormat.HOURS_FORMAT_24));
        			
        			// check if day of month has been changed at run time
        			if(dayOfMonthAtAppStart != ControlDateInfos.getInstance().getCurrentDayOfMonth()) {
        				
        				// set new date info's dependent on the current active time zone
        				dataBean.getDigitalClockFXMLcontroller().setLblWeekDay(ControlDateInfos.getInstance().getCurrentDayOfWeek());
        	        	dataBean.getDigitalClockFXMLcontroller().setLblMonth(ControlDateInfos.getInstance().getCurrentMonth());
        	        	dataBean.getDigitalClockFXMLcontroller().setLblMonthDay(ControlDateInfos.getInstance().getCurrentDayOfMonth());
        			}
        		}));
		
		this.timeLineClock.play();
	}
	
	/**
	 * Set once the digital clock on the current system time (no time line playing)
	 */
	public void setDigitalClockOnStartUp() {
		this.dataBean.getDigitalClockFXMLcontroller().getLblDigitalClock()
		.setText(DigitalClock.getInstance().getDigitalTime(EHourFormat.HOURS_FORMAT_24));
	}
}
