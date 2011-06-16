/**
 * 
 */
package com.licenta.park.types;

import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Put;

/**
 * @author vladucu
 *
 */
public interface ReservationResource extends Resources {
	
	@Get("json")
	public Reservation getReservation();
	
	@Put("json")
	public Reservation updateReservation(Reservation reservation);
	
	@Delete
	public void deleteReservation();

}
