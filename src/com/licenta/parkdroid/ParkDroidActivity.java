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
import android.view.Window;
import android.widget.TabHost;

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
    
    private BroadcastReceiver mRefreshReservations = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d("DSADS", "receiving refresh broadcast");
			if (intent.getAction().equals(ActiveReservationsListActivity.REFRESH_INTENT)) {
				mTabHost.setCurrentTab(2);
            }
			
		}
	};
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        if (DEBUG) Log.d(TAG, "onCreate()");
        //setDefaultKeyMode(Activity.DEFAULT_KEYS_SEARCH_LOCAL);
        registerReceiver(mLoggedOutReceiver, new IntentFilter(ParkDroid.INTENT_ACTION_LOGGED_OUT));
        //registerReceiver(mRefreshReservations, new IntentFilter(ActiveReservationsListActivity.REFRESH_INTENT));
        // Don't start the main activity if we don't have credentials
        if (!((ParkDroid) getApplication()).isReady()) {
            if (DEBUG) Log.d(TAG, "Not ready for user.");
            redirectToLoginActivity();
        }
        
        if (DEBUG) Log.d(TAG, "Setting up main activity layout.");
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.main_activity);
        initTabHost();
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mLoggedOutReceiver);
    }
    
    private void initTabHost() {
    	if (mTabHost != null) {
            throw new IllegalStateException("Trying to intialize already initializd TabHost");
        }

        mTabHost = getTabHost();
        TabsUtil.addTab(mTabHost, getString(R.string.tab_main_nav_map),
                R.drawable.tab_nav_map_selector, 1, new Intent(this, MapActivity.class));
        TabsUtil.addTab(mTabHost, getString(R.string.tab_main_nav_parking_lots_list),
                R.drawable.tab_parking_list_selector, 2, new Intent(this, ParkingSpacesListActivity.class));
        TabsUtil.addTab(mTabHost, getString(R.string.tab_main_nav_active_reservations),
                R.drawable.tab_active_reservations_selector, 3, new Intent(this, ActiveReservationsListActivity.class));      
   
        mTabHost.setCurrentTab(0);   
    }
    
    // Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }    

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {        
        switch (item.getItemId()) {
            case R.id.preferences:                
                Log.d(TAG, "Preferences Tab selected");
                startActivity(new Intent(this, PreferencesActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    private void redirectToLoginActivity() {
        setVisible(false);
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setAction(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
                | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

}