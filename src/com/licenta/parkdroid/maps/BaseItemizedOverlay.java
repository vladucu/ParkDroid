/**
 * 
 */
package com.licenta.parkdroid.maps;

import java.util.List;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
import com.licenta.parkdroid.types.ParkTypes;

import android.graphics.drawable.Drawable;
import android.util.Log;

/**
 * @author vladucu
 *
 */
abstract class BaseItemizedOverlay<T extends ParkTypes> extends ItemizedOverlay<OverlayItem> {
    
    private static final String TAG = "BaseItemizedOverlay";
    private static boolean DEBUG = true;

    List<T> group = null;
    
    public BaseItemizedOverlay(Drawable defaultMarker) {
        super(boundCenterBottom(defaultMarker));
        if (DEBUG) Log.d(TAG, "BaseItemizedOverlay()");
    }    
   
    @Override
    public int size() {
        if (DEBUG) Log.d(TAG, "size()");
        if (group == null) {
            return 0;
        }
        return group.size();
    }

    @Override
    public boolean onTap(GeoPoint p, MapView mapView) {
        return super.onTap(p, mapView);
    }

    @Override
    protected boolean onTap(int index) {
        return super.onTap(index);
    }

    public void setGroup(List<T> g) {
        if (DEBUG) Log.d(TAG, "addGroup()");
        group = g;
        super.populate();
    }

}
