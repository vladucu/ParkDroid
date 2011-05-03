/**
 * 
 */
package com.licenta.parkdroid.preferences;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcelable;
import android.preference.Preference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * @author vladucu
 *
 */
public class ClickPreference extends Preference {
    
    public static final String TAG = "PreferencesActivity";
    //debug mode
    public static final boolean DEBUG = true;
    

    /**
     * Constructor that is called when inflating a Preference from XML. This is called when 
     * a Preference is being constructed from an XML file, supplying attributes that were 
     * specified in the XML file. This version uses a default style of 0, so the only attribute
     *  values applied are those in the Context's Theme and the given AttributeSet.
     * @param context
     * @param attrs
     */
    public ClickPreference(Context context, AttributeSet attrs) {       
        super(context, attrs);
        if (DEBUG) Log.d(TAG, "ClickPreference");
    }
    
    /* Binds the created View to the data for this Preference.
     * @see android.preference.Preference#onBindView(android.view.View)
     */
    @Override
    protected void onBindView(View view) {        
        super.onBindView(view);
    }

    /* Processes a click on the preference.
     * @see android.preference.Preference#onClick()
     */
    @Override
    protected void onClick() {
        // Data has changed, notify so UI can be refreshed!
        notifyChanged();
    }

    /* 
     * Called when a Preference is being inflated and the default value attribute needs to be read
     * @see android.preference.Preference#onGetDefaultValue(android.content.res.TypedArray, int)
     */
    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        // This preference type's value type is Integer, so we read the default
        // value from the attributes as an Integer.
        return a.getInteger(index, 0);
    }

   /*  (non-Javadoc)
     * @see android.preference.Preference#onSetInitialValue(boolean, java.lang.Object)
     
    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        //super.onSetInitialValue(restorePersistedValue, defaultValue);
    }*/

    /* (non-Javadoc)
     * @see android.preference.Preference#onRestoreInstanceState(android.os.Parcelable)
     */
    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(state);
        notifyChanged();
    }    
}
