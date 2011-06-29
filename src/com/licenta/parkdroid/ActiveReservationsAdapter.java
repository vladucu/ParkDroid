package com.licenta.parkdroid;

import com.licenta.parkdroid.R;
import com.licenta.parkdroid.R.id;
import com.licenta.parkdroid.R.layout;
import com.licenta.parkdroid.types.Reservation;
import com.licenta.parkdroid.utils.FormatStrings;

import android.content.Context;
import android.database.DataSetObserver;
import android.os.Handler;
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
public class ActiveReservationsAdapter extends BaseReservationsAdapter {
    private static final String TAG = "ActiveReservationsAdapter";
    private static boolean DEBUG = ParkDroid.DEBUG;
    
    private Handler mHandler;
    private LayoutInflater mInflater;
      
    public ActiveReservationsAdapter(Context context) {
        super(context);
        
        if (DEBUG) Log.d(TAG, "ActiveReservationsAdapter()");
        mInflater = LayoutInflater.from(context);
        mHandler = new Handler();
    }

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
            
            convertView = mInflater.inflate(R.layout.active_reservations_list_item, null);

            // Creates a ViewHolder and store references to the children views
            // we want to bind data to.
            holder = new ViewHolder();            
            holder.parkingLotName = (TextView) convertView.findViewById(R.id.activeReservationsListActivityParkingLotName);
            holder.startingTime = (TextView) convertView.findViewById(R.id.activeReservationsListActivityStartingTime);
            holder.endingTime = (TextView) convertView.findViewById(R.id.activeReservationsListActivityEndingTime);
        /*    holder.parkingLotDistance = (TextView) convertView.findViewById(R.id.parkingLotDistance);
            holder.iconTrending = (ImageView) convertView.findViewById(R.id.iconTrending);
            holder.parkingLotSpaces = (TextView) convertView.findViewById(R.id.parkingLotSpaces);
            holder.parkingPrice = (TextView) convertView.findViewById(R.id.parkingPrice);*/
            convertView.setTag(holder);
        } else {
            // Get the ViewHolder back to get fast access to the TextView
            // and the ImageView.
            holder = (ViewHolder) convertView.getTag();
        }
        
        Reservation reservation = (Reservation) getItem(position);
        holder.parkingLotName.setText(reservation.getParkingSpace().getName());        
        holder.startingTime.setText(reservation.getStartTime());
        holder.endingTime.setText(reservation.getEndTime());
     /*   holder.parkingLotDistance.setText(reservation.getParkingLot().getDistance() + " meters");
        holder.iconTrending.setVisibility(View.VISIBLE);
        holder.parkingLotSpaces.setText(reservation.getParkingLot().getEmptySpaces()+"/"+reservation.getParkingLot().getTotalSpaces());        
        holder.parkingPrice.setText(reservation.getParkingLot().getPrice()+"/h");
        */
        return convertView;
    }
    
    private class ViewHolder {
        TextView parkingLotName;
        TextView startingTime;
        TextView endingTime;
       /* TextView parkingLotDistance;
        ImageView iconTrending;
        TextView parkingLotSpaces;
        TextView parkingPrice;*/
    }

    private DataSetObserver mDataSetObserver = new DataSetObserver() {
        @Override
        public void onChanged() {
            notifyDataSetChanged();
        }
    };

}
