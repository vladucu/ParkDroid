/**
 * 
 */
package com.licenta.parkdroid;

import com.licenta.park.types.ReservationResult;
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
import android.util.Log;
import android.view.Window;

/**
 * @author vladucu
 *
 */
public class AddReservationExecuteActivity extends Activity {

	private static final String TAG = "AddReservationExecuteActivity";
    private static final boolean DEBUG = true;
    
    public static final String INTENT_EXTRA_PARKING_LOT_ID = ParkDroid.PACKAGE_NAME
    + ".AddReservationExecuteActivity.INTENT_EXTRA_PARKING_LOT_ID";

    private static final int DIALOG_ADD_RESERVATION_RESULT = 1;

    private StateHolder mStateHolder;
    private ProgressDialog mDlgProgress;

    private BroadcastReceiver mLoggedOutReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (DEBUG) Log.d(TAG, "onReceive: " + intent);
            finish();
        }
    };
    
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		if (DEBUG) Log.d(TAG, "onCreate()");
		//we don't need the title of the window
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.add_reservation_execute_activity);
		registerReceiver(mLoggedOutReceiver, new IntentFilter(ParkDroid.INTENT_ACTION_LOGGED_OUT));
		
		//we start execution immediately on creation of activity
		Object retained = getLastNonConfigurationInstance();
		if (retained != null && retained instanceof StateHolder) {
			mStateHolder = (StateHolder) retained;
			mStateHolder.setActivity(this);
		} 
		else {
			mStateHolder = new StateHolder();
			
			String parkingLotId = null;
			if (getIntent().getExtras().containsKey(INTENT_EXTRA_PARKING_LOT_ID)) {
				parkingLotId = (String) getIntent().getStringExtra(INTENT_EXTRA_PARKING_LOT_ID);
			}
			else {
				Log.e(TAG, "AddReservationExecuteActivity needs an 'INTENT_EXTRA_PARKING_LOT_ID' extra !");
				finish();
				return;
			}
			
			mStateHolder.startTask(AddReservationExecuteActivity.this, parkingLotId);
		}		
	}
	
	@Override
    public Object onRetainNonConfigurationInstance() {
		if (DEBUG) Log.d(TAG, "onRetainNonConfigurationInstance()");
        mStateHolder.setActivity(null);
        return mStateHolder;
    }

    @Override
    public void onResume() {
    	if (DEBUG) Log.d(TAG, "onResume()");
        super.onResume();

        if (mStateHolder.getIsRunning()) {
        	startProgressBar(getResources().getString(R.string.add_reservation_execute_activity_action_label),
					getResources().getString(R.string.add_reservation_execute_activity_progress_bar_message));
        }
    }
	
	/* (non-Javadoc)
     * @see android.app.Activity#onDestroy()
     */
	 @Override
    public void onPause() {
        super.onPause();
        if (DEBUG) Log.d(TAG, "onPause()");
        stopProgressBar();

        if (isFinishing()) {
            mStateHolder.cancelTasks();
        }
    }
 
	 /* (non-Javadoc)
     * @see android.app.Activity#onDestroy()
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (DEBUG) Log.d(TAG, "onDestroy()");
        unregisterReceiver(mLoggedOutReceiver);
    }
    
    private void startProgressBar(String title, String message) {
    	if (DEBUG) Log.d(TAG, "startProgressBar()");
        if (mDlgProgress == null) {
            mDlgProgress = ProgressDialog.show(this, title, message);
        }
        mDlgProgress.show();
    }

    private void stopProgressBar() {
    	if (DEBUG) Log.d(TAG, "stopProgressBar()");
        if (mDlgProgress != null) {
            mDlgProgress.dismiss();
            mDlgProgress = null;
        }
    }
	
    @Override
    protected Dialog onCreateDialog(int id) {
    	if (DEBUG) Log.d(TAG, "onCreateDialog()");
        switch (id) {
            case DIALOG_ADD_RESERVATION_RESULT:
                // When the user cancels the dialog (by hitting the 'back' key),
                // we finish this activity. We don't listen to onDismiss() for this
                // action, because a device rotation will fire onDismiss(), and
                // our dialog would not be re-displayed after the rotation is
                // complete.
            	AddReservationResultDialog dlg = new AddReservationResultDialog(this, mStateHolder
                        .getReservationResult(), ((ParkDroid) getApplication()));
                dlg.setOnCancelListener(new OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        removeDialog(DIALOG_ADD_RESERVATION_RESULT);
                        setResult(Activity.RESULT_OK);
                        finish();
                    }
                });
                return dlg;
        }
        return null;
    }

	public void onAddReservationComplete(ReservationResult result) {		
		if (DEBUG) Log.d(TAG, "onAddReservationComplete()");
		mStateHolder.setIsRunning(false);
		stopProgressBar();
		
		if (result != null) {
			mStateHolder.setReservationResult(result);
			showDialog(DIALOG_ADD_RESERVATION_RESULT);
		}
		else {
			setResult(Activity.RESULT_CANCELED);
			finish();
		}
		
	}    
	
	private static class AddReservationTask extends AsyncTask<Void, Void, ReservationResult> {

		private static final String TAG = "AddReservationTask";
	    private static final boolean DEBUG = true;

		private AddReservationExecuteActivity mActivity;
		private String mParkingLotId;
		
		public AddReservationTask(AddReservationExecuteActivity activity, String parkingLotId) {
			if (DEBUG) Log.d(TAG, "AddReservationTask()");
			mActivity = activity;
			mParkingLotId = parkingLotId;			
		}

		/* (non-Javadoc)
		 * @see android.os.AsyncTask#onPreExecute()
		 */
		@Override
		protected void onPreExecute() {
			if (DEBUG) Log.d(TAG, "onPreExecute()");
			mActivity.startProgressBar(mActivity.getResources().getString(R.string.add_reservation_execute_activity_action_label),
					mActivity.getResources().getString(R.string.add_reservation_execute_activity_progress_bar_message));
		}


		/* (non-Javadoc)
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected ReservationResult doInBackground(Void... params) {
			if (DEBUG) Log.d(TAG, "doInBackground()");
			
			// TODO Auto-generated method stub
			// TODO we could add exception handling and put this into try/catch block
			// TODO implement the method to call the rest web service
			
			ReservationResult result = new ReservationResult();
			try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
			return result;			
		}


		/* (non-Javadoc)
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(ReservationResult result) {
			if (DEBUG) Log.d(TAG, "onPostExecute()");
			if (mActivity != null) {
				mActivity.onAddReservationComplete(result);
			}
		}


		public void setActivity(AddReservationExecuteActivity activity) {
			if (DEBUG) Log.d(TAG, "setActivity()");
			mActivity = activity;
		}
	}
	
	private static class StateHolder {
		private static final String TAG = "StateHolder";
	    private static final boolean DEBUG = true;

		private AddReservationTask mTask;
		private ReservationResult mResult;
		private boolean mIsRunning;
		
		public StateHolder() {
			if (DEBUG) Log.d(TAG, "StateHolder()");
			mTask = null;
			mIsRunning = false;
		}
		
		//TODO to see what the parameters will be exactly
		public void startTask(AddReservationExecuteActivity activity, String parkingLotId) {
			if (DEBUG) Log.d(TAG, "startTask()");
			mIsRunning = true;
			mTask = new AddReservationTask(activity, parkingLotId);
			mTask.execute();
		}
		
		public void setActivity(AddReservationExecuteActivity activity) {
			if (DEBUG) Log.d(TAG, "setActivity()");
			if (mTask != null) {
				mTask.setActivity(activity);
			}
		}
		
		public boolean getIsRunning() {
			if (DEBUG) Log.d(TAG, "getIsRunning()");
            return mIsRunning;
        }

        public void setIsRunning(boolean isRunning) {
        	if (DEBUG) Log.d(TAG, "setIsRunning()");
            mIsRunning = isRunning;
        }
        
		public ReservationResult getReservationResult() {
			if (DEBUG) Log.d(TAG, "getReservationResult()");
            return mResult;
        }

        public void setReservationResult(ReservationResult result) {
        	if (DEBUG) Log.d(TAG, "setReservationResult()");
            mResult = result;
        }        
		
		public void cancelTasks() {
			if (DEBUG) Log.d(TAG, "cancelTasks()");
			if (mTask != null && mIsRunning) {
				mTask = null;
				mTask.cancel(true);
			}
		}		
	}
}
