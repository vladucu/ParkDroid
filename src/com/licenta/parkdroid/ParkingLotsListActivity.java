/**
 * 
 */
package com.licenta.parkdroid;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ListView;

/**
 * @author vladucu
 *
 */
public class ParkingLotsListActivity extends ListActivity {
    
    private ListView mListView;
    private ParkingLotListAdapter mParkingLotListAdapter;

    /* (non-Javadoc)
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        
        mListView = getListView();
        mListView.setAdapter(mParkingLotListAdapter);
    }

    
}
