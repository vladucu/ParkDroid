/**
 * 
 */
package com.licenta.parkdroid;

import com.licenta.park.types.ParkingLot;

import android.content.Context;

/**
 * @author vladucu
 *
 */
abstract public class BaseParkingLotAdapter extends BaseGroupAdapter<ParkingLot> {

    public BaseParkingLotAdapter(Context context) {
        super(context);
    }

}
