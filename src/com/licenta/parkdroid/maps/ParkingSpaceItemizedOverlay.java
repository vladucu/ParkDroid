/**
 * 
 */
package com.licenta.parkdroid.maps;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;
import com.licenta.parkdroid.types.ParkingSpace;

import android.graphics.drawable.Drawable;
import android.util.Log;

/**
 * @author vladucu
 *
 */
public class ParkingSpaceItemizedOverlay extends BaseItemizedOverlay<ParkingSpace> {

    private static final String TAG = "ParkingSpaceItemizedOverlay";
    private static boolean DEBUG = true;
        
    private boolean spanCalculated = false;
    private SpanHolder mSpanHolder = new SpanHolder();
    
    public ParkingSpaceItemizedOverlay(Drawable defaultMarker) {
        super(defaultMarker);
    }

    @Override
    protected OverlayItem createItem(int i) {
    	ParkingSpace parkingSpace = group.get(i);
        if (DEBUG) Log.d(TAG, "creating parking lot overlayItem: " + parkingSpace.getName());
        int lat = (int) (Double.parseDouble(parkingSpace.getGeoLat()) * 1E6);
        int lng = (int) (Double.parseDouble(parkingSpace.getGeoLong()) * 1E6);
        GeoPoint point = new GeoPoint(lat, lng);
        return new ParkingSpaceOverlayItem(point, parkingSpace);
    }
    
    /* (non-Javadoc)
     * @see com.google.android.maps.ItemizedOverlay#getLatSpanE6()
     */
    @Override
    public int getLatSpanE6() {
        if (DEBUG) Log.d(TAG, "getLatSpanE6()");
        if (!spanCalculated) {
            calculateSpan();
        }
        return mSpanHolder.latSpanE6;
        
    }

    /* (non-Javadoc)
     * @see com.google.android.maps.ItemizedOverlay#getLonSpanE6()
     */
    @Override
    public int getLonSpanE6() {
        if (DEBUG) Log.d(TAG, "getLonSpanE6()");
        if (!spanCalculated) {
            calculateSpan();
        }
        return mSpanHolder.lonSpanE6;
    }

    private void calculateSpan() {
        if (DEBUG) Log.d(TAG, "calculateSpan()");
        int minLat = 0;
        int maxLat = 0;
        int minLon = 0;
        int maxLon = 0;
        for (int i=0;i<group.size();i++) {
        	ParkingSpace parkingSpace = group.get(i);
            //TODO validare coordonate locatie parcare 
        
            int lat = (int) (Double.parseDouble(parkingSpace.getGeoLat()) * 1E6);
            int lon = (int) (Double.parseDouble(parkingSpace.getGeoLong()) * 1E6);

            // LatSpan
            if (lat > maxLat || maxLat == 0) {
                maxLat = lat;
            }
            if (lat < minLat || minLat == 0) {
                minLat = lat;
            }

            // LonSpan
            if (lon < minLon || minLon == 0) {
                minLon = lon;
            }
            if (lon > maxLon || maxLon == 0) {
                maxLon = lon;
            }        
        }        
        spanCalculated = true;
        mSpanHolder.latSpanE6 = maxLat - minLat;
        mSpanHolder.lonSpanE6 = maxLon - minLon;
    }
    
    public static class ParkingSpaceOverlayItem extends OverlayItem {

        private ParkingSpace mParkingSpace;

        public ParkingSpaceOverlayItem(GeoPoint point, ParkingSpace parkingSpace) {
            super(point, parkingSpace.getName(), parkingSpace.getAddress());
            mParkingSpace = parkingSpace;
        }

        public ParkingSpace getParkingLot() {
            return mParkingSpace;
        }
    }
    
    public static final class SpanHolder {
        int latSpanE6 = 0;
        int lonSpanE6 = 0;
    }

}
