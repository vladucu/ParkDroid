/**
 * 
 */
package com.licenta.parkdroid;

import java.util.List;
import org.restlet.resource.Get;

import com.licenta.park.types.ParkingSpace;
import com.licenta.park.types.ParkingSpaces;

/**
 * @author vladucu
 *
 */
public interface ParkingSpacesResource {

	@Get("json") 
	public ParkingSpaces retrieve();

}
