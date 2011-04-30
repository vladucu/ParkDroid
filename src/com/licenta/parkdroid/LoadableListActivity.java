<<<<<<< HEAD
/**
 * 
 */
=======
>>>>>>> dev4
package com.licenta.parkdroid;

import android.app.ListActivity;
import android.os.Bundle;
<<<<<<< HEAD
import android.util.Log;
=======
>>>>>>> dev4
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * @author vladucu
 *
 */
public class LoadableListActivity extends ListActivity {
<<<<<<< HEAD
    private static final String TAG = "LoadableListActivity";
    private static final boolean DEBUG = true;
=======
>>>>>>> dev4

    private int mNoSearchResultsString = getNoSearchResultsStringId();

    private ProgressBar mEmptyProgress;
    private TextView mEmptyText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
<<<<<<< HEAD
        
        if (DEBUG) Log.d(TAG, "onCreate()");
=======
>>>>>>> dev4
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
<<<<<<< HEAD
        if (DEBUG) Log.d(TAG, "setLoadingView()");
=======
>>>>>>> dev4
        mEmptyProgress.setVisibility(View.VISIBLE);
        mEmptyText.setText("");// R.string.loading);
    }

    public void setLoadingView(String loadingText) {
<<<<<<< HEAD
        if (DEBUG) Log.d(TAG, "setLoadingView(loadingText)");
=======
>>>>>>> dev4
        setLoadingView();
        mEmptyText.setText(loadingText);
    }

    public int getNoSearchResultsStringId() {
        return R.string.no_search_results;
    }
<<<<<<< HEAD

=======
>>>>>>> dev4
}
