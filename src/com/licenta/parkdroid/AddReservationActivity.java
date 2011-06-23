/**
 * 
 */
package com.licenta.parkdroid;

import com.licenta.parkdroid.types.ParkingSpace;
import com.licenta.parkdroid.types.Reservation;
import com.licenta.parkdroid.utils.DateTimePicker;
import com.licenta.parkdroid.utils.FormatStrings;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.DialogInterface.OnCancelListener;
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
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

/**
 * @author vladucu
 *
 */
public class AddReservationActivity extends Activity implements OnClickListener {

    private static final String TAG = "AddReservationActivity";
    private static boolean DEBUG = true;
    
    public static final String INTENT_EXTRA_PARKING_SPACE = ParkDroid.PACKAGE_NAME + ".AddReservationActivity.INTENT_EXTRA_PARKING_SPACE";
    public static final String INTENT_EXTRA_RESERVATION = ParkDroid.PACKAGE_NAME + ".AddReservationActivity.INTENT_EXTRA_RESERVATION";
    public static final String INTENT_EXTRA_RETURNED_RESERVATION = ParkDroid.PACKAGE_NAME + ".AddReservationActivity.INTENT_EXTRA_RETURNED_RESERVATION";
    public static final String INTENT_EXTRA_START_TIME = ParkDroid.PACKAGE_NAME + ".AddReservationExecuteActivity.INTENT_EXTRA_START_TIME";
    public static final String INTENT_EXTRA_END_TIME = ParkDroid.PACKAGE_NAME + ".AddReservationExecuteActivity.INTENT_EXTRA_END_TIME";

    private static final int DIALOG_RESERVATION_RESULT = 1;
    
    private static StateHolder mStateHolder;
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

    //TODO validate date and time

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
            mStateHolder.setActivity(this);
        } else {
            mStateHolder = new StateHolder();
            if (getIntent().hasExtra(INTENT_EXTRA_PARKING_SPACE)) {
                mStateHolder.setParkingSpace((ParkingSpace) getIntent().getParcelableExtra(INTENT_EXTRA_PARKING_SPACE));
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
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mLoggedOutReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        
        if (mStateHolder.getIsRunning()) {
            startProgressBar(getResources().getString(R.string.add_reservation_action_label),
                    getResources().getString(R.string.add_reservation_activity_progress_bar_message));
        }
    }

	@Override
    public void onPause() {
        super.onPause();
        if (DEBUG) Log.d(TAG, "onPause()");
        stopProgressBar();

        if (isFinishing()) {
            mStateHolder.cancelTasks();
        }
    }	 

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
        
        TextView tvParkingLotName = (TextView)findViewById(R.id.addReservationActivityParkingLotName);
        TextView tvParkingLotLocation = (TextView)findViewById(R.id.addReservationActivityParkingLotAddress);
        TextView tvParkingHourPrice = (TextView)findViewById(R.id.addReservationActivityPriceValue);
        TextView tvParkingSelectedTime = (TextView) findViewById(R.id.addReservationActivityTimeValue);
        TextView tvParkingTotalCost = (TextView) findViewById(R.id.addReservationActivityTotalPriceValue);
        
        TextView tvStartTime = (TextView) findViewById(R.id.addReservationActivityStartingTime);
        View viewStartTime = findViewById(R.id.addReservationActivityStartingTimeDetails);        
        TextView tvEndTime = (TextView) findViewById(R.id.addReservationActivityEndingTime);
        View viewEndTime = findViewById(R.id.addReservationActivityEndingTimeDetails);
        
        tvParkingLotName.setText(mStateHolder.getParkingSpace().getName());
        tvParkingLotLocation.setText(mStateHolder.getParkingSpace().getAddress());
        tvParkingHourPrice.setText(Integer.toString(mStateHolder.getParkingSpace().getPrice()));
        
        viewStartTime.setOnClickListener(this);
        viewEndTime.setOnClickListener(this);
        
        //set the title of the activity
        setTitle(getTitle()+ " Reservation - " + mStateHolder.getParkingSpace().getName() );
       
        final Calendar c =  Calendar.getInstance();
        if (mStateHolder.getReservation() == null) {
            //Get the current date
            //default the start time to next hour and end time startTime+1hour
            mStartYear = mEndYear = c.get(Calendar.YEAR);
            mStartMonth = mEndMonth = c.get(Calendar.MONTH);
            mStartDay = mEndDay = c.get(Calendar.DAY_OF_MONTH);
            // get the current time        
            mStartHour = c.get(Calendar.HOUR_OF_DAY) + 1;
            mEndHour = mStartHour + 1;
            mStartMinute = mEndMinute = 0;

            updateDisplay(tvStartTime, mStartDay, mStartMonth, mStartYear, mStartHour, mStartMinute, is24h);
            updateDisplay(tvEndTime, mEndDay, mEndMonth, mEndYear, mEndHour, mEndMinute, is24h);

        }
        else {        	
        	String date = mStateHolder.getStartingTime();
        	Date date2 = null;
    		try {
				date2 = FormatStrings.DATE_FORMAT.parse(date);
			} catch (ParseException e) {
				e.printStackTrace();
			}
    
        	c.setTime((Date) date2);
        	
        	updateDisplay(tvStartTime, c.get(Calendar.DAY_OF_MONTH), c.get(Calendar.MONTH), c.get(Calendar.YEAR), c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), is24h);
        	date = mStateHolder.getEndingTime();
        	date2 = null;
    		try {
				date2 = FormatStrings.DATE_FORMAT.parse(date);
			} catch (ParseException e) {
				e.printStackTrace();
			}
    
        	c.setTime((Date) date2);
        	updateDisplay(tvEndTime, c.get(Calendar.DAY_OF_MONTH), c.get(Calendar.MONTH), c.get(Calendar.YEAR), c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), is24h);           
            //TODO aici atentie
        }
        
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
        
        //initialize dialog date & time values to the existing reservation or to something in the near future
        final Calendar calendar = Calendar.getInstance();
        	Date date = null;
        	String strDate;
        	if (textViewId == R.id.addReservationActivityStartingTime) {
        		strDate = mStateHolder.getStartingTime();
        	} else {
        		strDate = mStateHolder.getEndingTime();
        	}
	        
	        try {
				date = FormatStrings.DATE_FORMAT.parse(strDate);
			} catch (ParseException e) {
				e.printStackTrace();
			}	        
			calendar.setTime(date);
	        mDateTimePicker.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
	        mDateTimePicker.updateTime(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
        
        
        // Update demo TextViews when the "OK" button is clicked 
        ((Button) mDateTimeDialogView.findViewById(R.id.setDateTime)).setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                // TODO save reservation time values in mStateHolder
                updateDisplay((TextView)findViewById(textViewId), mDateTimePicker.get(Calendar.DAY_OF_MONTH), mDateTimePicker.get(Calendar.MONTH), 
                        mDateTimePicker.get(Calendar.YEAR), mDateTimePicker.get(Calendar.HOUR_OF_DAY),  mDateTimePicker.get(Calendar.MINUTE), 
                        mDateTimePicker.is24HourView());   
                
                mDateTimeDialog.dismiss();
            }
        });

        // Cancel the dialog when the "Cancel" button is clicked
        ((Button) mDateTimeDialogView.findViewById(R.id.cancelDialog)).setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                mDateTimeDialog.cancel();
            }
        });

        // Reset Date and Time pickers when the "Reset" button is clicked
        ((Button) mDateTimeDialogView.findViewById(R.id.resetDateTime)).setOnClickListener(new OnClickListener() {

            public void onClick(View v) {

                mDateTimePicker.reset();
            }
        });
        
        // Setup TimePicker
        mDateTimePicker.setIs24HourView(true);
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
                    mStateHolder.startTask(AddReservationActivity.this, mStateHolder.getParkingSpace(), 
                    		mStateHolder.getStartingTime(), mStateHolder.getEndingTime(), mStateHolder.getTotalTime(), mStateHolder.getCost());                    
                }
            });
        }
        else {
            btnReserveNow.setOnClickListener(new OnClickListener() {                
                @Override
                public void onClick(View v) {
                	mStateHolder.startTask(AddReservationActivity.this, mStateHolder.getParkingSpace(), 
                    		mStateHolder.getStartingTime(), mStateHolder.getEndingTime(), mStateHolder.getTotalTime(), mStateHolder.getCost()); 
                }
            });
        }        
    }

    // updates the date in the TextView
    private Calendar updateDisplay(TextView view, int day, int month, int year, int hourOfDay, int minute, boolean is24h) {
        if (DEBUG) Log.d( TAG, "updateDisplay()");
        Calendar result = Calendar.getInstance();
        result.set(year, month, day, hourOfDay, minute);
        String date = FormatStrings.DATE_FORMAT.format(result.getTime());
        String temp;
        view.setText(FormatStrings.DATE_FORMAT.format(result.getTime()));
        if (view.getId() == R.id.addReservationActivityStartingTime) {
        	temp = mStateHolder.getStartingTime();
        	mStateHolder.setStartingTime(date);        	
        } else if (view.getId() == R.id.addReservationActivityEndingTime) {
        	temp = mStateHolder.getEndingTime();
        	mStateHolder.setEndingTime(date);        	
        }        
        if (mStateHolder.getStartingTime() != null && mStateHolder.getEndingTime() != null) {
        	calculateCostAndTime();
        }
        return result;
    }   
    
    private void calculateCostAndTime() {
    	if (DEBUG) Log.d( TAG, "calculateCostAndTime()");
    	String s = mStateHolder.getStartingTime();
    	Date date1 = (Date) FormatStrings.StringToDate(mStateHolder.getStartingTime());
		Calendar cal1 = Calendar.getInstance();
		cal1.setTime(date1);
		date1 = (Date) FormatStrings.StringToDate(mStateHolder.getEndingTime());
		Calendar cal2 = Calendar.getInstance();
		cal2.setTime(date1);
		long mil1 = cal1.getTimeInMillis();
		long mil2 = cal2.getTimeInMillis();
		long diff = mil2-mil1;
		long hours = diff/(3600000);
		diff = diff - (hours*3600000);
		long minutes = diff/60000;
		TextView tvParkingSelectedTime = (TextView) findViewById(R.id.addReservationActivityTimeValue);
        TextView tvParkingTotalCost = (TextView) findViewById(R.id.addReservationActivityTotalPriceValue);
        String hour_pad = pad((int) hours);
        String minutes_pad = pad((int) minutes);
        String selectedTime = (String)hour_pad+":"+minutes_pad;
        mStateHolder.setTotalTime(selectedTime);
        
        tvParkingSelectedTime.setText(selectedTime);
        int price = mStateHolder.getParkingSpace().getPrice();
        int totalCost = (int) ((int) (hours*price) + ((minutes*price)/60));
        mStateHolder.setCost(totalCost);
        tvParkingTotalCost.setText(Integer.toString(totalCost) + "$");
		
	}

	private static String pad(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + String.valueOf(c);
    }
    
    @Override
    protected Dialog onCreateDialog(int id) {
    	if (DEBUG) Log.d(TAG, "onCreateDialog()");
        switch (id) {
            case DIALOG_RESERVATION_RESULT:
                // When the user cancels the dialog (by hitting the 'back' key),
                // we finish this activity. We don't listen to onDismiss() for this
                // action, because a device rotation will fire onDismiss(), and
                // our dialog would not be re-displayed after the rotation is
                // complete.
            	AddReservationResultDialog dlg = new AddReservationResultDialog(this, mStateHolder
                        .getReservation());
                dlg.setOnCancelListener(new OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        removeDialog(DIALOG_RESERVATION_RESULT);
                        prepareResultIntent();                        
                    }
                });
                return dlg;
        }
        return null;
    }
    
    private void prepareResultIntent() {
    	if (DEBUG) Log.d(TAG, "onCreateDialog()");
        Reservation reservation = mStateHolder.getReservation();

        Intent intent = new Intent();
        if (reservation != null) {
            intent.putExtra(INTENT_EXTRA_RETURNED_RESERVATION, reservation);
        }
        setResult(Activity.RESULT_OK, intent);
        finish();
    }
    
    public void onAddReservationComplete(Reservation result) {		
		if (DEBUG) Log.d(TAG, "onAddReservationComplete()");
		mStateHolder.setIsRunning(false);
		stopProgressBar();
		
		if (result != null) {
			mStateHolder.setReservation(result);
			showDialog(DIALOG_RESERVATION_RESULT);
		}
		else {
			setResult(Activity.RESULT_CANCELED);
			finish();
		}
		
	}    
    
    public static class ReservationTask extends AsyncTask<Void, Void, Reservation> {
        private static final String TAG = "ReservationTask";
        private static boolean DEBUG = true;
        
        private AddReservationActivity mActivity;
        private ParkDroid mParkDroid;
        private ParkingSpace mParkingSpace;
		private String mStartTime;
		private String mEndTime;
		private String mTotalTime;
		private int mCost;
		private int mUserId;
        
        public ReservationTask(AddReservationActivity activity, ParkingSpace parkingSpace, String startTime, String endTime,
        		String totalTime, int cost) {
            if (DEBUG) Log.d( TAG, "ReservationTask()");
            mActivity = activity;
			mParkingSpace = parkingSpace;
			mStartTime = startTime;
			mEndTime = endTime;
			mParkDroid = (ParkDroid) mActivity.getApplication();
			mUserId = Integer.parseInt(mParkDroid.getUserId());
			mTotalTime = totalTime;
			mCost = cost;
			
        }        
        
        @Override
        public void onPreExecute() {
            if (DEBUG) Log.d( TAG, "onPreExecute()");
            mActivity.startProgressBar(mActivity.getResources().getString(R.string.add_reservation_action_label), 
                    mActivity.getResources().getString(R.string.add_reservation_activity_progress_bar_message));
        }

        @Override
        public Reservation doInBackground(Void... params) {
            if (DEBUG) Log.d( TAG, "doInBackground()");                        
            Reservation result = (Reservation) mStateHolder.getReservation();         
			try {
				if (result == null) {
					result = mParkDroid.createReservation(mUserId, mParkingSpace, mStartTime, mEndTime, mTotalTime, mCost);
				} else {
					result.setStartTime(mStartTime);result.setEndTime(mEndTime);
		            result.setTotalTime(mTotalTime);result.setCost(mCost);
		            result = mParkDroid.updateReservation(result);
				}
			} catch (Exception e) {	}
			
			return result;			
        }        
         
        @Override
        public void onPostExecute(Reservation result) {
            if (DEBUG) Log.d( TAG, "onPostExecute()");
            if (mActivity != null) {
                mActivity.onAddReservationComplete(result);
            }
        }        

        public void setActivity(AddReservationActivity activity) {
            if (DEBUG) Log.d( TAG, "setActivity()");
            mActivity = activity;
        }
    }

    private class StateHolder {
        private static final String TAG = "StateHolder";
        private boolean DEBUG = true;
        
        private ParkingSpace mParkingSpace;
        private boolean mIsRunning;
        private ReservationTask mTask;
        private Reservation mReservation;
        private String mStartingTime;
        private String mEndingTime;
        private String mTotalTime;
        private int mCost;
        
        public StateHolder() {
            if (DEBUG) Log.d( TAG, "StateHolder()");
            mReservation = null;
            mIsRunning = false;
            mStartingTime = null;
            mEndingTime = null;
            mTotalTime = null;
            mCost = 0;
        }
        
		public void setReservation(Reservation reservation) {
            mReservation = reservation;           
            mStartingTime = mReservation.getStartTime();
            mEndingTime = mReservation.getEndTime();
            mTotalTime = mReservation.getTotalTime();
            mCost = mReservation.getCost();            
        }
        
        public Reservation getReservation() {
            return mReservation;
        }

        public void startTask(AddReservationActivity activity, ParkingSpace parkingSpace, String startTime, String endTime,
        		String totalTime, int cost) {
            if (DEBUG) Log.d( TAG, "startTask()");
            mIsRunning = true;
            mTask = new ReservationTask(activity, parkingSpace, startTime, endTime, totalTime, cost);
            mTask.execute();            
        }
        
        public void setParkingSpace(ParkingSpace parkingSpace) {
            if (DEBUG) Log.d( TAG, "setParkingLot()");
            mParkingSpace = parkingSpace;       
        }
        
        public ParkingSpace getParkingSpace() {
            if (DEBUG) Log.d( TAG, "getParkingSpace()");
            return mParkingSpace;
        }
        
        public void setActivity(AddReservationActivity activity) {
            if (DEBUG) Log.d( TAG, "setActivity()");
            if (mTask != null) {
                mTask.setActivity(activity);
            }
        }
        
        public void setStartingTime(String time) {
        	mStartingTime = time;
        }
        
        public String getStartingTime() {
        	return mStartingTime;
        }
        
        public void setEndingTime(String time) {
        	mEndingTime = time;
        }
        
        public String getEndingTime() {
        	return mEndingTime;
        }

        public String getTotalTime() {
			return mTotalTime;
		}

        public void setTotalTime(String totalTime) {
        	mTotalTime = totalTime;
        }
        
		public int getCost() {
			return mCost;
		}
		
		public void setCost(int cost) {
			mCost = cost;
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
