/**
 * 
 */
package com.licenta.parkdroid;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.licenta.parkdroid.preferences.Preferences;

/**
 * @author vladucu
 *
 */
public class PreferencesActivity extends android.preference.PreferenceActivity {

    public static final String TAG = "PreferencesActivity";
    //debug mode
    public static final boolean DEBUG = ParkDroid.DEBUG;
    
    private static final int DIALOG_PROFILE_SETTINGS = 1;
    
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
        
        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        /*
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
            mPrefs.edit().clear().commit();
            ((ParkDroid) getApplication()).setCredentials(null, null);
            
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setAction(Intent.ACTION_MAIN);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY
                    | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            sendBroadcast(new Intent(ParkDroid.INTENT_ACTION_LOGGED_OUT));            
        } else if (Preferences.PREFERENCE_RADIUS.equals(key)) {
        	sendBroadcast(new Intent(ParkingSpacesListActivity.REFRESH_PARKING_SPACES_INTENT));
        }
  /*      else if (Preferences.PREFERENCE_PROFILE_SETTINGS.equals(key)) { 
            if (DEBUG) Log.d(TAG, "onPreferenceTreeClick profile settings");
            showDialog(DIALOG_PROFILE_SETTINGS);
        }*/
        return true;        
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onCreateDialog(int)
     */
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DIALOG_PROFILE_SETTINGS:
                String userId = ((ParkDroid) getApplication()).getUserId();
                String userEmail = ((ParkDroid) getApplication()).getUserEmail();
                
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View layout = inflater.inflate(R.layout.settings_user_info, (ViewGroup) findViewById(R.id.settings_user_info_layout_root));
                TextView tvUserId = (TextView) findViewById(R.id.settings_user_info_label_user_id);
                TextView tvUserEmail = (TextView) findViewById(R.id.settings_user_info_label_user_email);
                
                tvUserId.setText(userId);
                tvUserEmail.setText(userEmail);
                
                AlertDialog dlgProfileSettings = new AlertDialog.Builder(this).setTitle(getResources().getString(R.string.preference_activity_profile_settings_dlg_title)).setView(layout).create();
                return dlgProfileSettings;
        }
        return null;
    }
    
    
}
