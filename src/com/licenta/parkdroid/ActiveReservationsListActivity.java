/**
 * 
 */
package com.licenta.parkdroid;

import java.util.Iterator;
import java.util.List;
import com.licenta.park.Park;
import com.licenta.park.types.Group;
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
import android.widget.Toast;
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
    //TODO afisam pretul parcarii pe ora sau costul rezervarii
    
    /* (non-Javadoc)
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
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
            ParkDroid parkDroid = (ParkDroid) getApplication();
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
                Toast.makeText(ActiveReservationsListActivity.this, reservation.getParkingSpace().getName(), Toast.LENGTH_LONG);
                startItemActivity(reservation);
            }            
        });
        
    }
    
    private void startItemActivity(Reservation reservation) {
        if (DEBUG) Log.d(TAG, "startItemActivity()");
        Intent intent = new Intent(ActiveReservationsListActivity.this, ReservationActivity.class);
        intent.putExtra(ReservationActivity.INTENT_EXTRA_RESERVATION, reservation);
        startActivityForResult(intent, RESULT_CODE_ACTIVITY_RESERVATION);        
    }
        
    /* (non-Javadoc)
	 * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			case RESULT_CODE_ACTIVITY_RESERVATION:
				if (resultCode == Activity.RESULT_OK && data.hasExtra(ReservationActivity.INTENT_EXTRA_RESERVATION)) {
					Reservation reservation = data.getParcelableExtra(ReservationActivity.INTENT_EXTRA_RESERVATION);
					mStateHolder.updateReservation(reservation);
					//mListAdapter.notifyDataSetChanged();
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
            // TODO Auto-generated method stub
            /*try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }*/
            try {
            	reservations = mPark.reservations();
            	/*ParkingSpace mPark1 = new ParkingSpace();
            	ParkingSpace mPark2 = new ParkingSpace();
                mPark1.setName("Parcarea principala");
                mPark2.setName("Parcarea secundara");
                mPark1.setGeolat("44.43472");
                mPark1.setGeolong("26.09704");
                mPark2.setGeolat("44.43797");
                mPark2.setGeolong("26.11576");     
                //37.44461, -122.13846 San Francisco - Palo Alto Library
                mPark1.setGeoLat("37.44461");
                mPark1.setGeoLong("-122.13846");
                //37.4597, -122.1064 San Francisco - Lucy Evans Baylands Nature Interpretive Center (The City of Palo Alto)
                mPark2.setGeoLat("37.4597");
                mPark2.setGeoLong("-122.1064");
                mPark1.setAddress("Bulevardul Regina Elisabeta 23");
                mPark2.setAddress("Bulevardul Carol 76");
                //mPark1.setPrice("25");
                //mPark2.setPrice("125");
                
                Reservation r1 = new Reservation();
                Reservation r2 = new Reservation();
                r1.setParkingSpace(mPark1);r2.setParkingSpace(mPark2);
                r1.setId(123);r2.setId(234);                
                r1.setStartTime("Wed, 27 April 11 15:00:00 +0000");
                r1.setEndTime("Wed, 27 April 11 17:00:00 +0000");
                r2.setStartTime("Fri, 29 April 11 15:00:00 +0000");
                r2.setEndTime("Fri, 29 April 11 17:35:00 +0000");
                Group<Reservation> g = new Group<Reservation>();
                g.add(r1);g.add(r2);
                return g;*/
            } catch (Exception e) {
                // TODO Auto-generated catch block
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
