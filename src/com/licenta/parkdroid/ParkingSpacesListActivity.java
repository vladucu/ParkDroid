/**
 * 
 */
package com.licenta.parkdroid;

import com.licenta.park.Park;
import com.licenta.park.types.ParkingSpace;
import com.licenta.park.types.ParkingSpaces;
import com.licenta.parkdroid.LoadableListActivity;
import com.licenta.parkdroid.ParkDroid;
import com.licenta.parkdroid.ParkingSpaceListAdapter;
import android.app.Activity;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import java.util.ArrayList;
import java.util.List;

/**
 * @author vladucu
 *
 */
public class ParkingSpacesListActivity extends LoadableListActivity {
    
    private static final String TAG = "ParkingSpacesListActivity";
    private static boolean DEBUG = false;
    
    private static final int RESULT_CODE_ACTIVITY_PARKING_SPACE = 1;
    private static final int MENU_REFRESH = 0;
    
    private StateHolder mStateHolder = new StateHolder();
    private ParkingSpaceListAdapter mListAdapter;
    private ParkingSpaceListAdapter mParkingSpaceListAdapter;
    private ListView mListView;
    private Handler mHandler;
    
    
    private BroadcastReceiver mLoggedOutReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (DEBUG) Log.d(TAG, "onReceive: " + intent);
            finish();
        }
    };

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
                startItemActivity(parkingSpace);
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
            mStateHolder.setQuery("");
        }
        
        // Start a new search if one is not running or we have no results.
        if (mStateHolder.getIsRunningTask()) {
            if (DEBUG) Log.d(TAG, "mIsRunning true.");
            setProgressBarIndeterminateVisibility(true);
            putResultsInAdapter(mStateHolder.getResults());
        } else if (mStateHolder.getResults().size() == 0) {    
            if (DEBUG) Log.d(TAG, "mIsRunning not running but no results.");
            startTask();
        } else {
            if (DEBUG) Log.d(TAG, "mIsRunning false.");
            onTaskComplete(mStateHolder.getResults(), null);
        }
    }    
    
   @Override
    protected void onDestroy() {        
        super.onDestroy();
        unregisterReceiver(mLoggedOutReceiver);
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
    
	/*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(Menu.NONE, MENU_REFRESH, Menu.NONE, R.string.refresh) //
                .setIcon(R.drawable.ic_menu_refresh);
        menu.add(Menu.NONE, MENU_SEARCH, Menu.NONE, R.string.search_label) //
                .setIcon(R.drawable.ic_menu_search) //
                .setAlphabeticShortcut(SearchManager.MENU_KEY);
        menu.add(Menu.NONE, MENU_ADD_VENUE, Menu.NONE, R.string.nearby_menu_add_venue) //
                .setIcon(R.drawable.ic_menu_add);

        // Shows a map of all nearby venues, works but not going into this
        // version.
        // menu.add(Menu.NONE, MENU_MAP, Menu.NONE, "Map")
        // .setIcon(R.drawable.ic_menu_places);

        //MenuUtils.addPreferencesToMenu(this, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_REFRESH:
                if (mStateHolder.getIsRunningTask() == false) {
                    startTask();
                }
                return true;
            case MENU_SEARCH:
                Intent intent = new Intent(NearbyVenuesActivity.this, SearchVenuesActivity.class);
                intent.setAction(Intent.ACTION_SEARCH);
                startActivity(intent);
                return true;
            case MENU_ADD_VENUE:
                startActivity(new Intent(NearbyVenuesActivity.this, AddVenueActivity.class));
                return true;
            case MENU_MAP:
                startActivity(new Intent(NearbyVenuesActivity.this, NearbyVenuesMapActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
*/
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
    
    private void startTask() {
        if (DEBUG) Log.d(TAG, "startTask");
        if (mStateHolder.getIsRunningTask() == false) {
            if (DEBUG) Log.d(TAG, "startTask mIsRunning false");
            setProgressBarIndeterminateVisibility(true);
            if (mStateHolder.getResults().size() == 0) {
                setLoadingView("");
            }
            if (DEBUG) Log.d(TAG, "startTask()");
            mStateHolder.startTask(this, mStateHolder.getQuery());
        }
    }
    
    private void onTaskComplete(List<ParkingSpace> result, Exception ex) {
        if (DEBUG) Log.d(TAG, "onTaskComplete()");
        if (result != null) {
            mStateHolder.setResults(result);
       } else {
            mStateHolder.setResults(new ArrayList<ParkingSpace>());
            //notification of failure ?
        }
        
        putResultsInAdapter(mStateHolder.getResults());
        setProgressBarIndeterminateVisibility(false);       

        mStateHolder.cancelAllTasks();
    }
/*
    *//** If location changes, auto-start a nearby parking space search. *//*
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
    }*/
    
    private static class ParkingSpacesListTask extends AsyncTask<Void, Void, ParkingSpaces> {
        private static final String TAG = "ParkingSpacesListTask";
        private static boolean DEBUG = false;
        
        private ParkingSpacesListActivity mActivity;
        private String mQuery;
        private Park mPark;
        private ParkDroid mParkDroid;
        private ParkingSpaces results = null;
        
        public ParkingSpacesListTask(ParkingSpacesListActivity activity, String query) {
           
            super();
            
            Log.d(TAG, "ParkingSpacesListTask()");
            mActivity = activity;
            mQuery = query;
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
                mActivity.onTaskComplete(results.getParkingSpaces(), null);
            }
        }

        public void setActivity(ParkingSpacesListActivity activity) {
            mActivity = activity;            
        }
        
    }
    
    private static class StateHolder {
        private List<ParkingSpace> mResults;
        private String mQuery;
        private ParkingSpacesListTask mTask;        

        public StateHolder() {            
            mResults = new ArrayList<ParkingSpace>();
            mTask = null;           
        }

        public void startTask(ParkingSpacesListActivity activity, String query) {
        	mTask = new ParkingSpacesListTask(activity, query);
        	mTask.execute();            
        }

        public void setActivity(ParkingSpacesListActivity activity) {
           if (mTask != null) {
        	   mTask.setActivity(activity);
           }
            
        }

        public String getQuery() {
            return mQuery;
        }

        public void setQuery(String query) {
            mQuery = query;
        }

        public List<ParkingSpace> getResults() {
            return mResults;
        }

        public void setResults(List<ParkingSpace> results) {
            mResults = results;
        }
        
        public boolean getIsRunningTask() {
            return mTask != null;
        }

        public void cancelAllTasks() {
            if (mTask != null) {
            	mTask.cancel(true);
            	mTask = null;
            }
        }
   } 
}
