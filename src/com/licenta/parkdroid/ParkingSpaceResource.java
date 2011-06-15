/**
 * 
 */
package com.licenta.parkdroid;

import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import com.licenta.park.types.ParkingSpace;


/**
 * @author vladucu 
 * The resource associated to a ParkingSpace
 */
public interface ParkingSpaceResource {
	
	@Get("json")
	public ParkingSpace retrieve();
	
}
