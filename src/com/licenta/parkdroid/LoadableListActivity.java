/**
 * 
 */
package com.licenta.parkdroid;

import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * @author vladucu
 *
 */
public class LoadableListActivity extends ListActivity {
    private static final String TAG = "LoadableListActivity";
    private static final boolean DEBUG = true;

    private int mNoSearchResultsString = getNoSearchResultsStringId();

    private ProgressBar mEmptyProgress;
    private TextView mEmptyText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        if (DEBUG) Log.d(TAG, "onCreate()");
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.loadable_list_activity);
        mEmptyProgress = (ProgressBar) findViewById(R.id.emptyProgress);
        mEmptyText = (TextView) findViewById(R.id.emptyText);
        setLoadingView();

        getListView().setDividerHeight(0);
    }

    public void setEmptyView() {
        mEmptyProgress.setVisibility(View.GONE);
        mEmptyText.setText(mNoSearchResultsString);
    }

    public void setLoadingView() {
        if (DEBUG) Log.d(TAG, "setLoadingView()");
        mEmptyProgress.setVisibility(View.VISIBLE);
        mEmptyText.setText("");// R.string.loading);
    }

    public void setLoadingView(String loadingText) {
        if (DEBUG) Log.d(TAG, "setLoadingView(loadingText)");
        setLoadingView();
        mEmptyText.setText(loadingText);
    }

    public int getNoSearchResultsStringId() {
        return R.string.no_search_results;
    }

}
