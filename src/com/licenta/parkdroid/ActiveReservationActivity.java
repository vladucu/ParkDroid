package com.licenta.parkdroid;

import com.licenta.park.Park;
import com.licenta.park.types.Reservation;
import android.app.Activity;
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
import android.widget.TextView;

/**
 * @author vladucu
 *
 */
public class ActiveReservationActivity extends Activity {

    public static final String TAG = "ActiveReservationActivity";
    public static final boolean DEBUG = true;
    
    public static final String INTENT_EXTRA_RESERVATION = ParkDroid.PACKAGE_NAME + ".ActiveReservationActivity.INTENT_EXTRA_RESERVATION";
    private static final int RESULT_CODE_ACTIVITY_EXTEND_RESERVATION = 1;
    
    private Handler mHandler;
    private StateHolder mStateHolder;
    private ProgressDialog mDlgProgress;
    
    private BroadcastReceiver mLoggedOutReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (DEBUG) Log.d(TAG, "onReceive: " + intent);
            finish();
        }
    };
	//TODO add delete confirmation dialog box
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        if (DEBUG) Log.d(TAG, "onCreate()");
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        registerReceiver(mLoggedOutReceiver, new IntentFilter(ParkDroid.INTENT_ACTION_LOGGED_OUT));
        setContentView(R.layout.reservation_activity);
        
        mHandler = new Handler();
        
        Object retained = getLastNonConfigurationInstance();
        if (retained != null && retained instanceof StateHolder) {
        	mStateHolder = (StateHolder) retained;
            mStateHolder.setActivity(this);
        } else {
            mStateHolder = new StateHolder();
            if (getIntent().hasExtra(INTENT_EXTRA_RESERVATION)) {
                mStateHolder.setReservation((Reservation) getIntent().getParcelableExtra(INTENT_EXTRA_RESERVATION));
            }
            else {
                if (DEBUG) Log.d(TAG, "ActiveReservationActivity needs an reservation parcelable extra");
                finish();
                return;
            }
        }
        
        ensureUi();
    }
    
    private void ensureUi() {
        if (DEBUG) Log.d(TAG, "ensureUi()");
        TextView tvReservationActivityParkingLotName = (TextView) findViewById(R.id.reservationActivityParkingLotName);
        TextView tvReservationActivityParkingLotAddress = (TextView) findViewById(R.id.reservationActivityParkingLotAddress);
   
        TextView tvReservationActivityId = (TextView) findViewById(R.id.reservationActivityId); 
        View viewId = findViewById(R.id.reservationActivityIdDetails);
        
        //View viewNavigation = findViewById(R.id.reservationActivityNavigationDetails);
        
        Button btnExtend = (Button)findViewById(R.id.reservationActivityButtonExtend);
        Button btnCancel = (Button)findViewById(R.id.reservationActivityButtonCancel);
        
        final Reservation reservation = mStateHolder.getReservation();
        tvReservationActivityParkingLotName.setText(reservation.getParkingSpace().getName());
        tvReservationActivityParkingLotAddress.setText(reservation.getParkingSpace().getAddress());        
   
        
        tvReservationActivityId.setText(Integer.toString(reservation.getId()));
        viewId.setVisibility(View.VISIBLE);
        
        updateView();
 /*        
         * We could put a link to get navigation to the parking lot,
         * but the Navigation App is still beta, not official and works only i US
         
        viewNavigation.setVisibility(View.GONE);        
        viewNavigation.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                //starting navigation intent
                if (DEBUG) Log.d(TAG, "Starting navigation LAT=" + reservation.getParkingSpace().getGeoLat()
                        + "LONG=" + reservation.getParkingSpace().getGeoLong());
                Uri navigationUri = Uri.parse("google.navigation:ll=" + reservation.getParkingSpace().getGeoLat() + "," +
                        reservation.getParkingSpace().getGeoLat());                        
                Intent intent = new Intent(Intent.ACTION_VIEW, navigationUri);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW, 
                        Uri.parse("http://maps.google.com/maps?saddr="+reservation.getParkingLot().getGeolat()+"&daddr="+reservation.getParkingLot().getGeolong()));
                        startActivity(intent);
                
            }
        });*/
        
        btnExtend.setEnabled(true);
        btnExtend.setText(R.string.reservation_activity_extend_button);
        btnExtend.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                if (DEBUG) Log.d(TAG, "ensureUI() button extend clicked");
                 
                Intent intent = new Intent(ActiveReservationActivity.this, AddReservationActivity.class);
                intent.putExtra(AddReservationActivity.INTENT_EXTRA_PARKING_SPACE, reservation.getParkingSpace());
                intent.putExtra(AddReservationActivity.INTENT_EXTRA_RESERVATION, reservation);
                startActivityForResult(intent, RESULT_CODE_ACTIVITY_EXTEND_RESERVATION);       
            }
        });
        btnCancel.setEnabled(true);
        btnCancel.setText(R.string.reservation_activity_cancel_button);
        btnCancel.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                
                if (DEBUG) Log.d(TAG, "ensureUI() button cancel clicked");
                mStateHolder.startDeleteTask(ActiveReservationActivity.this, reservation.getUser().getId(), reservation.getId());
            }
        });        
    }    
   
    public void updateView() {
    	if (DEBUG) Log.d(TAG, "updateView()");
    	TextView tvReservationActivityStartingTime = (TextView) findViewById(R.id.reservationActivityStartingTime);
        TextView tvReservationActivityEndingTime = (TextView) findViewById(R.id.reservationActivityEndingTime);
        
        TextView tvReservationActivityPrice = (TextView) findViewById(R.id.reservationActivityPriceValue);
        TextView tvReservationActivityTime = (TextView) findViewById(R.id.reservationActivityTimeValue);
        TextView tvReservationActivityCosts = (TextView) findViewById(R.id.reservationActivityTotalPriceValue);
        
        final Reservation reservation = mStateHolder.getReservation();
        tvReservationActivityPrice.setText(Integer.toString(reservation.getParkingSpace().getPrice()) + "$");
        tvReservationActivityStartingTime.setText(reservation.getStartTime());
        tvReservationActivityEndingTime.setText(reservation.getEndTime());
        tvReservationActivityTime.setText(reservation.getTotalTime());
        tvReservationActivityCosts.setText(Integer.toString(reservation.getCost()));
    }
    
    private void prepareResultIntent() {
        Reservation reservation = mStateHolder.getReservation();

        Intent intent = new Intent();       
        intent.putExtra(INTENT_EXTRA_RESERVATION, reservation);
        
        setResult(Activity.RESULT_OK, intent);
    }
    
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			case RESULT_CODE_ACTIVITY_EXTEND_RESERVATION:
				if (resultCode == Activity.RESULT_OK) {
					Reservation reservation = data.getParcelableExtra(AddReservationActivity.INTENT_EXTRA_RETURNED_RESERVATION);
					mStateHolder.setReservation(reservation);		
					updateView();
					prepareResultIntent();
				}
				break;
				
		}
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mLoggedOutReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (DEBUG) Log.d(TAG, "onResume()");
        if (mStateHolder.getIsRunning()) {
            startProgressBar(getResources().getString(R.string.add_reservation_action_label),
                    getResources().getString(R.string.add_reservation_activity_progress_bar_message));
        }
    }

	@Override
	protected void onPause() {
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


	public void onDeleteReservationComplete(Boolean result) {
		if (DEBUG) Log.d(TAG, "onDeleteReservationComplete()");
		mStateHolder.setIsRunning(false);
		stopProgressBar();
		
		if (result == true) {		
			mStateHolder.setReservation(null);
			//sendBroadcast(new Intent(ActiveReservationsListActivity.REFRESH_INTENT));
			/*Intent intent = new Intent(ActiveReservationActivity.this, ActiveReservationsListActivity.class);
			intent.putExtra(INTENT_EXTRA_RESERVATION, mStateHolder.getReservation());
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);*/
			
			//sendBroadcast(new Intent(ActiveReservationsListActivity.REFRESH_INTENT));
			
			prepareResultIntent();
			finish();
		}
		else {
			setResult(Activity.RESULT_CANCELED);
			finish();
		}
		
	}
	
    public static class DeleteReservationTask extends AsyncTask<Void, Void, Boolean> {
        private static final String TAG = "DeleteReservationTask";
        private static boolean DEBUG = true;
        
        private ActiveReservationActivity mActivity;
		private int mUserId;
		private int mReservationId;
        
        public DeleteReservationTask(ActiveReservationActivity activity, int userId, int reservationId) {
            if (DEBUG) Log.d( TAG, "ReservationTask()");
            mActivity = activity;
			mReservationId = reservationId;
			mUserId = userId;
        }        
        
        @Override
        public void onPreExecute() {
            if (DEBUG) Log.d( TAG, "onPreExecute()");
            mActivity.startProgressBar(mActivity.getResources().getString(R.string.add_reservation_action_label), 
                    mActivity.getResources().getString(R.string.add_reservation_activity_progress_bar_message));
        }

        @Override
        public Boolean doInBackground(Void... params) {
            if (DEBUG) Log.d( TAG, "doInBackground()");                        
            boolean result = false;
			try {
				final ParkDroid mParkDroid = (ParkDroid) mActivity.getApplication();
				final Park mPark = mParkDroid.getPark();
				result = mPark.deleteReservation(mUserId, mReservationId);
			} catch (Exception e) {	}
			
			return result;			
        }        
         
        @Override
        public void onPostExecute(Boolean result) {
            if (DEBUG) Log.d( TAG, "onPostExecute()");
            if (mActivity != null) {
                mActivity.onDeleteReservationComplete(result);
            }
        }        

        public void setActivity(ActiveReservationActivity activity) {
            if (DEBUG) Log.d( TAG, "setActivity()");
            mActivity = activity;
        }
    }


    private static class StateHolder {
        
        private Reservation mReservation;
        private boolean mIsRunning;
        private DeleteReservationTask mDeleteTask;
        
        public void StateHolder() {  
        	mReservation = null;
        	mDeleteTask = null;
        	mIsRunning = false;
        }
        
        public void setActivity(ActiveReservationActivity activity) {
    		if (DEBUG) Log.d( TAG, "setActivity()");
            if (mDeleteTask != null) {
            	mDeleteTask.setActivity(activity);
            }			
		}

		public void startDeleteTask(ActiveReservationActivity activity, int userId, int reservationId) {
            if (DEBUG) Log.d( TAG, "startTask()");
            mIsRunning = true;
            mDeleteTask = new DeleteReservationTask(activity, userId, reservationId);
            mDeleteTask.execute();            
        }
        
        public void setReservation(Reservation reservation) {
            mReservation = reservation;
        }
        
        public Reservation getReservation() {
            return mReservation;         
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
            if (mDeleteTask !=null && mIsRunning) {
            	mDeleteTask.setActivity(null);
            	mDeleteTask.cancel(true);
            }            
        }
    }
}
