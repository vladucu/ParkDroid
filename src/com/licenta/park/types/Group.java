/**
 * 
 */
package com.licenta.park.types;


import java.util.ArrayList;
import java.util.Collection;

/**
 * @author vladucu
 *
 */
public class Group<T extends ParkTypes> extends ArrayList<T> implements ParkTypes {
    
    private static final long serialVersionUID = 1L;
    
    public Group() {        
        super();
    }
    
    public Group(Collection<T> collection) {
        super(collection);
    }
}
