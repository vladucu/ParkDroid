package com.licenta.parkdroid.utils;

import com.licenta.parkdroid.R;

import java.util.Calendar;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TimePicker;
import android.widget.ViewSwitcher;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.TimePicker.OnTimeChangedListener;

public class DateTimePicker extends RelativeLayout implements View.OnClickListener, OnDateChangedListener, OnTimeChangedListener {

    private static final String TAG = "DateTimePicker";
    private static boolean DEBUG = true;
    
	// DatePicker reference
	private DatePicker datePicker;
	// TimePicker reference
	private TimePicker timePicker;
	// ViewSwitcher reference
	private ViewSwitcher viewSwitcher;
	// Calendar reference
	private Calendar mCalendar;

	// Constructor start
	public DateTimePicker(Context context) {
		this(context, null);
	    if (DEBUG) Log.d(TAG, "DateTimePicker(context)");
	}

	public DateTimePicker(Context context, AttributeSet attrs) {	    
		this(context, attrs, 0);
		if (DEBUG) Log.d(TAG, "DateTimePicker(context, attrs)");
	}

	public DateTimePicker(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		
		if (DEBUG) Log.d(TAG, "DateTimePicker(context, attrs, defStyle)");

		// Get LayoutInflater instance
		final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// Inflate myself
		inflater.inflate(R.layout.date_time_picker, this, true);

		// Inflate the date and time picker views
		final LinearLayout datePickerView = (LinearLayout) inflater.inflate(R.layout.date_picker, null);
		final LinearLayout timePickerView = (LinearLayout) inflater.inflate(R.layout.time_picker, null);

		// Grab a Calendar instance
		mCalendar = Calendar.getInstance();
		// Grab the ViewSwitcher so we can attach our picker views to it
		viewSwitcher = (ViewSwitcher) this.findViewById(R.id.dateTimePickerVS);

		// Init date picker
		datePicker = (DatePicker) datePickerView.findViewById(R.id.datePicker);		
		datePicker.init(mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH), this);
		
		// Init time picker
		timePicker = (TimePicker) timePickerView.findViewById(R.id.timePicker);
		timePicker.setOnTimeChangedListener(this);

		// Handle button clicks
		((Button) findViewById(R.id.switchFromTime)).setOnClickListener(this); // shows the time picker
		((Button) findViewById(R.id.switchFromDate)).setOnClickListener(this); // shows the date picker

		// Populate ViewSwitcher
		viewSwitcher.addView(timePickerView, 0);
		viewSwitcher.addView(datePickerView, 1);		
	}
	// Constructor end

	// Called every time the user changes DatePicker values
	public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
	    if (DEBUG) Log.d(TAG, "onDateChanged()");
		// Update the internal Calendar instance
		mCalendar.set(year, monthOfYear, dayOfMonth, mCalendar.get(Calendar.HOUR_OF_DAY), mCalendar.get(Calendar.MINUTE));
	}

	// Called every time the user changes TimePicker values
	public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
	    if (DEBUG) Log.d(TAG, "onTimeChanged()");
		// Update the internal Calendar instance
		mCalendar.set(mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH), hourOfDay, minute);
	}

	// Handle button clicks
	public void onClick(View view) {
		switch (view.getId()) {
		   
			case R.id.switchFromDate:
			    if (DEBUG) Log.d(TAG, "clicked switchFromDate");
			    view.setEnabled(false);
				findViewById(R.id.switchFromTime).setEnabled(true);
				viewSwitcher.showPrevious();
				break;		
				
			 case R.id.switchFromTime:
			    if (DEBUG) Log.d(TAG, "clicked switchFromTime");
			    view.setEnabled(false);
                findViewById(R.id.switchFromDate).setEnabled(true);
                viewSwitcher.showNext();
                break;                
		}
	}

	// Convenience wrapper for internal Calendar instance
	public int get(final int field) {
		return mCalendar.get(field);
	}

	// Reset DatePicker, TimePicker and internal Calendar instance
	public void reset() {
		final Calendar c = Calendar.getInstance();
		updateDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
		updateTime(c.get(Calendar.HOUR_OF_DAY),c.get(Calendar.MINUTE));
	}

	// Convenience wrapper for internal Calendar instance
	public long getDateTimeMillis() {
		return mCalendar.getTimeInMillis();
	}

	// Convenience wrapper for internal TimePicker instance
	public void setIs24HourView(boolean is24HourView) {
		timePicker.setIs24HourView(is24HourView);
	}
	
	// Convenience wrapper for internal TimePicker instance
	public boolean is24HourView() {
		return timePicker.is24HourView();
	}

	// Convenience wrapper for internal DatePicker instance
	public void updateDate(int year, int monthOfYear, int dayOfMonth) {
		datePicker.updateDate(year, monthOfYear, dayOfMonth);
	}

	// Convenience wrapper for internal TimePicker instance
	public void updateTime(int currentHour, int currentMinute) {
		timePicker.setCurrentHour(currentHour);
		timePicker.setCurrentMinute(currentMinute);
	}
}