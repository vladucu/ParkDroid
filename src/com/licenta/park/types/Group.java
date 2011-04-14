/**
 * 
 */

package com.licenta.park.types;

import java.util.ArrayList;
import java.util.Collection;
import com.licenta.park.types.ParkType;


/**
 * @author vladucu
 *
 */
public class Group<T extends ParkType> extends ArrayList<T> implements ParkType {

    private static final long serialVersionUID = 1L;

    private String mType;

    public Group() {
        super();
    }

    public Group(Collection<T> collection) {
        super(collection);
    }

    public void setType(String type) {
        mType = type;
    }

    public String getType() {
        return mType;
    }
}
