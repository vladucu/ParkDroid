package com.licenta.parkdroid;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

/**
 * Acts as an interface to the TabSpec class for setting the content view. The
 * level 3 SDK doesn't support setting a View for the content sections of the
 * tab, so we can only use the big native tab style. The level 4 SDK and up
 * support specifying a custom view for the tab.
 * 
 * @date March 9, 2010
 * @author Mark Wyszomierski (markww@gmail.com), foursquare.
 */
public abstract class TabsUtil {

    public static void addTab(TabHost host, String title, int drawable, int index, int layout) {
        TabHost.TabSpec spec = host.newTabSpec("tab" + index);
        spec.setContent(layout);
        View view = prepareTabView(host.getContext(), title, drawable);
        spec.setIndicator(view);
        host.addTab(spec);
    }

    public static void addTab(TabHost host, String title, int drawable, int index, Intent intent) {
        TabHost.TabSpec spec = host.newTabSpec("tab" + index);
        spec.setContent(intent);
        View view = prepareTabView(host.getContext(), title, drawable);
        spec.setIndicator(view);
        host.addTab(spec);
    }

    private static View prepareTabView(Context context, String text, int drawable) {
        View view = LayoutInflater.from(context).inflate(R.layout.tab_main_nav, null);
        TextView tv = (TextView) view.findViewById(R.id.tvTitle);
        tv.setText(text);
        ImageView iv = (ImageView) view.findViewById(R.id.ivIcon);
        iv.setImageResource(drawable);
        return view;
    }
}
