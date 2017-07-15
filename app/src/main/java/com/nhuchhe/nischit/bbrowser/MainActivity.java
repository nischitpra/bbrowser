package com.nhuchhe.nischit.bbrowser;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.nhuchhe.nischit.bbrowser.Utilities.Variables;


public class MainActivity extends AppCompatActivity {
    BackgroundService backgroundService;

    String SystemAlertWindow = Manifest.permission.SYSTEM_ALERT_WINDOW;
    String Internet = Manifest.permission.INTERNET;
    String Vibrator = Manifest.permission.VIBRATE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //initiating service
        backgroundService=new BackgroundService();
        //setting screen size to the constants
        Window window=getWindow();
        Point size=new Point();
        window.getWindowManager().getDefaultDisplay().getSize(size);
        Variables.SCREEN_WIDTH=size.x;
        Variables.SCREEN_HEIGHT=size.y;


        ImageView rateing=(ImageView) findViewById(R.id.rating_imageView);
        rateing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("market://details?id=" + getPackageName());
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                        Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                try {
                    startActivity(goToMarket);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
                }
            }
        });
        //statring service here
        Button createBubble=(Button)findViewById(R.id.create_bubble);
        createPermission();
        createBubble.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkPermission()){
                    Intent intent=new Intent(MainActivity.this,BackgroundService.class);
                    startService(intent);
                }else{
                    Toast.makeText(getApplicationContext(),"You dont have permission",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void createPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(!Settings.canDrawOverlays(getApplicationContext())){
                Intent intent = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                }
                this.startActivity(intent);
                //ActivityCompat.requestPermissions(this,new String[]{SystemAlertWindow},123);
                Toast.makeText(getApplicationContext(),"You dont have permission for Overlay Permission",Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(getApplicationContext(),"You already have permission for SOverlay Permission",Toast.LENGTH_SHORT).show();
            }
        }else{
            if(ContextCompat.checkSelfPermission(getApplicationContext(),SystemAlertWindow)!=PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this,new String[]{SystemAlertWindow},123);
                Toast.makeText(getApplicationContext(),"You dont have permission for System Alert Window",Toast.LENGTH_SHORT).show();
            } else{
                Toast.makeText(getApplicationContext(),"You already have permission for System Alert Window",Toast.LENGTH_SHORT).show();
            }
        }
        if(ContextCompat.checkSelfPermission(getApplicationContext(),Internet)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Internet},123);
            Toast.makeText(getApplicationContext(),"You dont have permission for Internet",Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getApplicationContext(),"You already have permission for Internet",Toast.LENGTH_SHORT).show();
        }
        if(ContextCompat.checkSelfPermission(getApplicationContext(),Vibrator)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Vibrator},123);
            Toast.makeText(getApplicationContext(),"You dont have permission for Vibrator",Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getApplicationContext(),"You already have permission for Vibrator",Toast.LENGTH_SHORT).show();
        }

    }
    private boolean checkPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return (   Settings.canDrawOverlays(getApplicationContext())
                    && ContextCompat.checkSelfPermission(getApplicationContext(),Internet)==PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(getApplicationContext(),Vibrator)==PackageManager.PERMISSION_GRANTED
            );
        }
        return(ContextCompat.checkSelfPermission(getApplicationContext(),SystemAlertWindow)==PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(getApplicationContext(),Internet)==PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(getApplicationContext(),Vibrator)==PackageManager.PERMISSION_GRANTED);
    }
}
