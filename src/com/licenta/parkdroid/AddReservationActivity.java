/**
 * 
 */
package com.licenta.parkdroid;

import com.licenta.park.types.ParkingLot;
import com.licenta.park.types.Reservation;
import com.licenta.parkdroid.widgets.DateTimePicker;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Calendar;

/**
 * @author vladucu
 *
 */
public class AddReservationActivity extends Activity implements OnClickListener {

    private static final String TAG = "AddReservationActivity";
    private static boolean DEBUG = true;
    
    public static final String INTENT_EXTRA_PARKING_LOT = ParkDroid.PACKAGE_NAME + ".AddReservationActivity.INTENT_EXTRA_PARKING_LOT";
    public static final String INTENT_EXTRA_RESERVATION = ParkDroid.PACKAGE_NAME + ".AddReservationActivity.INTENT_EXTRA_RESERVATION";
    public static final String INTENT_EXTRA_RETURNED_RESERVATION = ParkDroid.PACKAGE_NAME + ".AddReservationActivity.INTENT_EXTRA_RETURNED_RESERVATION";

    private static final int DIALOG_RESERVATION_RESULT = 1;
    
    private StateHolder mStateHolder;
    private Handler mHandler;
    private ProgressDialog mDlgProgress;
    
    private int mStartYear, mStartMonth, mStartDay;    
    private int mStartHour, mStartMinute;
    private int mEndYear, mEndMonth, mEndDay;
    private int mEndHour, mEndMinute;
    
    boolean is24h;
       
    private BroadcastReceiver mLoggedOutReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (DEBUG) Log.d(TAG, "onReceive: " + intent);
            finish();
        }
    };

    //TODO how will reservation be handled? passed a reservation parcelable type ?
    
    /* (non-Javadoc)
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        if (DEBUG) Log.d( TAG, "onCreate()");
        
        setContentView(R.layout.add_reservation);
        registerReceiver(mLoggedOutReceiver, new IntentFilter(ParkDroid.INTENT_ACTION_LOGGED_OUT));
        
        
        // Check is system is set to use 24h time (this doesn't seem to work as expected though)
        final String timeS = android.provider.Settings.System.getString(getContentResolver(), android.provider.Settings.System.TIME_12_24);
        is24h = !(timeS == null || timeS.equals("12"));
        
        mHandler = new Handler();        
        Object retained = getLastNonConfigurationInstance();
        if (retained != null && retained instanceof StateHolder) {
            mStateHolder = (StateHolder) retained;
            //mStateHolder.setActivity(this);
        } else {
            mStateHolder = new StateHolder();
            if (getIntent().hasExtra(INTENT_EXTRA_PARKING_LOT)) {
                mStateHolder.setParkingLot((ParkingLot) getIntent().getParcelableExtra(INTENT_EXTRA_PARKING_LOT));
            } else {
                Log.e(TAG, "AddReservationActivity must be given a parking lot parcel as intent extras.");
                finish();
                return;
            }
            if (getIntent().hasExtra(INTENT_EXTRA_RESERVATION)) {
                mStateHolder.setReservation((Reservation) getIntent().getParcelableExtra(INTENT_EXTRA_RESERVATION));
            }
        }
        
        ensureUi();
    }  
    
    
    /* (non-Javadoc)
     * @see android.app.Activity#onDestroy()
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mLoggedOutReceiver);
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onResume()
     */
    @Override
    protected void onResume() {
        super.onResume();
        
        if (mStateHolder.getIsRunning()) {
            startProgressBar(getResources().getString(R.string.add_reservation_action_label),
                    getResources().getString(R.string.add_reservation_activity_progress_bar_message));
        }
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onRetainNonConfigurationInstance()
     */
    @Override
    public Object onRetainNonConfigurationInstance() {
        //mStateHolder.setActivity(null);
        return mStateHolder;
    }

    private void startProgressBar(String title, String message) {
        if (DEBUG) Log.d( TAG, "startProgressBar()");
        if (mDlgProgress == null) {
            mDlgProgress = ProgressDialog.show(this, title, message);
        }
        mDlgProgress.show();
    }
    
    private void stopProgressBar() {
        if (DEBUG) Log.d( TAG, "stopProgressBar()");
        if (mDlgProgress != null) {
            mDlgProgress.dismiss();
            mDlgProgress = null;
        }
    }
    
    private void ensureUi() {
        if (DEBUG) Log.d( TAG, "ensureUi()");
        
        TextView tvParkingLotName = (TextView)findViewById(R.id.addReservationActivityParkingLotName);
        TextView tvParkingLotLocation = (TextView)findViewById(R.id.addReservationActivityParkingLotAddress);
        TextView tvParkingHourPrice = (TextView)findViewById(R.id.addReservationActivityPriceValue);
        TextView tvParkingSelectedTime = (TextView) findViewById(R.id.addReservationActivityTimeValue);
        TextView tvParkingTotalPrice = (TextView) findViewById(R.id.addReservationActivityTotalPriceValue);
        
        TextView tvStartTime = (TextView) findViewById(R.id.addReservationActivityStartingTime);
        View viewStartTime = findViewById(R.id.addReservationActivityStartingTimeDetails);        
        TextView tvEndTime = (TextView) findViewById(R.id.addReservationActivityEndingTime);
        View viewEndTime = findViewById(R.id.addReservationActivityEndingTimeDetails);
        
        tvParkingLotName.setText(mStateHolder.getParkingLot().getName());
        tvParkingLotLocation.setText(mStateHolder.getParkingLot().getAddress());
        tvParkingHourPrice.setText(mStateHolder.getParkingLot().getPrice()+"/hour");
        tvParkingSelectedTime.setText("3:35");
        tvParkingTotalPrice.setText("250");
        
        viewStartTime.setOnClickListener(this);
        viewEndTime.setOnClickListener(this);
        
        //set the title of the activity
        setTitle(getTitle()+ " Reservation - " + mStateHolder.getParkingLot().getName() );
       
        if (mStateHolder.getReservation() == null) {
            //Get the current date
            final Calendar c =  Calendar.getInstance();
            mStartYear = mEndYear = c.get(Calendar.YEAR);
            mStartMonth = mEndMonth = c.get(Calendar.MONTH);
            mStartDay = mEndDay = c.get(Calendar.DAY_OF_MONTH);
            // get the current time        
            mStartHour = mEndHour = c.get(Calendar.HOUR_OF_DAY);
            mStartMinute = mEndMinute = c.get(Calendar.MINUTE);

            updateDisplay(tvStartTime, mStartDay, mStartMonth, mStartYear, mStartHour, mStartMinute, is24h);
            updateDisplay(tvEndTime, mEndDay, mEndMonth, mEndYear, mEndHour, mEndMinute, is24h);

        }
        else {
            // TODO set reservation time values in mStateHolder
            /*mPickStartDate.setText(new String.)
            final Calendar c = (Calendar) mStateHolder.getReservation().getStartTime();
            mPickStartDate.setText(mStateHolder.getReservation().getStartTime());
            //TODO aici atentie
*/        }
        
        ensureUiReserveNowButton();
    
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.addReservationActivityStartingTimeDetails) {
            if (DEBUG) Log.d( TAG, "onClick() + Time");
            showDateTimeDialog(R.id.addReservationActivityStartingTime);
        } else if (view.getId() == R.id.addReservationActivityEndingTimeDetails) {
            if (DEBUG) Log.d( TAG, "onClick() + Date");
            showDateTimeDialog(R.id.addReservationActivityEndingTime);
        }        
    }
    
    private void showDateTimeDialog(final int textViewId) {
     // Create the dialog
        final Dialog mDateTimeDialog = new Dialog(this);
        // Inflate the root layout
        final RelativeLayout mDateTimeDialogView = (RelativeLayout) getLayoutInflater().inflate(R.layout.date_time_dialog, null);
        // Grab widget instance
        final DateTimePicker mDateTimePicker = (DateTimePicker) mDateTimeDialogView.findViewById(R.id.dateTimePicker);        
        
        // Update demo TextViews when the "OK" button is clicked 
        ((Button) mDateTimeDialogView.findViewById(R.id.setDateTime)).setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                // TODO save reservation time values in mStateHolder
                updateDisplay((TextView)findViewById(textViewId), mDateTimePicker.get(Calendar.DAY_OF_MONTH), mDateTimePicker.get(Calendar.MONTH), 
                        mDateTimePicker.get(Calendar.YEAR), mDateTimePicker.get(Calendar.HOUR),  mDateTimePicker.get(Calendar.MINUTE), 
                        mDateTimePicker.is24HourView());   
                
                mDateTimeDialog.dismiss();
            }
        });

        // Cancel the dialog when the "Cancel" button is clicked
        ((Button) mDateTimeDialogView.findViewById(R.id.cancelDialog)).setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                mDateTimeDialog.cancel();
            }
        });

        // Reset Date and Time pickers when the "Reset" button is clicked
        ((Button) mDateTimeDialogView.findViewById(R.id.resetDateTime)).setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                mDateTimePicker.reset();
            }
        });
        
        // Setup TimePicker
        mDateTimePicker.setIs24HourView(is24h);
        // No title on the dialog window
        mDateTimeDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        // Set the dialog content view
        mDateTimeDialog.setContentView(mDateTimeDialogView);
        // Display the dialog
        mDateTimeDialog.show();
    }
    
    private void ensureUiReserveNowButton() {
        if (DEBUG) Log.d( TAG, "ensureUiReserveNowButton()");
        Button btnReserveNow = (Button)findViewById(R.id.addReservationActivityButtonReserveNow);
        btnReserveNow.setEnabled(true);
        if (mStateHolder.getReservation() != null) {
            btnReserveNow.setText(R.string.reservation_activity_extend_button);
            btnReserveNow.setOnClickListener(new OnClickListener() {                
                @Override
                public void onClick(View v) {
               /*     Toast.makeText(AddReservationActivity.this, "Extending reservation", Toast.LENGTH_LONG);
                    mStateHolder.startTask(AddReservationActivity.this, mStateHolder.getParkingLot().getId());                    */
                }
            });
        }
        else {
            btnReserveNow.setOnClickListener(new OnClickListener() {                
                @Override
                public void onClick(View v) {
                    /*Toast.makeText(AddReservationActivity.this, "Starting reservation", Toast.LENGTH_LONG);
                    mStateHolder.startTask(AddReservationActivity.this, mStateHolder.getParkingLot().getId());*/
                	makeReservation();
                }
            });
        }        
    }

    // updates the date in the TextView
    private void updateDisplay(TextView view, int day, int month, int year, int hour, int minute, boolean is24h) {
        if (DEBUG) Log.d( TAG, "updateDisplay()");
        view.setText(
                new StringBuilder()                        
                        .append(pad(hour)).append(":")
                        .append(pad(minute)).append(", ")
                        .append(day).append("-")
                        // Month is 0 based so add 1
                        .append(month + 1).append("-")                        
                        .append(year).append(" "));
    }   
    
    private static String pad(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + String.valueOf(c);
    }
    
    private void makeReservation() {
    	Intent intent = new Intent(AddReservationActivity.this, AddReservationExecuteActivity.class);
    	intent.putExtra(AddReservationExecuteActivity.INTENT_EXTRA_PARKING_LOT_ID, mStateHolder.getParkingLot().getId());
    	startActivityForResult(intent, DIALOG_RESERVATION_RESULT);
    }
    
    private void onReservationComplete(Reservation result, Exception ex) {
        mStateHolder.setIsRunning(false);
        stopProgressBar();

        if (result != null) {
            mStateHolder.setReservation(result);
            showDialog(DIALOG_RESERVATION_RESULT);
        } else {
            Toast.makeText(this, "Reservation error", Toast.LENGTH_LONG);
            setResult(Activity.RESULT_CANCELED);
            finish();
        }
    }
    
  /*  private static class ReservationTask extends AsyncTask<Void, Void, Reservation> {
        private static final String TAG = "ReservationTask";
        private static boolean DEBUG = true;
        
        private AddReservationActivity mActivity;
        private String mParkingLotId;

        public void setActivity(AddReservationActivity activity) {
            if (DEBUG) Log.d( TAG, "setActivity()");
            mActivity = activity;
        }
        
        public ReservationTask(AddReservationActivity activity, String parkingLotId) {
            if (DEBUG) Log.d( TAG, "ReservationTask()");
            mActivity = activity;
            mParkingLotId = parkingLotId;
        }        
        
         (non-Javadoc)
         * @see android.os.AsyncTask#onPreExecute()
         
        @Override
        protected void onPreExecute() {
            if (DEBUG) Log.d( TAG, "onPreExecute()");
            mActivity.startProgressBar(mActivity.getResources().getString(R.string.add_reservation_action_label), 
                    mActivity.getResources().getString(R.string.add_reservation_activity_progress_bar_message));
        }

        @Override
        protected Reservation doInBackground(Void... params) {
            if (DEBUG) Log.d( TAG, "doInBackground()");
            // TODO Auto-generated method stub
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return new Reservation();
        }
        
         (non-Javadoc)
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         
        @Override
        protected void onPostExecute(Reservation result) {
            if (DEBUG) Log.d( TAG, "onPostExecute()");
            if (mActivity != null) {
                mActivity.onReservationComplete(result, null);
            }
        }
        
    }
*/
    private class StateHolder {
        private static final String TAG = "AddReservationTask";
        private boolean DEBUG = true;
        
        private ParkingLot mParkingLot;
        private boolean mIsRunning;
        //private ReservationTask mTask;
        private Reservation mReservation;
        private String mStartingTime;
        private String mEndingTime;
        
        public StateHolder() {
            if (DEBUG) Log.d( TAG, "StateHolder()");
            mReservation = null;
            mIsRunning = false;
        }
        
        public void setReservation(Reservation reservation) {
            mReservation = reservation;            
        }
        
        public Reservation getReservation() {
            return mReservation;
        }

      /*  public void startTask(AddReservationActivity activity, String parkingLotId) {
            if (DEBUG) Log.d( TAG, "startTask()");
            mIsRunning = true;
            mTask = new ReservationTask(activity, parkingLotId);
            mTask.execute();
            
        }*/
        
        public void setParkingLot(ParkingLot parkignLot) {
            if (DEBUG) Log.d( TAG, "setParkingLot()");
            mParkingLot = parkignLot;       
        }
        
        public ParkingLot getParkingLot() {
            if (DEBUG) Log.d( TAG, "getParkingLot()");
            return mParkingLot;
        }
        
      /*  public void setActivity(AddReservationActivity activity) {
            if (DEBUG) Log.d( TAG, "setActivity()");
            if (mTask != null) {
                mTask.setActivity(activity);
            }
        }*/
        
        public void setStartingTime(String time) {
            mStartingTime = time;
        }
        
        public boolean getIsRunning() {
            if (DEBUG) Log.d( TAG, "getIsRunning()");
            return mIsRunning;
        }

        public void setIsRunning(boolean isRunning) {
            if (DEBUG) Log.d( TAG, "setIsRunning()");
            mIsRunning = isRunning;
        }
        
     /*   public void cancelTasks() {
            if (DEBUG) Log.d( TAG, "cancelTasks()");
            if (mTask !=null && mIsRunning) {
                mTask.setActivity(null);
                mTask.cancel(true);
            }            
        }*/
    }
}
