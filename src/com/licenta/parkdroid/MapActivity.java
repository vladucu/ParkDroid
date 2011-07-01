package com.licenta.parkdroid;

import java.util.ArrayList;
import java.util.List;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.OverlayItem;
import com.licenta.parkdroid.maps.ParkingSpaceItemizedOverlayIcons;
import com.licenta.parkdroid.maps.ParkingSpaceItemizedOverlayIcons.ParkingSpaceItemizedOverlayTapListener;
import com.licenta.parkdroid.types.ParkingSpace;
import com.licenta.parkdroid.utils.GeoUtils;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

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
    private StateHolder mStateHolder;
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
            mStateHolder.setParkingSpaces(ParkDroidActivity.mStateHolder.getParkingSpaces());
            ensureUi();
        }
    };  

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        if (DEBUG) Log.d(TAG, "onCreate()");
        registerReceiver(mLoggedOutReceiver, new IntentFilter(ParkDroid.INTENT_ACTION_LOGGED_OUT));  
        registerReceiver(mRefreshUi, new IntentFilter(ParkDroid.INTENT_ACTION_REFRESH_UI));
        setContentView(R.layout.map_activity);
        
        mHandler = new Handler();
        
        Object retained = getLastNonConfigurationInstance();
        if (retained != null && retained instanceof StateHolder) {
            mStateHolder = (StateHolder) retained;
        }
        else {
            mStateHolder = new StateHolder();            
        }
        
        ensureUi();        
    }
    
    @Override
    protected void onDestroy() {        
        super.onDestroy();
        unregisterReceiver(mLoggedOutReceiver);
        unregisterReceiver(mRefreshUi);
    }
    
	@Override
	public Object onRetainNonConfigurationInstance() {
		return mStateHolder;
	}

	@Override
    protected void onPause() {
        super.onPause();
        if (DEBUG) Log.d(TAG, "onPause()");
        mMyLocationOverlay.disableMyLocation();        
        ((ParkDroid) getApplication()).removeLocationUpdates(ParkDroidActivity.mSearchLocationObserver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (DEBUG) Log.d(TAG, "onResume()");
        mMyLocationOverlay.enableMyLocation();
        ((ParkDroid) getApplication()).requestLocationUpdates(ParkDroidActivity.mSearchLocationObserver);
    }

    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }
    
    private void ensureUi() {
        if (DEBUG) Log.d(TAG, "ensureUi()");
        
        mMapView = (MapView) findViewById(R.id.mapView);
        // Display zoom controls (+/-)
        mMapView.setBuiltInZoomControls(true);
        mMapController = mMapView.getController();
        mMapView.setSatellite(false);
        
        mMyLocationOverlay = new MyLocationOverlay(this, mMapView);
        //mMyLocationOverlay.enableMyLocation();
        mMapView.getOverlays().add(mMyLocationOverlay);
        updateMap();               
    }
    
    public void updateMap() {
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

	private static class StateHolder {
        private List<ParkingSpace> mParkingSpaces;   

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
