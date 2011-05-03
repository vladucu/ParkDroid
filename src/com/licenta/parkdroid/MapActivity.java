package com.licenta.parkdroid;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.OverlayItem;
import com.licenta.park.types.Group;
import com.licenta.park.types.ParkingLot;
import com.licenta.park.utils.GeoUtils;
import com.licenta.parkdroid.ParkingLotItemizedOverlayIcons;
import com.licenta.parkdroid.ParkingLotItemizedOverlayIcons.ParkingLotItemizedOverlayTapListener;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

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
    private ParkingLotItemizedOverlayIcons mOverlay = null; 
    
    private String mTappedParkingLot;
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
    
    /* (non-Javadoc)
     * @see com.google.android.maps.MapActivity#onPause()
     */
    
    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
    }

    /* (non-Javadoc)
     * @see com.google.android.maps.MapActivity#onResume()
     */
    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
    }

    /* (non-Javadoc)
     * @see com.google.android.maps.MapActivity#isRouteDisplayed()
     */
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
        if (DEBUG) Log.d(TAG, "ensureUi()="+(Group<ParkingLot>)mStateHolder.getParkingLots());
        
        mMapView = (MapView) findViewById(R.id.mapView);
        // Display zoom controls (+/-)
        mMapView.setBuiltInZoomControls(true);
        mMapController = mMapView.getController();
        mMapView.setSatellite(false);
        
        mMyLocationOverlay = new MyLocationOverlay(this, mMapView);
        mMapView.getOverlays().add(mMyLocationOverlay);
              
        mOverlay = new ParkingLotItemizedOverlayIcons(this, getResources().getDrawable(R.drawable.map_marker_blue), mParkingLotOverlayTapListener);        
        Group<ParkingLot> g = new Group<ParkingLot>();
        for (ParkingLot it:mStateHolder.getParkingLots()) {
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
               ParkingLot parkingLot = mStateHolder.getParkingLots().get(0);
               mMapController.animateTo(new GeoPoint(
                       (int) (Float.valueOf(parkingLot.getGeolat()) * 1E6), (int) (Float.valueOf(parkingLot
                               .getGeolong()) * 1E6)));
               mMapController.setZoom(8);
           }
       }
    }
    
    /**
     * Handle taps on one of the pins.
     */
    private ParkingLotItemizedOverlayTapListener mParkingLotOverlayTapListener = new ParkingLotItemizedOverlayTapListener() {
        @Override
        public void onTap(OverlayItem itemSelected, OverlayItem itemLastSelected, ParkingLot parkingLot) {
            Intent intent = new Intent(MapActivity.this, ParkingLotActivity.class);
            intent.setAction(Intent.ACTION_VIEW);
            intent.putExtra(ParkingLotActivity.INTENT_EXTRA_PARKING_LOT, parkingLot);
            startActivity(intent);
            /*mMapController.animateTo(GeoUtils.stringLocationToGeoPoint(parkingLot.getGeolat(), parkingLot
                    .getGeolong()));*/
        }

        @Override
        public void onTap(GeoPoint p, MapView mapView) {
            
        }
    };


    private class StateHolder {
        
        private static final String TAG = "StateHolder";
        private static final boolean DEBUG = true;
    
        private Group<ParkingLot> mParkingLots;
        
        public StateHolder(){
            if (DEBUG) Log.d(TAG, "StateHolder()");
            ParkingLot mPark1 = new ParkingLot();
            ParkingLot mPark2 = new ParkingLot();
            mPark1.setId("123");
            mPark1.setName("Parcarea principala");
            mPark1.setPhone("0733683111");
            mPark1.setAddress("Bulevardul Regina Elisabeta 23 si o iei pe acolo pe unde vezi cu ochii");
            mPark1.setCity("Bucuresti");
            mPark1.setZip("050012");
            mPark1.setGeolat("44.43472");
            mPark1.setGeolong("26.09704");
            mPark1.setDistance("100");
            mPark1.setEmptySpaces("150");
            mPark1.setTotalSpaces("500");
            mPark1.setPrice("25");
            mPark1.setUrl("http://google.com");
            mPark2.setId("523");
            mPark2.setName("Parcarea secundara");
            mPark2.setPhone("0733683444");
            mPark2.setAddress("Bulevardul Carol 76");
            mPark2.setCity("Bucuresti");
            mPark2.setZip("020926");
            mPark2.setGeolat("44.43797");
            mPark2.setGeolong("26.11576");
            mPark2.setDistance("500");
            mPark2.setEmptySpaces("550");
            mPark2.setTotalSpaces("1500");
            mPark2.setPrice("125");
            mPark2.setUrl("http://igoogle.com");  
            mParkingLots = new Group<ParkingLot>();
            mParkingLots.add(mPark1);mParkingLots.add(mPark2);       
           /* Log.d(TAG, "p1="+mParkingLots.get(0).getName());
            Log.d(TAG, "p1="+mParkingLots.get(1).getName());*/
        }

        public Group<ParkingLot> getParkingLots() {
            if (DEBUG) Log.d(TAG, "getParkingLots()");
            return mParkingLots;
        }
        
    }
}
