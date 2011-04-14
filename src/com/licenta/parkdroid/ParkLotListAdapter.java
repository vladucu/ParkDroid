/**
 *
 */

package com.licenta.parkdroid;

import com.licenta.park.types.Group;
import com.licenta.park.types.ParkLot;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.io.File;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;


/**
 * @author vladucu
 *
 */
public class ParkLotListAdapter extends BaseAdapter implements ObservableAdapter {
    private static final String TAG = "ParkLotListAdapter";
    private static final boolean DEBUG = true;

    private LayoutInflater mInflater;
    private Observable mRrm;
    private Handler mHandler;
    private ParkLot mParkLot;
    private RemoteResourceManagerObserver mResourcesObserver;

    public ParkLotListAdapter(Context context) {
        super();
        if (DEBUG) Log.d(TAG, "ParkLotListAdapter()");
        mInflater = LayoutInflater.from(context);
        mHandler = new Handler();
        mResourcesObserver = new RemoteResourceManagerObserver();

        //mRrm.addObserver(mResourcesObserver);
    }

    @Override
    public void removeObserver() {
        mRrm.deleteObserver(mResourcesObserver);
    }

    /**
     * Make a view to hold each row.
     * 
     * @see android.widget.ListAdapter#getView(int, android.view.View,
     *      android.view.ViewGroup)
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        
        if (DEBUG) Log.d(TAG, "getView()");
        // A ViewHolder keeps references to children views to avoid unnecessary
        // calls to findViewById() on each row.
        ViewHolder holder;

        // When convertView is not null, we can reuse it directly, there is no
        // need to re-inflate it. We only inflate a new View when the
        // convertView supplied by ListView is null.
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.park_lot_list_item, null);

            // Creates a ViewHolder and store references to the two children
            // views we want to bind data to.
            holder = new ViewHolder();
            holder.icon = (ImageView) convertView.findViewById(R.id.icon);
            holder.parkLotName = (TextView) convertView.findViewById(R.id.parkLotName);
            holder.parkLotLocationLine1 = (TextView) convertView.findViewById(R.id.parkLotLocationLine1);
            //holder.iconSpecial = (ImageView) convertView.findViewById(R.id.iconSpecialHere);
            holder.parkLotDistance = (TextView) convertView.findViewById(R.id.parkLotDistance);
            holder.iconTrending = (ImageView) convertView.findViewById(R.id.iconTrending);
            holder.parkEmptySpacesCount = (TextView) convertView.findViewById(R.id.parkEmptySpacesCount);
            holder.parkSpacesCount = (TextView) convertView.findViewById(R.id.parkSpacesCount);
            convertView.setTag(holder);
            if (DEBUG) Log.d(TAG, "parkLotName="+holder.parkLotName.toString());
        } else {
            // Get the ViewHolder back to get fast access to the TextView
            // and the ImageView.
            holder = (ViewHolder) convertView.getTag();
        }

        // Check if the venue category icon exists on disk, if not default to a
        // venue pin icon.
        ParkLot parkLot = (ParkLot) getItem(position);
        setDefaultVenueCategoryIcon(parkLot, holder);        

        // ParkLot name.
        holder.parkLotName.setText(parkLot.getName());

       /* // Venue street address (cross streets | city, state zip).
        if (!TextUtils.isEmpty(venue.getAddress())) {
            holder.locationLine1.setText(StringFormatters.getVenueLocationFull(venue));
        } else {
            holder.locationLine1.setText("");
        }*/
        holder.parkLotLocationLine1.setText("Adresa de adresa");

      /*  // If there's a special here, show the special here icon.
        if (VenueUtils.getSpecialHere(venue)) {
            holder.iconSpecial.setVisibility(View.VISIBLE);
        } else {
            holder.iconSpecial.setVisibility(View.GONE);
        }
*/
        // Show venue distance.
        if (parkLot.getDistance() != null) {
            holder.parkLotDistance.setText(parkLot.getDistance() + " meters");
        } else {
            holder.parkLotDistance.setText("?meters");
        }

    /*    // If more than two people here, then show trending text.
        Stats stats = venue.getStats();
        if (stats != null && !stats.getHereNow().equals("0") && !stats.getHereNow().equals("1")
                && !stats.getHereNow().equals("2")) {
            holder.iconTrending.setVisibility(View.VISIBLE);
            holder.venueCheckinCount.setVisibility(View.VISIBLE);
            holder.venueCheckinCount.setText(stats.getHereNow() + " people here");
        } else {
            holder.iconTrending.setVisibility(View.GONE);
            holder.venueCheckinCount.setVisibility(View.GONE);
        }*/
        holder.iconTrending.setVisibility(View.VISIBLE);
        holder.parkEmptySpacesCount.setVisibility(View.VISIBLE);
        holder.parkEmptySpacesCount.setText(parkLot.getEmptySpaces());
        holder.parkSpacesCount.setVisibility(View.VISIBLE);
        holder.parkSpacesCount.setText("/"+parkLot.getSpaces());
        /*// If we have a todo here, show the corner folded over.
        if (venue.getHasTodo()) {
            holder.todoHere.setVisibility(View.VISIBLE);
        } else {
            holder.todoHere.setVisibility(View.INVISIBLE);
        }*/

        return convertView;
    }

    private void setDefaultVenueCategoryIcon(ParkLot parkLot, ViewHolder holder) {
        holder.icon.setImageResource(R.drawable.category_none);
    }

    public void setGroup() {
        
    }
    
   /* public void setGroup(Group<ParkLot> g) {
        super.setGroup(g);

        for (Venue it : g) {
            // Start download of category icon if not already in the cache.
            // At the same time, check the age of each of these images, if
            // expired, delete and request a fresh copy. This should be
            // removed once category icon set urls are versioned.
            Category category = it.getCategory();
            if (category != null) {
                Uri photoUri = Uri.parse(category.getIconUrl());

                File file = mRrm.getFile(photoUri);
                if (file != null) {
                    if (System.currentTimeMillis() - file.lastModified() > FoursquaredSettings.CATEGORY_ICON_EXPIRATION) {
                        mRrm.invalidate(photoUri);
                        file = null;
                    }
                }

                if (file == null) {
                    mRrm.request(photoUri);
                }
            }
        }
    }*/

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return 0;
    }

    public void setGroup(ParkLot parkLot) {
        // TODO Auto-generated method stub
        mParkLot=parkLot;
        notifyDataSetInvalidated();
        
    }
    
    private class RemoteResourceManagerObserver implements Observer {
        @Override
        public void update(Observable observable, Object data) {
            if (DEBUG) Log.d(TAG, "Fetcher got: " + data);
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    notifyDataSetChanged();
                }
            });
        }
    }

    private static class ViewHolder {
        ImageView icon;
        TextView parkLotName;
        TextView parkLotLocationLine1;
        ImageView iconSpecial;
        TextView parkLotDistance;
        ImageView iconTrending;   
        TextView parkEmptySpacesCount;
        TextView parkSpacesCount;
    }
}
