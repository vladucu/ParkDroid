/**
 * 
 */
package com.licenta.parkdroid;


import com.licenta.parkdroid.LoadableListActivity;
import com.licenta.parkdroid.ParkDroid;
import com.licenta.parkdroid.types.ParkingSpace;
import com.licenta.parkdroid.types.ParkingSpaces;
import com.licenta.parkdroid.utils.GeoUtils;
import com.licenta.parkdroid.utils.LocationUtils;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * @author vladucu
 *
 */
public class ParkingSpacesListActivity extends LoadableListActivity {
    
    private static final String TAG = "ParkingSpacesListActivity";
    private static boolean DEBUG = ParkDroid.DEBUG;
    
    private StateHolder mStateHolder = new StateHolder();
    private ParkingSpaceListAdapter mListAdapter;
    private ListView mListView;
    private Handler mHandler;    
    
    private BroadcastReceiver mLoggedOutReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (DEBUG) Log.d(TAG, "onReceive: " + intent);
            finish();
        }
    };
    
    private BroadcastReceiver mRefreshUi = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (DEBUG) Log.d(TAG, "onReceive: " + intent);
            if (mStateHolder.getParkingSpaces() == null) mStateHolder = new StateHolder();
            mStateHolder.setParkingSpaces(ParkDroidActivity.mStateHolder.getParkingSpaces());
            putResultsInAdapter();
        }
    }; 
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (DEBUG) Log.d(TAG, "onCreate()");
         
        registerReceiver(mLoggedOutReceiver, new IntentFilter(ParkDroid.INTENT_ACTION_LOGGED_OUT));  
        registerReceiver(mRefreshUi, new IntentFilter(ParkDroid.INTENT_ACTION_REFRESH_UI));
        
        mHandler = new Handler();
        mListView = getListView();
        mListAdapter = new ParkingSpaceListAdapter(this);        
        
        mListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (DEBUG) Log.d(TAG, "onCreate()+ on item click");
                ParkingSpace parkingSpace = (ParkingSpace) parent.getAdapter().getItem(position);
                startItemActivity(parkingSpace);
            }
        });
        
        // Check if we're returning from a configuration change.
        if (getLastNonConfigurationInstance() != null) {
            if (DEBUG) Log.d(TAG, "Restoring state.");
            mStateHolder = (StateHolder) getLastNonConfigurationInstance();            
        } else {
            if (DEBUG) Log.d(TAG, "Creating new StateHolder instance.");
            mStateHolder = new StateHolder();
        }     
    }    
    
   @Override
    protected void onDestroy() {        
        super.onDestroy();
        unregisterReceiver(mLoggedOutReceiver);
        unregisterReceiver(mRefreshUi);
    }

   @Override
    protected void onPause() {
        super.onPause();
        if (DEBUG) Log.d(TAG, "onPause()");       
        //ParkDroidActivity.mStateHolder.setParkingSpaces(mStateHolder.getResults());
        ((ParkDroid) getApplication()).removeLocationUpdates(ParkDroidActivity.mSearchLocationObserver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (DEBUG) Log.d(TAG, "onResume()");
        //mStateHolder.setResults(ParkDroidActivity.mStateHolder.getParkingSpaces());
        ((ParkDroid) getApplication()).requestLocationUpdates(ParkDroidActivity.mSearchLocationObserver);
    }

    @Override
    public Object onRetainNonConfigurationInstance() {
        return mStateHolder;
    }
    
	/*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(Menu.NONE, MENU_REFRESH, Menu.NONE, R.string.refresh) //
                .setIcon(R.drawable.ic_menu_refresh);
        menu.add(Menu.NONE, MENU_SEARCH, Menu.NONE, R.string.search_label) //
                .setIcon(R.drawable.ic_menu_search) //
                .setAlphabeticShortcut(SearchManager.MENU_KEY);
        menu.add(Menu.NONE, MENU_ADD_VENUE, Menu.NONE, R.string.nearby_menu_add_venue) //
                .setIcon(R.drawable.ic_menu_add);

        // Shows a map of all nearby venues, works but not going into this
        // version.
        // menu.add(Menu.NONE, MENU_MAP, Menu.NONE, "Map")
        // .setIcon(R.drawable.ic_menu_places);

        //MenuUtils.addPreferencesToMenu(this, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_REFRESH:
                if (mStateHolder.getIsRunningTask() == false) {
                    startTask();
                }
                return true;
            case MENU_SEARCH:
                Intent intent = new Intent(NearbyVenuesActivity.this, SearchVenuesActivity.class);
                intent.setAction(Intent.ACTION_SEARCH);
                startActivity(intent);
                return true;
            case MENU_ADD_VENUE:
                startActivity(new Intent(NearbyVenuesActivity.this, AddVenueActivity.class));
                return true;
            case MENU_MAP:
                startActivity(new Intent(NearbyVenuesActivity.this, NearbyVenuesMapActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
*/
    
    public void putResultsInAdapter() {
        if (DEBUG) Log.d(TAG, "putResultsInAdapter()");
        List<ParkingSpace> parkingSpaces = mStateHolder.getParkingSpaces();
        mListAdapter = new ParkingSpaceListAdapter(this);
        if (parkingSpaces !=null && parkingSpaces.size()>0) {
            if (DEBUG) Log.d(TAG, "putResultsInAdapter() if size="+parkingSpaces.size()); 
            Location location = GeoUtils.getBestLastGeolocation(this);        
        	for (ParkingSpace it: parkingSpaces) {	        		
	        	Location to = new Location("ParkDroid");
	        	to.setLatitude(Double.parseDouble(it.getGeoLat()));
	        	to.setLongitude(Double.parseDouble(it.getGeoLong()));
	        	it.setDistance(Integer.toString((int) location.distanceTo(to)));	    
        	}
            mListAdapter.setGroup(parkingSpaces);            
        } else {
            setEmptyView();
        }
        mListView.setAdapter(mListAdapter);
    }

	@Override
	public int getNoSearchResultsStringId() {
		return R.string.no_parking_spaces;
	}

	private void startItemActivity(ParkingSpace parkingSpace) {
        if (DEBUG) Log.d(TAG, "startItemActivity()");
        Intent intent = new Intent(ParkingSpacesListActivity.this, ParkingSpaceActivity.class);
        intent.putExtra(ParkingSpaceActivity.INTENT_EXTRA_PARKING_SPACE, parkingSpace);
        startActivity(intent);        
    }
    
/*    private void onTaskComplete(List<ParkingSpace> result, Exception ex) {
        if (DEBUG) Log.d(TAG, "onTaskComplete()");
          
        Location location = GeoUtils.getBestLastGeolocation(this);
        if (result != null) {
        	for (ParkingSpace it: result) {	        		
	        	Location to = new Location("ParkDroid");
	        	to.setLatitude(Double.parseDouble(it.getGeoLat()));
	        	to.setLongitude(Double.parseDouble(it.getGeoLong()));
	        	it.setDistance(Integer.toString((int) location.distanceTo(to)));
	        }
        	mStateHolder.setResults(result);
        	putResultsInAdapter(result);
        } else {
        	Toast.makeText(this, "Sorry cannot acquire location. Please fix the problem and try again.", Toast.LENGTH_LONG).show();
        	setEmptyView();
        }            
        setProgressBarIndeterminateVisibility(false); 
        mStateHolder.cancelAllTasks();
    }
*/
    
    private static class StateHolder {
        private List<ParkingSpace> mParkingSpaces = null;
      
        public StateHolder() {            
        	mParkingSpaces = new ArrayList<ParkingSpace>();           
        }       

        public List<ParkingSpace> getParkingSpaces() {
            return mParkingSpaces;
        }

        public void setParkingSpaces(List<ParkingSpace> parkingSpaces) {
        	mParkingSpaces = parkingSpaces;
        }       
   } 
}
