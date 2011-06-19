/**
 * 
 */
package com.licenta.park.types;

import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;



/**
 * @author vladucu 
 * The resource associated to a ParkingSpace
 */
public interface ParkingSpaceResource {
	
	@Get("json")
	public ParkingSpace retrieve();
	
}
