/**
 * 
 */
package com.licenta.parkdroid;

import com.licenta.park.types.Reservation;

import android.content.Context;

/**
 * @author vladucu
 *
 */
public abstract class BaseReservationsAdapter extends BaseGroupAdapter<Reservation> {

    public BaseReservationsAdapter(Context context) {
        super(context);
    }

}
