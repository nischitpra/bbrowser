package com.nhuchhe.nischit.bbrowser.Utilities;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Path;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/**
 * Created by Nischit on 11/5/2016.
 */
public class Utils {
    public static Bitmap circleCropBitmap(Bitmap image){
        int bubble_width= Variables.SCREEN_WIDTH/5;
        if(bubble_width>180){
            bubble_width=180;
        }
        image=Bitmap.createScaledBitmap(image,bubble_width,bubble_width,false);
        int width=image.getWidth();
        int height=image.getHeight();
        Bitmap output=Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);
        Path path=new Path();
        path.addCircle((float)width/2,(float)height/2,(float) Math.min(width/2,height/2), Path.Direction.CCW);
        Canvas canvas=new Canvas(output);
        canvas.clipPath(path);
        canvas.drawBitmap(image,0,0,null);
        return output;
    }
    public static void ProgressBar(ImageView imageView,int width,int progress){
        imageView.setLayoutParams(new RelativeLayout.LayoutParams(width*progress/100,70));
    }
    public static void changeLayoutParams(LinearLayout bubble_layout,boolean focusable){
        Variables.NOT_FOCUSABLE_LAYOUT_PARAMS.x = Variables.LAYOUT_PARAMS.x;
        Variables.NOT_FOCUSABLE_LAYOUT_PARAMS.y = Variables.LAYOUT_PARAMS.y;

        Variables.FOCUSABLE_LAYOUT_PARAMS.x = Variables.LAYOUT_PARAMS.x;
        Variables.FOCUSABLE_LAYOUT_PARAMS.y = Variables.LAYOUT_PARAMS.y;
        if(focusable){
            Variables.LAYOUT_PARAMS = Variables.FOCUSABLE_LAYOUT_PARAMS;
        }else {
            Variables.LAYOUT_PARAMS = Variables.NOT_FOCUSABLE_LAYOUT_PARAMS;
        }
        Variables.WINDOW_MANAGER.updateViewLayout(bubble_layout, Variables.LAYOUT_PARAMS);
    }
    public static void clampLayoutParams(WindowManager.LayoutParams layoutParams){
        if(layoutParams.x<0){
            layoutParams.x=0;
        }
        if(layoutParams.y<0){
            layoutParams.y=0;
        }
        if(layoutParams.x> Variables.SCREEN_WIDTH){
            layoutParams.x= Variables.SCREEN_WIDTH;
        }
        if(layoutParams.y> Variables.SCREEN_HEIGHT){
            layoutParams.y= Variables.SCREEN_HEIGHT;
        }
    }
}
