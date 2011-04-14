/**
 * 
 */
package com.licenta.park.types;

import com.licenta.park.utils.ParcelUtils;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author vladucu
 *
 */
public class ParkingLot implements ParkTypes, Parcelable {
    //TODO ??? eventual sa adaugam istorie a rezervarilor trecute si actuale
    
    private String mId;
    private String mName;
    private String mPhone;
    private String mAddress;
    private String mCity;
    private String mZip;
    private String mGeolat;
    private String mGeolong;
    private String mDistance;
    private String mEmptySpaces;
    private String mTotalSpaces;
    private String mPrice;
    private String mUrl;
    private String mIconUrl;

    public ParkingLot() {        
    }
    
    private ParkingLot(Parcel in) {
        mId = ParcelUtils.readStringFromParcel(in);
        mName = ParcelUtils.readStringFromParcel(in);
        mPhone = ParcelUtils.readStringFromParcel(in);
        mAddress = ParcelUtils.readStringFromParcel(in);
        mCity = ParcelUtils.readStringFromParcel(in);
        mZip = ParcelUtils.readStringFromParcel(in);
        mGeolat = ParcelUtils.readStringFromParcel(in);
        mGeolong = ParcelUtils.readStringFromParcel(in);
        mDistance = ParcelUtils.readStringFromParcel(in);
        mEmptySpaces = ParcelUtils.readStringFromParcel(in);
        mTotalSpaces = ParcelUtils.readStringFromParcel(in);
        mPrice = ParcelUtils.readStringFromParcel(in);
        mUrl = ParcelUtils.readStringFromParcel(in);
        mIconUrl = ParcelUtils.readStringFromParcel(in);
    }
    
    /*
     * Implement required public static field Creator wich will create a new object 
     * based on incoming Parcel.
     */
    public static final Parcelable.Creator<ParkingLot> CREATOR = new Parcelable.Creator<ParkingLot>() {
        public ParkingLot createFromParcel(Parcel in) {
            return new ParkingLot(in);
        }
        
        @Override
        public ParkingLot[] newArray(int size) {
            return new ParkingLot[size];
        }
    };
    
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
    
    public String getZip() {
        return mZip;
    }

    public void setZip(String zip) {
        mZip = zip;
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
    
    public String getEmptySpaces() {
        return mEmptySpaces;
    }

    public void setEmptySpaces(String emptySpaces) {
        mEmptySpaces = emptySpaces;
    }
    
    public String getTotalSpaces() {
        return mTotalSpaces;
    }

    public void setTotalSpaces(String totalSpaces) {
        mTotalSpaces = totalSpaces;
    }
    
    public String getPrice() {
        return mPrice;
    }

    public void setPrice(String price) {
        mPrice = price;
    }
    
    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        mUrl = url;
    }
    
    public String getIconUrl() {
        return mIconUrl;
    }

    public void setIconUrl(String iconUrl) {
        mIconUrl = iconUrl;
    }
    
    /* (non-Javadoc)
     * @see android.os.Parcelable#describeContents()
     */
    @Override
    public int describeContents() {        
        return 0;
    }

    /* (non-Javadoc)
     * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
     */
    @Override
    public void writeToParcel(Parcel out, int flags) {
        ParcelUtils.writeStringToParcel(out, mId);
        ParcelUtils.writeStringToParcel(out, mName);
        ParcelUtils.writeStringToParcel(out, mPhone);
        ParcelUtils.writeStringToParcel(out, mAddress);
        ParcelUtils.writeStringToParcel(out, mCity);
        ParcelUtils.writeStringToParcel(out, mZip);
        ParcelUtils.writeStringToParcel(out, mGeolat);
        ParcelUtils.writeStringToParcel(out, mGeolong);
        ParcelUtils.writeStringToParcel(out, mDistance);
        ParcelUtils.writeStringToParcel(out, mEmptySpaces);
        ParcelUtils.writeStringToParcel(out, mTotalSpaces);
        ParcelUtils.writeStringToParcel(out, mPrice);
        ParcelUtils.writeStringToParcel(out, mUrl);
        ParcelUtils.writeStringToParcel(out, mIconUrl);

    }

}
