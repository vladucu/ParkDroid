/**
 * 
 */
package com.licenta.park.types;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author vladucu
 *
 */
public class User implements  Parcelable, ParkTypes {

	//private static final long serialVersionUID = 1L;
	
	int id;
    String email;
    String password;
    String name;

    public User() {
    }

    public User(String name, String email, String password) {
    	super();
    	this.name = name;
        this.email = email;
        this.password = password;
    }
    
    public User(Parcel in) {
    	id = in.readInt();
    	email = in.readString();
    	password = in.readString();
    	name = in.readString();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String email) {
        this.password = email;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
        out.writeInt(id);
        out.writeString(email);
        out.writeString(password);
        out.writeString(name);
    }

    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {

		@Override
		public User createFromParcel(Parcel in) {
			return new User(in);
		}

		@Override
		public User[] newArray(int size) {
			return new User[size];
		}
    	
    };
    
}
