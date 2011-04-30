/**
 * 
 */
package com.licenta.parkdroid.maps;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;
import com.licenta.park.types.ParkingLot;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;

/**
 * @author vladucu
 *
 */
public class ParkingLotItemizedOverlayIcons extends BaseItemizedOverlay<ParkingLot> {

    private static final String TAG = "ParkingLotItemizedOverlayIcons";
    private static boolean DEBUG = true;
        
    private Context mContext;
    
    public ParkingLotItemizedOverlayIcons(Context context, Drawable defaultMarker) {
        super(defaultMarker);
        mContext = context;
    }

    @Override
    protected OverlayItem createItem(int i) {
        ParkingLot parkingLot = group.get(i);
        if (DEBUG) Log.d(TAG, "creating parking lot overlayItem: " + parkingLot.getName());
        int lat = (int) (Double.parseDouble(parkingLot.getGeolat()) * 1E6);
        int lng = (int) (Double.parseDouble(parkingLot.getGeolong()) * 1E6);
        GeoPoint point = new GeoPoint(lat, lng);
        return new ParkingLotOverlayItem(point, parkingLot, mContext);
    }
    
    public static class ParkingLotOverlayItem extends OverlayItem {

        private ParkingLot mParkingLot;

        public ParkingLotOverlayItem(GeoPoint point, ParkingLot parkingLot, Context context) {
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
