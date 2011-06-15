/**
 * 
 */
package com.licenta.parkdroid;

import com.licenta.park.types.Reservation;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

/**
 * @author vladucu
 *
 */
public class AddReservationResultDialog extends Dialog {
	
	private static final String TAG = "AddReservationResultDialog";
    private static final boolean DEBUG = true;
    
    private Handler mHandler;
    private ParkDroid mApplication;
    private Reservation mReservationResult;
    
    //TODO use this class to show a confirmation dialog with reservation results (ID + others)
    public AddReservationResultDialog(Context context, Reservation result, ParkDroid app) {
    	super(context);
    	mReservationResult = result;
    	mApplication = app;
    	mHandler = new Handler();
    	
    	if (DEBUG) Log.d(TAG, "AddReservationResultDialog");
    }

	/* (non-Javadoc)
	 * @see android.app.Dialog#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		if (DEBUG) Log.d(TAG, "AddReservationResultDialog");		
		setContentView(R.layout.add_reservation_result_dialog);
		setTitle(getContext().getResources().getString(R.string.add_reservation_title_result));
		
		TextView tvMessage = (TextView) findViewById(R.id.textViewAddReservationMessage);
		TextView tvReservationId = (TextView) findViewById(R.id.textViewReservationId);
		tvMessage.setText("Reservation succesfully created");
		tvReservationId.setText(Integer.toString(mReservationResult.getId()));
	}

	/* (non-Javadoc)
	 * @see android.app.Dialog#onStop()
	 */
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}
    
    

}
