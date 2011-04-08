/**
 * 
 */
package com.licenta.parkdroid;

import com.licenta.park.Park;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * @author vladucu
 *
 */
public class ParkDroid extends Application {
    
    private static final String TAG = "ParkDroidApp";
    //debug mode
    private static final boolean DEBUG = true;
    
    public static final String INTENT_ACTION_LOGGED_OUT = "com.licenta.parkdroid.intent.action.LOGGED_OUT";
    public static final String INTENT_ACTION_LOGGED_IN = "com.licenta.parkdroid.intent.action.LOGGED_IN";
    
    private SharedPreferences mPrefs;
    
    private Park mPark;

    /* (non-Javadoc)
     * @see android.app.Application#onCreate()
     */
    @Override
    public void onCreate() {
        // TODO finish ParkDroid
        
        // Catch logins or logouts.
        new LoggedInOutBroadcastReceiver().register();

    }
    
    private void loadPark() {
       /* // Try logging in and setting up foursquare oauth, then user
        // credentials.
        if (FoursquaredSettings.USE_DEBUG_SERVER) {
            mFoursquare = new Foursquare(Foursquare.createHttpApi("10.0.2.2:8080", mVersion, false));
        } else {
            mFoursquare = new Foursquare(Foursquare.createHttpApi(mVersion, false));
        }
*/
        if (DEBUG) Log.d(TAG, "loadCredentials()");
        String phoneNumber = mPrefs.getString(Preferences.PREFERENCE_LOGIN, null);
        String password = mPrefs.getString(Preferences.PREFERENCE_PASSWORD, null);
        mPark.setCredentials(phoneNumber, password);
        if (mPark.hasLoginAndPassword()) {
            sendBroadcast(new Intent(INTENT_ACTION_LOGGED_IN));
        } else {
            sendBroadcast(new Intent(INTENT_ACTION_LOGGED_OUT));
        }
    }
    
    public boolean isReady() {
        // TODO Auto-generated method stub
        return false;
    }

    public Park getPark() {
        return mPark;
    }
    
    private class LoggedInOutBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (INTENT_ACTION_LOGGED_IN.equals(intent.getAction())) {
               // requestUpdateUser();
            }
        }

        public void register() {
            // Register our media card broadcast receiver so we can
            // enable/disable the cache as
            // appropriate.
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(INTENT_ACTION_LOGGED_IN);
            intentFilter.addAction(INTENT_ACTION_LOGGED_OUT);
            registerReceiver(this, intentFilter);
        }
    }    
}
