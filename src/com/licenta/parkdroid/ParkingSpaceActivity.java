/**
 * 
 */
package com.licenta.parkdroid;

import com.licenta.park.types.ParkingSpace;
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
public class ParkingSpaceActivity extends Activity {
    
    public static final String TAG = "ParkingSpaceActivity";
    //debug mode
    public static final boolean DEBUG = false;
    
    private Handler mHandler;
    private StateHolder mStateHolder;
    
    public static final String INTENT_EXTRA_PARKING_SPACE_ID = ParkDroid.PACKAGE_NAME + ".ParkingSpaceActivity.INTENT_EXTRA_PARKING_SPACE_ID";
    public static final String INTENT_EXTRA_PARKING_SPACE = ParkDroid.PACKAGE_NAME + ".ParkingSpaceActivity.INTENT_EXTRA_PARKING_SPACE";
    private static final String EXTRA_PARKKING_SPACE_RETURNED = ParkDroid.PACKAGE_NAME + ".ParkingSpaceActivity.EXTRA_PARKKING_SPACE_RETURNED";
    
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
        setContentView(R.layout.parking_space_activity);
        registerReceiver(mLoggedOutReceiver, new IntentFilter(ParkDroid.INTENT_ACTION_LOGGED_OUT));
        
        mHandler = new Handler();
        
        StateHolder holder = (StateHolder) getLastNonConfigurationInstance();
        if (holder == null) {
            mStateHolder = new StateHolder();
            if (getIntent().hasExtra(INTENT_EXTRA_PARKING_SPACE)) {
                if (DEBUG) Log.d(TAG, "getIntent().hasExtra(INTENT_EXTRA_PARKING_LOT)");
                mStateHolder.setLoadType(StateHolder.LOAD_TYPE_PARKING_LOT_FULL);
                mStateHolder.setParkingSpace((ParkingSpace)getIntent().getParcelableExtra(INTENT_EXTRA_PARKING_SPACE));                
            }
            else if (getIntent().hasExtra(INTENT_EXTRA_PARKING_SPACE_ID)) {
                if (DEBUG) Log.d(TAG, "getIntent().hasExtra(INTENT_EXTRA_PARKING_LOT_ID)");
                mStateHolder.setLoadType(StateHolder.LOAD_TYPE_PARKING_LOT_ID);
                mStateHolder.setParkingSpaceId(getIntent().getStringExtra(INTENT_EXTRA_PARKING_SPACE_ID));
                mStateHolder.startTaskParkingSpace(this);
            }
            else {
                if (DEBUG) Log.d(TAG, "ParkingSpaceActivity needs an parking lot Id or parcelable extra");
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
        if (DEBUG) Log.d(TAG, "ensureUI() mStateHolder.parkingSpace="+mStateHolder.getParkingSpace().getName());
        TextView tvParkingkSpaceActivityName = (TextView)findViewById(R.id.parkingkLotActivityName);
        TextView tvParkingSpaceActivityAddress = (TextView)findViewById(R.id.parkingLotActivityAddress);
        Button btnReserveNow = (Button)findViewById(R.id.parkingLotActivityButtonReserveNow);
        Button btnBack = (Button) findViewById(R.id.parkingLotActivityButtonBack);
        LinearLayout progress = (LinearLayout) findViewById(R.id.parkingLotActivityDetailsProgress);
        
        TextView tvParkingSpaceActivitySpaces = (TextView) findViewById(R.id.parkingLotActivitySpacesValue);
        TextView tvParkingSpaceActivityDistance = (TextView) findViewById(R.id.parkingLotActivityDistanceValue);
        TextView tvParkingSpaceActivityPrice = (TextView) findViewById(R.id.parkingLotActivityPriceValue);
        
        View viewPhone = findViewById(R.id.parkingLotActivityPhoneDetails);
        TextView tvPhoneText = (TextView) findViewById(R.id.parkingLotActivityPhone);
        View viewOpenHours = findViewById(R.id.parkingLotActivityHourDetails);
        TextView tvOpenHours = (TextView) findViewById(R.id.parkingLotActivityHour);
       
        ParkingSpace parkingSpace = mStateHolder.getParkingSpace();
        
        if (mStateHolder.getLoadType() == StateHolder.LOAD_TYPE_PARKING_LOT_FULL) {
            if (DEBUG) Log.d(TAG, "ensureUI() LOAD_TYPE_PARKING_LOT_FULL");
            tvParkingkSpaceActivityName.setText(parkingSpace.getName());
            tvParkingSpaceActivityAddress.setText(parkingSpace.getAddress());
            tvParkingSpaceActivitySpaces.setText(Integer.toString(parkingSpace.getSpaces()));
            //tvParkingSpaceActivityDistance.setText(parkingSpace.getDistance()+"m");
            //tvParkingSpaceActivityPrice.setText(parkingSpace.getPrice()+"$/h");            
            btnReserveNow.setText(R.string.parking_lot_activity_reserve_now_button);
            btnBack.setText(R.string.parking_lot_activity_button_back);
            setTitle(getTitle()+ " - " + mStateHolder.getParkingSpace().getName());
       /*     
            if (mStateHolder.getParkingSpace().getPhone() != null) {
                tvPhoneText.setText(mStateHolder.getParkingSpace().getPhone());
                viewPhone.setOnClickListener(new OnClickListener() {                    
                    @Override
                    public void onClick(View v) {
                        Intent dial = new Intent();
                        dial.setAction(Intent.ACTION_DIAL);
                        dial.setData(Uri.parse("tel:" + mStateHolder.getParkingSpace().getPhone()));
                        startActivity(dial);
                    }
                });
                viewPhone.setVisibility(View.VISIBLE);                
            }
            
            if (mStateHolder.getParkingSpace().getOpenHours() != null) {
                tvOpenHours.setText(mStateHolder.getParkingSpace().getOpenHours());
                viewOpenHours.setVisibility(View.VISIBLE);
            }
            
            if (mStateHolder.getParkingSpace().getHasReservation()) {  
                btnReserveNow.setEnabled(false);
            }
            else {      */          
                btnReserveNow.setEnabled(true);
                btnReserveNow.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (DEBUG) Log.d(TAG, "ensureUI() button enabled clicked");
                        Intent intent = new Intent(ParkingSpaceActivity.this, AddReservationActivity.class);
                        intent.putExtra(AddReservationActivity.INTENT_EXTRA_PARKING_SPACE, mStateHolder.getParkingSpace());
                        startActivityForResult(intent, RESULT_CODE_ACTIVITY_ADD_RESERVATION);                        
                    }                    
                });
            }
      /*  }*/
        
        btnBack.setOnClickListener(new OnClickListener() {            
            @Override
            public void onClick(View v) {
                if (DEBUG) Log.d(TAG, "ensureUI() going back button pushed");
                finish();                
            }
        });
        //ensureUiReservationHere();
        progress.setVisibility(View.GONE);
    }

   /* private void ensureUiReservationHere() {
        if (DEBUG) Log.d(TAG, "ensureUiReservationHere()");
        final ParkingSpace parkingSpace = mStateHolder.getParkingSpace();
        RelativeLayout rlReservationHere = (RelativeLayout) findViewById(R.id.parkingLotActivityReservationHere); 
        if (parkingSpace != null && parkingSpace.getHasReservation()) {
            rlReservationHere.setVisibility(View.VISIBLE);
            rlReservationHere.setOnClickListener(new OnClickListener() {
                
                @Override
                public void onClick(View arg0) {
                    if (DEBUG) Log.d(TAG, "ensureUiReservationHere() onclick");
                    //TODO working I think....check when REST services ready
                    Intent intent = new Intent(ParkingSpaceActivity.this, ReservationActivity.class);
                    intent.putExtra(ReservationActivity.INTENT_EXTRA_RESERVATION, parkingSpace.getReservation());
                    startActivityForResult(intent, RESULT_CODE_ACTIVITY_RESERVATION);
                }
            });
        }
        else {
            rlReservationHere.setVisibility(View.GONE);
        }
    }*/

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
                    mStateHolder.setReservationHere(true);
                    Toast.makeText(ParkingSpaceActivity.this, "Returned from add reservation with succes", Toast.LENGTH_LONG);
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
        ParkingSpace parkingSpace = mStateHolder.getParkingSpace();

        Intent intent = new Intent();
        if (parkingSpace != null) {
            //intent.putExtra(EXTRA_PARKKING_SPACE_RETURNED, parkingSpace);
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

  /*   (non-Javadoc)
     * @see android.app.Activity#onPrepareOptionsMenu(android.view.Menu)
     
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean callEnabled = mStateHolder.getParkingSpace() != null && !TextUtils.isEmpty(mStateHolder.getParkingSpace().getPhone());
        menu.findItem(MENU_CALL).setEnabled(callEnabled);
        
        return super.onPrepareOptionsMenu(menu);
    }
*/
    /* (non-Javadoc)
     * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
     */
 /*   @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_CALL:
                try {
                    Intent dial = new Intent();
                    dial.setAction(Intent.ACTION_DIAL);
                    dial.setData(Uri.parse("tel:" + mStateHolder.getParkingSpace().getPhone()));
                    startActivity(dial);
                } catch (Exception ex) {
                    Log.e(TAG, "Error starting phone dialer intent.", ex);
                    Toast.makeText(this, "Sorry, we couldn't find any app to place a phone call!",
                            Toast.LENGTH_SHORT).show();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }*/



    private static class TaskParkingSpace extends AsyncTask<String, Void, ParkingSpace> {

        public static final String TAG = "TaskParkingSpace";
        //debug mode
        public static final boolean DEBUG = true;
        
        private ParkingSpaceActivity mActivity;
        
        public TaskParkingSpace(ParkingSpaceActivity activity) {
            if (DEBUG) Log.d(TAG, "TaskParkingSpace()");            
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
        protected ParkingSpace doInBackground(String... params) {
            if (DEBUG) Log.d(TAG, "onPreExecute()");
            ParkingSpace result = null;
            try {
                ParkDroid parkDroid = (ParkDroid) mActivity.getApplication();
                //result = parkDroid.getPark().parkingSpace(Integer.parseInt("1"));
            } catch (Exception e) {
                // TODO Auto-generated catch block
                if (DEBUG) Log.d(TAG, "Error getting parking lot details");
                e.printStackTrace();
            }
            return result;
        }
        
        /* (non-Javadoc)
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        protected void onPostExecute(ParkingSpace parkingSpace) {
            // TODO Auto-generated method stub
            if (DEBUG) Log.d(TAG, "onPreExecute()"); 
            
            if (mActivity != null) {
                mActivity.mStateHolder.setIsRunningTaskParkingSpace(false);
                if (parkingSpace != null) {
                    mActivity.mStateHolder.setLoadType(StateHolder.LOAD_TYPE_PARKING_LOT_FULL);
                    mActivity.mStateHolder.setParkingSpace(parkingSpace);
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

        public void setActivity(ParkingSpaceActivity activity) {
           mActivity = activity;            
        }        
    }
        
    private static final class StateHolder {
        
        public static final String TAG = "StateHolder";
        //debug mode
        public static final boolean DEBUG = true;
        
        private boolean mIsRunningTaskParkingSpace;
        private TaskParkingSpace mTaskParkingSpace;
        private boolean mReservationHere;
        private ParkingSpace mParkingSpace;
        private String mParkingSpaceId;        
        private int mLoadType;
        
        private static final int LOAD_TYPE_PARKING_LOT_ID = 0;
        private static final int LOAD_TYPE_PARKING_LOT_FULL = 1;
        
        
        public StateHolder() {
            if (DEBUG) Log.d(TAG, "StateHolder()");
            mIsRunningTaskParkingSpace = false;
        }
        
        public void setActivityForTasks(ParkingSpaceActivity activity) {
            if (mTaskParkingSpace != null) {
                mTaskParkingSpace.setActivity(activity);
            }            
        }

        public void setParkingSpace(ParkingSpace parkingSpace) {
            if (DEBUG) Log.d(TAG, "setParkingSpace()");
            mParkingSpace = parkingSpace;
        }
        
        public ParkingSpace getParkingSpace() {
            if (DEBUG) Log.d(TAG, "getParkingSpace()");
            return mParkingSpace;
        }
        
        public void setParkingSpaceId(String id) {
            if (DEBUG) Log.d(TAG, "setParkingSpaceId()");
            mParkingSpaceId = id;
        }
        
        public void setLoadType(int loadType) {
            if (DEBUG) Log.d(TAG, "setLoadType()");
            mLoadType = loadType;
        }
        
        public int getLoadType() {
            if (DEBUG) Log.d(TAG, "getLoadType()");
            return mLoadType;
        }
        
        public void setReservationHere(boolean reservation) {
            mReservationHere = reservation;
        }
        
        public boolean getReservation() {
            return mReservationHere;
        }
        
        public void setIsRunningTaskParkingSpace(boolean mIsRunningTaskParkingSpace) {
            if (DEBUG) Log.d(TAG, "setIsRunningTaskParkingSpace()");
            mIsRunningTaskParkingSpace = mIsRunningTaskParkingSpace;
        }
        
        public void startTaskParkingSpace(ParkingSpaceActivity activity) {
            if (DEBUG) Log.d(TAG, "startTaskParkingSpace()");
            if (!mIsRunningTaskParkingSpace) {
                mIsRunningTaskParkingSpace = true;
                mTaskParkingSpace = new TaskParkingSpace(activity);
                if (mLoadType == LOAD_TYPE_PARKING_LOT_ID) {
                    mTaskParkingSpace.execute(mParkingSpaceId);
                }
                else {
                    mTaskParkingSpace.execute(Integer.toString(mParkingSpace.getId()));
                }
            }
        }
    }
    

}
