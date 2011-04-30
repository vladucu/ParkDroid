/**
 * 
 */
package com.licenta.parkdroid;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;
import com.licenta.park.types.ParkingLot;
import android.graphics.drawable.Drawable;
import android.util.Log;

/**
 * @author vladucu
 *
 */
public class ParkingLotItemizedOverlay extends BaseItemizedOverlay<ParkingLot> {

    private static final String TAG = "ParkingLotItemizedOverlay";
    private static boolean DEBUG = true;
        
    private boolean spanCalculated = false;
    private SpanHolder mSpanHolder = new SpanHolder();
    
    public ParkingLotItemizedOverlay(Drawable defaultMarker) {
        super(defaultMarker);
    }

    @Override
    protected OverlayItem createItem(int i) {
        ParkingLot parkingLot = group.get(i);
        if (DEBUG) Log.d(TAG, "creating parking lot overlayItem: " + parkingLot.getName());
        int lat = (int) (Double.parseDouble(parkingLot.getGeolat()) * 1E6);
        int lng = (int) (Double.parseDouble(parkingLot.getGeolong()) * 1E6);
        GeoPoint point = new GeoPoint(lat, lng);
        return new ParkingLotOverlayItem(point, parkingLot);
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
            ParkingLot parkingLot = group.get(i);
            //TODO validare coordonate locatie parcare 
        
            int lat = (int) (Double.parseDouble(parkingLot.getGeolat()) * 1E6);
            int lon = (int) (Double.parseDouble(parkingLot.getGeolong()) * 1E6);

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
    
    public static class ParkingLotOverlayItem extends OverlayItem {

        private ParkingLot mParkingLot;

        public ParkingLotOverlayItem(GeoPoint point, ParkingLot parkingLot) {
            super(point, parkingLot.getName(), parkingLot.getAddress());
            mParkingLot = parkingLot;
        }

        public ParkingLot getParkingLot() {
            return mParkingLot;
        }
    }
    
    public static final class SpanHolder {
        int latSpanE6 = 0;
        int lonSpanE6 = 0;
    }

}
