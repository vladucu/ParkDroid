package com.licenta.parkdroid;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @author vladucu
 *
 */
public class MapCalloutView extends LinearLayout {

    private TextView mTextViewTitle;
    private TextView mTextViewMessage;

    public MapCalloutView(Context context) {
        super(context);
    }

    public MapCalloutView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onFinishInflate() {
        super.onFinishInflate();
        ((Activity) getContext()).getLayoutInflater().inflate(R.layout.map_callout_view, this);
        mTextViewTitle = (TextView) findViewById(R.id.title);
        mTextViewMessage = (TextView) findViewById(R.id.message);
    }

    public void setTitle(String title) {
        mTextViewTitle.setText(title);
    }

    public void setMessage(String message) {
        mTextViewMessage.setText(message);
    }
}
