package com.licenta.park.types;

import java.io.Serializable;

public class ParkingSpace implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private int id;
	private String name;
	private String address;
	private String geolat;
	private String geolong;
	private int spaces;

    public ParkingSpace() {
    }

    public ParkingSpace(int id, String name, String address, String geolat, String geolong, int spaces) {
    	super();
    	this.id = id;
    	this.name = name;
        this.address = address;
        this.geolat = geolat;
        this.geolong = geolong;
        this.spaces = spaces;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getGeoLat() {
        return geolat;
    }

    public void setGeoLat(String geolat) {
        this.geolat = geolat;
    }    

    public String getGeoLong() {
        return geolong;
    }

    public void setGeoLong(String geolong) {
        this.geolong = geolong;
    }  
    
    public int getSpaces() {
        return spaces;
    }

    public void setSpaces(int spaces) {
        this.spaces = spaces;
    }
    /*
    public User getUser() {
    	return user;
    }
    
    public void setUser(User user) {
    	this.user = user;
    }*/
}