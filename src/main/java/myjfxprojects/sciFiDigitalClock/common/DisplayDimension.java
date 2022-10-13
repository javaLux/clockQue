/**
 * 
 */
package myjfxprojects.sciFiDigitalClock.common;

import java.awt.Dimension;
import java.awt.Toolkit;

/**
 * @author Christian
 * 
 * Class to get the current display dimension.
 */
public class DisplayDimension {

	// private constructor
	private DisplayDimension( ) {}
	
	/**
	 * Method to get the display size of the current screen.
	 * On systems with multiple displays, the primary display screen resolution will used.
	 * 
	 * @return	->	[Dimension]	with the current display size
	 */
	public static Dimension getCurrentDisplayDimension() {
		
		return Toolkit.getDefaultToolkit().getScreenSize();
	}
	
	/**
	 * Method get the height of the current display size.
	 * On systems with multiple displays, the primary display screen resolution will used.
	 * 
	 * @return	->	[double]	the display height
	 */
	public static double getScreenHeight() {
		
		return Toolkit.getDefaultToolkit().getScreenSize().getHeight();
	}
	
	/**
	 * Method get the width of the current display size.
	 * On systems with multiple displays, the primary display screen resolution will used.
	 * 
	 * @return	->	[double]	the display width
	 */
	public static double getScreenWidth() {
		
		return Toolkit.getDefaultToolkit().getScreenSize().getWidth();
	}
}
