/**
 * 
 */
package com.licenta.parkdroid;

import com.licenta.park.types.ParkingLot;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @author vladucu
 *
 */
public class ParkingLotListAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    
    public ParkingLotListAdapter(Context context) {
     // Cache the LayoutInflate to avoid asking for a new one each time.
        mInflater = LayoutInflater.from(context);
    }
    
    /* (non-Javadoc)
     * @see android.widget.Adapter#getCount()
     */
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return 0;
    }

    /* (non-Javadoc)
     * @see android.widget.Adapter#getItem(int)
     */
    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see android.widget.Adapter#getItemId(int)
     */
    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return 0;
    }

    /* 
     * Make a view to hold each row
     * 
     * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {        
        // A ViewHolder keeps references to children views to avoid unnecessary
        // calls to findViewById() on each row.
        ViewHolder holder;
        
        // When convertView is not null, we can reuse it directly, there is no need
        // to reinflate it. We only inflate a new View when the convertView supplied
        // by ListView is null.
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.parking_lot_list_item, null);

            // Creates a ViewHolder and store references to the two children views
            // we want to bind data to.
            holder = new ViewHolder();            
            holder.icon = (ImageView) convertView.findViewById(R.id.icon);
            holder.parkingLotName = (TextView) convertView.findViewById(R.id.parkingLotName);
            holder.parkingLotLocationLine1 = (TextView) convertView.findViewById(R.id.parkingLotLocationLine1);
            holder.parkingLotDistance = (TextView) convertView.findViewById(R.id.parkingLotDistance);
            holder.iconTrending = (ImageView) convertView.findViewById(R.id.iconTrending);
            holder.parkingLotEmptySpaces = (TextView) convertView.findViewById(R.id.parkingLotEmptySpaces);
            holder.parkingLotSpaces = (TextView) convertView.findViewById(R.id.parkingLotSpaces);
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
        holder.parkingLotEmptySpaces.setText(parkingLot.getEmptySpaces());
        holder.parkingLotSpaces.setText(parkingLot.getTotalSpaces());

        return convertView;
    }

    private static class ViewHolder {
        ImageView icon;
        TextView parkingLotName;
        TextView parkingLotLocationLine1;
        //ImageView iconSpecial;
        TextView parkingLotDistance;
        ImageView iconTrending;
        TextView parkingLotEmptySpaces;
        TextView parkingLotSpaces;
    }
}
