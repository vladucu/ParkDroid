/**
 * 
 */
package com.licenta.parkdroid;

import java.util.List;
import com.licenta.parkdroid.types.ParkTypes;
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

    @Override
    public int getCount() {
        return (group == null) ? 0 : group.size();
    }

    @Override
    public Object getItem(int position) {
        return group.get(position);
    }

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
