/**
 * 
 */
package com.licenta.parkdroid;

import java.util.Iterator;
import java.util.List;

import com.licenta.parkdroid.types.Reservation;
import com.licenta.parkdroid.types.Reservations;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * @author vladucu
 *
 */
public class ActiveReservationsListActivity extends LoadableListActivity {
    
	//TODO add top bar intermediate progress bar
    private static final String TAG = "ActiveReservationsListActivity";
    private static boolean DEBUG = true;
    
    private StateHolder mStateHolder;
    private ListView mListView;
    private ActiveReservationsAdapter mListAdapter;
    
    private static final int RESULT_CODE_ACTIVITY_RESERVATION = 1;
    public static final String REFRESH_INTENT = "com.licenta.parkdroid.intent.action.REFRESH_INTENT";
    public static final String INTENT_EXTRA_RESERVATION = ParkDroid.PACKAGE_NAME + ".ActiveReservationsListActivity.INTENT_EXTRA_RESERVATION";
    
    private BroadcastReceiver mLoggedOutReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    };
    
    private BroadcastReceiver mRefreshReservations = new BroadcastReceiver() {
    			
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d("DSADS", "receiving refresh broadcast");
			if (intent.getAction().equals(ActiveReservationsListActivity.REFRESH_INTENT)) {
                mStateHolder.startActiveReservationsTask(ActiveReservationsListActivity.this);
                mListAdapter.notifyDataSetChanged();
                ensureUi();
            }
			
		}
	};
    //TODO ce facem cu rezervarile care au expirat?

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        
        if (DEBUG) Log.d(TAG, "onCreate()");
        registerReceiver(mLoggedOutReceiver, new IntentFilter(ParkDroid.INTENT_ACTION_LOGGED_OUT));
        registerReceiver(mRefreshReservations, new IntentFilter(ActiveReservationsListActivity.REFRESH_INTENT));
        Object retained = getLastNonConfigurationInstance();
        if (retained != null & retained instanceof StateHolder) {
            mStateHolder = (StateHolder) retained;
            mStateHolder.setActivity(this);
            if (getIntent().hasExtra(INTENT_EXTRA_RESERVATION)) {
                mStateHolder.updateReservation((Reservation) getIntent().getParcelableExtra(INTENT_EXTRA_RESERVATION), true);
            }
        } else {            
            mStateHolder = new StateHolder();
            mStateHolder.startActiveReservationsTask(this);
        }
        
        ensureUi();
    }
    
    private void ensureUi() {
        if (DEBUG) Log.d(TAG, "ensureUi()");
        
        mListAdapter = new ActiveReservationsAdapter(this);
        mListAdapter.setGroup(mStateHolder.getReservations());
        
        mListView = getListView();
        mListView.setAdapter(mListAdapter);
        mListView.setSmoothScrollbarEnabled(true);
        mListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long arg3) {                
                Reservation reservation = (Reservation) parent.getAdapter().getItem(position);
                if (DEBUG) Log.d(TAG, "ensureUi() =" + reservation.getParkingSpace().getName());
                startItemActivity(reservation);
            }            
        });
        
    }
    
    private void startItemActivity(Reservation reservation) {
        if (DEBUG) Log.d(TAG, "startItemActivity()");
        mStateHolder.setActiveReservation(reservation);
        Intent intent = new Intent(ActiveReservationsListActivity.this, ActiveReservationActivity.class);
        intent.putExtra(ActiveReservationActivity.INTENT_EXTRA_RESERVATION, reservation);
        startActivityForResult(intent, RESULT_CODE_ACTIVITY_RESERVATION);        
    }

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			case RESULT_CODE_ACTIVITY_RESERVATION:
				if (resultCode == Activity.RESULT_OK && data.hasExtra(ActiveReservationActivity.INTENT_EXTRA_RESERVATION)) {
					Reservation reservation = data.getParcelableExtra(ActiveReservationActivity.INTENT_EXTRA_RESERVATION);
					
					mStateHolder.updateReservation(reservation, false);
					mListAdapter.notifyDataSetChanged();
					ensureUi();
				}
				break;
		}
	}

   @Override
    protected void onDestroy() {        
        super.onDestroy();
        unregisterReceiver(mLoggedOutReceiver);
        unregisterReceiver(mRefreshReservations);
    }

   @Override
    protected void onPause() {
        super.onPause();
        
        if (isFinishing()) {
            mStateHolder.cancelAllTasks();
        }        
    }
	
    @Override
    protected void onResume() {
        super.onResume();
        if (DEBUG) Log.d(TAG, "onResume()");
	}
	
	@Override
	public Object onRetainNonConfigurationInstance() {
	    mStateHolder.setActivity(null);
	    return mStateHolder;
	}

	public void onActiveReservationsTaskComplete(Reservations reservations) {
        if (DEBUG) Log.d(TAG, "onActiveReservationsTaskComplete()");
        
        mListAdapter = new ActiveReservationsAdapter(this);
        
        if (reservations != null) {
            mStateHolder.setReservations(reservations.getReservations());
            mListAdapter.setGroup(mStateHolder.getReservations());
            getListView().setAdapter(mListAdapter);
        }
        mStateHolder.setIsRunning(false);
        
        if (mStateHolder.getReservations().size() == 0) {
            setEmptyView();
        }
        
    }
    
    private static class ActiveReservationsTask extends AsyncTask<Void, Void, Reservations> {

        private static final String TAG = "ActiveReservationsTask";
        private static boolean DEBUG = true;
        
        private ActiveReservationsListActivity mActivity;
        private Reservations reservations;
        private ParkDroid mParkDroid;
        
        public ActiveReservationsTask(ActiveReservationsListActivity activity) {
            if (DEBUG) Log.d(TAG, "ActiveReservationsTask()");
            mActivity = activity;
            mParkDroid = (ParkDroid) mActivity.getApplication();
        }
        
        @Override
        protected void onPreExecute() {
            if (DEBUG) Log.d(TAG, "onPreExecute()");
            mActivity.setLoadingView();            
        }
        
        @Override
        protected Reservations doInBackground(Void... params) {
            if (DEBUG) Log.d(TAG, "doInBackground()");
       
            try {
            	reservations = mParkDroid.getReservations(mParkDroid.getUserId());            
            } catch (Exception e) {
                e.printStackTrace();
            }
            return reservations;
        }
        
        @Override
        protected void onPostExecute(Reservations reservations) {
            if (DEBUG) Log.d(TAG, "onPostExecute()");
            if (mActivity != null) {
                mActivity.onActiveReservationsTaskComplete(reservations);
            }
        }
        
        public void setActivity(ActiveReservationsListActivity activity) {
            if (DEBUG) Log.d(TAG, "setActivity()");
            mActivity = activity;
        }
    }
    
    private static class StateHolder {
        private static final String TAG = "StateHolder";
        private static boolean DEBUG = true;
        
        private List<Reservation> mReservations;
        private Reservation mActiveReservation;
        private ActiveReservationsTask mTask;        
        private boolean mIsRunning;
        
        public StateHolder() {
            if (DEBUG) Log.d(TAG, "StateHolder()");            
            mIsRunning = false;
            mReservations = null;
            mActiveReservation = null;
        }
        
        public void startActiveReservationsTask(ActiveReservationsListActivity activity) {
            if (DEBUG) Log.d(TAG, "startActiveReservationsTask()");
            mIsRunning = true;
            mTask = new ActiveReservationsTask(activity);
            mTask.execute();
        }

        public void setIsRunning(boolean isRunning) {
            if (DEBUG) Log.d(TAG, "setIsRunning()");
            mIsRunning = isRunning;            
        }
        
        public boolean getIsRunning() {
            if (DEBUG) Log.d(TAG, "getIsRunning()");
            return mIsRunning;
        }
        
        public void setReservations(List<Reservation> reservations) {
            if (DEBUG) Log.d(TAG, "setReservations()");
            mReservations = reservations;
        }
        
        public List<Reservation> getReservations() {
            if (DEBUG) Log.d(TAG, "getReservations()");
            return mReservations;
        }
        
        public void setActiveReservation(Reservation reservation) {
            if (DEBUG) Log.d(TAG, "setActiveReservationReservation()");
            mActiveReservation = reservation;
        }
        
        public Reservation getActiveReservation() {
            if (DEBUG) Log.d(TAG, "getActiveReservation()");
            return mActiveReservation;
        }
        
        public void setActivity(ActiveReservationsListActivity activity) {
            if (DEBUG) Log.d(TAG, "setActivity()");
            if (mTask != null) {
                mTask.setActivity(activity);
            }
        }
        
        public void cancelAllTasks() {
            if (mTask != null) {
            	mTask.cancel(true);
            	mTask = null;
            }
        }
        
        //add the modified reservation to the list of reservations
        public void updateReservation(Reservation reservation, boolean newReservation) {
        	if (newReservation) {
        		mReservations.add(reservation);
        	} else {
	        	Iterator<Reservation> it = mReservations.iterator();
	        	Reservation res;
	        	int i=0;
	        	while (it.hasNext()) {
	        		res = it.next();        		
	        		if (res.getId() == mActiveReservation.getId()) {
	        			//TODO should we calculate the distance here?
	        			it.remove();        		
	        			if (reservation != null) mReservations.add(reservation);
	        			return;
	        		}
	        		
	        		i++;
	        	}            
        	}
        }
    }    
    
    private class RefreshListReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (REFRESH_INTENT.equals(intent.getAction())) {
            	mStateHolder.startActiveReservationsTask(ActiveReservationsListActivity.this);
            	//notify the list adapter that data changed and it should automatically refresh
                mListAdapter.notifyDataSetChanged();
            }
        }
    }    
}
