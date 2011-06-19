/**
 * 
 */
package com.licenta.park.types;

import org.restlet.resource.Get;

/**
 * @author vladucu 
 * The resource associated to a ParkingSpace
 */
public interface ParkingSpaceResource {
	
	@Get("json")
	public ParkingSpace retrieve();
	
}
