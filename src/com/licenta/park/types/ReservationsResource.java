/**
 * 
 */
package com.licenta.park.types;

import org.restlet.resource.Get;
import org.restlet.resource.Post;

/**
 * @author vladucu
 *
 */
public interface ReservationsResource {
	
	@Get("json")
	public Reservations getReservations();

	@Post("json")
	public Reservation createReservation(Reservation reservation); 
    	
}
