package com.licenta.parkdroid;

import com.licenta.parkdroid.types.ParkingSpace;

import android.content.Context;

/**
 * @author vladucu
 *
 */
public abstract class BaseParkingSpaceAdapter extends BaseGroupAdapter<ParkingSpace> {
    
    public BaseParkingSpaceAdapter(Context context) {
        super(context);
    }
}
