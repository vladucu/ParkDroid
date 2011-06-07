package com.licenta.parkdroid;

import java.io.Serializable;
import java.util.Date;

import org.codehaus.jackson.annotate.JsonTypeInfo;

//@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS)
public class Reservation implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private int reservationId;
	private String start_time;
	private String end_time;
	private int cost;	
	private int parkingId;
	private int userId;
	
	/*
	 * [ID] [int] NOT NULL,
	[start_time] [datetime] NOT NULL,
	[end_time] [datetime] NOT NULL,
	[cost] [datetime] NOT NULL,
	[parking_ID] [int] NOT NULL,
	[user_ID] [int] NOT NULL,
 CONSTRAINT [PK_UserReservations] PRIMARY KEY CLUSTERED 
	 */

    public Reservation() {
    }

    public Reservation(String start_time, String end_time, int cost, int parkingId, int userId) {
    	this.start_time = start_time;
        this.end_time = end_time;
        this.cost = cost;
        this.parkingId = parkingId;
        this.userId = userId;
    }

    public int getId() {
        return reservationId;
    }

    public void setId(int id) {
        this.reservationId = id;
    }

    public String getStartTime() {
        return start_time;
    }

    public void setStartTime(String start_time) {
        this.start_time = start_time;
    }
    
    public String getEndTime() {
        return end_time;
    }

    public void setEndTime(String end_time) {
        this.end_time = end_time;
    }
    
    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }
    
    public int getParkingId() {
        return parkingId;
    }

    public void setParkingId(int parkingId) {
        this.parkingId = parkingId;
    }
    
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}