/**
 * 
 */
package com.licenta.parkdroid;

import com.licenta.park.types.ParkingLot;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import java.util.Calendar;

/**
 * @author vladucu
 *
 */
public class AddReservationActivity extends Activity {

    private static final String TAG = "AddReservationActivity";
    private static boolean DEBUG = true;
    
    public static final String INTENT_EXTRA_PARKING_LOT = ParkDroid.PACKAGE_NAME + ".AddReservationActivity.INTENT_EXTRA_PARKING_LOT";
    public static final String INTENT_EXTRA_RETURNED_RESERVATION = ParkDroid.PACKAGE_NAME + ".AddReservationActivity.INTENT_EXTRA_RETURNED_RESERVATION";
    
    private static final int DIALOG_SELECT_DATE = 1;
    
    private StateHolder mStateHolder;
    private Handler mHandler;
    private int mYear, mMonth, mDay;
    TextView mDateDisplay;
    
    /* (non-Javadoc)
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        
        if (DEBUG) Log.d( TAG, "onCreate()");
        setContentView(R.layout.add_reservation);
        
        mHandler = new Handler();        
        StateHolder stateHolder = (StateHolder)getLastNonConfigurationInstance();
        if (stateHolder != null) {
            mStateHolder = stateHolder;            
        } else {
            mStateHolder = new StateHolder();
            if (getIntent().hasExtra(INTENT_EXTRA_PARKING_LOT)) {
                mStateHolder.setParkingLot((ParkingLot) getIntent().getParcelableExtra(INTENT_EXTRA_PARKING_LOT));
            } else {
                Log.e(TAG, "AddTodoActivity must be given a venue parcel as intent extras.");
                finish();
                return;
            }
        }
        
        ensureUi();
    }  
    
    private void ensureUi() {
        if (DEBUG) Log.d( TAG, "ensureUi()");
        TextView tvParkingLotName = (TextView)findViewById(R.id.parkingLotName);
        TextView tvParkingLotLocation = (TextView)findViewById(R.id.parkingLotLocation);
        mDateDisplay = (TextView)findViewById(R.id.dateDisplay);
        TextView tvTimeDisplay = (TextView)findViewById(R.id.timeDisplay);
        Button btnPickDate = (Button)findViewById(R.id.pickDate);
        
        tvParkingLotName.setText(mStateHolder.getParkingLot().getName());
        tvParkingLotLocation.setText(mStateHolder.getParkingLot().getAddress());
        
        // add a click listener to the date picker button
        btnPickDate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(DIALOG_SELECT_DATE);
            }
        });

        //Get the current date
        final Calendar c =  Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        updateUi();       
        
    }
    
    // updates the date in the TextView
    private void updateUi() {
        if (DEBUG) Log.d( TAG, "updateUi()");
        mDateDisplay.setText(
                new StringBuilder()
                        // Month is 0 based so add 1
                        .append(mDay).append("-")
                        .append(mMonth + 1).append("-")                        
                        .append(mYear).append(" "));
    }   
    
    // the callback received when the user "sets" the date in the dialog
    private DatePickerDialog.OnDateSetListener mDateSetListener =
            new DatePickerDialog.OnDateSetListener() {

                public void onDateSet(DatePicker view, int year, 
                                      int monthOfYear, int dayOfMonth) {
                    mYear = year;
                    mMonth = monthOfYear;
                    mDay = dayOfMonth;
                    updateUi();
                }
            };
    
    /* (non-Javadoc)
     * @see android.app.Activity#onCreateDialog(int)
     */
    @Override
    protected Dialog onCreateDialog(int id) {
        if (DEBUG) Log.d( TAG, "onCreateDialog()");
        switch (id) {
            case DIALOG_SELECT_DATE:
                return new DatePickerDialog(this, mDateSetListener, mYear, mMonth, mDay);               
        }
        return null;
    }



    private class StateHolder {
        
        private ParkingLot mParkingLot;
        
        public StateHolder() {
            
        }

        public void setParkingLot(ParkingLot parkignLot) {
            mParkingLot = parkignLot;            
        }
        
        public ParkingLot getParkingLot() {
            return mParkingLot;
        }
    }

}
