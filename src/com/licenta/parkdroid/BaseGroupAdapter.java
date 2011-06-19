/**
 * 
 */
package com.licenta.parkdroid;

import java.util.List;
import com.licenta.park.types.ParkTypes;
import android.content.Context;
import android.widget.BaseAdapter;

/**
 * @author vladucu
 *
 */
abstract class BaseGroupAdapter<T extends ParkTypes> extends BaseAdapter {
    
	List<T> group = null;
    
    public BaseGroupAdapter(Context context) {        
    }

    /* (non-Javadoc)
     * @see android.widget.Adapter#getCount()
     */
    @Override
    public int getCount() {
        return (group == null) ? 0 : group.size();
    }

    /* (non-Javadoc)
     * @see android.widget.Adapter#getItem(int)
     */
    @Override
    public Object getItem(int position) {
        return group.get(position);
    }

    /* (non-Javadoc)
     * @see android.widget.Adapter#getItemId(int)
     */
    @Override
    public long getItemId(int position) {
       return position;
    }

    @Override
    public boolean isEmpty() {
        return (group == null) ? true : group.isEmpty();
    }

    public void setGroup(List<T> g) {
        group = g;
        notifyDataSetInvalidated();
    }

}
