/**
 * 
 */
package com.licenta.parkdroid.preferences;

import com.licenta.park.Park;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

/**
 * @author vladucu
 *
 */
public class Preferences {
    private static final String TAG = "Preferences";
    private static final boolean DEBUG = true;
    
    // Hacks for preference activity extra UI elements.
    public static final String PREFERENCE_ADVANCED_SETTINGS = "advanced_settings";
    public static final String PREFERENCE_LOGOUT = "logout";
    
    //Profile settings
    public static final String PREFERENCE_PROFILE_SETTINGS = "profile_settings";
    
    // Credentials related preferences
    public static final String PREFERENCE_LOGIN = "phone";
    public static final String PREFERENCE_PASSWORD = "password";
    
    // Extra info for getUserId
    private static final String PREFERENCE_ID = "id";

    // Extra for storing user's supplied email address.
    private static final String PREFERENCE_USER_EMAIL = "user_email";
    
    public static boolean loginUser(Park park, String login, String password, Editor editor) {
        if (DEBUG) Log.d(Preferences.TAG, "Trying to log in.");

        park.setCredentials(login, password);
        storeLoginAndPassword(editor, login, password);
        if (!editor.commit()) {
            if (DEBUG) Log.d(TAG, "storeLoginAndPassword commit failed");
            return false;
        }

        Park.User user = park.user(login, password);
        storeUser(editor, user);
        if (!editor.commit()) {
            if (DEBUG) Log.d(TAG, "storeUser commit failed");
            return false;
            		
        }
        return true;
    }

    public static boolean logoutUser(Park park, Editor editor) {
        if (DEBUG) Log.d(Preferences.TAG, "Trying to log out.");
        park.setCredentials(null, null);
        return editor.clear().commit();
    }
    
    /**
     * @param prefs
     * @return
     */
    public static String getUserId(SharedPreferences prefs) {
        if (DEBUG) Log.d(TAG, "getUserId="+prefs.getString(PREFERENCE_ID, null));
        return prefs.getString(PREFERENCE_ID, null);
    }
        
    /** 
     * @param prefs
     * @return user_email
     */
    public static String getUserEmail(SharedPreferences prefs) {
        return prefs.getString(PREFERENCE_USER_EMAIL, null);
    }
    
    public static void storeLoginAndPassword(final Editor editor, String login, String password) {
        if (DEBUG) Log.d(TAG, "storeLoginAndPassword");
        editor.putString(PREFERENCE_LOGIN, login);
        editor.putString(PREFERENCE_PASSWORD, password);
    }
    
    public static void storeUser(final Editor editor, Park.User user) {
        if (DEBUG) Log.d(TAG, "storeUser");
        if (user != null && user.getId() != null) {
            editor.putString(PREFERENCE_ID, user.getId());
            editor.putString(PREFERENCE_USER_EMAIL, user.getEmail());           
            if (DEBUG) Log.d(TAG, "Setting user info");
        } else {
            if (DEBUG) Log.d(TAG, "Unable to lookup user.");
        }
    }

}
