package com.licenta.parkdroid.utils;

import com.licenta.parkdroid.R;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;


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
