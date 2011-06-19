/**
 * 
 */
package com.licenta.park.types;

import org.restlet.resource.Get;


/**
 * @author vladucu
 *
 */
public interface ParkingSpacesResource {

	@Get("json") 
	public ParkingSpaces retrieve();

}
