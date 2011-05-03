/**
 * 
 */
package com.licenta.parkdroid;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
import com.licenta.park.types.ParkingLot;
import com.licenta.park.utils.GeoUtils;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import java.io.IOException;

/**
 * @author vladucu
 *
 */
public class ParkingLotItemizedOverlayIcons extends BaseItemizedOverlay<ParkingLot> {

    private static final String TAG = "ParkingLotItemizedOverlayIcons";
    private static boolean DEBUG = true;
        
    private Context mContext;
    private OverlayItem mLastSelected;
    
    private ParkingLotItemizedOverlayTapListener mTapListener;
    
    public ParkingLotItemizedOverlayIcons(Context context, Drawable defaultMarker, 
            ParkingLotItemizedOverlayTapListener tapListener) {
        super(defaultMarker);
        mContext = context;
        mTapListener = tapListener;
    }

    @Override
    protected OverlayItem createItem(int i) {
        ParkingLot parkingLot = group.get(i);
        if (DEBUG) Log.d(TAG, "creating parking lot overlayItem: " + parkingLot.getName());
        int lat = (int) (Double.parseDouble(parkingLot.getGeolat()) * 1E6);
        int lng = (int) (Double.parseDouble(parkingLot.getGeolong()) * 1E6);
        GeoPoint point = new GeoPoint(lat, lng);
        //GeoPoint point = GeoUtils.stringLocationToGeoPoint(parkingLot.getGeolat(), parkingLot.getGeolong());
        return new ParkingLotOverlayItem(point, parkingLot, mContext);
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
    
    public static class ParkingLotOverlayItem extends OverlayItem {

        private ParkingLot mParkingLot;

        public ParkingLotOverlayItem(GeoPoint point, ParkingLot parkingLot, Context context) {
            super(point, parkingLot.getName(), parkingLot.getAddress());
            mParkingLot = parkingLot;
            //constructPinDrawable(parkingLot, context);
        }

        public ParkingLot getParkingLot() {
            return mParkingLot;
        }
        
        private static int dddi(int dd, float screenDensity) {
            return (int) (dd * screenDensity + 0.5f);
        }

        private void constructPinDrawable(ParkingLot parkingLot, Context context) {

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

    public interface ParkingLotItemizedOverlayTapListener {
        public void onTap(OverlayItem itemSelected, OverlayItem itemLastSelected, ParkingLot parkignLot);

        public void onTap(GeoPoint p, MapView mapView);
    }
    
}
