/**
 * 
 */
package com.licenta.parkdroid;

import com.licenta.parkdroid.preferences.Preferences;
import com.licenta.parkdroid.types.ParkingSpace;
import com.licenta.parkdroid.types.ParkingSpaces;
import com.licenta.parkdroid.types.ParkingSpacesResource;
import com.licenta.parkdroid.types.Reservation;
import com.licenta.parkdroid.types.ReservationResource;
import com.licenta.parkdroid.types.Reservations;
import com.licenta.parkdroid.types.ReservationsResource;
import com.licenta.parkdroid.types.User;
import com.licenta.parkdroid.types.UserResource;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.LocationManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import java.io.IOException;
import java.util.Observer;
import org.restlet.data.ChallengeResponse;
import org.restlet.data.ChallengeScheme;
import org.restlet.engine.Engine;
import org.restlet.ext.httpclient.HttpClientHelper;
import org.restlet.ext.jackson.JacksonConverter;
import org.restlet.resource.ClientResource;

/**
 * @author vladucu
 *
 */
public class ParkDroid extends Application {
    
    private static final String TAG = "ParkDroidApp";
    //debug mode
    private static final boolean DEBUG = true;
    
    public static final String INTENT_ACTION_LOGGED_OUT = "com.licenta.parkdroid.intent.action.LOGGED_OUT";
    public static final String INTENT_ACTION_LOGGED_IN = "com.licenta.parkdroid.intent.action.LOGGED_IN";
    public static final String PACKAGE_NAME = "com.licenta.parkdroid";;
    public static final String DOMAIN = "89.37.147.104:8443";
    public static final String PROTOCOL = "https://";
    public static final String USERS = "/users";
    public static final String RESERVATIONS = "/reservations";
    public static final String PARKINGSPACES = "/parkingspaces";
    
    private ChallengeResponse authentication;
    private ChallengeScheme scheme = ChallengeScheme.HTTP_BASIC;
	private ParkingSpacesResource parkingSpacesResource;
	private UserResource userResource;
	private ReservationsResource reservationsResource;
	private ReservationResource reservationResource;
	
    private ClientResource clientResource;
    
    private SharedPreferences mPrefs;
    
    private TaskHandler mTaskHandler;
    private HandlerThread mTaskThread;
    private BestLocationListener mBestLocationListener;
    
    //sa vedem daca revenim de la login sau e start de aplicatie
    //private static boolean isLoggedIn = false;

    /* (non-Javadoc)
     * @see android.app.Application#onCreate()
     */
    @Override
    public void onCreate() {
        // TODO finish ParkDroid
    	if (DEBUG) Log.d(TAG, "onCreate()");
        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        
        // Sometimes we want the application to do some work on behalf of the
        // Activity. Lets do that
        // asynchronously.
        mTaskThread = new HandlerThread(TAG + "-AsyncThread");
        mTaskThread.start();
        mTaskHandler = new TaskHandler(mTaskThread.getLooper());
        
        Engine.getInstance().getRegisteredClients().clear();
		Engine.getInstance().getRegisteredClients().add(new HttpClientHelper(null));
		Engine.getInstance().getRegisteredConverters().clear();
		Engine.getInstance().getRegisteredConverters().add(new JacksonConverter());
		
        // Catch logins or logouts.
        new LoggedInOutBroadcastReceiver().register();
        
        // Log into ParkDroid, if we can.
        loadPark();

    }
    
    public boolean isReady() {
        /*if (isLoggedIn) return true;
        else return false;*/
        if (DEBUG) Log.d(TAG, "isReady()");
        return hasCredentials() && !TextUtils.isEmpty(getUserId());
    }
    
    public String getUserId() {
        return Preferences.getUserId(mPrefs);
    }
    
    public String getUserEmail() {
        return Preferences.getUserEmail(mPrefs);
    }
    public void requestUpdateUser() {
        mTaskHandler.sendEmptyMessage(TaskHandler.MESSAGE_UPDATE_USER);
    }
    
    private void loadPark() {

    	if (DEBUG) Log.d(TAG, "loadPark()");
        
        String email = mPrefs.getString(Preferences.PREFERENCE_LOGIN, null);
        String password = mPrefs.getString(Preferences.PREFERENCE_PASSWORD, null);        
        setCredentials(email, password);

        if (hasCredentials()) {
            if (DEBUG) Log.d(TAG, "loadCredentials() phoneNumber="+email);
            sendBroadcast(new Intent(INTENT_ACTION_LOGGED_IN));
        } else {
            sendBroadcast(new Intent(INTENT_ACTION_LOGGED_OUT));
        }
    }
    
    public void setCredentials(String email, String password) {        
    	   
        if (DEBUG) Log.d(TAG, "setCredentials email="+email);
        //mLogin = email;
        //mPassword = password;
        if (email == null || email.length() == 0 || password == null || password.length() == 0) {
            if (DEBUG) Log.d(TAG, "setCredentials() Clearing Credentials");
            authentication = null;
            
        } else {
        	if (DEBUG) Log.d(TAG, "setCredentials() email="+email+" password="+password);
            authentication = new ChallengeResponse(scheme, email, password);
        }    
    }
    
	public boolean hasCredentials() {
		return (authentication != null);
	}
    
    private class LoggedInOutBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (INTENT_ACTION_LOGGED_IN.equals(intent.getAction())) {
                //isLoggedIn = true;
                requestUpdateUser();
            }
        }

        public void register() {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(INTENT_ACTION_LOGGED_IN);
            intentFilter.addAction(INTENT_ACTION_LOGGED_OUT);
            registerReceiver(this, intentFilter);
        }
    }    
    
    private class TaskHandler extends Handler {

        private static final int MESSAGE_UPDATE_USER = 1;

        public TaskHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (DEBUG) Log.d(TAG, "handleMessage: " + msg.what);

            switch (msg.what) {
                case MESSAGE_UPDATE_USER:
                    try {
                        // Update user info
                        Log.d(TAG, "Updating user.");

             
                        /*User user = getPark().createUser(login, password);

                        Editor editor = mPrefs.edit();
                        Preferences.storeUser(editor, user);
                        editor.commit();*/
                    } catch (Error e) {
                        if (DEBUG) Log.d(TAG, "ParkDroid", e);
                    } catch (Exception e) {
                        if (DEBUG) Log.d(TAG, "ParkDroid", e);
                    }
                    return;
            }
        }
    }

    public BestLocationListener requestLocationUpdates(Observer observer) {
        if (DEBUG) Log.d(TAG, "requestLocationUpdates");
        mBestLocationListener.addObserver(observer);
        mBestLocationListener.register((LocationManager) getSystemService(Context.LOCATION_SERVICE), true);
        return mBestLocationListener;
        
    }

    public void removeLocationUpdates(Observer observer) {
        mBestLocationListener.unregister((LocationManager) getSystemService(Context.LOCATION_SERVICE));        
    }
        
    public User user(String login, String password) {
    	//TODO de vazut cu email si parola locala cum putem face
    	//Prepare the request
    	//clientResource = new ClientResource(PROTOCOL+DOMAIN/users/1");
    	
    	clientResource.setChallengeResponse(authentication);
    	
    	userResource = clientResource.wrap(UserResource.class);
    	User user = userResource.retrieveUser();
    	clientResource.release();
    	
		return user;
    }
    
    public String buildURI(String arg1, String arg2, String arg3) {
    	StringBuilder uri = new StringBuilder();
    	uri.append(PROTOCOL);uri.append(DOMAIN);
    	uri.append(USERS);
    	if (arg1 != null) {
    		uri.append("/"+arg1);
    		if (arg2 != null) {
    			uri.append(arg2);
    			uri.append("/"+arg3);    			
    		}
    	}
    	return uri.toString();
    }
    
    public ParkingSpaces parkingSpaces(int userId) throws IOException {
		if (DEBUG) Log.d(TAG, "parkingSpaces()");
		
		clientResource = new ClientResource(buildURI(Integer.toString(userId), PARKINGSPACES, null));
		clientResource.setChallengeResponse(authentication);
		parkingSpacesResource = clientResource.wrap(ParkingSpacesResource.class);
		ParkingSpaces parkingSpaces = parkingSpacesResource.retrieve();
		
		if (clientResource.getStatus().isSuccess()) {
			return parkingSpaces;
		}
		else {
			clientResource.getResponseEntity().exhaust();
		}
		
		clientResource.release();
		
		return parkingSpaces;
	}
    
	public Reservations getReservations() throws IOException {
		if (DEBUG) Log.d(TAG, "reservations()");
		clientResource = new ClientResource("http://10.0.2.2:8080/users/1/reservations");
		clientResource.setChallengeResponse(authentication);
		reservationsResource = clientResource.wrap(ReservationsResource.class);
		Reservations reservations = null;
		try {
			reservations = reservationsResource.getReservations();
		} catch (Exception e) {}
		if (clientResource.getStatus().isSuccess()) {
			return reservations;
		}
		else {
			clientResource.getResponseEntity().exhaust();
		}
	
		clientResource.release();
	
		return reservations;	
	}
	
	public  Reservation createReservation(int userId, ParkingSpace mParkingSpace, String mStartTime, String mEndTime, String mTotalTime, int mCost) throws IOException {
		if (DEBUG) Log.d(TAG, "createReservation()");
		Reservation reservation = new Reservation();
		reservation.setParkingSpace(mParkingSpace);
		reservation.setUser(null);
		reservation.setStartTime(mStartTime);
		reservation.setEndTime(mEndTime);
		reservation.setTotalTime(mTotalTime);
		reservation.setCost(mCost);
		//TODO on the server return full reservation with user and parkingspace
		
		clientResource = new ClientResource("http://10.0.2.2:8080/users/"+userId+"/reservations");
		clientResource.setChallengeResponse(authentication);
		reservationsResource = clientResource.wrap(ReservationsResource.class);
		
		try {
			reservation = reservationsResource.createReservation(reservation);			
		} catch (Exception e) {}
		if (clientResource.getStatus().isSuccess()) {
		}
		else {
			clientResource.getResponseEntity().exhaust();
		}
		clientResource.release();
		return reservation;
		
	}
	
	public Reservation updateReservation(Reservation reservation) throws IOException {
		if (DEBUG) Log.d(TAG, "updateReservation()");
		clientResource = new ClientResource("http://10.0.2.2:8080/users/"+reservation.getUser().getId()+"/reservations/"+reservation.getId());
		clientResource.setChallengeResponse(authentication);
		reservationResource = clientResource.wrap(ReservationResource.class);
		
		try {
			reservation = reservationResource.updateReservation(reservation);			
		} catch (Exception e) {}
		if (clientResource.getStatus().isSuccess()) {
		}
		else {
			clientResource.getResponseEntity().exhaust();
		}
		clientResource.release();
		return reservation;
	}
	
	public boolean deleteReservation(int userId, int reservationId) throws IOException {
		if (DEBUG) Log.d(TAG, "updateReservation()");
		boolean result = false;
		clientResource = new ClientResource("http://10.0.2.2:8080/users/"+userId+"/reservations/"+reservationId);
		clientResource.setChallengeResponse(authentication);
		reservationResource = clientResource.wrap(ReservationResource.class);
		
		try {
			reservationResource.deleteReservation();			
		} catch (Exception e) {}
		if (clientResource.getStatus().isSuccess()) {
			result = true;
		}
		else {
			clientResource.getResponseEntity().exhaust();
		}
		clientResource.release();
		return result;
	}
	}


