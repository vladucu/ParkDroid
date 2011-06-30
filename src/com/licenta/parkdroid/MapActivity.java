package com.licenta.parkdroid;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.OverlayItem;
import com.licenta.parkdroid.maps.CrashFixMyLocationOverlay;
import com.licenta.parkdroid.maps.ParkingSpaceItemizedOverlayIcons;
import com.licenta.parkdroid.maps.ParkingSpaceItemizedOverlayIcons.ParkingSpaceItemizedOverlayTapListener;
import com.licenta.parkdroid.types.ParkingSpace;
import com.licenta.parkdroid.types.ParkingSpaces;
import com.licenta.parkdroid.utils.GeoUtils;
import com.licenta.parkdroid.utils.LocationUtils;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

/**
 * @author vladucu
 *
 */
public class MapActivity extends com.google.android.maps.MapActivity {

    private static final String TAG = "MapActivity";
    private static final boolean DEBUG = ParkDroid.DEBUG;
    
    private MapView mMapView;
    private MapController mMapController;
    private MyLocationOverlay mMyLocationOverlay = null;
    private ParkingSpaceItemizedOverlayIcons mOverlay = null; 
    private SearchLocationObserver mSearchLocationObserver = new SearchLocationObserver();
    
    private String mTappedParkingSpace;
    private StateHolder mStateHolder;
    private Handler mHandler;
    
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
			if (intent.getAction().equals(ParkingSpacesListActivity.REFRESH_PARKING_SPACES_INTENT)) {
                startTask();                
            }
			
		}
	};

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        if (DEBUG) Log.d(TAG, "onCreate()");
        registerReceiver(mLoggedOutReceiver, new IntentFilter(ParkDroid.INTENT_ACTION_LOGGED_OUT));  
        registerReceiver(mRefreshParkingSpaces, new IntentFilter(ParkingSpacesListActivity.REFRESH_PARKING_SPACES_INTENT));
        setContentView(R.layout.map_activity);
        
        mHandler = new Handler();
        
        /*Object retained = getLastNonConfigurationInstance();
        if (retained != null && retained instanceof StateHolder) {
            mStateHolder = (StateHolder) retained;
        }
        else {
            mStateHolder = new StateHolder();            
        }*/
        
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
        
        ensureUi();        
    }
    
    @Override
    protected void onDestroy() {        
        super.onDestroy();
        unregisterReceiver(mLoggedOutReceiver);
        unregisterReceiver(mRefreshParkingSpaces);
    }
    
	@Override
	public Object onRetainNonConfigurationInstance() {
		mStateHolder.setActivity(null);
		return mStateHolder;
	}

	@Override
    protected void onPause() {
        super.onPause();
        if (DEBUG) Log.d(TAG, "onPause()");
        mMyLocationOverlay.disableMyLocation();
        ((ParkDroid) getApplication()).removeLocationUpdates(mSearchLocationObserver);

        if (isFinishing()) {
            mStateHolder.cancelAllTasks();            
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (DEBUG) Log.d(TAG, "onResume()");
        mMyLocationOverlay.enableMyLocation();
        ((ParkDroid) getApplication()).requestLocationUpdates(mSearchLocationObserver);
    }

    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }

    private void initMyLocation() {
        if (DEBUG) Log.d(TAG, "initMyLocation()");
        mMyLocationOverlay = new MyLocationOverlay(this, mMapView);
        mMyLocationOverlay.enableMyLocation();
        // myLocationOverlay.enableCompass(); // does not work in emulator
        //when new location center and zoom
        mMyLocationOverlay.runOnFirstFix(new Runnable() {
            public void run() {
                // Zoom in to current location
                //TODO de facut ca la sfarsit sa calculeze zoom-ul in functie de RADIUS setat la preferinte (vazut ensureUi si recenterMap)
                mMapController.zoomToSpan(mOverlay.getLatSpanE6(), mOverlay.getLonSpanE6());
                mMapController.animateTo(mMyLocationOverlay.getMyLocation());
            }
        });
        mMapView.getOverlays().add(mMyLocationOverlay);
    }
    
    private void ensureUi() {
        if (DEBUG) Log.d(TAG, "ensureUi()");
        
        mMapView = (MapView) findViewById(R.id.mapView);
        // Display zoom controls (+/-)
        mMapView.setBuiltInZoomControls(true);
        mMapController = mMapView.getController();
        mMapView.setSatellite(false);
        
        //mMyLocationOverlay = new MyLocationOverlay(this, mMapView);
        mMyLocationOverlay = new CrashFixMyLocationOverlay(this, mMapView);
        mMyLocationOverlay.enableMyLocation();
        mMapView.getOverlays().add(mMyLocationOverlay);
        //initMyLocation();
        updateMap();       
        
    }
    
    private void updateMap() {
    	mOverlay = new ParkingSpaceItemizedOverlayIcons(this, getResources().getDrawable(R.drawable.pin), mParkingSpaceOverlayTapListener);        
        List<ParkingSpace> g = new ArrayList<ParkingSpace>();
        for (ParkingSpace it:mStateHolder.getParkingSpaces()) {
            g.add(it);
        }
        mOverlay.setGroup(g);
        mMapView.getOverlays().add(mOverlay);       
        if (mOverlay != null && mOverlay.size()>0) {
            reCenterMap();
        }
    }
    
    private void reCenterMap() {
       if (DEBUG) Log.d(TAG, "reCenterMap()");
    // Previously we'd try to zoom to span, but this gives us odd results a
       // lot of times,
       // so falling back to zoom at a fixed level.
       GeoPoint center = mMyLocationOverlay.getMyLocation();
       if (center != null) {
           mMapController.animateTo(center);
           mMapController.setZoom(14);
       } else {
           // Location overlay wasn't ready yet, try using last known
           // geolocation from manager.
           Location bestLocation = GeoUtils.getBestLastGeolocation(this);
           if (bestLocation != null) {
               mMapController.animateTo(GeoUtils.locationToGeoPoint(bestLocation));
               mMapController.setZoom(14);
           } else {
               // We have no location information at all, so we'll just show
               // the map at a high
               // zoom level and the user can zoom in as they wish.
               ParkingSpace parkingSpace = mStateHolder.getParkingSpaces().get(0);
               mMapController.animateTo(new GeoPoint(
                       (int) (Float.valueOf(parkingSpace.getGeoLat()) * 1E6), (int) (Float.valueOf(parkingSpace
                               .getGeoLong()) * 1E6)));
               mMapController.setZoom(8);
           }
       }
    }
    
    /**
     * Handle taps on one of the pins.
     */
    private ParkingSpaceItemizedOverlayTapListener mParkingSpaceOverlayTapListener = new ParkingSpaceItemizedOverlayTapListener() {
        @Override
        public void onTap(OverlayItem itemSelected, OverlayItem itemLastSelected, ParkingSpace parkingSpace) {
            Intent intent = new Intent(MapActivity.this, ParkingSpaceActivity.class);
            intent.setAction(Intent.ACTION_VIEW);
            intent.putExtra(ParkingSpaceActivity.INTENT_EXTRA_PARKING_SPACE, parkingSpace);
            startActivity(intent);
            /*mMapController.animateTo(GeoUtils.stringLocationToGeoPoint(parkingSpace.getGeolat(), parkingSpace
                    .getGeolong()));*/
        }

        @Override
        public void onTap(GeoPoint p, MapView mapView) {
            
        }
    };

    // If location changes, auto-start a nearby parkingspaces search.
    private class SearchLocationObserver implements Observer {

        private boolean mRequestedFirstSearch = false;

        @Override
        public void update(Observable observable, Object data) {
            Location location = (Location) data;
            // Fire a search if we haven't done so yet.
            //if (!mRequestedFirstSearch)
                    //&& ((BestLocationListener) observable).isAccurateEnough(location)) 
            {
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
        } else {
        	Toast.makeText(this, "Sorry cannot acquire location. Please fix the problem and try again.", Toast.LENGTH_LONG).show();
        }            
        setProgressBarIndeterminateVisibility(false); 
        mStateHolder.cancelAllTasks();
        updateMap();
    }
    
    private static class ParkingSpacesListTask extends AsyncTask<Void, Void, ParkingSpaces> {
        private static final String TAG = "ParkingSpacesListTask";
        private static boolean DEBUG = false;
        
        private MapActivity mActivity;
        private ParkDroid mParkDroid;
        private ParkingSpaces results = new ParkingSpaces();
        
        public ParkingSpacesListTask(MapActivity activity) {           
            super();            
            Log.d(TAG, "ParkingSpacesListTask()");
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

        public void setActivity(MapActivity activity) {
            mActivity = activity;            
        }
        
    }
    
    private static class StateHolder {
        private List<ParkingSpace> mParkingSpaces;
        private ParkingSpacesListTask mTask;        

        public StateHolder() {            
        	mParkingSpaces = new ArrayList<ParkingSpace>();
            mTask = null;           
        }

        public void startTask(MapActivity activity) {
        	mTask = new ParkingSpacesListTask(activity);
        	mTask.execute();            
        }

        public void setActivity(MapActivity activity) {
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
    
  /*  private class StateHolder {
        
        private static final String TAG = "MapActivity - StateHolder";
        private static final boolean DEBUG = true;
    
        private List<ParkingSpace> mParkingSpaces;
        
        public StateHolder(){
            if (DEBUG) Log.d(TAG, "StateHolder()");
            ParkingSpace mPark1 = new ParkingSpace();
            ParkingSpace mPark2 = new ParkingSpace();
            ParkingSpace mPark3 = new ParkingSpace();
            mPark1.setId(1);
            mPark1.setName("Shopper's Parking");
            //mPark1.setPhone("0733683111");
            mPark1.setAddress("833 Mission Street, San Francisco, CA 94103");
            mPark1.setGeoLat("37.78362");
            mPark1.setGeoLong("-122.40508");
            //mPark1.setDistance("100");
            mPark1.setSpaces(20);
            mPark1.setPrice(100);
            //mPark1.setOpenHours("09-20");
            mPark2.setId(2);
            mPark2.setName("Civic Center Plaza Garage");
            //mPark2.setPhone("0733683444");
            mPark2.setAddress("355 McAllister Street, San Francisco, CA 94102");
            mPark2.setGeoLat("37.780398");
            mPark2.setGeoLong("-122.417823");
            //mPark2.setDistance("500");
            mPark2.setSpaces(150);
            mPark2.setPrice(25);
            mPark3.setId(2);
            mPark3.setName("Japan Center Garage");
            //mPark2.setPhone("0733683444");
            mPark3.setAddress("1610 Geary Boulevard, San Francisco, CA 94115");
            mPark3.setGeoLat("37.78500");
            mPark3.setGeoLong("-122.43004");
            //mPark2.setDistance("500");
            mPark3.setSpaces(250);

            mPark3.setPrice(50);
            //mPark2.setOpenHours("09-20"); 
            mParkingSpaces = new ArrayList<ParkingSpace>();
            mParkingSpaces.add(mPark1);mParkingSpaces.add(mPark2);   
            mParkingSpaces.add(mPark3);    
            Log.d(TAG, "p1="+mParkingSpaces.get(0).getName());
            Log.d(TAG, "p1="+mParkingSpaces.get(1).getName());
        }

        public List<ParkingSpace> getParkingSpaces() {
            if (DEBUG) Log.d(TAG, "getParkingSpaces()");
            return mParkingSpaces;
        }
        
    }*/
}
