/**
 * 
 */
package com.licenta.park.types;

import java.util.Collection;
import java.util.List;

import org.codehaus.jackson.annotate.JsonSetter;
import org.restlet.resource.Get;


/**
 * @author vladucu
 *
 */
public interface ParkingSpacesResource {

	@Get("json") 
	public ParkingSpaces retrieve();

}
