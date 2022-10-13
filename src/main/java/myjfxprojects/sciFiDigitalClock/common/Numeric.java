/**
 * 
 */
package myjfxprojects.sciFiDigitalClock.common;

import java.util.regex.Pattern;

/**
 * @author Christian
 *
 */
public class Numeric {

	// RegEx to find non digit characters
	private static final Pattern nonDigit = Pattern.compile("\\D");
	
	private Numeric() {}
	
	
	/**
	 * Method replace all non digit characters from the given string.
	 * 
	 * @param txtFieldInput	->	[String]	String to check on non digit chars
	 * @return				->	[String]	replaced String, or an empty String if no non digit chars where found
	 */
	public static String removeAllNotDigitChars(String txtFieldInput) {
		
		return txtFieldInput.replaceAll(nonDigit.toString(), "");
	}
}
