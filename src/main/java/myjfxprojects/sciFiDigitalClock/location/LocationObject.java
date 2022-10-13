package myjfxprojects.sciFiDigitalClock.location;

import java.util.Objects;

/**
 * Class represents a location with a reduced location name and a full qualified location name and the geo data.
 * 
 * @author Christian
 *
 */
public class LocationObject {
	
	// fields to define a location
	private final String fullLocationName;
	private final String reducedLocationName;
	private final String latitude;
	private final String longitude;
	
	
	/**
	 * public constructor.
	 * Use location builder class to initialize fields correctly.
	 * 
	 * @param locationBuilder
	 */
	public LocationObject(String fullName, String reducedName, String latidude, String longitude) {
		
		// initialize the members
		this.fullLocationName = fullName;
		this.reducedLocationName = reducedName;
		this.latitude = latidude;
		this.longitude = longitude;
	}
	
	/**
	 * @return ->	[String]	the fullLocationName
	 */
	public String getFullLocationName() {
		return this.fullLocationName;
	}
	/**
	 * @return ->	[String]	reducedLocationName
	 */
	public String getReducedLocationName() {
		return this.reducedLocationName;
	}
	/**
	 * @return ->	[String]	the latitude
	 */
	public String getLatitude() {
		return this.latitude;
	}
	/**
	 * @return ->	[String]	longitude
	 */
	public String getLongitude() {
		return this.longitude;
	}
	
	
	/**
	 * IMPORTANT: return only the reduced location name to display the location in a right way on combo box
	 */
	@Override
	public String toString() {

		return this.reducedLocationName;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(fullLocationName, reducedLocationName);
	}

	/**
	 * Method define when an LocationObject is equal against another LocationObject
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LocationObject other = (LocationObject) obj;
		return Objects.equals(this.fullLocationName, other.fullLocationName) && Objects.equals(this.reducedLocationName, other.reducedLocationName);
	}
}
