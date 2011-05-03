/**
 * 
 */
package com.licenta.parkdroid;

import com.licenta.park.types.ParkingLot;
import com.licenta.park.types.Reservation;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author vladucu
 *
 */
public class ParkingLotActivity extends Activity {
    
    public static final String TAG = "ParkingLotActivity";
    //debug mode
    public static final boolean DEBUG = true;
    
    private Handler mHandler;
    private StateHolder mStateHolder;
    
    public static final String INTENT_EXTRA_PARKING_LOT_ID = ParkDroid.PACKAGE_NAME + ".ParkingLotActivity.INTENT_EXTRA_PARKING_LOT_ID";
    public static final String INTENT_EXTRA_PARKING_LOT = ParkDroid.PACKAGE_NAME + ".ParkingLotActivity.INTENT_EXTRA_PARKING_LOT";
    private static final String EXTRA_PARKKING_LOT_RETURNED = ParkDroid.PACKAGE_NAME + ".ParkingLotActivity.EXTRA_PARKKING_LOT_RETURNED";
    
    private static final int RESULT_CODE_ACTIVITY_ADD_RESERVATION = 1;
    private static final int RESULT_CODE_ACTIVITY_RESERVATION = 2;
    
    private static final int MENU_CALL = 1;
    
    private BroadcastReceiver mLoggedOutReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (DEBUG) Log.d(TAG, "onReceive: " + intent);
            finish();
        }
    };

    //TODO add image to the view
    
    /* (non-Javadoc)
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        if (DEBUG) Log.d(TAG, "onCreate()");
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.parking_lot_activity);
        registerReceiver(mLoggedOutReceiver, new IntentFilter(ParkDroid.INTENT_ACTION_LOGGED_OUT));
        
        mHandler = new Handler();
        
        StateHolder holder = (StateHolder) getLastNonConfigurationInstance();
        if (holder == null) {
            mStateHolder = new StateHolder();
            if (getIntent().hasExtra(INTENT_EXTRA_PARKING_LOT)) {
                if (DEBUG) Log.d(TAG, "getIntent().hasExtra(INTENT_EXTRA_PARKING_LOT)");
                mStateHolder.setLoadType(StateHolder.LOAD_TYPE_PARKING_LOT_FULL);
                mStateHolder.setParkingLot((ParkingLot)getIntent().getParcelableExtra(INTENT_EXTRA_PARKING_LOT));                
            }
            else if (getIntent().hasExtra(INTENT_EXTRA_PARKING_LOT_ID)) {
                if (DEBUG) Log.d(TAG, "getIntent().hasExtra(INTENT_EXTRA_PARKING_LOT_ID)");
                mStateHolder.setLoadType(StateHolder.LOAD_TYPE_PARKING_LOT_ID);
                mStateHolder.setParkingLotId(getIntent().getStringExtra(INTENT_EXTRA_PARKING_LOT_ID));
                mStateHolder.startTaskParkingLot(this);
            }
            else {
                if (DEBUG) Log.d(TAG, "ParkingLotActivity needs an parking lot Id or parcelable extra");
                finish();
                return;
            }
        }
        else {
            if (DEBUG) Log.d(TAG, "mStateHolder != null");
            mStateHolder = holder;
            mStateHolder.setActivityForTasks(this);
            prepareResultIntent();
        }
        
        ensureUI();
    }

    
    
    private void ensureUI() {        
        if (DEBUG) Log.d(TAG, "ensureUI() mStateHolder.parkingLot="+mStateHolder.getParkingLot().getName());
        TextView tvParkingkLotActivityName = (TextView)findViewById(R.id.parkingkLotActivityName);
        TextView tvParkingLotActivityAddress = (TextView)findViewById(R.id.parkingLotActivityAddress);
        Button btnReserveNow = (Button)findViewById(R.id.parkingLotActivityButtonReserveNow);
        LinearLayout progress = (LinearLayout) findViewById(R.id.parkingLotActivityDetailsProgress);
        
        TextView tvParkingLotActivitySpaces = (TextView) findViewById(R.id.parkingLotActivitySpacesValue);
        TextView tvParkingLotActivityDistance = (TextView) findViewById(R.id.parkingLotActivityDistanceValue);
        TextView tvParkingLotActivityPrice = (TextView) findViewById(R.id.parkingLotActivityPriceValue);
        
        View viewUrl = findViewById(R.id.parkingLotActivityUrlDetails);
        TextView tvUrlText = (TextView) findViewById(R.id.parkingLotActivityUrl);
        ImageView ivUrlArrow = (ImageView) findViewById(R.id.parkingLotActivityUrlArrow); 
    /*
        TextView tvParkingLotActivitySpaces = (TextView)findViewById(R.id.parkingLotActivitySpaces);
        TextView tvParkingLotActivityDistance = (TextView)findViewById(R.id.parkingLotActivityDistance);
        TextView tvParkingLoatActivityPrice = (TextView)findViewById(R.id.parkingLotActivityPrice);*/
        
        ParkingLot parkingLot = mStateHolder.getParkingLot();
        
        if (mStateHolder.getLoadType() == StateHolder.LOAD_TYPE_PARKING_LOT_FULL) {
            if (DEBUG) Log.d(TAG, "ensureUI() LOAD_TYPE_PARKING_LOT_FULL");
            tvParkingkLotActivityName.setText(parkingLot.getName());
            tvParkingLotActivityAddress.setText(parkingLot.getAddress());
            tvParkingLotActivitySpaces.setText(parkingLot.getEmptySpaces()+"/"+parkingLot.getTotalSpaces());
            tvParkingLotActivityDistance.setText(parkingLot.getDistance()+"m");
            tvParkingLotActivityPrice.setText(parkingLot.getPrice()+"$/h");
            
            if (mStateHolder.getParkingLot().getUrl() != null) {
                tvUrlText.setText(parkingLot.getUrl());
                setClickHandlerUrl(viewUrl, parkingLot.getUrl());
                viewUrl.setVisibility(View.VISIBLE);
            } else {
                viewUrl.setVisibility(View.GONE);
            }
            
            if (mStateHolder.getParkingLot().getHasReservation()) {                
                btnReserveNow.setEnabled(false);
            }
            else {                
                btnReserveNow.setEnabled(true);
                btnReserveNow.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (DEBUG) Log.d(TAG, "ensureUI() button enabled clicked");
                        Intent intent = new Intent(ParkingLotActivity.this, AddReservationActivity.class);
                        intent.putExtra(AddReservationActivity.INTENT_EXTRA_PARKING_LOT, mStateHolder.getParkingLot());
                        startActivityForResult(intent, RESULT_CODE_ACTIVITY_ADD_RESERVATION);                        
                    }                    
                });
            }
        }
        ensureUiReservationHere();
        progress.setVisibility(View.GONE);
    }

    private void ensureUiReservationHere() {
        if (DEBUG) Log.d(TAG, "ensureUiReservationHere()");
        final ParkingLot parkingLot = mStateHolder.getParkingLot();
        RelativeLayout rlReservationHere = (RelativeLayout) findViewById(R.id.parkingLotActivityReservationHere); 
        if (parkingLot != null && parkingLot.getHasReservation()) {
            rlReservationHere.setVisibility(View.VISIBLE);
            rlReservationHere.setOnClickListener(new OnClickListener() {
                
                @Override
                public void onClick(View arg0) {
                    if (DEBUG) Log.d(TAG, "ensureUiReservationHere() onclick");
                    //TODO working I think....check when REST services ready
                    /*Intent intent = new Intent(ParkingLotActivity.this, ReservationActivity.class);
                    intent.putExtra(ReservationActivity.INTENT_EXTRA_RESERVATION, parkingLot.getReservation());
                    startActivityForResult(intent, RESULT_CODE_ACTIVITY_RESERVATION);*/
                }
            });
        }
        else {
            rlReservationHere.setVisibility(View.GONE);
        }
    }
    
    /*
     * Launches the browser and opens the parking lot url link
     */
    private void setClickHandlerUrl(View view, final String address) {
        //TODO de rezolvat pentru linkuri lungi
        view.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View arg0) {
                Uri uriUrl = Uri.parse(address);
                Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                startActivity(launchBrowser);                
            }
        });       
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        //TODO process add reservation returned code
        switch (requestCode) {
            case RESULT_CODE_ACTIVITY_ADD_RESERVATION:
                if (resultCode == Activity.RESULT_OK) {
                    if (DEBUG) Log.d(TAG, "onActivityResult() returned with reservation done succesfully");
                    Toast.makeText(ParkingLotActivity.this, "Returned from add reservation with succes", Toast.LENGTH_LONG);
                    break;
                }                
        }
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onDestroy()
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mLoggedOutReceiver);
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onResume()
     */
    @Override
    protected void onResume() {
        super.onResume();
        //ensureUiCheckinButton();
        // TODO: ensure mayor photo.
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onRetainNonConfigurationInstance()
     */
    @Override
    public Object onRetainNonConfigurationInstance() {
        // TODO Auto-generated method stub
        //return super.onRetainNonConfigurationInstance();
        mStateHolder.setActivityForTasks(null);
        return mStateHolder;
    }
    
    private void prepareResultIntent() {
        if (DEBUG) Log.d(TAG, "prepareResultIntent()");
        ParkingLot parkingLot = mStateHolder.getParkingLot();

        Intent intent = new Intent();
        if (parkingLot != null) {
            intent.putExtra(EXTRA_PARKKING_LOT_RETURNED, parkingLot);
        }
        setResult(Activity.RESULT_OK, intent);
    }    
    
    /* (non-Javadoc)
     * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // TODO sa vedem ce mai putem adauga aici
        super.onCreateOptionsMenu(menu);
        
        menu.add(Menu.NONE, MENU_CALL, 1, R.string.parking_lot_activity_menu_call).setIcon(R.drawable.ic_menu_call);
        
        return true;
    }    

    /* (non-Javadoc)
     * @see android.app.Activity#onPrepareOptionsMenu(android.view.Menu)
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean callEnabled = mStateHolder.getParkingLot() != null && !TextUtils.isEmpty(mStateHolder.getParkingLot().getPhone());
        menu.findItem(MENU_CALL).setEnabled(callEnabled);
        
        return super.onPrepareOptionsMenu(menu);
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_CALL:
                try {
                    Intent dial = new Intent();
                    dial.setAction(Intent.ACTION_DIAL);
                    dial.setData(Uri.parse("tel:" + mStateHolder.getParkingLot().getPhone()));
                    startActivity(dial);
                } catch (Exception ex) {
                    Log.e(TAG, "Error starting phone dialer intent.", ex);
                    Toast.makeText(this, "Sorry, we couldn't find any app to place a phone call!",
                            Toast.LENGTH_SHORT).show();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }



    private static class TaskParkingLot extends AsyncTask<String, Void, ParkingLot> {

        public static final String TAG = "TaskParkingLot";
        //debug mode
        public static final boolean DEBUG = true;
        
        private ParkingLotActivity mActivity;
        
        public TaskParkingLot(ParkingLotActivity activity) {
            if (DEBUG) Log.d(TAG, "TaskParkingLot()");            
            mActivity = activity;
        }
        
        /* (non-Javadoc)
         * @see android.os.AsyncTask#onPreExecute()
         */
        @Override
        protected void onPreExecute() {   
            if (DEBUG) Log.d(TAG, "onPreExecute()");
        }
        
        @Override
        protected ParkingLot doInBackground(String... params) {
            if (DEBUG) Log.d(TAG, "onPreExecute()");
            try {
                ParkDroid parkDroid = (ParkDroid) mActivity.getApplication();
                return parkDroid.getPark().parkingLot(params[0]);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                if (DEBUG) Log.d(TAG, "Error getting parking lot details");
                e.printStackTrace();
            }
            return null;
        }
        
        /* (non-Javadoc)
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        protected void onPostExecute(ParkingLot parkingLot) {
            // TODO Auto-generated method stub
            if (DEBUG) Log.d(TAG, "onPreExecute()"); 
            
            if (mActivity != null) {
                mActivity.mStateHolder.setIsRunningTaskParkingLot(false);
                if (parkingLot != null) {
                    mActivity.mStateHolder.setLoadType(StateHolder.LOAD_TYPE_PARKING_LOT_FULL);
                    mActivity.mStateHolder.setParkingLot(parkingLot);
                    mActivity.prepareResultIntent();
                    mActivity.ensureUI();
                    
                }
                else {
                    mActivity.finish();
                }
                //TODO verifica si asta la sfarsit
                /*else {
                    NotificationsUtil.ToastReasonForFailure(mActivity, mReason);
                    mActivity.finish();
                }*/
            }
        }

        public void setActivity(ParkingLotActivity activity) {
           mActivity = activity;            
        }
        
        
    }
        
    private static final class StateHolder {
        
        public static final String TAG = "StateHolder";
        //debug mode
        public static final boolean DEBUG = true;
        
        private boolean mIsRunningTaskParkingLot;
        private ParkingLot mParkingLot;
        private String mParkingLotId;
        private TaskParkingLot mTaskParkingLot;
        private int mLoadType;
        
        private static final int LOAD_TYPE_PARKING_LOT_ID = 0;
        private static final int LOAD_TYPE_PARKING_LOT_FULL = 1;
        
        
        public StateHolder() {
            if (DEBUG) Log.d(TAG, "StateHolder()");
            mIsRunningTaskParkingLot = false;
        }
        
        public void setActivityForTasks(ParkingLotActivity activity) {
            if (mTaskParkingLot != null) {
                mTaskParkingLot.setActivity(activity);
            }            
        }

        public void setParkingLot(ParkingLot parkingLot) {
            if (DEBUG) Log.d(TAG, "setParkingLot()");
            mParkingLot = parkingLot;
        }
        
        public ParkingLot getParkingLot() {
            if (DEBUG) Log.d(TAG, "getParkingLot()");
            return mParkingLot;
        }
        
        public void setParkingLotId(String id) {
            if (DEBUG) Log.d(TAG, "setParkingLotId()");
            mParkingLotId = id;
        }
        
        public void setLoadType(int loadType) {
            if (DEBUG) Log.d(TAG, "setLoadType()");
            mLoadType = loadType;
        }
        
        public int getLoadType() {
            if (DEBUG) Log.d(TAG, "getLoadType()");
            return mLoadType;
        }
        
        public void setIsRunningTaskParkingLot(boolean mIsRunningTaskParkingLot) {
            if (DEBUG) Log.d(TAG, "setIsRunningTaskParkingLot()");
            mIsRunningTaskParkingLot = mIsRunningTaskParkingLot;
        }
        
        public void startTaskParkingLot(ParkingLotActivity activity) {
            if (DEBUG) Log.d(TAG, "startTaskParkingLot()");
            if (!mIsRunningTaskParkingLot) {
                mIsRunningTaskParkingLot = true;
                mTaskParkingLot = new TaskParkingLot(activity);
                if (mLoadType == LOAD_TYPE_PARKING_LOT_ID) {
                    mTaskParkingLot.execute(mParkingLotId);
                }
                else {
                    mTaskParkingLot.execute(mParkingLot.getId());
                }
            }
        }
    }
    

}
