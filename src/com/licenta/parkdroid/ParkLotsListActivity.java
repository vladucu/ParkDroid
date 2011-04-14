package com.licenta.parkdroid;

import com.licenta.park.Park;
import com.licenta.park.types.Group;
import com.licenta.park.types.ParkLot;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

public class ParkLotsListActivity extends LoadableListActivity {
    
    static final String TAG = "ListActivity";
    static final boolean DEBUG = true;
    
    private StateHolder mStateHolder = new StateHolder();
    private SearchLocationObserver mSearchLocationObserver = new SearchLocationObserver();
    
    private ListView mListView;
    private SeparatedListAdapter mListAdapter;
    private LinearLayout mFooterView;
    private TextView mTextViewFooter;
    private Handler mHandler;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        if (DEBUG) Log.d(TAG, "onCreate()");
        
        mHandler = new Handler();
        mListView = getListView();
        mListAdapter = new SeparatedListAdapter(this);
        mListView.setAdapter(mListAdapter);
        mListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub
                Toast.makeText(ParkLotsListActivity.this, "click parking lot list item", Toast.LENGTH_LONG);
                
            }
            
        });      
        
        // We can dynamically add a footer to our loadable listview.
        LayoutInflater inflater = LayoutInflater.from(this);
        
        // Start a new search if one is not running or we have no results.
        if (mStateHolder.getIsRunningTask()) {
            setProgressBarIndeterminateVisibility(true);
            putSearchResultsInAdapter(mStateHolder.getResults());
            ensureTitle(false);
        } else if (mStateHolder.getResults().size() == 0) {
            long firstLocDelay = 0L;
            /*if (getIntent().getExtras() != null) {
                firstLocDelay = getIntent().getLongExtra(INTENT_EXTRA_STARTUP_GEOLOC_DELAY, 0L);
            }*/
            if (DEBUG) Log.d(TAG, "onCreate()+startTask(firstLocDelay)");
            startTask(firstLocDelay);
        } else {
            onTaskComplete(mStateHolder.getResults(), mStateHolder.getReverseGeoLoc(), null);
        }
    }    
    
    /* (non-Javadoc)
     * @see android.app.ListActivity#onDestroy()
     */
    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onResume()
     */
    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
    }   

    /* (non-Javadoc)
     * @see android.app.Activity#onPause()
     */
    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onRetainNonConfigurationInstance()
     */
    @Override
    public Object onRetainNonConfigurationInstance() {
        // TODO Auto-generated method stub
        mStateHolder.setActivity(null);
        return mStateHolder;
    }

    public void putSearchResultsInAdapter(Group<ParkLot> searchResults) {
        if (DEBUG) Log.d(TAG, "putSearchResultsInAdapter");

        mListAdapter.removeObserver();
        mListAdapter = new SeparatedListAdapter(this);
        if (searchResults != null && searchResults.size() > 0) {
            int groupCount = searchResults.size();
            for (int groupsIndex = 0; groupsIndex < groupCount; groupsIndex++) {
                ParkLot parkLot = searchResults.get(groupsIndex);  
                if (DEBUG) Log.d(TAG, "put parklot.name="+parkLot.getName());
                ParkLotListAdapter parkLotListAdapter = new ParkLotListAdapter(this);
                parkLotListAdapter.setGroup(parkLot);
               // if (DEBUG) Log.d(TAG, "Adding Section: " + parkLotListAdapter.getItem(0).toString());
                mListAdapter.addSection("Section", parkLotListAdapter);                
        }
        } else {
            if (DEBUG) Log.d(TAG, "setemptyview");
            setEmptyView();
        }
        if (DEBUG) Log.d(TAG, "put parklot.name="+mListAdapter.sections.hashCode());
        mListView.setAdapter(mListAdapter);
    }

    private void ensureTitle(boolean finished) {
        if (DEBUG) Log.d(TAG, "ensureTitle("+finished+")");
        if (finished) {
            setTitle(getString(R.string.title_search_finished_noquery));
        } else {
            setTitle(getString(R.string.title_search_inprogress_noquery));
        }
    }
    
   /* private void populateFooter(String reverseGeoLoc) {
        mFooterView.setVisibility(View.VISIBLE);
        mTextViewFooter.setText(reverseGeoLoc);
    }*/

    /** If location changes, auto-start a nearby parking lots search. */
    private class SearchLocationObserver implements Observer {

        private boolean mRequestedFirstSearch = false;

        @Override
        public void update(Observable observable, Object data) {
            Location location = (Location) data;
            // Fire a search if we haven't done so yet
            //TODO extend by checking location accuracy
            if (!mRequestedFirstSearch) {
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
    
    private void startTask(long geoLocDelayTimeInMs) {
        if (mStateHolder.getIsRunningTask() == false) {
            if (DEBUG) Log.d(TAG, "startTask !isRunning");
            setProgressBarIndeterminateVisibility(true);
            ensureTitle(false);
            if (mStateHolder.getResults().size() == 0) {
                setLoadingView("");
            }
            mStateHolder.startTask(this, mStateHolder.getQuery(), geoLocDelayTimeInMs);
        }
    }

    private void onTaskComplete(Group<ParkLot> result, String reverseGeoLoc, Exception ex) {
        if (result != null) {
            if (DEBUG) Log.d(TAG, "onTaskComplete !results");
            mStateHolder.setResults(result);
            mStateHolder.setReverseGeoLoc(reverseGeoLoc);
        } else {
            mStateHolder.setResults(new Group<ParkLot>());
            //NotificationsUtil.ToastReasonForFailure(ParkLotsListActivity.this, ex);
        }

        //populateFooter(mStateHolder.getReverseGeoLoc());
        putSearchResultsInAdapter(mStateHolder.getResults());
        setProgressBarIndeterminateVisibility(false);
        ensureTitle(true);
        if (DEBUG) Log.d(TAG, "onTaskComplete cancell allTasks");
        mStateHolder.cancelAllTasks();
    }

    
    /** Handles the work of finding nearby parking lots. */
    private static class SearchTask extends AsyncTask<Void, Void, Group<ParkLot>> {

        private ParkLotsListActivity mActivity;
        private Exception mReason = null;
        private String mQuery;
        private long mSleepTimeInMs;
        private Park mPark;
        private String mReverseGeoLoc; // Filled in after execution.
        private String mNoLocException;
        private String mLabelNearby;

        public SearchTask(ParkLotsListActivity activity, String query, long sleepTimeInMs) {
            super();
            mActivity = activity;
            mQuery = query;
            mSleepTimeInMs = sleepTimeInMs;
            mPark = ((ParkDroid) activity.getApplication()).getPark();
            mNoLocException = activity.getResources().getString(R.string.nearby_venues_no_location);
            mLabelNearby = activity.getResources().getString(R.string.nearby_venues_label_nearby);
        }

        @Override
        public void onPreExecute() {
        }

        @Override
        public Group<ParkLot> doInBackground(Void... params) {

            try {
                // If the user has chosen to clear their geolocation on each
                // search, wait briefly
                // for a new fix to come in. The two-second wait time is
                // arbitrary and can be
                // changed to something more intelligent.
                if (mSleepTimeInMs > 0L) {
                    Thread.sleep(mSleepTimeInMs);
                }

                // Get last known location.
                /*Location location = ((ParkDroid) mActivity.getApplication())
                        .getLastKnownLocation();
                if (location == null) {
                    throw new Exception(mNoLocException);
                }*/                
                Location location = null;
                // Get the parking lots.
                //TODO use LocationUtils
                Group<ParkLot> results = mPark.parkLots(location, mQuery, 30);

                // Try to get our reverse geolocation.
               // mReverseGeoLoc = getGeocode(mActivity, location);

                return results;

            } catch (Exception e) {
                mReason = e;
            }
            return null;
        }

        @Override
        public void onPostExecute(Group<ParkLot> groups) {
            if (mActivity != null) {
                mActivity.onTaskComplete(groups, mReverseGeoLoc, mReason);
            }
        }

        private String getGeocode(Context context, Location location) {
            Geocoder geocoded = new Geocoder(context, Locale.getDefault());
            try {
                List<Address> addresses = geocoded.getFromLocation(location.getLatitude(), location
                        .getLongitude(), 1);
                if (addresses.size() > 0) {
                    Address address = addresses.get(0);

                    StringBuilder sb = new StringBuilder(128);
                    sb.append(mLabelNearby);
                    sb.append(" ");
                    sb.append(address.getAddressLine(0));
                    if (addresses.size() > 1) {
                        sb.append(", ");
                        sb.append(address.getAddressLine(1));
                    }
                    if (addresses.size() > 2) {
                        sb.append(", ");
                        sb.append(address.getAddressLine(2));
                    }

                    if (!TextUtils.isEmpty(address.getLocality())) {
                        if (sb.length() > 0) {
                            sb.append(", ");
                        }
                        sb.append(address.getLocality());
                    }

                    return sb.toString();
                }
            } catch (Exception ex) {
                if (DEBUG) Log.d(TAG, "SearchTask: error geocoding current location.", ex);
            }

            return null;
        }

        public void setActivity(ParkLotsListActivity activity) {
            mActivity = activity;
        }
    }
    
    private static class StateHolder {
        private Group<ParkLot> mResults;
        private String mQuery;
        private String mReverseGeoLoc;
        private SearchTask mSearchTask;
        private Set<String> mFullyLoadedVenueIds;

        public StateHolder() {
            mResults = new Group<ParkLot>();
            mSearchTask = null;
            mFullyLoadedVenueIds = new HashSet<String>();
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

        public Group<ParkLot> getResults() {
            return mResults;
        }

        public void setResults(Group<ParkLot> results) {
            mResults = results;
        }

        public void startTask(ParkLotsListActivity activity, String query, long sleepTimeInMs) {
            mSearchTask = new SearchTask(activity, query, sleepTimeInMs);
            mSearchTask.execute();
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

        public void setActivity(ParkLotsListActivity activity) {
            if (mSearchTask != null) {
                mSearchTask.setActivity(activity);
            }
        }

       /* public void updateVenue(ParkLot parkLot) {
            for (ParkLot it : mResults) {
                for (int j = 0; j < it.size(); j++) {
                    if (it.get(j).getId().equals(venue.getId())) {
                        // The /venue api call does not supply the venue's
                        // distance value,
                        // so replace it manually here.
                        ParkLot replaced = it.get(j);
                        ParkLot replacer = VenueUtils.cloneVenue(venue);
                        replacer.setDistance(replaced.getDistance());

                        it.set(j, replacer);
                        mFullyLoadedVenueIds.add(replacer.getId());
                    }
                }
            }
        }*/

        public boolean isFullyLoadedVenue(String vid) {
            return mFullyLoadedVenueIds.contains(vid);
        }
    }
}