/**
 * 
 */
package myjfxprojects.sciFiDigitalClock.common;

/**
 * @author Christian
 *
 * Define the possible units of measurement and associated the right values for example °C or °F
 */
public enum ETempUnits {
	
	METRIC("metric", "°C"),
	IMPERIAL("imperial", "°F");
	
	private String tempUnit = "";
	private String unitOfMeasurement = "";
	
	private ETempUnits(String unitMeasurment, String tempUnitValue) {
		
		this.unitOfMeasurement = unitMeasurment;
		this.tempUnit = tempUnitValue;
	}
	
	/**
	 * Get the unit of measurement for API call.
	 * 
	 * @return	->	[String]	metric or imperial
	 */
	public String getUnitOfMeasurement() {
		return this.unitOfMeasurement;
	}
	
	/**
	 * Get temperature unit.
	 * 
	 * @return	->	[String]	metric=°C imperial=°F
	 */
	public String getTempUnit() {
		return this.tempUnit;
	}
}
