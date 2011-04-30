/**
 * 
 */
package com.licenta.parkdroid;

import com.licenta.park.types.Group;
import com.licenta.park.types.ParkingLot;
import android.content.Context;
import android.database.DataSetObserver;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * @author vladucu
 *
 */
public class ParkingLotListAdapter extends BaseParkingLotAdapter {
    
    private static final String TAG = "ParkingLotListAdapter";
    private static boolean DEBUG = true;
    
   // private Group<ParkingLot> items;
    private LayoutInflater mInflater;
    private Handler mHandler;

    public ParkingLotListAdapter(Context context) {
        super(context);
        
     //   this.items = items;
        mInflater = LayoutInflater.from(context);
        mHandler = new Handler();
        if (DEBUG) Log.d(TAG, "ParkingLotListAdapter()");
    
        this.registerDataSetObserver(mDataSetObserver);
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
            
            convertView = mInflater.inflate(R.layout.parking_lot_list_item, null);

            // Creates a ViewHolder and store references to the children views
            // we want to bind data to.
            holder = new ViewHolder();            
            holder.icon = (ImageView) convertView.findViewById(R.id.icon);
            holder.parkingLotName = (TextView) convertView.findViewById(R.id.parkingLotName);
            holder.parkingLotLocationLine1 = (TextView) convertView.findViewById(R.id.parkingLotLocationLine1);
            holder.parkingLotDistance = (TextView) convertView.findViewById(R.id.parkingLotDistance);
            holder.iconTrending = (ImageView) convertView.findViewById(R.id.iconTrending);
            holder.parkingLotSpaces = (TextView) convertView.findViewById(R.id.parkingLotSpaces);
            holder.parkingPrice = (TextView) convertView.findViewById(R.id.parkingPrice);
            holder.reservationHere = (ImageView) convertView.findViewById(R.id.reservationCorner);
            convertView.setTag(holder);
        } else {
            // Get the ViewHolder back to get fast access to the TextView
            // and the ImageView.
            holder = (ViewHolder) convertView.getTag();
        }
        
        ParkingLot parkingLot = (ParkingLot) getItem(position);
        
        // Parking lot name.
        holder.parkingLotName.setText(parkingLot.getName());
        
      // Parking lot street address
        if (!TextUtils.isEmpty(parkingLot.getAddress())) {
            holder.parkingLotLocationLine1.setText(parkingLot.getAddress());
        } else {
            holder.parkingLotLocationLine1.setText("");
        }
                
        // Show parking lot distance.
        if (parkingLot.getDistance() != null) {
            holder.parkingLotDistance.setText(parkingLot.getDistance() + " meters");
        } else {
            holder.parkingLotDistance.setText("");
        }
        holder.iconTrending.setVisibility(View.VISIBLE);
        holder.parkingLotSpaces.setText(parkingLot.getEmptySpaces()+"/"+parkingLot.getTotalSpaces());        
        holder.parkingPrice.setText(parkingLot.getPrice()+"/h");       
        
        // If we have a reservation here, show the corner folded over.
        if (parkingLot.getHasReservation()) {
            holder.reservationHere.setVisibility(View.VISIBLE);
        } else {
            holder.reservationHere.setVisibility(View.INVISIBLE);
        }

        return convertView;
    }

    private static class ViewHolder {
        ImageView icon;
        TextView parkingLotName;
        TextView parkingLotLocationLine1;
        //ImageView iconSpecial;
        TextView parkingLotDistance;
        ImageView iconTrending;
        TextView parkingLotSpaces;
        TextView parkingPrice;
        ImageView reservationHere;
    }
    
    private DataSetObserver mDataSetObserver = new DataSetObserver() {
        @Override
        public void onChanged() {
            notifyDataSetChanged();
        }
    };
}
