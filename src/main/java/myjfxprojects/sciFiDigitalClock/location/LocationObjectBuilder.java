package myjfxprojects.sciFiDigitalClock.location;

import java.util.Objects;

/**
 * Builder class for location object.
 * 
 * @author Christian
 *
 */
public class LocationObjectBuilder {
	 
	// fields to define a location
	private String fullLocationName = null;
	private String reducedLocationName = null;
	private String latitude = null;
	private String longitude = null;
	
	
	// Singleton instance
	private static LocationObjectBuilder instance = null;
	
	// private constructor
	private LocationObjectBuilder() {}
	
	/**
	 * GETTER for an Singleton instance of this class
	 * @return	->	[LocationObjectBuilder] this
	 */
	public static LocationObjectBuilder getInstance() {
		
		if(instance == null) {
			instance = new LocationObjectBuilder();
		}
		
		return instance;
	}
	
	// builder Methods to initialize members
	public LocationObjectBuilder withFullLocationName(String fullLocationName) {
		
		this.fullLocationName = fullLocationName;
		return this;
	}
	
	public LocationObjectBuilder withReducedLocationName(String reducedLocationName) {
		this.reducedLocationName = reducedLocationName;
		return this;
	}
	
	public LocationObjectBuilder withLatitude(String latidude) {
		this.latitude = latidude;
		return this;
	}
	
	public LocationObjectBuilder withLongitude(String longitude) {
		this.longitude = longitude;
		return this;
	}
	

	public LocationObject build() {
		// check all location properties against null
		Objects.requireNonNull(this.fullLocationName, "Property 'full location name' was not correctly set.");
		Objects.requireNonNull(this.reducedLocationName, "Property 'reduced location name' was not correctly set.");
		Objects.requireNonNull(this.latitude, "Property 'latidude' was not correctly set.");
		Objects.requireNonNull(this.longitude, "Property 'longitude' was not correctly set.");
		
		return new LocationObject(this.fullLocationName, this.reducedLocationName,
				this.latitude, this.longitude);
	}
}
