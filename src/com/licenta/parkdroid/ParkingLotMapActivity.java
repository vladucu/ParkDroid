/**
 * 
 */
package com.licenta.parkdroid;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.licenta.park.types.ParkingLot;
import android.os.Bundle;
import android.util.Log;

/**
 * @author vladucu
 *
 */
public class ParkingLotMapActivity extends MapActivity {

    public static final String TAG = "ParkingLotMapActivity";
    //debug mode
    public static final boolean DEBUG = true;
    
    public static final String INTENT_EXTRA_PARKING_LOT = ParkDroid.PACKAGE_NAME + ".ParkingLotMapActivity.INTENT_EXTRA_PARKING_LOT";
    
    private MapView mMapView;
    private MapController mMapController;
    private MyLocationOverlay mMyLocationOverlay = null;
    private ParkingLotItemizedOverlay mOverlay = null;
    private StateHolder mStateHolder;
    
    /* (non-Javadoc)
     * @see com.google.android.maps.MapActivity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (DEBUG) Log.d(TAG, "onCreate()");
        setContentView(R.layout.map_activity);
        
        Object retained = getLastNonConfigurationInstance();
        if (retained != null && retained instanceof StateHolder) {
            mStateHolder = (StateHolder) retained;
        } else {
            mStateHolder = new StateHolder();
            if (getIntent().hasExtra(INTENT_EXTRA_PARKING_LOT)) {              
                mStateHolder.setParkingLot((ParkingLot) getIntent().getExtras().getParcelable(
                        INTENT_EXTRA_PARKING_LOT));
            } else {
                Log.e(TAG, "ParkingLotMapActivity requires a parking lot parcel to its intent extras.");
                finish();
                return;
            }
        }

        ensureUi();
    }

    private void ensureUi() {
        if (DEBUG) Log.d(TAG, "ensureUi()");
        
        mMapView = (MapView) findViewById(R.id.mapView);
        mMapController = mMapView.getController();        
        mMapView.setBuiltInZoomControls(true);
        
        mMyLocationOverlay = new MyLocationOverlay(this, mMapView);
        mMapView.getOverlays().add(mMyLocationOverlay);
        
        mOverlay = new ParkingLotItemizedOverlay(this.getResources().getDrawable(R.drawable.map_marker_blue));
        Group<ParkingLot> group = new Group<ParkingLot>();
        group.add(mStateHolder.getParkingLot());
        mOverlay.addGroup(group);
        mMapView.getOverlays().add(mOverlay);
        if (DEBUG) Log.d(TAG, "ensureUi()=");
        updateMap();
    }
    
    private void updateMap() {
        if (DEBUG) Log.d(TAG, "updateMap()");
        
        if (mOverlay != null && mOverlay.size()>0) {
            if (DEBUG) Log.d(TAG, "updateMap()="+"true");
            GeoPoint center = mOverlay.getCenter();
            mMapController.animateTo(center);
            mMapController.setZoom(17);
        }
        
    }
    
    /* (non-Javadoc)
     * @see com.google.android.maps.MapActivity#isRouteDisplayed()
     */
    @Override
    protected boolean isRouteDisplayed() {
        // TODO Auto-generated method stub
        return false;
    }
   
    /* (non-Javadoc)
     * @see com.google.android.maps.MapActivity#onResume()
     */
    @Override
    protected void onResume() {
        if (DEBUG) Log.d(TAG, "onResume()");
        // TODO Auto-generated method stub
        super.onResume();
    }
    
    /* (non-Javadoc)
     * @see com.google.android.maps.MapActivity#onPause()
     */
    @Override
    protected void onPause() {
        if (DEBUG) Log.d(TAG, "onPause()");
        // TODO Auto-generated method stub
        super.onPause();
    }


    private static class StateHolder {
        
        private static final String TAG = "StateHolder";
        private static boolean DEBUG = true;
        
        private ParkingLot mParkingLot;
        
        public StateHolder() {
            
        }
        
        public void setParkingLot(ParkingLot parkingLot) {
            if (DEBUG) Log.d(TAG, "setParkingLot()");
            mParkingLot = parkingLot;
        }
        
        public ParkingLot getParkingLot() {
            if (DEBUG) Log.d(TAG, "getParkingLot()");
            return mParkingLot;
        }
    }

}
