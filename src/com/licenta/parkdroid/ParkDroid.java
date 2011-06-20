/**
 * 
 */
package com.licenta.parkdroid;

import com.licenta.park.Park;
import com.licenta.park.types.User;
import com.licenta.parkdroid.preferences.Preferences;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.LocationManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import java.util.Observer;

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
    public static final String PACKAGE_NAME = "com.licenta.parkdroid";;
    public static final String DOMAIN = "89.37.147.104:8080";
    
    private SharedPreferences mPrefs;
    
    private TaskHandler mTaskHandler;
    private HandlerThread mTaskThread;
    private BestLocationListener mBestLocationListener;
    
    private Park mPark;
    //sa vedem daca revenim de la login sau e start de aplicatie
    //private static boolean isLoggedIn = false;

    /* (non-Javadoc)
     * @see android.app.Application#onCreate()
     */
    @Override
    public void onCreate() {
        // TODO finish ParkDroid
    	if (DEBUG) Log.d(TAG, "onCreate()");
        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        
        // Sometimes we want the application to do some work on behalf of the
        // Activity. Lets do that
        // asynchronously.
        mTaskThread = new HandlerThread(TAG + "-AsyncThread");
        mTaskThread.start();
        mTaskHandler = new TaskHandler(mTaskThread.getLooper());
        
        // Catch logins or logouts.
        new LoggedInOutBroadcastReceiver().register();
        
        // Log into ParkDroid, if we can.
        loadPark();

    }
    
    public boolean isReady() {
        /*if (isLoggedIn) return true;
        else return false;*/
        if (DEBUG) Log.d(TAG, "isReady()");
        return getPark().hasLoginAndPassword() && !TextUtils.isEmpty(getUserId());
    }

    public Park getPark() {
    	if (DEBUG) Log.d(TAG, "getPark()");
        return mPark;
    }
    
    public String getUserId() {
        return Preferences.getUserId(mPrefs);
    }
    
    public String getUserEmail() {
        return Preferences.getUserEmail(mPrefs);
    }
    public void requestUpdateUser() {
        mTaskHandler.sendEmptyMessage(TaskHandler.MESSAGE_UPDATE_USER);
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
    	if (DEBUG) Log.d(TAG, "loadPark()");
        mPark = new Park(Park.createHttpApi(DOMAIN));
        
        String email = mPrefs.getString(Preferences.PREFERENCE_LOGIN, null);
        String password = mPrefs.getString(Preferences.PREFERENCE_PASSWORD, null);        
        mPark.setCredentials(email, password);
        if (DEBUG) Log.d(TAG, "loadPark() hasloginandpassword="+mPark.hasLoginAndPassword());
        if (mPark.hasLoginAndPassword()) {
            if (DEBUG) Log.d(TAG, "loadCredentials() phoneNumber="+email);
            sendBroadcast(new Intent(INTENT_ACTION_LOGGED_IN));
        } else {
            sendBroadcast(new Intent(INTENT_ACTION_LOGGED_OUT));
        }
    }
    
    private class LoggedInOutBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (INTENT_ACTION_LOGGED_IN.equals(intent.getAction())) {
                //isLoggedIn = true;
                requestUpdateUser();
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
    
    private class TaskHandler extends Handler {

        private static final int MESSAGE_UPDATE_USER = 1;
        //private static final int MESSAGE_START_SERVICE = 2;

        public TaskHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (DEBUG) Log.d(TAG, "handleMessage: " + msg.what);

            switch (msg.what) {
                case MESSAGE_UPDATE_USER:
                    try {
                        // Update user info
                        Log.d(TAG, "Updating user.");

             
                        /*User user = getPark().createUser(login, password);

                        Editor editor = mPrefs.edit();
                        Preferences.storeUser(editor, user);
                        editor.commit();*/
                    } catch (Error e) {
                        if (DEBUG) Log.d(TAG, "ParkDroid", e);
                    } catch (Exception e) {
                        if (DEBUG) Log.d(TAG, "ParkDroid", e);
                    }
                    return;
            }
        }
    }

    public BestLocationListener requestLocationUpdates(Observer observer) {
        if (DEBUG) Log.d(TAG, "requestLocationUpdates");
        mBestLocationListener.addObserver(observer);
        mBestLocationListener.register((LocationManager) getSystemService(Context.LOCATION_SERVICE), true);
        return mBestLocationListener;
        
    }

    public void removeLocationUpdates(Observer observer) {
        mBestLocationListener.unregister((LocationManager) getSystemService(Context.LOCATION_SERVICE));
        
    }
}
