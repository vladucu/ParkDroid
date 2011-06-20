/**
 * 
 */
package com.licenta.park;

import com.licenta.park.types.ParkingSpace;
import com.licenta.park.types.ParkingSpaces;
import com.licenta.park.types.Reservations;
import com.licenta.park.types.Reservation;
import com.licenta.park.types.User;
import android.util.Log;
import java.io.IOException;
import java.util.Date;

/**
 * @author vladucu
 *
 */
public class Park {
    
    public static final String TAG = "Park";
    //debug mode
    public static final boolean DEBUG = true;
    
    private String mLogin;
    private String mPassword;
    
    private ParkServerHttpApi mParkDroidApi;
    
    public Park(ParkServerHttpApi parkDroidApi) {
    	if (DEBUG) Log.d(TAG, "Park ");
        mParkDroidApi = parkDroidApi;
    }
    
    public void setCredentials(String email, String password) {        
   
        if (DEBUG) Log.d(TAG, "setCredentials email="+email);
        mLogin = email;
        mPassword = password;
        mParkDroidApi.setCredentials(email, password);
    
    }
    

    public boolean hasLoginAndPassword() {
        if (DEBUG) Log.d(TAG, "hasLoginAndPassword()");
        return mParkDroidApi.hasCredentials();
    }

    public User user(String login, String password) {
        /*User user = new User();        
        user.mEmail = "x";
        user.mPassword = "x";
        if (user.mEmail == login && user.mPassword == password) return user;
        else return new User();
        return user;*/
    	return mParkDroidApi.user(login, password);
    }
    
    

    public ParkingSpace parkingLot(String string) {
        // TODO Auto-generated method stub
        return null;
    }

	public static final ParkServerHttpApi createHttpApi(String domain) {
		if (DEBUG) Log.d(TAG, "createHttpApi("+domain+")");
		return new ParkServerHttpApi(domain);
	}

	public ParkingSpaces parkingSpaces(int userId) throws IOException {
		if (DEBUG) Log.d(TAG, "parkingSpaces()");
		return mParkDroidApi.parkingspaces(userId);
	}

	public ParkingSpace parkingSpace(int userId, int parkingSpaceId) throws IOException {
		//if (DEBUG) Log.d(TAG, "parkingSpace("+id+")");
		return mParkDroidApi.parkingSpace(userId, parkingSpaceId);
	}

	public Reservations getReservations() throws IOException {
		if (DEBUG) Log.d(TAG, "reservations()");
		return mParkDroidApi.reservations();				
	}

	public  Reservation createReservation(int mUserId, ParkingSpace mParkingSpace, String mStartTime, String mEndTime, String mTotalTime, int mCost) throws IOException {
		if (DEBUG) Log.d(TAG, "createReservation()");
		return mParkDroidApi.createReservation(mUserId, mParkingSpace, mStartTime, mEndTime, mTotalTime, mCost);
		
	}

	public Reservation updateReservation(Reservation reservation) throws IOException {
		if (DEBUG) Log.d(TAG, "updateReservation()");
		return mParkDroidApi.updateReservation(reservation);
	}

	public boolean deleteReservation(int userId, int reservationId) throws IOException {
		if (DEBUG) Log.d(TAG, "updateReservation()");
		return mParkDroidApi.deleteReservation(userId, reservationId);
	}
}
