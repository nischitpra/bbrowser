package com.nhuchhe.nischit.bbrowser;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.IBinder;
import android.os.Vibrator;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.nhuchhe.nischit.bbrowser.Utilities.Constants;
import com.nhuchhe.nischit.bbrowser.Utilities.Variables;
import com.nhuchhe.nischit.bbrowser.Utilities.Utils;

public class BackgroundService extends Service {
    LayoutInflater layoutInflater;
    LinearLayout bubble_layout,browser_layout;
    ImageView bubble_ImageView,bubble_progressBar;
    Point deltaPoint=new Point(0,100);
    long startTime;
    boolean bubble_isClicked=false,bubble_isInflated=false,should_vibrate=true;
    Point perviousPosition=new Point(0,100);

    public BackgroundService() {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        WindowManager windowManager=(WindowManager)getSystemService(WINDOW_SERVICE);

        WindowManager.LayoutParams layoutParams_focusable= Variables.FOCUSABLE_LAYOUT_PARAMS;
        layoutParams_focusable.gravity=Gravity.TOP|Gravity.LEFT;
        layoutParams_focusable.x=0;
        layoutParams_focusable.y=100;
        Variables.FOCUSABLE_LAYOUT_PARAMS = layoutParams_focusable;

        WindowManager.LayoutParams layoutParams_nonfocusable= Variables.NOT_FOCUSABLE_LAYOUT_PARAMS;
        layoutParams_nonfocusable.gravity=Gravity.TOP|Gravity.LEFT;
        layoutParams_nonfocusable.x=0;
        layoutParams_nonfocusable.y=100;
        Variables.NOT_FOCUSABLE_LAYOUT_PARAMS = layoutParams_nonfocusable;

        layoutInflater=(LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
        inflateBubbleLayout();
        inflateBrowserLayout();

        Variables.base_layout=new LinearLayout(this);
        Variables.base_layout.addView(bubble_layout);
        windowManager.addView(Variables.base_layout,layoutParams_focusable);
        //windowManager.addView(bubble_layout,layoutParams_focusable);
        Variables.WINDOW_MANAGER=windowManager;

        Variables.LAYOUT_PARAMS=layoutParams_focusable;
        //bubble_layout.setOnTouchListener(new View.OnTouchListener() {
        Variables.base_layout.setOnTouchListener(new View.OnTouchListener() {
                @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction()==MotionEvent.ACTION_OUTSIDE){
                    //Utils.changeLayoutParams(bubble_layout,false);
                    Utils.changeLayoutParams(Variables.base_layout,false);
                    Log.i("Nischit: ","outside touch, not focusable");
                }else{
                    //Utils.changeLayoutParams(bubble_layout,true);
                    if(Variables.LAYOUT_PARAMS!=Variables.FOCUSABLE_LAYOUT_PARAMS)
                        Utils.changeLayoutParams(Variables.base_layout,true);
                    Log.i("Nischit: ","inside touch, focusable");
                }
                return true;
            }
        });
    }
    private void inflateBubbleLayout(){
        bubble_layout=(LinearLayout) layoutInflater.inflate(R.layout.bubble,null);
        bubble_progressBar=(ImageView) bubble_layout.findViewById(R.id.bubble_progressBar);

        bubble_ImageView=(ImageView) bubble_layout.findViewById(R.id.bubble);
        bubble_ImageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        bubble_isClicked=true;
                        perviousPosition=new Point((int)motionEvent.getRawX(),(int)motionEvent.getRawY());
                        //deltaPoint is for relative positioning of the layout to the exact touch position
                        deltaPoint.x= (int) (motionEvent.getRawX()- Variables.LAYOUT_PARAMS.x);
                        deltaPoint.y=(int) (motionEvent.getRawY()- Variables.LAYOUT_PARAMS.y);
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        Vibrate();
                        Point curr=new Point((int)motionEvent.getRawX(),(int)motionEvent.getRawY());
                        if(distance(curr,perviousPosition)> Constants.MOVE_THRESHOLD){
                            bubble_isClicked=false;
                            Variables.LAYOUT_PARAMS.x= (int) motionEvent.getRawX()-deltaPoint.x;
                            Variables.LAYOUT_PARAMS.y=(int) motionEvent.getRawY()-deltaPoint.y;
                            Utils.clampLayoutParams(Variables.LAYOUT_PARAMS);
                        //    Variables.WINDOW_MANAGER.updateViewLayout(bubble_layout, Variables.LAYOUT_PARAMS);
                            Variables.WINDOW_MANAGER.updateViewLayout(Variables.base_layout, Variables.LAYOUT_PARAMS);
                        }else{
                            bubble_isClicked=true;
                        }
                        return true;
                    case MotionEvent.ACTION_UP:
                        if(Variables.LAYOUT_PARAMS.y> Variables.SCREEN_HEIGHT*0.8){
//                            Variables.WINDOW_MANAGER.removeView(bubble_layout);
                            Variables.WINDOW_MANAGER.removeView(Variables.base_layout);

                            stopSelf();
                        }
                        if(bubble_isClicked){
                            bubble_isClicked=false;
                            if(!bubble_isInflated){
                                bubble_isInflated=true;
                                // for converting circle to rectangle. basically the background image has been removed
                                // background colour is changed to green and minimum width and height has been set to get a rect
                                // the minimum height of bubble layout has been set
                                bubble_ImageView.setImageBitmap(null);
                                bubble_ImageView.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));


                                bubble_ImageView.setMinimumWidth((int) (Variables.SCREEN_WIDTH*0.8));
                                bubble_ImageView.setMinimumHeight(70);
                                bubble_progressBar.setAlpha(0.5f);
                                // adding browser layout to the bubble layout with green rect
                                bubble_layout.addView(browser_layout);
                            }else{

                                bubble_isInflated=false;
                                if(Variables.BUBBLE_BITMAP!=null){
                                    bubble_ImageView.setImageBitmap(Variables.BUBBLE_BITMAP);
                                }else{
                                    bubble_ImageView.setImageDrawable(getResources().getDrawable(R.drawable.circle));
                                }
                                bubble_ImageView.setBackgroundColor(0);
                                bubble_ImageView.setMinimumWidth(0);
                                bubble_ImageView.setMinimumHeight(0);

                                bubble_progressBar.setAlpha(0.0f);//should be 0
                                bubble_progressBar.setMinimumWidth(0);
                                bubble_progressBar.setMinimumHeight(0);
                                bubble_progressBar.setLayoutParams(new RelativeLayout.LayoutParams(0,0));

                                //remove browser layout from the bubble
                                bubble_layout.removeView(browser_layout);
                            }
                        }
                        return true;
                }
                return false;
            }
        });
    }
    private void inflateBrowserLayout(){
        browser_layout=(LinearLayout) layoutInflater.inflate(R.layout.browser,null);
        new BrowserFunction(this,
                browser_layout,bubble_layout,
                (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE));
    }
    private double distance(Point fin,Point ini){
        return Math.sqrt(Math.pow(fin.x-ini.x,2)+Math.pow(fin.y-ini.y,2));
    }
    /*
    private Point unitVector(Point initialPoint,Point finalPoint){
        double distance=Math.sqrt((finalPoint.x-initialPoint.x)*(finalPoint.x-initialPoint.x)
        +(finalPoint.y-initialPoint.y)*(finalPoint.y-initialPoint.y));
        return new Point((int)((finalPoint.x-initialPoint.x)/distance),(int)((finalPoint.y-initialPoint.y)/distance));
    }
    private Point velocity(Point ini,Point fin,double time){
        time*=2;
        return new Point((int)((fin.x-ini.x)/time),(int)((fin.y-ini.y)/time));
    }
    private Thread movement=new Thread(new Runnable() {
        @Override
        public void run() {
            while(true){
                Point unitVector=unitVector(initialPoint,finalPoint);
                Point vel=velocity(initialPoint,finalPoint, (System.currentTimeMillis()-startTime)/1000);//in seconds
                layoutParams.x*= unitVector.x*vel.x;
                layoutParams.y*= unitVector.y*vel.y;
            }
        }

    });

    void updatePosition(){
        windowManager.updateViewLayout(bubble_layout,layoutParams);
    }*/
    private void Vibrate(){
        if(Variables.LAYOUT_PARAMS.y> Variables.SCREEN_HEIGHT*0.8){
            if(should_vibrate){
                Vibrator v = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                v.vibrate(50);
                should_vibrate=false;
            }
        }else{
            should_vibrate=true;
        }
    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
