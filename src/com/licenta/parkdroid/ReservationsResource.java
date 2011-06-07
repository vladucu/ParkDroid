/**
 * 
 */
package com.licenta.parkdroid;

import java.util.ArrayList;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;

/**
 * @author vladucu
 *
 */
public interface ReservationsResource {
	
	@Post("json")
    public Reservation createReservation(Reservation res);
	
	@Get("json")
	public ArrayList<Reservation> getReservations();
    
    
    	
}
