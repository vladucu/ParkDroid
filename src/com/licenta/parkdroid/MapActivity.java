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
import com.licenta.parkdroid.maps.ParkingSpaceItemizedOverlayIcons;
import com.licenta.parkdroid.maps.ParkingSpaceItemizedOverlayIcons.ParkingSpaceItemizedOverlayTapListener;
import com.licenta.parkdroid.types.ParkingSpace;
import com.licenta.parkdroid.utils.GeoUtils;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

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
   // private SearchLocationObserver mSearchLocationObserver = new SearchLocationObserver();
    
    private String mTappedParkingSpace;
    private StateHolder mStateHolder;
    private Handler mHandler;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        if (DEBUG) Log.d(TAG, "onCreate()");
        
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
	public Object onRetainNonConfigurationInstance() {
		return mStateHolder;
	}

	@Override
    protected void onPause() {
        super.onPause();
        if (DEBUG) Log.d(TAG, "onPause()");
        mMyLocationOverlay.disableMyLocation();
     //   ((ParkDroid) getApplication()).removeLocationUpdates(mSearchLocationObserver);

        /*if (isFinishing()) {
            mStateHolder.cancelAllTasks();
            mListAdapter.removeObserver();
        }*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (DEBUG) Log.d(TAG, "onResume()");
        mMyLocationOverlay.enableMyLocation();
        
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
        if (DEBUG) Log.d(TAG, "ensureUi()="+(List<ParkingSpace>)mStateHolder.getParkingSpaces());
        
        mMapView = (MapView) findViewById(R.id.mapView);
        // Display zoom controls (+/-)
        mMapView.setBuiltInZoomControls(true);
        mMapController = mMapView.getController();
        mMapView.setSatellite(false);
        
        mMyLocationOverlay = new MyLocationOverlay(this, mMapView);
        mMapView.getOverlays().add(mMyLocationOverlay);
        //initMyLocation();
        
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
        else {
            finish();
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
/*
    // If location changes, auto-start a nearby parkingspaces search.
    private class SearchLocationObserver implements Observer {

        private boolean mRequestedFirstSearch = false;

        @Override
        public void update(Observable observable, Object data) {
            Location location = (Location) data;
            // Fire a search if we haven't done so yet.
            if (!mRequestedFirstSearch
                    && ((BestLocationListener) observable).isAccurateEnough(location)) {
                mRequestedFirstSearch = true;
                //if (mStateHolder.getIsRunningTask() == false) {
                    // Since we were told by the system that location has
                    // changed, no need to make the
                    // task wait before grabbing the current location.
                    mHandler.post(new Runnable() {
                        public void run() {
                           // startTask(0L);
                        }
                    });
                //}
            }
        }
    }
*/

    private class StateHolder {
        
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
           /* Log.d(TAG, "p1="+mParkingSpaces.get(0).getName());
            Log.d(TAG, "p1="+mParkingSpaces.get(1).getName());*/
        }

        public List<ParkingSpace> getParkingSpaces() {
            if (DEBUG) Log.d(TAG, "getParkingSpaces()");
            return mParkingSpaces;
        }
        
    }
}
