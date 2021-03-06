package com.licenta.parkdroid.types;

import android.os.Parcel;
import android.os.Parcelable;

public class ParkingSpace implements ParkTypes, Parcelable {

	//private static final long serialVersionUID = 1L;
	
	private int id;
	private String name;
	private String address;
	private String geolat;
	private String geolong;
	private int spaces;  
	private int price;
	private String phone;
	private String openHours;
	private String distance;

    public ParkingSpace() {
    }

    public ParkingSpace(int id, String name, String address, String geolat, String geolong, int spaces, int price,
    		String phone, String openHours, String distance) {
    	super();
    	this.id = id;
    	this.name = name;
        this.address = address;
        this.geolat = geolat;
        this.geolong = geolong;
        this.spaces = spaces;
        this.price = price;
        this.phone = phone;
        this.openHours = openHours;
        this.distance = distance;     
    }
    
    private ParkingSpace(Parcel in) {
    	id = in.readInt();
    	name = in.readString();
        address = in.readString();
        geolat = in.readString();
        geolong = in.readString();
        spaces = in.readInt();
        price = in.readInt();
        phone = in.readString();
        openHours = in.readString();
        distance = in.readString();
    }

    /*
     * Implement required public static field Creator wich will create a new object 
     * based on incoming Parcel.
     */
    public static final Parcelable.Creator<ParkingSpace> CREATOR = new Parcelable.Creator<ParkingSpace>() {
        public ParkingSpace createFromParcel(Parcel in) {
            return new ParkingSpace(in);
        }
        
        @Override
        public ParkingSpace[] newArray(int size) {
            return new ParkingSpace[size];
        }
    };
    
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
    
    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }  
    
    public String getPhone() {
        return phone;
    }
    
    public void setOpenHours(String openHours) {
        this.openHours = openHours;
    }  
    
    public String getOpenHours() {
        return openHours;
    }
    
    public void setDistance(String distance) {
        this.distance = distance;
    }  
    
    public String getDistance() {
        return distance;
    }
    
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeInt(id);
		out.writeString(name);
		out.writeString(address);
		out.writeString(geolat);
		out.writeString(geolong);
        out.writeInt(spaces);
        out.writeInt(price);
        out.writeString(phone);
        out.writeString(openHours);
        out.writeString(distance);		
	}

}