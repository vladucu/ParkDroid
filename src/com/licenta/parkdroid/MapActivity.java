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

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

/**
 * @author vladucu
 *
 */
public class MapActivity extends com.google.android.maps.MapActivity {

    private static final String TAG = "MapActivity";
    private static final boolean DEBUG = true;
    
    private MapView mMapView;
    private MapController mMapController;
    private MyLocationOverlay mMyLocationOverlay = null;
    private ParkingSpaceItemizedOverlayIcons mOverlay = null; 
    
    private String mTappedParkingSpace;
    private StateHolder mStateHolder;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        if (DEBUG) Log.d(TAG, "onCreate()");
        
        setContentView(R.layout.map_activity);
        
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
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }

/*    private void initMyLocation() {
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
    */
    private void ensureUi() {
        if (DEBUG) Log.d(TAG, "ensureUi()="+(List<ParkingSpace>)mStateHolder.getParkingSpaces());
        
        mMapView = (MapView) findViewById(R.id.mapView);
        // Display zoom controls (+/-)
        mMapView.setBuiltInZoomControls(true);
        mMapController = mMapView.getController();
        mMapView.setSatellite(false);
        
        mMyLocationOverlay = new MyLocationOverlay(this, mMapView);
        mMapView.getOverlays().add(mMyLocationOverlay);
              
        mOverlay = new ParkingSpaceItemizedOverlayIcons(this, getResources().getDrawable(R.drawable.map_marker_blue), mParkingSpaceOverlayTapListener);        
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


    private class StateHolder {
        
        private static final String TAG = "MapActivity - StateHolder";
        private static final boolean DEBUG = true;
    
        private List<ParkingSpace> mParkingSpaces;
        
        public StateHolder(){
            if (DEBUG) Log.d(TAG, "StateHolder()");
            ParkingSpace mPark1 = new ParkingSpace();
            ParkingSpace mPark2 = new ParkingSpace();
            mPark1.setId(1);
            mPark1.setName("Parcarea principala");
            //mPark1.setPhone("0733683111");
            mPark1.setAddress("Bulevardul Regina Elisabeta 23 si o iei pe acolo pe unde vezi cu ochii");
            //mPark1.setCity("Bucuresti");
            //mPark1.setZip("050012");
            mPark1.setGeoLat("44.43472");
            mPark1.setGeoLong("26.09704");
            //mPark1.setDistance("100");
            mPark1.setSpaces(150);
            //mPark1.setTotalSpaces("500");
            //mPark1.setPrice("25");
            //mPark1.setOpenHours("09-20");
            mPark2.setId(2);
            mPark2.setName("Parcarea secundara");
            //mPark2.setPhone("0733683444");
            mPark2.setAddress("Bulevardul Carol 76");
            //mPark2.setCity("Bucuresti");
            //mPark2.setZip("020926");
            mPark2.setGeoLat("44.43797");
            mPark2.setGeoLong("26.11576");
            //mPark2.setDistance("500");
            mPark2.setSpaces(550);
            ///mPark2.setTotalSpaces("1500");
            //mPark2.setPrice("125");
            //mPark2.setOpenHours("09-20"); 
            mParkingSpaces = new ArrayList<ParkingSpace>();
            mParkingSpaces.add(mPark1);mParkingSpaces.add(mPark2);       
           /* Log.d(TAG, "p1="+mParkingSpaces.get(0).getName());
            Log.d(TAG, "p1="+mParkingSpaces.get(1).getName());*/
        }

        public List<ParkingSpace> getParkingSpaces() {
            if (DEBUG) Log.d(TAG, "getParkingSpaces()");
            return mParkingSpaces;
        }
        
    }
}
