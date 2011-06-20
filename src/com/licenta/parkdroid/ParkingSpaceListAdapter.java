package com.licenta.parkdroid;

import com.licenta.park.types.ParkingSpace;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @author vladucu
 *
 */
public class ParkingSpaceListAdapter extends BaseParkingSpaceAdapter {
    
    private static final String TAG = "ParkingSpaceListAdapter";
    private static boolean DEBUG = true;
    
   // private Group<ParkingLot> items;
    private LayoutInflater mInflater;
    private Handler mHandler;

    public ParkingSpaceListAdapter(Context context) {
        super(context);
        
        mInflater = LayoutInflater.from(context);
        mHandler = new Handler();
        if (DEBUG) Log.d(TAG, "ParkingSpaceListAdapter()");
    }

    /* 
     * Make a view to hold each row
     * 
     * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {     
        if (DEBUG) Log.d(TAG, "getView()");
        // A ViewHolder keeps references to children views to avoid unnecessary
        // calls to findViewById() on each row.
        ViewHolder holder;
        
        // When convertView is not null, we can reuse it directly, there is no need
        // to reinflate it. We only inflate a new View when the convertView supplied
        // by ListView is null.
        if (convertView == null) {
            
            convertView = mInflater.inflate(R.layout.parking_space_list_item, null);

            // Creates a ViewHolder and store references to the children views
            // we want to bind data to.
            holder = new ViewHolder();            
            holder.icon = (ImageView) convertView.findViewById(R.id.icon);
            holder.parkingSpaceName = (TextView) convertView.findViewById(R.id.parkingSpaceName);
            holder.parkingSpaceLocationLine1 = (TextView) convertView.findViewById(R.id.parkingSpaceLocationLine1);
            //holder.parkingSpaceSpaces = (TextView) convertView.findViewById(R.id.parkingSpaceSpaces);
            /*holder.parkingSpaceDistance = (TextView) convertView.findViewById(R.id.parkingSpaceDistance);
            holder.iconTrending = (ImageView) convertView.findViewById(R.id.iconTrending);
            
            holder.parkingPrice = (TextView) convertView.findViewById(R.id.parkingPrice);*/
            //holder.reservationHere = (ImageView) convertView.findViewById(R.id.reservationCorner);
            convertView.setTag(holder);
        } else {
            // Get the ViewHolder back to get fast access to the TextView
            // and the ImageView.
            holder = (ViewHolder) convertView.getTag();
        }
        
        ParkingSpace parkingSpace = (ParkingSpace) getItem(position);
        
        // Parking lot name.
        holder.parkingSpaceName.setText(parkingSpace.getName());
        
      // Parking lot street address
        if (!TextUtils.isEmpty(parkingSpace.getAddress())) {
            holder.parkingSpaceLocationLine1.setText(parkingSpace.getAddress());
        } else {
            holder.parkingSpaceLocationLine1.setText("");
        }
       /*         
        // Show parking lot distance.
        if (parkingSpace.getDistance() != null) {
            holder.parkingSpaceDistance.setText(parkingSpace.getDistance() + " meters");
        } else {
            holder.parkingSpaceDistance.setText("");
        }
        holder.iconTrending.setVisibility(View.VISIBLE);
        holder.parkingSpaceSpaces.setText(parkingSpace.getEmptySpaces()+"/"+parkingSpace.getTotalSpaces());        
        holder.parkingPrice.setText(parkingSpace.getPrice()+"/h");       */
        
        // If we have a reservation here, show the corner folded over.
     /*   if (parkingSpace.getHasReservation()) {
            holder.reservationHere.setVisibility(View.VISIBLE);
        } else {
            holder.reservationHere.setVisibility(View.INVISIBLE);
        }
*/
        return convertView;
    }

    private static class ViewHolder {       
        ImageView icon;
        TextView parkingSpaceName;
        TextView parkingSpaceLocationLine1;
        /*ImageView iconSpecial;
        TextView parkingSpaceDistance;
        ImageView iconTrending;
        TextView parkingSpaceSpaces;
        TextView parkingPrice;*/
        ImageView reservationHere;
    }

}
