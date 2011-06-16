/**
 * 
 */
package com.licenta.parkdroid;

import com.licenta.park.Park;
import com.licenta.park.types.Group;
import com.licenta.park.types.ParkingSpace;
import com.licenta.park.types.ParkingSpaces;
import com.licenta.park.types.Reservation;
import com.licenta.parkdroid.LoadableListActivity;
import com.licenta.parkdroid.ParkDroid;
import com.licenta.parkdroid.ParkingSpaceListAdapter;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

/**
 * @author vladucu
 *
 */
public class ParkingSpacesListActivity extends LoadableListActivity {
    
    private static final String TAG = "ParkingSpacesListActivity";
    private static boolean DEBUG = false;
    
    private static final int RESULT_CODE_ACTIVITY_PARKING_LOT = 1;
    
    private StateHolder mStateHolder = new StateHolder();
    private ParkingSpaceListAdapter mListAdapter;
    private ParkingSpaceListAdapter mParkingSpaceListAdapter;
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
        mListAdapter = new ParkingSpaceListAdapter(this);        
        mListView.setAdapter(mParkingSpaceListAdapter);
        mListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (DEBUG) Log.d(TAG, "onCreate()+ on item click");
                ParkingSpace parkingSpace = (ParkingSpace) parent.getAdapter().getItem(position);
                //Toast.makeText(ParkingSpacesListActivity.this, parkingLot.getName(), Toast.LENGTH_LONG).show();
                startItemActivity(parkingSpace);
            }
        });

        // We can dynamically add a footer to our loadable listview.
        LayoutInflater inflater = LayoutInflater.from(this);
        
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
            //onTaskComplete(mStateHolder.getResults(), mStateHolder.getReverseGeoLoc(), null);
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
            mListAdapter.removeObserver();
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

    public void putResultsInAdapter(List<ParkingSpace> group) {
        if (DEBUG) Log.d(TAG, "putResultsInAdapter()");
        
        mListAdapter = new ParkingSpaceListAdapter(this);
        if (group !=null && group.size()>0) {
            if (DEBUG) Log.d(TAG, "putResultsInAdapter() if size="+group.size());           
            mListAdapter.setGroup(group);            
        } else {
            setEmptyView();
        }
        mListView.setAdapter(mListAdapter);
    }

    private void startItemActivity(ParkingSpace parkingSpace) {
        if (DEBUG) Log.d(TAG, "startItemActivity()");
        Intent intent = new Intent(ParkingSpacesListActivity.this, ParkingSpaceActivity.class);
        intent.putExtra(ParkingSpaceActivity.INTENT_EXTRA_PARKING_SPACE, parkingSpace);
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
    
    private void onTaskComplete(ParkingSpaces result, String reverseGeoLoc, Exception ex) {
        if (DEBUG) Log.d(TAG, "onTaskComplete()");
        if (result != null) {
            mStateHolder.setResults(result.getParkingSpaces());
            //mStateHolder.setReverseGeoLoc(reverseGeoLoc);
        } else {
            mStateHolder.setResults(new Group<ParkingSpace>());
            //notification of failure ?
        }
        
        putResultsInAdapter(mStateHolder.getResults());
        setProgressBarIndeterminateVisibility(false);       

        mStateHolder.cancelAllTasks();
    }

    /** If location changes, auto-start a nearby parking space search. */
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
     * Handles the work of finding nearby parking spaces
     */
    private static class SearchTask extends AsyncTask<Void, Void, ParkingSpaces> {
        private static final String TAG = "SearchTask";
        private static boolean DEBUG = false;
        
        private ParkingSpacesListActivity mActivity;
        private String mQuery;
        private long mSleepTimeInMs;
        private Park mPark;
        private ParkDroid mParkDroid;
        private ParkingSpaces results = null;
        
        public SearchTask(ParkingSpacesListActivity activity, String query, long sleepTimeInMs) {
           
            super();
            
            Log.d(TAG, "SearchTask()");
            mActivity = activity;
            mQuery = query;
            mSleepTimeInMs = sleepTimeInMs;
            mParkDroid = (ParkDroid) mActivity.getApplication();
            mPark = mParkDroid.getPark();
        }
        
        @Override
        public void onPreExecute() {
            Log.d(TAG, "onPreExecute()");
        }

        @Override
        public ParkingSpaces doInBackground(Void... params) {
            Log.d(TAG, "doInBackground()");
            // TODO get last known location and get the parking lots from the server
            
            try {
            	
            	results = mPark.parkingSpaces(Integer.parseInt(mParkDroid.getUserId()));
            } catch (Exception e) {
               
            }
            return results;
        }
    
        @Override
        public void onPostExecute(ParkingSpaces results) {
            Log.d(TAG, "onPostExecute()");
            if (mActivity != null) {
                mActivity.onTaskComplete(results, "", null);
            }
        }

        public void setActivity(ParkingSpacesListActivity activity) {
            mActivity = activity;            
        }
        
    }
    
    private static class StateHolder {
        private List<ParkingSpace> mResults;
        private String mQuery;
        private String mReverseGeoLoc;
        private SearchTask mSearchTask;
        private Set<String> mFullyLoadedParkingLotsIds;
        

        public StateHolder() {            
            mResults = new ArrayList<ParkingSpace>();
            mSearchTask = null;           
        }

        public void startTask(ParkingSpacesListActivity activity, String query, long sleepTimeInMs) {
            mSearchTask = new SearchTask(activity, query, sleepTimeInMs);
            mSearchTask.execute();            
        }

        public void setActivity(ParkingSpacesListActivity activity) {
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

        public List<ParkingSpace> getResults() {
            return mResults;
        }

        public void setResults(List<ParkingSpace> results) {
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
