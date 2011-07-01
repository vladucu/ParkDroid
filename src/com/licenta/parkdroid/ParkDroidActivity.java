package com.licenta.parkdroid;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import com.licenta.parkdroid.types.ParkingSpace;
import com.licenta.parkdroid.types.ParkingSpaces;
import com.licenta.parkdroid.utils.LocationUtils;
import com.licenta.parkdroid.utils.TabsUtil;
import android.app.TabActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.widget.TabHost;
import android.widget.Toast;

public class ParkDroidActivity extends TabActivity {
    
    public static final String TAG = "ParkDroidActivity";
    //debug mode
    public static final boolean DEBUG = ParkDroid.DEBUG;    
    
    private TabHost mTabHost;
    private Handler mHandler;
    public static SearchLocationObserver mSearchLocationObserver;
    public static StateHolder mStateHolder;
    
    private BroadcastReceiver mLoggedOutReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (DEBUG) Log.d(TAG, "onReceive: " + intent);
            finish();
        }
    };
    
    private BroadcastReceiver mRefreshParkingSpaces = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			if (DEBUG) Log.d(TAG, "onReceive: " + intent);
			if (intent.getAction().equals(ParkDroid.INTENT_ACTION_REFRESH_PARKING_SPACES)) {
                startTask();                
            }
			
		}
	};
    
    private BroadcastReceiver mRefreshReservations = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(TAG, "receiving refresh broadcast");
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
        registerReceiver(mLoggedOutReceiver, new IntentFilter(ParkDroid.INTENT_ACTION_LOGGED_OUT));
        registerReceiver(mRefreshReservations, new IntentFilter(ActiveReservationsListActivity.REFRESH_INTENT));
        registerReceiver(mRefreshParkingSpaces, new IntentFilter(ParkDroid.INTENT_ACTION_REFRESH_PARKING_SPACES));
        
        mHandler = new Handler();
        mSearchLocationObserver = new SearchLocationObserver();
        // Don't start the main activity if we don't have credentials
        if (!((ParkDroid) getApplication()).isReady()) {
            if (DEBUG) Log.d(TAG, "Not ready for user.");
            redirectToLoginActivity();
        } else {	        
	        if (DEBUG) Log.d(TAG, "Setting up main activity layout.");
	        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
	        setContentView(R.layout.main_activity);
	        initTabHost();
	        
	     // Check if we're returning from a configuration change.
	        if (getLastNonConfigurationInstance() != null) {
	            if (DEBUG) Log.d(TAG, "Restoring state.");
	            mStateHolder = (StateHolder) getLastNonConfigurationInstance();
	            mStateHolder.setActivity(this);
	        } else {
	            if (DEBUG) Log.d(TAG, "Creating new StateHolder instance.");
	            mStateHolder = new StateHolder();
	        }
	        
	        // Start a new search if one is not running or we have no results.
	        if (mStateHolder.getIsRunningTask()) {
	            if (DEBUG) Log.d(TAG, "mIsRunning true.");
	            setProgressBarIndeterminateVisibility(true);            
	        } else if (mStateHolder.getParkingSpaces().size() == 0) {    
	            if (DEBUG) Log.d(TAG, "mIsRunning not running but no results.");
	            startTask();
	        } else {
	            if (DEBUG) Log.d(TAG, "mIsRunning false.");
	            onTaskComplete(mStateHolder.getParkingSpaces(), null);
	        }
        }
    }
    
    @Override
	public Object onRetainNonConfigurationInstance() {
    	mStateHolder.setActivity(null);
		return mStateHolder;
	}
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (DEBUG) Log.d(TAG, "onDestroy()");
        unregisterReceiver(mLoggedOutReceiver);
        unregisterReceiver(mRefreshReservations);
        unregisterReceiver(mRefreshParkingSpaces);
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        if (DEBUG) Log.d(TAG, "onPause()");

        if (isFinishing()) {
            mStateHolder.cancelAllTasks();            
        }
    }
    
	@Override
	protected void onResume() {
		super.onResume();
		if (DEBUG) Log.d(TAG, "onResume()");		
	}

	private void initTabHost() {
		if (DEBUG) Log.d(TAG, "initTabHost()");
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

    /* If location changes, auto-start a nearby parking space search. */
    private class SearchLocationObserver implements Observer {

        private boolean mRequestedFirstSearch = false;

        @Override
        public void update(Observable observable, Object data) {
            Location location = (Location) data;
            // Fire a search if we haven't done so yet.
           /* if (!mRequestedFirstSearch
                    && ((BestLocationListener) observable).isAccurateEnough(location)) */{
                mRequestedFirstSearch = true;
                if (mStateHolder.getIsRunningTask() == false) {
                    // Since we were told by the system that location has
                    // changed, no need to make the
                    // task wait before grabbing the current location.
                    mHandler.post(new Runnable() {
                        public void run() {
                            startTask();
                        }
                    });
                }
            }
        }
    }
    
    private void startTask() {
        if (DEBUG) Log.d(TAG, "startTask");
        if (mStateHolder.getIsRunningTask() == false) {
            if (DEBUG) Log.d(TAG, "startTask mIsRunning false");
            setProgressBarIndeterminateVisibility(true);
            if (DEBUG) Log.d(TAG, "startTask()");
            mStateHolder.startTask(this);
        }
    }
    
    private void onTaskComplete(List<ParkingSpace> result, Exception ex) {
        if (DEBUG) Log.d(TAG, "onTaskComplete()");
          
        if (result != null) {        	
        	mStateHolder.setParkingSpaces(result);
        	sendBroadcast(new Intent(ParkDroid.INTENT_ACTION_REFRESH_UI));     
        } else {
        	Toast.makeText(this, "Sorry cannot acquire location. Please fix the problem and try again.", Toast.LENGTH_LONG).show();
        }            
        setProgressBarIndeterminateVisibility(false); 
        mStateHolder.cancelAllTasks();
    }
    
    private static class ParkingSpacesTask extends AsyncTask<Void, Void, ParkingSpaces> {
        private static final String TAG = "ParkingSpacesTask";
        
        private ParkDroidActivity mActivity;
        private ParkDroid mParkDroid;
        private ParkingSpaces results = new ParkingSpaces();
        
        public ParkingSpacesTask(ParkDroidActivity activity) {           
            super();            
            Log.d(TAG, "ParkingSpacesTask()");
            mActivity = activity;
            mParkDroid = (ParkDroid) mActivity.getApplication();
        }
        
        @Override
        public void onPreExecute() {
        	if (DEBUG) Log.d(TAG, "onPreExecute()");
        }

        @Override
        public ParkingSpaces doInBackground(Void... params) {
            if (DEBUG) Log.d(TAG, "doInBackground()");
            
            try {      
            	Thread.sleep(5000);
            	
            	// Get last known location.
                Location location = mParkDroid.getLastKnownLocation();
                System.out.println("Location="+location);                
                if (location != null) {
                	results = mParkDroid.getParkingSpaces(mParkDroid.getUserId(), LocationUtils.createParkDroidLocation(location),
                			mParkDroid.getRadius());
                }
                
            } catch (Exception e) {
               e.printStackTrace();
            }
            return results;
        }
    
        @Override
        public void onPostExecute(ParkingSpaces results) {
        	if (DEBUG) Log.d(TAG, "onPostExecute()");
            if (mActivity != null) {            	
            	mActivity.onTaskComplete(results.getParkingSpaces(), null);            	
            }
        }

        public void setActivity(ParkDroidActivity activity) {
            mActivity = activity;            
        }
        
    }
    
    static class StateHolder {
        private List<ParkingSpace> mParkingSpaces;
        private ParkingSpacesTask mTask;        

        public StateHolder() {            
        	mParkingSpaces = new ArrayList<ParkingSpace>();
            mTask = null;           
        }

        public void startTask(ParkDroidActivity activity) {
        	mTask = new ParkingSpacesTask(activity);
        	mTask.execute();            
        }

        public void setActivity(ParkDroidActivity activity) {
           if (mTask != null) {
        	   mTask.setActivity(activity);
           }
            
        }

        public List<ParkingSpace> getParkingSpaces() {
            return mParkingSpaces;
        }

        public void setParkingSpaces(List<ParkingSpace> parkingSpaces) {
        	mParkingSpaces = parkingSpaces;
        }
        
        public boolean getIsRunningTask() {
            return mTask != null;
        }

        public void cancelAllTasks() {
            if (mTask != null) {
            	mTask.cancel(true);
            	mTask = null;
            }
        }
   } 

}