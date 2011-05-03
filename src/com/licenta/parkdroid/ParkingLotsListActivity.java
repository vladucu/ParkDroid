/**
 * 
 */
package com.licenta.parkdroid;

import com.licenta.park.types.Group;
import com.licenta.park.types.ParkingLot;
import com.licenta.park.types.Reservation;
import com.licenta.parkdroid.LoadableListActivity;
import com.licenta.parkdroid.ParkDroid;
import com.licenta.parkdroid.ParkingLotListAdapter;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

/**
 * @author vladucu
 *
 */
public class ParkingLotsListActivity extends LoadableListActivity {
    
    private static final String TAG = "ParkingLotsListActivity";
    private static boolean DEBUG = true;
    
    private static final int RESULT_CODE_ACTIVITY_PARKING_LOT = 1;
    
    private StateHolder mStateHolder = new StateHolder();
    private ParkingLotListAdapter mListAdapter;
    private ParkingLotListAdapter mParkingLotListAdapter;
    private ListView mListView;
    private LinearLayout mFooterView;
    private TextView mTextViewFooter;
    private SearchLocationObserver mSearchLocationObserver = new SearchLocationObserver();
    private Handler mHandler;
    
    
    private BroadcastReceiver mLoggedOutReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (DEBUG) Log.d(TAG, "onReceive: " + intent);
            finish();
        }
    };
    
    //TODO add menu to search parking lot, add reservation, etc.

    /* (non-Javadoc)
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (DEBUG) Log.d(TAG, "onCreate()");
        
        setDefaultKeyMode(Activity.DEFAULT_KEYS_SEARCH_LOCAL);
        registerReceiver(mLoggedOutReceiver, new IntentFilter(ParkDroid.INTENT_ACTION_LOGGED_OUT));      
        
        mHandler = new Handler();
        mListView = getListView();
        mListAdapter = new ParkingLotListAdapter(this);        
        mListView.setAdapter(mParkingLotListAdapter);
        mListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (DEBUG) Log.d(TAG, "onCreate()+ on item click");
                ParkingLot parkingLot = (ParkingLot) parent.getAdapter().getItem(position);
                Toast.makeText(ParkingLotsListActivity.this, parkingLot.getName(), Toast.LENGTH_LONG).show();
                startItemActivity(parkingLot);
            }
        });

        
        
        // Check if we're returning from a configuration change.
        if (getLastNonConfigurationInstance() != null) {
            if (DEBUG) Log.d(TAG, "Restoring state.");
            mStateHolder = (StateHolder) getLastNonConfigurationInstance();
            mStateHolder.setActivity(this);
        } else {
            if (DEBUG) Log.d(TAG, "Creating new StateHolder instance.");
            mStateHolder = new StateHolder();            
        }
        
        // Start a new search if one is not running or we have no results.
        if (mStateHolder.getIsRunningTask()) {
            if (DEBUG) Log.d(TAG, "mIsRunning true.");
            setProgressBarIndeterminateVisibility(true);
            putResultsInAdapter(mStateHolder.getResults());
            //ensureTitle(false);
        } else if (mStateHolder.getResults().size() == 0) {    
            if (DEBUG) Log.d(TAG, "mIsRunning not running but no results.");
            startTask(0L);
        } else {
            if (DEBUG) Log.d(TAG, "mIsRunning false.");
            onTaskComplete(mStateHolder.getResults(), mStateHolder.getReverseGeoLoc(), null);
        }
        //populateFooter(mStateHolder.getReverseGeoLoc());
    }    
    
    /* (non-Javadoc)
     * @see android.app.ListActivity#onDestroy()
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
        
        //((ParkDroid) getApplication()).removeLocationUpdates(mSearchLocationObserver);
        
        if (isFinishing()) {
            mStateHolder.cancelAllTasks();
        }
        
    }

    /* Called right before activity comes back to foreground. 
     * Register for Location updates.
     * @see android.app.Activity#onResume()
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (DEBUG) Log.d(TAG, "onResume()");
        //((ParkDroid) getApplication()).requestLocationUpdates(mSearchLocationObserver);
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onRetainNonConfigurationInstance()
     */
    @Override
    public Object onRetainNonConfigurationInstance() {
        mStateHolder.setActivity(null);
        return mStateHolder;
    }

    public void putResultsInAdapter(Group<ParkingLot> results) {
        if (DEBUG) Log.d(TAG, "putResultsInAdapter()");
        
        mListAdapter = new ParkingLotListAdapter(this);
        if (results !=null && results.size()>0) {
            if (DEBUG) Log.d(TAG, "putResultsInAdapter() if size="+results.size());           
            mListAdapter.setGroup(results);            
        } else {
            setEmptyView();
        }
        mListView.setAdapter(mListAdapter);
    }

    private void startItemActivity(ParkingLot parkingLot) {
        if (DEBUG) Log.d(TAG, "startItemActivity()");
        Intent intent = new Intent(ParkingLotsListActivity.this, ParkingLotActivity.class);
        intent.putExtra(ParkingLotActivity.INTENT_EXTRA_PARKING_LOT, parkingLot);
        startActivity(intent);        
    }
    
    private void startTask(long geoLocDelayTimeInMs) {
        if (DEBUG) Log.d(TAG, "startTask");
        if (mStateHolder.getIsRunningTask() == false) {
            if (DEBUG) Log.d(TAG, "startTask mIsRunning false");
            setProgressBarIndeterminateVisibility(true);
            if (mStateHolder.getResults().size() == 0) {
                setLoadingView("");
            }
            if (DEBUG) Log.d(TAG, "startTask()");
            mStateHolder.startTask(this, mStateHolder.getQuery(), geoLocDelayTimeInMs);
        }
    }
    
    private void onTaskComplete(Group<ParkingLot> result, String reverseGeoLoc, Exception ex) {
        if (DEBUG) Log.d(TAG, "onTaskComplete()");
        if (result != null) {
            mStateHolder.setResults(result);
            //mStateHolder.setReverseGeoLoc(reverseGeoLoc);
        } else {
            mStateHolder.setResults(new Group<ParkingLot>());
            //notification of failure ?
        }
        
        putResultsInAdapter(mStateHolder.getResults());
        setProgressBarIndeterminateVisibility(false);       

        mStateHolder.cancelAllTasks();
    }

    /** If location changes, auto-start a nearby parking lots. */
    private class SearchLocationObserver implements Observer {

        private boolean mRequestedFirstSearch = false;

        @Override
        public void update(Observable observable, Object data) {
            Location location = (Location) data;
            // Fire a search if we haven't done so yet.
            if (!mRequestedFirstSearch
                    && ((BestLocationListener) observable).isAccurateEnough(location)) {
                mRequestedFirstSearch = true;
                if (mStateHolder.getIsRunningTask() == false) {
                    // Since we were told by the system that location has
                    // changed, no need to make the
                    // task wait before grabbing the current location.
                    mHandler.post(new Runnable() {
                        public void run() {
                            startTask(0L);
                        }
                    });
                }
            }
        }
    }
    
    /*
     * Handles the work of finding nearby parking lots
     */
    private static class SearchTask extends AsyncTask<Void, Void, Group<ParkingLot>> {
        private static final String TAG = "SearchTask";
        private static boolean DEBUG = true;
        
        private ParkingLotsListActivity mActivity;
        private String mQuery;
        private long mSleepTimeInMs;
        
        public SearchTask(ParkingLotsListActivity activity, String query, long sleepTimeInMs) {
            Log.d(TAG, "SearchTask()");
            // super();
            mActivity = activity;
            mQuery = query;
            mSleepTimeInMs = sleepTimeInMs;
        }
        
        /* (non-Javadoc)
         * @see android.os.AsyncTask#onPreExecute()
         */
        @Override
        protected void onPreExecute() {
            Log.d(TAG, "onPreExecute()");
        }

        @Override
        protected Group<ParkingLot> doInBackground(Void... params) {
            Log.d(TAG, "doInBackground()");
            // TODO get last known location and get the parking lots from the server
            try {
                Group<ParkingLot> g = new Group<ParkingLot>();
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
                mPark1.setUrl("http://linkmailungpentruverificare.ro");
                mPark1.setHasReservation(true);
                Reservation reservation = new Reservation();
                reservation.setId("323213");
                reservation.setParkingLot(null);
                reservation.setStartTime("Wed, 27 April 11 15:00:00 +0000");
                reservation.setEndTime("Wed, 27 April 11 17:00:00 +0000");
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
                mPark2.setUrl("http://magentocommerce.com");
                mPark2.setHasReservation(false);
                mPark2.setReservation(null);
                for (int index=0;index<10;index++) {
                    g.add(mPark1);g.add(mPark2); 
                }
                if (DEBUG) Log.d(TAG, g.get(1).getName());
                
                return g;
            } catch (Exception e) {
                // TODO Auto-generated catch block
                if (DEBUG) Log.d(TAG, "Exception");
                e.printStackTrace();
            }
            return null;
        }
                
        /* (non-Javadoc)
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        protected void onPostExecute(Group<ParkingLot> result) {
            Log.d(TAG, "onPostExecute()");
            if (mActivity != null) {
                mActivity.onTaskComplete(result, "", null);
            }
        }

        public void setActivity(ParkingLotsListActivity activity) {
            mActivity = activity;            
        }
        
    }
    
    private static class StateHolder {
        private Group<ParkingLot> mResults;
        private String mQuery;
        private String mReverseGeoLoc;
        private SearchTask mSearchTask;
        private Set<String> mFullyLoadedVenueIds;
        

        public StateHolder() {            
            mResults = new Group<ParkingLot>();
            mSearchTask = null;           
        }

        public void startTask(ParkingLotsListActivity activity, String query, long sleepTimeInMs) {
            mSearchTask = new SearchTask(activity, query, sleepTimeInMs);
            mSearchTask.execute();            
        }

        public void setActivity(ParkingLotsListActivity activity) {
           if (mSearchTask != null) {
               mSearchTask.setActivity(activity);
           }
            
        }

        public String getQuery() {
            return mQuery;
        }

        public void setQuery(String query) {
            mQuery = query;
        }

        public String getReverseGeoLoc() {
            return mReverseGeoLoc;
        }

        public void setReverseGeoLoc(String reverseGeoLoc) {
            mReverseGeoLoc = reverseGeoLoc;
        }

        public Group<ParkingLot> getResults() {
            return mResults;
        }

        public void setResults(Group<ParkingLot> results) {
            mResults = results;
        }
        
        public boolean getIsRunningTask() {
            return mSearchTask != null;
        }

        public void cancelAllTasks() {
            if (mSearchTask != null) {
                mSearchTask.cancel(true);
                mSearchTask = null;
            }
        }
   } 
}
