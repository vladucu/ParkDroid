/**
 * 
 */
package com.licenta.parkdroid;


import com.licenta.park.types.ParkingLot;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import java.util.ArrayList;
import java.util.Set;

/**
 * @author vladucu
 *
 */
public class ParkingLotsListActivity extends LoadableListActivity {
    
    private static final String TAG = "ParkingLotsListActivity";
    private static boolean DEBUG = true;
    
    private StateHolder mStateHolder = new StateHolder();
    private ArrayList<ParkingLot> parkingLots = null;
    private ParkingLotListAdapter mParkingLotListAdapter;
    private ListView mListView;
    private Handler mHandler;    

    /* (non-Javadoc)
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        if (DEBUG) Log.d(TAG, "onCreate()");
      
        setContentView(R.layout.loadable_list_activity);
        mHandler = new Handler();
        mListView = getListView();
        mParkingLotListAdapter = new ParkingLotListAdapter(this, R.layout.parking_lot_list_item, mStateHolder.mResults);
        mListView.setAdapter(mParkingLotListAdapter);
        mListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (DEBUG) Log.d(TAG, "onCreate()+ on item click");
                ParkingLot parkingLot = (ParkingLot) parent.getAdapter().getItem(position);
                Toast.makeText(ParkingLotsListActivity.this, parkingLot.getName(), Toast.LENGTH_LONG).show();
            }
        });

        // We can dynamically add a footer to our loadable listview.
        LayoutInflater inflater = LayoutInflater.from(this);
    }
    
/*    public void putResultsInAdapter(Group<ParkingLot> results) {
        if (DEBUG) Log.d(TAG, "putResultsInAdapter()");
        
        ArrayAdapter<ParkingLot> mArrayAdapter = null;
        if (results !=null && results.size()>0) {
            if (DEBUG) Log.d(TAG, "putResultsInAdapter() if size="+results.size());
            int count = results.size();
            for (int index=0; index<count; index++) {
                //ParkingLotListAdapter mParkingLot = new  ParkingLotListAdapter(this);
               // if (DEBUG) Log.d(TAG, "putResultsInAdapter() parkignLot="+mParkingLot.getName());
               mArrayAdapter.add(results.get(index));
                
            }
        }
        mListView.setAdapter(mArrayAdapter);
    }
*/
    private static class StateHolder {
        private Group<ParkingLot> mResults;
        private String mQuery;
        private String mReverseGeoLoc;
        //private SearchTask mSearchTask;
        private Set<String> mFullyLoadedVenueIds;
        

        public StateHolder() {
            mResults = new Group<ParkingLot>();            
            ParkingLot mPark1 = new ParkingLot();
            ParkingLot mPark2 = new ParkingLot();
            mPark1.setId("123");
            mPark1.setName("Parcarea principala");
            mPark1.setPhone("0733683111");
            mPark1.setAddress("Bulevardul Regina Elisabeta 23");
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
            
            
            mResults.add(mPark1);mResults.add(mPark2);
            /*mSearchTask = null;
            mFullyLoadedVenueIds = new HashSet<String>();*/
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

        /*public void startTask(NearbyVenuesActivity activity, String query, long sleepTimeInMs) {
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

        public void setActivity(NearbyVenuesActivity activity) {
            if (mSearchTask != null) {
                mSearchTask.setActivity(activity);
            }
        }*/
   } 
}
