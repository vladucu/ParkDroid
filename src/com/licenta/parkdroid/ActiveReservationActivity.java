package com.licenta.parkdroid;

import com.licenta.park.types.Reservation;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

/**
 * @author vladucu
 *
 */
public class ActiveReservationActivity extends Activity {

    public static final String TAG = "ActiveReservationActivity";
    public static final boolean DEBUG = true;
    
    public static final String INTENT_EXTRA_RESERVATION = ParkDroid.PACKAGE_NAME + ".ActiveReservationActivity.INTENT_EXTRA_RESERVATION";
    private static final int RESULT_CODE_ACTIVITY_EXTEND_RESERVATION = 1;
    
    private Handler mHandler;
    private StateHolder mStateHolder;
    
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
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        registerReceiver(mLoggedOutReceiver, new IntentFilter(ParkDroid.INTENT_ACTION_LOGGED_OUT));
        setContentView(R.layout.reservation_activity);
        
        mHandler = new Handler();
        
        StateHolder holder = (StateHolder) getLastNonConfigurationInstance();
        if (holder == null) {
            
            mStateHolder = new StateHolder();
            if (getIntent().hasExtra(INTENT_EXTRA_RESERVATION)) {
                mStateHolder.setReservation((Reservation) getIntent().getParcelableExtra(INTENT_EXTRA_RESERVATION));
            }
            else {
                if (DEBUG) Log.d(TAG, "ActiveReservationActivity needs an reservation parcelable extra");
                finish();
                return;
            }
        }
        else {
            if (DEBUG) Log.d(TAG, "onCreate4()");
            mStateHolder = holder;
        }
        
        ensureUi();
    }
    
    private void ensureUi() {
        if (DEBUG) Log.d(TAG, "ensureUi()");
        TextView tvReservationActivityParkingLotName = (TextView) findViewById(R.id.reservationActivityParkingLotName);
        TextView tvReservationActivityParkingLotAddress = (TextView) findViewById(R.id.reservationActivityParkingLotAddress);
   
        TextView tvResercationActivityId = (TextView) findViewById(R.id.reservationActivityId); 
        View viewId = findViewById(R.id.reservationActivityIdDetails);
        
        //View viewNavigation = findViewById(R.id.reservationActivityNavigationDetails);
        
        Button btnExtend = (Button)findViewById(R.id.reservationActivityButtonExtend);
        Button btnCancel = (Button)findViewById(R.id.reservationActivityButtonCancel);
        
        final Reservation reservation = mStateHolder.getReservation();
        tvReservationActivityParkingLotName.setText(reservation.getParkingSpace().getName());
        tvReservationActivityParkingLotAddress.setText(reservation.getParkingSpace().getAddress());        
   
        
        tvResercationActivityId.setText(Integer.toString(reservation.getId()));
        viewId.setVisibility(View.VISIBLE);
        
        updateView();
 /*        
         * We could put a link to get navigation to the parking lot,
         * but the Navigation App is still beta, not official and works only i US
         
        viewNavigation.setVisibility(View.GONE);        
        viewNavigation.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                //starting navigation intent
                if (DEBUG) Log.d(TAG, "Starting navigation LAT=" + reservation.getParkingSpace().getGeoLat()
                        + "LONG=" + reservation.getParkingSpace().getGeoLong());
                Uri navigationUri = Uri.parse("google.navigation:ll=" + reservation.getParkingSpace().getGeoLat() + "," +
                        reservation.getParkingSpace().getGeoLat());                        
                Intent intent = new Intent(Intent.ACTION_VIEW, navigationUri);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW, 
                        Uri.parse("http://maps.google.com/maps?saddr="+reservation.getParkingLot().getGeolat()+"&daddr="+reservation.getParkingLot().getGeolong()));
                        startActivity(intent);
                
            }
        });*/
        
        btnExtend.setEnabled(true);
        btnExtend.setText(R.string.reservation_activity_extend_button);
        btnExtend.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                if (DEBUG) Log.d(TAG, "ensureUI() button extend clicked");
                final Reservation reservation = mStateHolder.getReservation(); 
                Intent intent = new Intent(ActiveReservationActivity.this, AddReservationActivity.class);
                intent.putExtra(AddReservationActivity.INTENT_EXTRA_PARKING_SPACE, reservation.getParkingSpace());
                intent.putExtra(AddReservationActivity.INTENT_EXTRA_RESERVATION, reservation);
                startActivityForResult(intent, RESULT_CODE_ACTIVITY_EXTEND_RESERVATION);       
            }
        });
        btnCancel.setEnabled(true);
        btnCancel.setText(R.string.reservation_activity_cancel_button);
        btnCancel.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                
                if (DEBUG) Log.d(TAG, "ensureUI() button cancel clicked");
            }
        });        
    }    
   
    public void updateView() {
    	if (DEBUG) Log.d(TAG, "updateView()");
    	TextView tvReservationActivityStartingTime = (TextView) findViewById(R.id.reservationActivityStartingTime);
        TextView tvReservationActivityEndingTime = (TextView) findViewById(R.id.reservationActivityEndingTime);
        
        TextView tvReservationActivityPrice = (TextView) findViewById(R.id.reservationActivityPriceValue);
        TextView tvReservationActivityTime = (TextView) findViewById(R.id.reservationActivityTimeValue);
        TextView tvReservationActivityCosts = (TextView) findViewById(R.id.reservationActivityTotalPriceValue);
        
        final Reservation reservation = mStateHolder.getReservation();
        tvReservationActivityPrice.setText(Integer.toString(reservation.getParkingSpace().getPrice()));
        tvReservationActivityStartingTime.setText(reservation.getStartTime());
        tvReservationActivityEndingTime.setText(reservation.getEndTime());
        tvReservationActivityTime.setText(reservation.getTotalTime());
        tvReservationActivityCosts.setText(Integer.toString(reservation.getCost()));
    }
    
    private void prepareResultIntent() {
        Reservation reservation = mStateHolder.getReservation();

        Intent intent = new Intent();
        if (reservation != null) {
            intent.putExtra(INTENT_EXTRA_RESERVATION, reservation);
        }
        setResult(Activity.RESULT_OK, intent);
    }
    
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			case RESULT_CODE_ACTIVITY_EXTEND_RESERVATION:
				if (resultCode == Activity.RESULT_OK) {
					Reservation reservation = data.getParcelableExtra(AddReservationActivity.INTENT_EXTRA_RETURNED_RESERVATION);
					mStateHolder.setReservation(reservation);		
					updateView();
					prepareResultIntent();
				}
				break;
				
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


    private static class StateHolder {
        
        private Reservation mReservation;
        
        public void StateHolder() {            
        }
        
        public void setReservation(Reservation reservation) {
            mReservation = reservation;
        }
        
        public Reservation getReservation() {
            return mReservation;         
        }
    }
}
