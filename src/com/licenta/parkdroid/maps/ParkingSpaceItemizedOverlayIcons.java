/**
 * 
 */
package com.licenta.parkdroid.maps;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;
import com.licenta.park.types.ParkingSpace;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;

/**
 * @author vladucu
 *
 */
public class ParkingSpaceItemizedOverlayIcons extends BaseItemizedOverlay<ParkingSpace> {

    private static final String TAG = "ParkingSpaceItemizedOverlayIcons";
    private static boolean DEBUG = true;
        
    private Context mContext;
    
    public ParkingSpaceItemizedOverlayIcons(Context context, Drawable defaultMarker) {
        super(defaultMarker);
        mContext = context;
    }

    @Override
    protected OverlayItem createItem(int i) {
    	ParkingSpace parkingSpace = group.get(i);
        if (DEBUG) Log.d(TAG, "creating parking lot overlayItem: " + parkingSpace.getName());
        int lat = (int) (Double.parseDouble(parkingSpace.getGeoLat()) * 1E6);
        int lng = (int) (Double.parseDouble(parkingSpace.getGeoLong()) * 1E6);
        GeoPoint point = new GeoPoint(lat, lng);
        return new ParkingSpaceOverlayItem(point, parkingSpace, mContext);
    }
    
    public static class ParkingSpaceOverlayItem extends OverlayItem {

        private ParkingSpace mParkingSpace;

        public ParkingSpaceOverlayItem(GeoPoint point, ParkingSpace parkingSpace, Context context) {
            super(point, parkingSpace.getName(), parkingSpace.getAddress());
            mParkingSpace = parkingSpace;
        }

        public ParkingSpace getParkingSpace() {
            return mParkingSpace;
        }
    }
    
    public static final class SpanHolder {
        int latSpanE6 = 0;
        int lonSpanE6 = 0;
    }

}
