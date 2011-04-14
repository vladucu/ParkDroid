package com.licenta.parkdroid;

import com.licenta.parkdroid.utils.TabsUtil;

import android.app.TabActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TabHost;
import com.licenta.parkdroid.ParkLotsListActivity;

public class ParkDroidActivity extends TabActivity {
    
    public static final String TAG = "ParkDroidActivity";
    //debug mode
    public static final boolean DEBUG = true;
    
    private TabHost mTabHost;
    
    private BroadcastReceiver mLoggedOutReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (DEBUG) Log.d(TAG, "onReceive: " + intent);
            finish();
        }
    };
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        if (DEBUG) Log.d(TAG, "onCreate()");
       // setDefaultKeyMode(Activity.DEFAULT_KEYS_SEARCH_LOCAL);
        registerReceiver(mLoggedOutReceiver, new IntentFilter(ParkDroid.INTENT_ACTION_LOGGED_OUT));
        
        // Don't start the main activity if we don't have credentials
        if (!((ParkDroid) getApplication()).isReady()) {
            if (DEBUG) Log.d(TAG, "Not ready for user.");
            redirectToLoginActivity();
        }
        
        if (DEBUG) Log.d(TAG, "Setting up main activity layout.");
        //requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.main_activity);
        initTabHost();
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mLoggedOutReceiver);
    }
    
    private void initTabHost() {
        //TODO de aflat de ce nu apare pe toata imaginea harta 
        if (mTabHost != null) {
            throw new IllegalStateException("Trying to intialize already initializd TabHost");
        }

        mTabHost = getTabHost();
        TabsUtil.addTab(mTabHost, getString(R.string.tab_main_nav_res),
                R.drawable.tab_main_nav_tips_selector, 1, new Intent(this, MapActivity.class));
        TabsUtil.addTab(mTabHost, getString(R.string.tab_main_nav_res),
                R.drawable.tab_main_nav_tips_selector, 2, new Intent(this, ParkLotsListActivity.class));
   
       /* TabsUtil.addTab(mTabHost, getString(R.string.tab_main_nav_res),
                R.drawable.tab_main_nav_tips_selector, 1, new Intent(this, MapActivity.class));
        TabsUtil.addTab(mTabHost, getString(R.string.tab_main_nav_res),
                R.drawable.tab_main_nav_tips_selector, 2, new Intent(this, PreferencesActivity.class));
        TabsUtil.addTab(mTabHost, getString(R.string.tab_main_nav_res),
                R.drawable.tab_main_nav_tips_selector, 3, new Intent(this, ParkingLotActivity.class));
        */
       
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
    
    // Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }    
    
    /* 
     * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {        
        // TODO Auto-generated method stub                
        switch (item.getItemId()) {
            case R.id.preferences:                
                Log.d(TAG, "Preferences Tab selected");
                startActivity(new Intent(this, PreferencesActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    private void redirectToLoginActivity() {
        // TODO implement redirectToLogin
        setVisible(false);
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setAction(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
                | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

}