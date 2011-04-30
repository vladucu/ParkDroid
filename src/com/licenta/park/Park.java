/**
 * 
 */
package com.licenta.park;

<<<<<<< HEAD
import com.licenta.park.types.Group;
import com.licenta.park.types.ParkLot;
=======
import com.licenta.park.types.ParkingLot;
>>>>>>> dev4

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.DefaultHttpClient;

import android.location.Location;
import android.util.Log;

import java.io.IOException;
import java.util.logging.Level;

/**
 * @author vladucu
 *
 */
public class Park {
    
    public static final String TAG = "Park";
    //debug mode
    public static final boolean DEBUG = true;
    
    private String mPhone;
    private String mPassword;
    private static boolean gotToken = false;
    
    private final AuthScope mAuthScope = new AuthScope("idew.wdca.ca", 79);
    private final DefaultHttpClient mHttpClient = new DefaultHttpClient();
    
    public void Park() {
        
    }
    
    public void setCredentials(String phone, String password) {        
        if (phone == null || phone.length() == 0 || password == null || password.length() == 0) {
            if (DEBUG) Log.d(TAG, "Clearing Credentials");
            mHttpClient.getCredentialsProvider().clear();
            gotToken = false;
        } else {
            if (DEBUG) Log.d(TAG, "setCredentials phone="+phone);
            mPhone = phone;
            mPassword = password;
            gotToken = true;
            mHttpClient.getCredentialsProvider().setCredentials(mAuthScope,
                    new UsernamePasswordCredentials(phone, password));
        }
    }
    
    public boolean hasCredentials() {
        if (DEBUG) Log.d(TAG, "hasCredentials");
        return mHttpClient.getCredentialsProvider().getCredentials(mAuthScope) != null;
    }
    
    public boolean hasOAuthTokenWithSecret() {
        if (DEBUG) Log.d(TAG, "hasOAuthTokenWithSecret");
        if (gotToken) return true;
        else return false;        
    }

    public boolean hasLoginAndPassword() {
        if (DEBUG) Log.d(TAG, "hasLoginAndPassword");
        return hasCredentials() && hasOAuthTokenWithSecret();
    }

    public User user(String login, String password) {
        User user = new User();
        user.mId = "1";
        user.mEmail = "x";
        user.mPassword = "x";
        /*if (user.mEmail == login && user.mPassword == password) return user;
        else return new User();*/
        return user;
    }
    
    public class User {
                
        private String mId;
        private String mEmail;
        private String mPassword;
        private String mPhone;
        
        public String getId() {
            return mId;
        }
        
        public String getEmail() {
            return mEmail;
        }
        
    }

<<<<<<< HEAD
    public Group<ParkLot> parkLots(Location location, String Query, int limit) throws Exception, Error, IOException {
        // TODO Auto-generated method stub
        Group<ParkLot> mGPLot = new Group<ParkLot>();
        ParkLot mParkLot = new ParkLot();
        mParkLot.setName("Parking #1");
        mParkLot.setGeolat("44.43472");
        mParkLot.setGeolong("26.09704");
        mParkLot.setDistance("300 meters");
        mParkLot.setPrice(200);
        mGPLot.add(mParkLot);
        return (Group<ParkLot>) mGPLot;
=======
    public ParkingLot parkingLot(String string) {
        // TODO Auto-generated method stub
        return null;
>>>>>>> dev4
    }

}
