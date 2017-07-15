package com.nhuchhe.nischit.bbrowser.Utilities;

import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.view.WindowManager;
import android.widget.LinearLayout;

/**
 * Created by Nischit on 10/31/2016.
 */
public class Variables {
    public static int SCREEN_WIDTH;
    public static int SCREEN_HEIGHT;
    public static WindowManager WINDOW_MANAGER;
    public static WindowManager.LayoutParams LAYOUT_PARAMS;
    public static WindowManager.LayoutParams FOCUSABLE_LAYOUT_PARAMS=new WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_SYSTEM_ALERT|WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL|WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
            PixelFormat.TRANSLUCENT
    );
    public static WindowManager.LayoutParams NOT_FOCUSABLE_LAYOUT_PARAMS=new WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_SYSTEM_ALERT|WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
            PixelFormat.TRANSLUCENT
    );
    public static LinearLayout base_layout;
    public static Bitmap BUBBLE_BITMAP=null;

    public static int BROWSER_WIDTH;
    public static int BROWSER_HEIGHT;

}
