/**
 * 
 */
package com.licenta.park.types;
import org.restlet.ext.jackson.JacksonRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;

/**
 * @author vladucu
 *
 */
public interface ReservationsResource extends Resources {
	/*
	@Post("json")
    public Reservation createReservation(Reservation res);*/
	
	@Get("json")
	public Reservations getReservations();

/*	@Post("json")
	public Reservation createReservation(int parkingSpaceId,
			String startTime, String endTime); */
	
	@Post("json")
	public Reservation createReservation(Reservation reservation); 
    	
}
