/**
 * 
 */
package com.licenta.parkdroid;

import com.licenta.park.types.Group;
import com.licenta.park.types.ParkingLot;
import com.licenta.park.types.Reservation;
import com.licenta.park.utils.FormatStrings;

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
            mStateHolder = new StateHolder(parkDroid.getUserEmail());
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
                if (DEBUG) Log.d(TAG, "ensureUi() =" + reservation.getParkingLot().getName());
                Toast.makeText(ActiveReservationsListActivity.this, reservation.getParkingLot().getName(), Toast.LENGTH_LONG);
                startItemActivity(reservation);
            }            
        });
        
    }
    
    private void startItemActivity(Reservation reservation) {
        if (DEBUG) Log.d(TAG, "startItemActivity()");
        Intent intent = new Intent(ActiveReservationsListActivity.this, ReservationActivity.class);
        intent.putExtra(ReservationActivity.INTENT_EXTRA_RESERVATION, reservation);
        startActivity(intent);        
    }
        
    public void onActiveReservationsTaskComplete(Group<Reservation> reservations) {
        if (DEBUG) Log.d(TAG, "onActiveReservationsTaskComplete()");
        
        mListAdapter = new ActiveReservationsAdapter(this);
        
        if (reservations != null) {
            mStateHolder.setReservations(reservations);
            mListAdapter.setGroup(mStateHolder.getReservations());
            getListView().setAdapter(mListAdapter);
        }
        mStateHolder.setIsRunning(false);
        
        if (mStateHolder.getReservations().size() == 0) {
            setEmptyView();
        }
        
    }
    
    private static class ActiveReservationsTask extends AsyncTask<Void, Void, Group<Reservation>> {

        private static final String TAG = "ActiveReservationsTask";
        private static boolean DEBUG = true;
        
        private ActiveReservationsListActivity mActivity;
        
        public ActiveReservationsTask(ActiveReservationsListActivity activity) {
            if (DEBUG) Log.d(TAG, "ActiveReservationsTask()");
            mActivity = activity;
        }
        
        @Override
        protected void onPreExecute() {
            if (DEBUG) Log.d(TAG, "onPreExecute()");
            mActivity.setLoadingView();            
        }
        
        @Override
        protected Group<Reservation> doInBackground(Void... params) {
            if (DEBUG) Log.d(TAG, "doInBackground()");
            // TODO Auto-generated method stub
            /*try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }*/
            try {
                ParkingLot mPark1 = new ParkingLot();
                ParkingLot mPark2 = new ParkingLot();
                mPark1.setId("123");
                mPark1.setName("Parcarea principala");
                mPark1.setPhone("0733683111");
                mPark1.setAddress("Bulevardul Regina Elisabeta 23 si o iei pe acolo pe unde vezi cu ochii");
                mPark1.setCity("Bucuresti");
                mPark1.setZip("050012");
                mPark1.setGeolat("44.43472");
                mPark1.setGeolong("26.09704");
                mPark1.setDistance("100");
                mPark1.setEmptySpaces("150");
                mPark1.setTotalSpaces("500");
                mPark1.setPrice("25");
                mPark1.setUrl("http://google.com");
                mPark2.setId("523");
                mPark2.setName("Parcarea secundara");
                mPark2.setPhone("0733683444");
                mPark2.setAddress("Bulevardul Carol 76");
                mPark2.setCity("Bucuresti");
                mPark2.setZip("020926");
                mPark2.setGeolat("44.43797");
                mPark2.setGeolong("26.11576");
                mPark2.setDistance("500");
                mPark2.setEmptySpaces("550");
                mPark2.setTotalSpaces("1500");
                mPark2.setPrice("125");
                mPark2.setUrl("http://igoogle.com");
                Reservation r1 = new Reservation();
                Reservation r2 = new Reservation();
                r1.setParkingLot(mPark1);r2.setParkingLot(mPark2);
                r1.setStartTime("Wed, 27 April 11 15:00:00 +0000");
                r1.setEndTime("Wed, 27 April 11 17:00:00 +0000");
                r2.setStartTime("Fri, 29 April 11 15:00:00 +0000");
                r2.setEndTime("Fri, 29 April 11 17:00:00 +0000");
                r1.setId("12345");
                Group<Reservation> g = new Group<Reservation>();
                g.add(r1);g.add(r2);
                return g;
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }
        
        @Override
        protected void onPostExecute(Group<Reservation> reservations) {
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
        
        private Group<Reservation> mReservations;
        private ActiveReservationsTask mTask;
        private String mUserEmail;
        private boolean mIsRunning;
        
        public StateHolder(String userEmail) {
            if (DEBUG) Log.d(TAG, "StateHolder()");
            mUserEmail = userEmail;
            mIsRunning = false;
            mReservations = new Group<Reservation>();
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
        
        public void setReservations(Group<Reservation> reservations) {
            if (DEBUG) Log.d(TAG, "setReservations()");
            mReservations = reservations;
        }
        
        public Group<Reservation> getReservations() {
            if (DEBUG) Log.d(TAG, "getReservations()");
            return mReservations;
        }
        
        public void setActivity(ActiveReservationsListActivity activity) {
            if (DEBUG) Log.d(TAG, "setActivity()");
            if (mTask != null) {
                mTask.setActivity(activity);
            }
        }
    }    
}
