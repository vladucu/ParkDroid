/**
 * 
 */
package com.licenta.parkdroid.maps;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
import com.licenta.park.types.ParkingSpace;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
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
    private OverlayItem mLastSelected;
    
    private ParkingSpaceItemizedOverlayTapListener mTapListener;
    
    public ParkingSpaceItemizedOverlayIcons(Context context, Drawable defaultMarker, ParkingSpaceItemizedOverlayTapListener tapListener) {
        super(defaultMarker);
        mContext = context;
        mTapListener = tapListener;
    }

    @Override
    protected OverlayItem createItem(int i) {
    	ParkingSpace parkingSpace = group.get(i);
        if (DEBUG) Log.d(TAG, "creating parking lot overlayItem: " + parkingSpace.getName());
        int lat = (int) (Double.parseDouble(parkingSpace.getGeoLat()) * 1E6);
        int lng = (int) (Double.parseDouble(parkingSpace.getGeoLong()) * 1E6);
        GeoPoint point = new GeoPoint(lat, lng);
        //GeoPoint point = GeoUtils.stringLocationToGeoPoint(parkingSpace.getGeolat(), parkingSpace.getGeolong());
        return new ParkingSpaceOverlayItem(point, parkingSpace, mContext);
    }
    
    @Override
    public boolean onTap(GeoPoint p, MapView mapView) {
        if (mTapListener != null) {
            mTapListener.onTap(p, mapView);
        }
        return super.onTap(p, mapView);
    }

    @Override
    public boolean onTap(int i) {
        if (mTapListener != null) {
            mTapListener.onTap(getItem(i), mLastSelected, group.get(i));
        }
        mLastSelected = getItem(i);
        return true;
    }

    @Override
    public void draw(android.graphics.Canvas canvas, MapView mapView, boolean shadow) {
        super.draw(canvas, mapView, false);
    }
    
    public static class ParkingSpaceOverlayItem extends OverlayItem {

        private ParkingSpace mParkingSpace;

        public ParkingSpaceOverlayItem(GeoPoint point, ParkingSpace parkingSpace, Context context) {
            super(point, parkingSpace.getName(), parkingSpace.getAddress());
            mParkingSpace = parkingSpace;
            //constructPinDrawable(parkingSpace, context);
        }

        public ParkingSpace getParkingSpace() {
            return mParkingSpace;
        }
        
        private static int dddi(int dd, float screenDensity) {
            return (int) (dd * screenDensity + 0.5f);
        }

        private void constructPinDrawable(ParkingSpace parkingSpace, Context context) {

            float screenDensity = context.getResources().getDisplayMetrics().density;
            int cx = dddi(32, screenDensity);
            int cy = dddi(32, screenDensity);

            Bitmap bmp = Bitmap.createBitmap(cx, cy, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bmp);
            Paint paint = new Paint();
            Drawable drw = context.getResources().getDrawable(R.drawable.map_marker_blue);
            drw.draw(canvas);

            Drawable bd = new BitmapDrawable(bmp);
            bd.setBounds(-cx / 2, -cy, cx / 2, 0);
            setMarker(bd);
        }
    }    

    public interface ParkingSpaceItemizedOverlayTapListener {
        public void onTap(OverlayItem itemSelected, OverlayItem itemLastSelected, ParkingSpace parkignLot);

        public void onTap(GeoPoint p, MapView mapView);
    }
    
}
