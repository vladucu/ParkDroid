/**
 * 
 */
package com.licenta.parkdroid;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author vladucu
 *
 */
public class ParkingLotActivity extends ListActivity {
    
    public static final String TAG = "ParkingLotActivity";
    //debug mode
    public static final boolean DEBUG = true;
    
    private Handler mHandler;
    private ListView mListView;
    private SeparatedListAdapter mListAdapter;
    
    /* (non-Javadoc)
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        
        if (DEBUG) Log.d(TAG, "onCreate()");
        mHandler = new Handler();
        mListView = getListView();
        mListAdapter = new SeparatedListAdapter(this);
        
        mListView.setAdapter(mListAdapter);
        mListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               // Venue venue = (Venue) parent.getAdapter().getItem(position);
              //  startItemActivity(venue);
            }
        });
        
        setContentView(R.layout.parking_lot);
    }
    
    private class SeparatedListAdapter extends BaseAdapter {
        
        public final Map<String, Adapter> sections = new LinkedHashMap<String, Adapter>();
        public final ArrayAdapter<String> headers;
        public final static int TYPE_SECTION_HEADER = 0;

        public SeparatedListAdapter(Context context) {
            super();
            headers = new ArrayAdapter<String>(context, R.layout.list_header);
        }
        
        @Override
        public int getCount() {
         // total together all sections, plus one for each section header
            int total = 0;
            for (Adapter adapter : this.sections.values())
                total += adapter.getCount() + 1;
            return total;
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            for (Object section : this.sections.keySet()) {
                Adapter adapter = sections.get(section);
                int size = adapter.getCount() + 1;

                // check if position inside this section
                if (position == 0) return section;
                if (position < size) return adapter.getItem(position - 1);

                // otherwise jump into next section
                position -= size;
            }
            
            return null;
        }

        
        /* 
         * Get the row id associated with the specified position in the list.
         * @see android.widget.Adapter#getItemId(int)
         */
        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {            
                int sectionnum = 0;
                for (Object section : this.sections.keySet()) {
                    Adapter adapter = sections.get(section);
                    int size = adapter.getCount() + 1;

                    if (position == 0) return headers.getView(sectionnum, convertView, parent);
                    if (position < size) return adapter.getView(position - 1, convertView, parent);

                    // otherwise jump into next section
                    position -= size;
                    sectionnum++;
                }
                return null;            
        }
        
    }
    
}
