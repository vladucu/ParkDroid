/**
 * 
 */
package com.licenta.parkdroid;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.util.Log;

/**
 * @author Vlad Vanca
 *
 */
public class PreferencesActivity extends PreferenceActivity {

    public static final String TAG = "PreferencesActivity";
    //debug mode
    public static final boolean DEBUG = true;
    
    private SharedPreferences mPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        
        if (DEBUG) Log.d(TAG, "onCreate()");
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
    }
    
    
    
    

}
