/**
 * 
 */
package com.licenta.parkdroid.types;

import org.restlet.representation.Representation;
import org.restlet.resource.Get;


/**
 * @author vladucu
 *
 */
public interface ParkingSpacesResource {

	@Get("json") 
	public ParkingSpaces retrieve();

}
