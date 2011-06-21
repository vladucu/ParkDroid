/**
 * 
 */
package com.licenta.parkdroid;

import com.licenta.park.types.Reservation;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

/**
 * @author vladucu
 *
 */
public class AddReservationResultDialog extends Dialog {
	
	private static final String TAG = "AddReservationResultDialog";
    private static final boolean DEBUG = true;
    
    private Reservation mReservationResult;
    
    //TODO use this class to show a confirmation dialog with reservation results (ID + others)
    public AddReservationResultDialog(Context context, Reservation result) {
    	super(context);
    	mReservationResult = result;
  
    	if (DEBUG) Log.d(TAG, "AddReservationResultDialog");
    }

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		
		if (DEBUG) Log.d(TAG, "AddReservationResultDialog");		
		setContentView(R.layout.add_reservation_result_dialog);
		setTitle(getContext().getResources().getString(R.string.add_reservation_title_result));
		
		TextView tvReservationId = (TextView) findViewById(R.id.addResResultDialogId);
		TextView tvStartingAt = (TextView) findViewById(R.id.addResResultDialogStartTime);
		TextView tvEndingAt = (TextView) findViewById(R.id.addResResultDialogEndTime);
		TextView tvTotalTime = (TextView) findViewById(R.id.addResResultDialogTotalTime);
		TextView tvPriceHour = (TextView) findViewById(R.id.addResResultDialogPrice);
		TextView tvCost = (TextView) findViewById(R.id.addResResultDialogCost);
		
		tvReservationId.setText(Integer.toString(mReservationResult.getId()));
		tvStartingAt.setText(mReservationResult.getStartTime());
		tvEndingAt.setText(mReservationResult.getEndTime());
		tvTotalTime.setText(mReservationResult.getTotalTime());
		tvPriceHour.setText(Integer.toString(mReservationResult.getParkingSpace().getPrice()) + "$");
		tvCost.setText(Integer.toString(mReservationResult.getCost()) + "$");
	}
}

