/**
 * 
 */
package com.licenta.parkdroid.types;

import java.io.Serializable;
import java.util.List;

/**
 * @author vladucu
 *
 */
public class Reservations implements ParkTypes, Serializable {

	private static final long serialVersionUID = 1L;
	
	private List<Reservation> reservations;
	
	public void Reservations() {}
	
	public void Reservations(List<Reservation> reservations) {
		this.reservations = reservations;
	}
	
	public void setReservations(List<Reservation> reservations) {
		this.reservations = reservations;
	}
	
	public List<Reservation> getReservations() {
		return reservations;
	}
}
