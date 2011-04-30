/**
 * 
 */
<<<<<<< HEAD

package com.licenta.park.types;

import java.util.ArrayList;
import java.util.Collection;
import com.licenta.park.types.ParkType;

=======
package com.licenta.park.types;


import java.util.ArrayList;
import java.util.Collection;
>>>>>>> dev4

/**
 * @author vladucu
 *
 */
<<<<<<< HEAD
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
=======
public class Group<T extends ParkTypes> extends ArrayList<T> implements ParkTypes {
    
    private static final long serialVersionUID = 1L;
    
    public Group() {        
        super();
    }
    
    public Group(Collection<T> collection) {
        super(collection);
    }
>>>>>>> dev4
}
