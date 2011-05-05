/**
 * 
 */
package com.licenta.parkdroid;

import com.licenta.park.types.ParkingLot;
import com.licenta.park.types.Reservation;
import com.licenta.park.types.ReservationResult;
import com.licenta.park.utils.FormatStrings;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * @author vladucu
 *
 */
public class AddReservationActivity extends Activity {

    private static final String TAG = "AddReservationActivity";
    private static boolean DEBUG = true;
    
    public static final String INTENT_EXTRA_PARKING_LOT = ParkDroid.PACKAGE_NAME + ".AddReservationActivity.INTENT_EXTRA_PARKING_LOT";
    public static final String INTENT_EXTRA_RESERVATION = ParkDroid.PACKAGE_NAME + ".AddReservationActivity.INTENT_EXTRA_RESERVATION";
    public static final String INTENT_EXTRA_RETURNED_RESERVATION = ParkDroid.PACKAGE_NAME + ".AddReservationActivity.INTENT_EXTRA_RETURNED_RESERVATION";
    
    private static final int DIALOG_SELECT_START_DATE = 0;
    private static final int DIALOG_SELECT_START_TIME = 1;
    private static final int DIALOG_SELECT_END_DATE = 2;
    private static final int DIALOG_SELECT_END_TIME = 3;
    private static final int DIALOG_RESERVATION_RESULT = 4;
    
    private StateHolder mStateHolder;
    private Handler mHandler;
    private ProgressDialog mDlgProgress;
    
    private int mStartYear, mStartMonth, mStartDay;    
    private int mStartHour, mStartMinute;
    private int mEndYear, mEndMonth, mEndDay;
    private int mEndHour, mEndMinute;
    Button mPickStartDate, mPickStartTime, mPickEndDate, mPickEndTime;
       
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
        
        mHandler = new Handler();        
        Object retained = getLastNonConfigurationInstance();
        if (retained != null && retained instanceof StateHolder) {
            mStateHolder = (StateHolder) retained;
            mStateHolder.setActivity(this);
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
     * @see android.app.Activity#onPause()
     */
    @Override
    protected void onPause() {
        super.onPause();
        
        stopProgressBar();

        if (isFinishing()) {
            mStateHolder.cancelTasks();
        }
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
        mStateHolder.setActivity(null);
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
        TextView tvParkingLotName = (TextView)findViewById(R.id.parkingLotName);
        TextView tvParkingLotLocation = (TextView)findViewById(R.id.parkingLotLocation);
        TextView tvParkingPrice = (TextView)findViewById(R.id.parkingPrice);
        mPickStartDate = (Button)findViewById(R.id.pickStartDate);
        mPickStartTime = (Button)findViewById(R.id.pickStartTime);
        mPickEndDate = (Button)findViewById(R.id.pickEndDate);
        mPickEndTime = (Button)findViewById(R.id.pickEndTime);
        
        tvParkingLotName.setText(mStateHolder.getParkingLot().getName());
        tvParkingLotLocation.setText(mStateHolder.getParkingLot().getAddress());
        tvParkingPrice.setText(mStateHolder.getParkingLot().getPrice()+"/hour");
        //set the title of the activity
        setTitle(getTitle()+ " Reservation - " + mStateHolder.getParkingLot().getName() );
        // add a click listener to the date picker button
        mPickStartDate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(DIALOG_SELECT_START_DATE);
            }
        });
        
        // add a click listener to the time picker button
        mPickStartTime.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(DIALOG_SELECT_START_TIME);
            }
        });
        
     // add a click listener to the date picker button
        mPickEndDate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(DIALOG_SELECT_END_DATE);
            }
        });
        
        // add a click listener to the time picker button
        mPickEndTime.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(DIALOG_SELECT_END_TIME);
            }
        });

        if (mStateHolder.getReservation() == null) {
            //Get the current date
            final Calendar c =  Calendar.getInstance();
            mStartYear = mEndYear = c.get(Calendar.YEAR);
            mStartMonth = mEndMonth = c.get(Calendar.MONTH);
            mStartDay = mEndDay = c.get(Calendar.DAY_OF_MONTH);
            // get the current time        
            mStartHour = mEndHour = c.get(Calendar.HOUR_OF_DAY);
            mStartMinute = mEndMinute = c.get(Calendar.MINUTE);
            updateDateDisplay(mPickStartDate, mStartDay, mStartMonth, mStartYear);
            updateDateDisplay(mPickEndDate, mEndDay, mEndMonth, mEndYear);       
            updateTimeDisplay(mPickStartTime, mStartHour, mStartMinute);
            updateTimeDisplay(mPickEndTime, mEndHour, mEndMinute);  
        }
        else {
           /* mPickStartDate.setText(new String.)
            final Calendar c = (Calendar) mStateHolder.getReservation().getStartTime();
            mPickStartDate.setText(mStateHolder.getReservation().getStartTime());
            //TODO aici atentie
*/        }
        
        ensureUiReserveNowButton();
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
                    Toast.makeText(AddReservationActivity.this, "Extending reservation", Toast.LENGTH_LONG);
                    mStateHolder.startTask(AddReservationActivity.this, mStateHolder.getParkingLot().getId());
                    
                }
            });
        }
        else {
            btnReserveNow.setOnClickListener(new OnClickListener() {
                
                @Override
                public void onClick(View v) {
                    Toast.makeText(AddReservationActivity.this, "Starting reservation", Toast.LENGTH_LONG);
                    mStateHolder.startTask(AddReservationActivity.this, mStateHolder.getParkingLot().getId());
                    
                }
            });
        }
        
    }

    // updates the date in the TextView
    private void updateDateDisplay(Button date, int day, int month, int year) {
        if (DEBUG) Log.d( TAG, "updateDateDisplay()");
        if (DEBUG) Log.d( TAG, "updateUi()");
        date.setText(
                new StringBuilder()
                        // Month is 0 based so add 1
                        .append(day).append("-")
                        .append(month + 1).append("-")                        
                        .append(year).append(" "));
    }   
    
    // updates the time we display in the View
    private void updateTimeDisplay(Button time, int hour, int minute) {
        if (DEBUG) Log.d( TAG, "updateTimeDisplay()");
        time.setText(
            new StringBuilder()
                    .append(pad(hour)).append(":")
                    .append(pad(minute)));
    }
    
    private static String pad(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + String.valueOf(c);
    }
    
    // the callback received when the user "sets" the date in the dialog
    private DatePickerDialog.OnDateSetListener mStartDateSetListener =
            new DatePickerDialog.OnDateSetListener() {

                public void onDateSet(DatePicker view, int year, 
                                      int monthOfYear, int dayOfMonth) {
                    mStartYear = year;
                    mStartMonth = monthOfYear;
                    mStartDay = dayOfMonth;
                    updateDateDisplay(mPickStartDate, mStartYear, mStartMonth, mStartDay);
                }
            };
           
    // the callback received when the user "sets" the time in the dialog
    private TimePickerDialog.OnTimeSetListener mStartTimeSetListener =
        new TimePickerDialog.OnTimeSetListener() {
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                mStartHour = hourOfDay;
                mStartMinute = minute;
                updateTimeDisplay(mPickStartTime, mStartHour, mStartMinute);
            }
        };
        
    // the callback received when the user "sets" the date in the dialog
    private DatePickerDialog.OnDateSetListener mEndDateSetListener =
            new DatePickerDialog.OnDateSetListener() {

                public void onDateSet(DatePicker view, int year, 
                                      int monthOfYear, int dayOfMonth) {
                    mEndYear = year;
                    mEndMonth = monthOfYear;
                    mEndDay = dayOfMonth;
                    updateDateDisplay(mPickEndDate, mEndYear, mEndMonth, mEndDay);
                }
            };
           
    // the callback received when the user "sets" the time in the dialog
    private TimePickerDialog.OnTimeSetListener mEndTimeSetListener =
        new TimePickerDialog.OnTimeSetListener() {
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                mEndHour = hourOfDay;
                mEndMinute = minute;
                updateTimeDisplay(mPickEndTime, mEndHour, mEndMinute);
            }
        };
    
    /* (non-Javadoc)
     * @see android.app.Activity#onCreateDialog(int)
     */
    @Override
    protected Dialog onCreateDialog(int id) {
        if (DEBUG) Log.d( TAG, "onCreateDialog()");
        switch (id) {
            case DIALOG_SELECT_START_DATE:
                return new DatePickerDialog(this, mStartDateSetListener, mStartYear, mStartMonth, mStartDay); 
            case DIALOG_SELECT_START_TIME:
                return new TimePickerDialog(this, mStartTimeSetListener, mStartHour, mStartMinute, true);
            case DIALOG_SELECT_END_DATE:
                return new DatePickerDialog(this, mEndDateSetListener, mEndYear, mEndMonth, mEndDay); 
            case DIALOG_SELECT_END_TIME:
                return new TimePickerDialog(this, mEndTimeSetListener, mEndHour, mEndMinute, true);
            case DIALOG_RESERVATION_RESULT:
                //TODO add reservationResult intent 
        }
        return null;
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
    
    private static class ReservationTask extends AsyncTask<Void, Void, Reservation> {
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
        
        /* (non-Javadoc)
         * @see android.os.AsyncTask#onPreExecute()
         */
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
        
        /* (non-Javadoc)
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        protected void onPostExecute(Reservation result) {
            if (DEBUG) Log.d( TAG, "onPostExecute()");
            if (mActivity != null) {
                mActivity.onReservationComplete(result, null);
            }
        }
        
    }

    private class StateHolder {
        private static final String TAG = "ReservationTask";
        private boolean DEBUG = true;
        
        private ParkingLot mParkingLot;
        private boolean mIsRunning;
        private ReservationTask mTask;
        private Reservation mReservation;
        
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

        public void startTask(AddReservationActivity activity, String parkingLotId) {
            if (DEBUG) Log.d( TAG, "startTask()");
            mIsRunning = true;
            mTask = new ReservationTask(activity, parkingLotId);
            mTask.execute();
            
        }
        
        public void setParkingLot(ParkingLot parkignLot) {
            if (DEBUG) Log.d( TAG, "setParkingLot()");
            mParkingLot = parkignLot;            
        }
        
        public ParkingLot getParkingLot() {
            if (DEBUG) Log.d( TAG, "getParkingLot()");
            return mParkingLot;
        }
        
        public void setActivity(AddReservationActivity activity) {
            if (DEBUG) Log.d( TAG, "setActivity()");
            if (mTask != null) {
                mTask.setActivity(activity);
            }
        }
        
        public boolean getIsRunning() {
            if (DEBUG) Log.d( TAG, "getIsRunning()");
            return mIsRunning;
        }

        public void setIsRunning(boolean isRunning) {
            if (DEBUG) Log.d( TAG, "setIsRunning()");
            mIsRunning = isRunning;
        }
        
        public void cancelTasks() {
            if (DEBUG) Log.d( TAG, "cancelTasks()");
            if (mTask !=null && mIsRunning) {
                mTask.setActivity(null);
                mTask.cancel(true);
            }            
        }
    }
}
