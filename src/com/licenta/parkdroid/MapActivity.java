package com.licenta.parkdroid;

import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import android.os.Bundle;
import android.util.Log;

/**
 * @author vladucu
 *
 */
public class MapActivity extends com.google.android.maps.MapActivity {

    private static final String TAG = "MapActivity";
    public static final boolean DEBUG = true;
    
    private MapView mapView;
    private MapController mapController;
    private MyLocationOverlay myLocationOverlay;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        if (DEBUG) Log.d(TAG, "onCreate()");
        setContentView(R.layout.map_activity);
        initMapView();
        initMyLocation();
        /*ParkingItemizedOverlay parkingMarkers = new ParkingItemizedOverlay(getResources().getDrawable(R.drawable.map_marker_blue));
        int lat = (int) (Double.parseDouble("26.11537") * 1E6);
        int lng = (int) (Double.parseDouble("44.43711") * 1E6);
        //Double lat = 26.11537*1E6;
        //Double lng = 44.43711*1E6;
        parkingMarkers.addNewItem(new GeoPoint(lat, lng), "markerText", "snippet");
        mapView.getOverlays().add(parkingMarkers);
        Log.d(TAG, "DEBUG   "+getResources().getDrawable(R.drawable.map_marker_blue));*/
    }
    
    /* (non-Javadoc)
     * @see com.google.android.maps.MapActivity#isRouteDisplayed()
     */
    @Override
    protected boolean isRouteDisplayed() {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * Initiates MapView
     */
    private void initMapView() {
        mapView = (MapView) findViewById(R.id.mapView);
        mapController = mapView.getController();
        mapView.setSatellite(true);
        // Display zoom controls (+/-)
        mapView.setBuiltInZoomControls(true);
    }

    private void initMyLocation() {
        myLocationOverlay = new MyLocationOverlay(this, mapView);
        myLocationOverlay.enableMyLocation();
        // myLocationOverlay.enableCompass(); // does not work in emulator
        //when new location center and zoom
        myLocationOverlay.runOnFirstFix(new Runnable() {
            public void run() {
                // Zoom in to current location
                mapController.setZoom(17);
                mapController.animateTo(myLocationOverlay.getMyLocation());
            }
        });
        mapView.getOverlays().add(myLocationOverlay);
    }

}
