/**
 * 
 */
package com.licenta.parkdroid;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.preference.Preference.OnPreferenceChangeListener;
import android.util.Log;

import com.licenta.parkdroid.Preferences;

/**
 * @author vladucu
 *
 */
public class PreferencesActivity extends android.preference.PreferenceActivity {

    public static final String TAG = "PreferencesActivity";
    //debug mode
    public static final boolean DEBUG = true;
    
    private SharedPreferences mPrefs;
    
    private BroadcastReceiver mLoggedOutReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (DEBUG) Log.d(TAG, "onReceive: " + intent);
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerReceiver(mLoggedOutReceiver, new IntentFilter(ParkDroid.INTENT_ACTION_LOGGED_OUT));
        
        if (DEBUG) Log.d(TAG, "onCreate()");
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
        
      /*  mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        Preference advancedSettingsPreference = getPreferenceScreen().findPreference(Preferences.PREFERENCE_ADVANCED_SETTINGS);
        advancedSettingsPreference.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                ((ParkDroid) getApplication()).requestUpdateUser();
                if (DEBUG) Log.d(TAG, "on PreferenceChange for advancedsettingspreference");
                return false;
            } 
        });*/
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mLoggedOutReceiver);
    }
     
    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (DEBUG) Log.d(TAG, "onPreferenceTreeClick");
        String key = preference.getKey();
        if (Preferences.PREFERENCE_LOGOUT.equals(key)) {
            if (DEBUG) Log.d(TAG, "onPreferenceTreeClick sent logout message");
            //mPrefs.edit().clear().commit();
            ((ParkDroid) getApplication()).getPark().setCredentials(null, null);
            
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setAction(Intent.ACTION_MAIN);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY
                    | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            sendBroadcast(new Intent(ParkDroid.INTENT_ACTION_LOGGED_OUT));
            
        }
        return true;        
    }
}
