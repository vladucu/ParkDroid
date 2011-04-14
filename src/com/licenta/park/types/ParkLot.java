package com.licenta.park.types;

import com.licenta.parkdroid.utils.ParcelUtils;

import android.os.Parcel;
import android.os.Parcelable;


/**
 * @author vladucu
 *
 */
public class ParkLot implements ParkType, Parcelable {

    private String mAddress;    
    private String mCity;
    private String mCityid;
    private String mCrossstreet;
    private String mDistance;
    private String mGeolat;
    private String mGeolong;    
    private String mId;
    private String mName;
    private String mPhone;   
    private int mPrice;
    private int mSpaces;
    private int mEmptySpaces;

    public ParkLot() {
    }

    private ParkLot(Parcel in) {
        mAddress = ParcelUtils.readStringFromParcel(in);
        mCity = ParcelUtils.readStringFromParcel(in);
        mCityid = ParcelUtils.readStringFromParcel(in);
        mCrossstreet = ParcelUtils.readStringFromParcel(in);
        mDistance = ParcelUtils.readStringFromParcel(in);
        mGeolat = ParcelUtils.readStringFromParcel(in);
        mGeolong = ParcelUtils.readStringFromParcel(in);
        mId = ParcelUtils.readStringFromParcel(in);
        mName = ParcelUtils.readStringFromParcel(in);
        mPhone = ParcelUtils.readStringFromParcel(in);
        mPrice = in.readInt();
        mSpaces = in.readInt();
        mEmptySpaces = in.readInt();
    }

    public static final Parcelable.Creator<ParkLot> CREATOR = new Parcelable.Creator<ParkLot>() {
        public ParkLot createFromParcel(Parcel in) {
            return new ParkLot(in);
        }

        @Override
        public ParkLot[] newArray(int size) {
            return new ParkLot[size];
        }
    };

    public String getAddress() {
        return mAddress;
    }

    public void setAddress(String address) {
        mAddress = address;
    }

    public String getCity() {
        return mCity;
    }

    public void setCity(String city) {
        mCity = city;
    }

    public String getCityid() {
        return mCityid;
    }

    public void setCityid(String cityid) {
        mCityid = cityid;
    }

    public String getCrossstreet() {
        return mCrossstreet;
    }

    public void setCrossstreet(String crossstreet) {
        mCrossstreet = crossstreet;
    }

    public String getDistance() {
        return mDistance;
    }

    public void setDistance(String distance) {
        mDistance = distance;
    }

    public String getGeolat() {
        return mGeolat;
    }

    public void setGeolat(String geolat) {
        mGeolat = geolat;
    }

    public String getGeolong() {
        return mGeolong;
    }

    public void setGeolong(String geolong) {
        mGeolong = geolong;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getPhone() {
        return mPhone;
    }

    public void setPhone(String phone) {
        mPhone = phone;
    }
    
    public int getPrice() {
        return mPrice;
    }
    
    public void setPrice(int price) {
        mPrice = price;
    }
    
    public int getSpaces() {
        return mSpaces;
    }
    
    public void setSpaces(int spaces) {
        mSpaces = spaces;
    }

    public int getEmptySpaces() {
        return mEmptySpaces;
    }
    
    public void setEmptySpaces(int empySpaces) {
        mEmptySpaces = empySpaces;
    }
    
    @Override
    public void writeToParcel(Parcel out, int flags) {
        ParcelUtils.writeStringToParcel(out, mAddress);
        ParcelUtils.writeStringToParcel(out, mCity);
        ParcelUtils.writeStringToParcel(out, mCityid);
        ParcelUtils.writeStringToParcel(out, mCrossstreet);
        ParcelUtils.writeStringToParcel(out, mDistance);
        ParcelUtils.writeStringToParcel(out, mGeolat);
        ParcelUtils.writeStringToParcel(out, mGeolong);
        ParcelUtils.writeStringToParcel(out, mId);
        ParcelUtils.writeStringToParcel(out, mName);
        ParcelUtils.writeStringToParcel(out, mPhone);
        out.writeInt(mPrice);
        out.writeInt(mSpaces);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
