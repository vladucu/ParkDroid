/**
 * 
 */
package com.licenta.parkdroid;

import com.licenta.parkdroid.types.ParkingSpace;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @author vladucu
 *
 */
public class ParkingSpaceActivity extends Activity {
    
    public static final String TAG = "ParkingSpaceActivity";
    //debug mode
    public static final boolean DEBUG = ParkDroid.DEBUG;
    
    private Handler mHandler;
    private StateHolder mStateHolder;
    
    public static final String INTENT_EXTRA_PARKING_SPACE_ID = ParkDroid.PACKAGE_NAME + ".ParkingSpaceActivity.INTENT_EXTRA_PARKING_SPACE_ID";
    public static final String INTENT_EXTRA_PARKING_SPACE = ParkDroid.PACKAGE_NAME + ".ParkingSpaceActivity.INTENT_EXTRA_PARKING_SPACE";
    
    private static final int RESULT_CODE_ACTIVITY_ADD_RESERVATION = 1;
    
    private static final int MENU_CALL = 1;
    
    private BroadcastReceiver mLoggedOutReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (DEBUG) Log.d(TAG, "onReceive: " + intent);
            finish();
        }
    };

    //TODO add image to the view
    
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
               mStateHolder.setParkingSpace((ParkingSpace)getIntent().getParcelableExtra(INTENT_EXTRA_PARKING_SPACE));                
            } else {
                if (DEBUG) Log.d(TAG, "ParkingSpaceActivity needs an parking space parcelable extra");
                finish();
                return;
            }
        } else {
            if (DEBUG) Log.d(TAG, "mStateHolder != null");
            mStateHolder = holder;
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
       
        final ParkingSpace parkingSpace = mStateHolder.getParkingSpace();
        tvParkingkSpaceActivityName.setText(parkingSpace.getName());
        tvParkingSpaceActivityAddress.setText(parkingSpace.getAddress());
        tvParkingSpaceActivitySpaces.setText(Integer.toString(parkingSpace.getSpaces()));
        tvParkingSpaceActivityDistance.setText(parkingSpace.getDistance());
        tvParkingSpaceActivityPrice.setText(Integer.toString(parkingSpace.getPrice()) + "$");
        
        btnReserveNow.setText(R.string.parking_lot_activity_reserve_now_button);
        btnBack.setText(R.string.parking_lot_activity_button_back);
        setTitle("View ParkingSpace - " + parkingSpace.getName());        
            
        if (parkingSpace.getPhone() != null) {
            tvPhoneText.setText(parkingSpace.getPhone());
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
                
        btnReserveNow.setEnabled(true);
        btnReserveNow.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DEBUG) Log.d(TAG, "ensureUI() button enabled clicked");
                Intent intent = new Intent(ParkingSpaceActivity.this, AddReservationActivity.class);
                intent.putExtra(AddReservationActivity.INTENT_EXTRA_PARKING_SPACE, parkingSpace);
                startActivityForResult(intent, RESULT_CODE_ACTIVITY_ADD_RESERVATION);                        
            }                    
        });    
        
        btnBack.setOnClickListener(new OnClickListener() {            
            @Override
            public void onClick(View v) {
                if (DEBUG) Log.d(TAG, "ensureUI() going back button pushed");
                finish();                
            }
        });
        progress.setVisibility(View.GONE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case RESULT_CODE_ACTIVITY_ADD_RESERVATION:
                if (resultCode == Activity.RESULT_OK && data.hasExtra(AddReservationActivity.INTENT_EXTRA_RETURNED_RESERVATION)) {
                    if (DEBUG) Log.d(TAG, "onActivityResult() returned with reservation done succesfully");
                    sendBroadcast(new Intent(ActiveReservationsListActivity.REFRESH_INTENT));               
                    finish();
                    break;
                }                
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mLoggedOutReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public Object onRetainNonConfigurationInstance() {
        return mStateHolder;
    }

    private static final class StateHolder {
        
        public static final String TAG = "StateHolder - ParkingSpaceActivity";

        private ParkingSpace mParkingSpace;        
        
        public StateHolder() {
            if (DEBUG) Log.d(TAG, "StateHolder()");        
        }
        
        public void setParkingSpace(ParkingSpace parkingSpace) {
            if (DEBUG) Log.d(TAG, "setParkingSpace()");
            mParkingSpace = parkingSpace;
        }
        
        public ParkingSpace getParkingSpace() {
            if (DEBUG) Log.d(TAG, "getParkingSpace()");
            return mParkingSpace;
        }       
    }
}
