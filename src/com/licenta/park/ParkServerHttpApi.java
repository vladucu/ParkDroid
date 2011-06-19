/**
 * 
 */
package com.licenta.park;

import java.io.IOException;
import org.restlet.data.ChallengeResponse;
import org.restlet.data.ChallengeScheme;
import org.restlet.data.Status;
import org.restlet.engine.Engine;
import org.restlet.ext.httpclient.HttpClientHelper;
import org.restlet.ext.jackson.JacksonConverter;
import org.restlet.resource.ClientResource;
import android.util.Log;
import com.licenta.park.types.ParkingSpace;
import com.licenta.park.types.ParkingSpaceResource;
import com.licenta.park.types.ParkingSpaces;
import com.licenta.park.types.ParkingSpacesResource;
import com.licenta.park.types.ReservationResource;
import com.licenta.park.types.Reservations;
import com.licenta.park.types.Reservation;
import com.licenta.park.types.ReservationsResource;
import com.licenta.park.types.User;
import com.licenta.park.types.UserResource;

/**
 * @author vladucu
 *
 */
class ParkServerHttpApi {
	
	private static final String TAG = "ParkServerHttpApi";
	private static final boolean DEBUG = true;	
	private ClientResource clientResource;
	private ChallengeResponse authentication;
	private ChallengeScheme scheme = ChallengeScheme.HTTP_BASIC;
	private ParkingSpacesResource parkingSpacesResource;
	private UserResource userResource;
	private ReservationsResource reservationsResource;
	private ReservationResource reservationResource;
	
	public ParkServerHttpApi(String domain) {
		if (DEBUG) Log.d(TAG, "ParkServerHttpApi() domain="+domain);
		
		Engine.getInstance().getRegisteredClients().clear();
		Engine.getInstance().getRegisteredClients().add(new HttpClientHelper(null));
		Engine.getInstance().getRegisteredConverters().clear();
		Engine.getInstance().getRegisteredConverters().add(new JacksonConverter());
		clientResource = new ClientResource(domain);		
	}

	public User user(String login, String password) {
		
		if (DEBUG) Log.d(TAG, "user("+login+", "+password+")");
		
		//Prepare the request
    	clientResource = new ClientResource("http://10.0.2.2:8080/users/1");
    	
    	clientResource.setChallengeResponse(authentication);
    	
    	userResource = clientResource.wrap(UserResource.class);
    	User user = userResource.retrieveUser();
    	clientResource.release();
    	
		return user;
	}

	public void setCredentials(String email, String password) {
	
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

	public ParkingSpaces parkingspaces(int userId) throws IOException {
		
		clientResource = new ClientResource("http://10.0.2.2:8080/users/"+userId+"/parkingspaces");
		clientResource.setChallengeResponse(authentication);
		parkingSpacesResource = clientResource.wrap(ParkingSpacesResource.class);
		ParkingSpaces parkingSpaces = parkingSpacesResource.retrieve();
		
		if (clientResource.getStatus() == Status.SUCCESS_OK) {
			return parkingSpaces;
		}
		else {
			clientResource.getResponseEntity().exhaust();
		}
		
		clientResource.release();
		
		return parkingSpaces;
	}
	
	public ParkingSpace parkingSpace(int userId, int parkingSpaceId) throws IOException {
		
		ClientResource clientResource = new ClientResource("http://10.0.2.2:8080/users/"+userId+"/parkingspaces/"+parkingSpaceId);
		clientResource.setChallengeResponse(authentication);
		ParkingSpaceResource parkingSpaceResource = clientResource.wrap(ParkingSpaceResource.class);
		
		ParkingSpace parkingSpace = parkingSpaceResource.retrieve();		
	
		clientResource.getResponseEntity().exhaust();
	
		clientResource.release();
		
		return parkingSpace;
	}

	public Reservations reservations() throws IOException {
		
		clientResource = new ClientResource("http://10.0.2.2:8080/users/1/reservations");
		clientResource.setChallengeResponse(authentication);
		reservationsResource = clientResource.wrap(ReservationsResource.class);
		Reservations reservations = null;
		try {
			reservations = reservationsResource.getReservations();
		} catch (Exception e) {}
		if (clientResource.getStatus() == Status.SUCCESS_OK) {
			return reservations;
		}
		else {
			clientResource.getResponseEntity().exhaust();
		}
	
		clientResource.release();
	
		return reservations;
	}

	public Reservation createReservation(int userId, ParkingSpace mParkingSpace, String mStartTime, String mEndTime, String mTotalTime, int mCost) throws IOException {
		
		Reservation reservation = new Reservation();
		reservation.setParkingSpace(mParkingSpace);
		reservation.setUser(null);
		reservation.setStartTime(mStartTime);
		reservation.setEndTime(mEndTime);
		reservation.setTotalTime(mTotalTime);
		reservation.setCost(mCost);
		
		clientResource = new ClientResource("http://10.0.2.2:8080/users/"+userId+"/reservations");
		clientResource.setChallengeResponse(authentication);
		reservationsResource = clientResource.wrap(ReservationsResource.class);
		
		try {
			reservation = reservationsResource.createReservation(reservation);			
		} catch (Exception e) {}
		if (clientResource.getStatus() == Status.SUCCESS_OK) {
		}
		else {
			clientResource.getResponseEntity().exhaust();
		}
		clientResource.release();
		return reservation;
	}

	public Reservation updateReservation(Reservation reservation) throws IOException {
		
		clientResource = new ClientResource("http://10.0.2.2:8080/users/"+reservation.getUser().getId()+"/reservations/"+reservation.getId());
		clientResource.setChallengeResponse(authentication);
		reservationResource = clientResource.wrap(ReservationResource.class);
		
		try {
			reservation = reservationResource.updateReservation(reservation);			
		} catch (Exception e) {}
		if (clientResource.getStatus() == Status.SUCCESS_OK) {
		}
		else {
			clientResource.getResponseEntity().exhaust();
		}
		clientResource.release();
		return reservation;
	}

}
