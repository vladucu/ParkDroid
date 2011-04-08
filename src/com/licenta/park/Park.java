/**
 * 
 */
package com.licenta.park;

import android.util.Log;

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
    
    public void setCredentials(String phone, String password) {
        mPhone = phone;
        mPassword = password;        
    }
    
    public boolean hasLoginAndPassword() {
        Log.d(TAG, "hasLoginAndPassword");
        return true;
    }

}
