/**
 * 
 */
package com.licenta.parkdroid;

import com.licenta.park.types.AddReservationResult;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

/**
 * @author vladucu
 *
 */
public class AddReservationResultDialog extends Dialog {
	
	private static final String TAG = "AddReservationResultDialog";
    private static final boolean DEBUG = true;
    
    private AddReservationResult mAddReservationResult;
    
    public AddReservationResultDialog(Context context, AddReservationResult result) {
    	super(context);
    	
    	if (DEBUG) Log.d(TAG, "AddReservationResultDialog");
    	mAddReservationResult = result;
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
