/**
 * 
 */
package com.licenta.parkdroid;

import java.util.Iterator;
import java.util.List;
import com.licenta.park.Park;
import com.licenta.park.types.Reservation;
import com.licenta.park.types.Reservations;
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
    
    private static final String TAG = "ActiveReservationsListActivity";
    private static boolean DEBUG = true;
    
    private StateHolder mStateHolder;
    private ActiveReservationsAdapter mListAdapter;
    private static final int RESULT_CODE_ACTIVITY_RESERVATION = 1;

    private BroadcastReceiver mLoggedOutReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    };
    
    //TODO ce facem cu rezervarile care au expirat?

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        
        if (DEBUG) Log.d(TAG, "onCreate()");
        registerReceiver(mLoggedOutReceiver, new IntentFilter(ParkDroid.INTENT_ACTION_LOGGED_OUT));
        Object retained = getLastNonConfigurationInstance();
        if (retained != null & retained instanceof StateHolder) {
            mStateHolder = (StateHolder) retained;
            mStateHolder.setActivity(this);
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
        
        ListView listView = getListView();
        listView.setAdapter(mListAdapter);
        listView.setSmoothScrollbarEnabled(true);
        listView.setOnItemClickListener(new OnItemClickListener() {

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
					mStateHolder.updateReservation(reservation);
					mListAdapter.notifyDataSetChanged();
					ensureUi();
				}
				break;
		}
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
        private Park mPark;
        private ParkDroid mParkDroid;
        
        public ActiveReservationsTask(ActiveReservationsListActivity activity) {
            if (DEBUG) Log.d(TAG, "ActiveReservationsTask()");
            mActivity = activity;
            mParkDroid = (ParkDroid) mActivity.getApplication();
            mPark = mParkDroid.getPark();
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
            	reservations = mPark.getReservations();            
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
        private ActiveReservationsTask mTask;        
        private boolean mIsRunning;
        
        public StateHolder() {
            if (DEBUG) Log.d(TAG, "StateHolder()");            
            mIsRunning = false;
            mReservations = null;
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
        
        public void setActivity(ActiveReservationsListActivity activity) {
            if (DEBUG) Log.d(TAG, "setActivity()");
            if (mTask != null) {
                mTask.setActivity(activity);
            }
        }
        
        //add the modified reservation to the list of reservations
        public void updateReservation(Reservation reservation) {
        	Iterator<Reservation> it = mReservations.iterator();
        	Reservation res;
        	int i=0;
        	while (it.hasNext()) {
        		res = it.next();        		
        		if (res.getId() == reservation.getId()) {
        			//TODO should we calculate the distance here?
        			it.remove();        		
        			mReservations.add(reservation);
        			return;
        		}
        		
        		i++;
        	}            
        }
    }    
}
