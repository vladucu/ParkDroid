/**
 * 
 */
package com.licenta.park.types;

import java.io.Serializable;
import java.util.List;

/**
 * @author vladucu
 *
 */
public class ParkingSpaces implements ParkTypes, Serializable {

	private static final long serialVersionUID = 1L;
	
	private List<ParkingSpace> parkingSpaces;
	
	public void ParkingSpaces() {}
	
	public void ParkingSpaces(List<ParkingSpace> parkingSpaces) {
		this.parkingSpaces = parkingSpaces;
	}
	
	public void setParkingSpaces(List<ParkingSpace> parkingSpaces) {
		this.parkingSpaces = parkingSpaces;
	}
	
	public List<ParkingSpace> getParkingSpaces() {
		return parkingSpaces;
	}
}
