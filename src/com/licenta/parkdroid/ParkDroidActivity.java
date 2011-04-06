package com.licenta.parkdroid;

import android.app.TabActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.TabHost;

public class ParkDroidActivity extends TabActivity {
    
    public static final String TAG = "ParkDroidActivity";
    //debug mode
    public static final boolean DEBUG = true;
    
    private TabHost mTabHost;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        if (DEBUG) Log.d(TAG, "onCreate()");
        setContentView(R.layout.main_activity);
        initTabHost();
    }
    
    private void initTabHost() {
        if (mTabHost != null) {
            throw new IllegalStateException("Trying to intialize already initializd TabHost");
        }

        mTabHost = getTabHost();
   
        TabsUtil.addTab(mTabHost, getString(R.string.tab_main_nav_res),
                R.drawable.tab_main_nav_tips_selector, 1, new Intent(this, MapActivity.class));
        TabsUtil.addTab(mTabHost, getString(R.string.tab_main_nav_res),
                R.drawable.tab_main_nav_tips_selector, 2, new Intent(this, PreferencesActivity.class));
        
        mTabHost.setCurrentTab(2);
        /*TabsUtil.addTab(mTabHost, getString(R.string.tab_main_nav_todos),
                R.drawable.tab_main_nav_todos_selector, 4, new Intent(this, TodosActivity.class));

        // 'Me' tab, just shows our own info. At this point we should have a
        // stored user id, and a user gender to control the image which is
        // displayed on the tab.
        String userId = ((Foursquared) getApplication()).getUserId();
        String userGender = ((Foursquared) getApplication()).getUserGender();

        Intent intentTabMe = new Intent(this, UserDetailsActivity.class);
        intentTabMe
                .putExtra(UserDetailsActivity.EXTRA_USER_ID, userId == null ? "unknown" : userId);
        TabsUtil.addTab(mTabHost, getString(R.string.tab_main_nav_me), UserUtils
                .getDrawableForMeTabByGender(userGender), 5, intentTabMe);
*/
    }
}