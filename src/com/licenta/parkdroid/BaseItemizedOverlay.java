/**
 * 
 */
package com.licenta.parkdroid;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
import com.licenta.park.types.ParkTypes;
import android.graphics.drawable.Drawable;
import android.util.Log;

/**
 * @author vladucu
 *
 */
abstract class BaseItemizedOverlay<T extends ParkTypes> extends ItemizedOverlay<OverlayItem> {
    
    private static final String TAG = "BaseItemizedOverlay";
    private static boolean DEBUG = true;

    Group<T> group = null;
    
    public BaseItemizedOverlay(Drawable defaultMarker) {
        super(boundCenterBottom(defaultMarker));
        if (DEBUG) Log.d(TAG, "BaseItemizedOverlay()");
    }    
   
    /* (non-Javadoc)
     * @see com.google.android.maps.ItemizedOverlay#size()
     */
    @Override
    public int size() {
        if (DEBUG) Log.d(TAG, "size()");
        if (group == null) {
            return 0;
        }
        return group.size();
    }

    /* (non-Javadoc)
     * @see com.google.android.maps.ItemizedOverlay#onTap(com.google.android.maps.GeoPoint, com.google.android.maps.MapView)
     */
    @Override
    public boolean onTap(GeoPoint p, MapView mapView) {
        // TODO Auto-generated method stub
        return super.onTap(p, mapView);
    }

    /* (non-Javadoc)
     * @see com.google.android.maps.ItemizedOverlay#onTap(int)
     */
    @Override
    protected boolean onTap(int index) {
        // TODO Auto-generated method stub
        return super.onTap(index);
    }

    public void addGroup(Group<T> g) {
        if (DEBUG) Log.d(TAG, "addGroup()");
        group = g;
        super.populate();
    }

}
