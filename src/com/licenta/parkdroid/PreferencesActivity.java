/**
 * 
 */
package com.licenta.parkdroid;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * @author Vlad Vanca
 *
 */
public class PreferencesActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
    }
    
    
    
    

}
