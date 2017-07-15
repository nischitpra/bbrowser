package com.nhuchhe.nischit.bbrowser;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.nhuchhe.nischit.bbrowser.Utilities.Variables;
import com.nhuchhe.nischit.bbrowser.Utilities.Utils;

/**
 * Created by Nischit on 11/4/2016.
 */
public class BrowserFunction {
    LinearLayout browser_layout,bubble_layout;
    Button browser_searchButton;
    EditText browser_urlBar;
    ImageView browser_backbutton;
    WebView browser_webView;
    Context context;
    InputMethodManager inputMethodManager;
    ImageView bubble_imageView,bubble_progressBar;
    LinearLayout menubar_longpress_container,menubar_longpress_layout;
    LayoutInflater menubar_layoutInflater;
    ClipboardManager clipboardManager;

    History history=new History();
    //boolean isClearButton=false;
    public BrowserFunction(Context context, LinearLayout browser_layout,LinearLayout bubble_layout,InputMethodManager inputMethodManager){
        this.context=context;
        this.browser_layout=browser_layout;
        this.browser_searchButton=(Button) browser_layout.findViewById(R.id.search_button);
        this.browser_urlBar=(EditText) browser_layout.findViewById(R.id.browser_urlBar);
        this.browser_webView=(WebView) browser_layout.findViewById(R.id.webView);
        this.inputMethodManager=inputMethodManager;
        this.menubar_longpress_container=(LinearLayout) browser_layout.findViewById(R.id.longpress_container);

        this.bubble_layout=bubble_layout;
        this.bubble_imageView=(ImageView) bubble_layout.findViewById(R.id.bubble);
        this.bubble_progressBar=(ImageView) bubble_layout.findViewById(R.id.bubble_progressBar);


        menubar_layoutInflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        inflateBrowserLayout();
    }
    private void inflateBrowserLayout(){
        browser_urlBar.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);

        browser_urlBar.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(view.hasFocus()){
                    Log.i("nischit ","has focus");
                    inputMethodManager.showSoftInput(view,InputMethodManager.SHOW_IMPLICIT);

                }else{
                    Log.i("nischit ", "Lost focus");
                    inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(),0);
                    menubar_longpress_container.removeAllViews();
                }
            }
        });
       /* browser_urlBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(Variables.LAYOUT_PARAMS!=Variables.FOCUSABLE_LAYOUT_PARAMS){
                    Utils.changeLayoutParams(Variables.base_layout,true);
                    Variables.base_layout.requestFocus();
                }
                //Utils.changeLayoutParams(bubble_layout,true);
                //browser_urlBar.requestFocus();
                return true;
            }
        });*/
        browser_urlBar.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                browser_urlBar.selectAll();
                menubar_longpress_container.removeAllViews();
                menubar_longpress_layout= (LinearLayout) menubar_layoutInflater.inflate(R.layout.longpress,null);
                menubar_longpress_container.addView(menubar_longpress_layout);

                Button copy =(Button) menubar_longpress_layout.findViewById(R.id.button_copy);
                Button paste =(Button) menubar_longpress_layout.findViewById(R.id.button_paste);
                Button cut =(Button) menubar_longpress_layout.findViewById(R.id.button_cut);

                clipboardManager=(ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);

                copy.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(browser_urlBar.getText()==null)
                            return;
                        ClipData clip=ClipData.newPlainText("url",browser_urlBar.getText().toString());
                        clipboardManager.setPrimaryClip(clip);
                        Toast.makeText(context,"url copied",Toast.LENGTH_SHORT).show();
                        menubar_longpress_container.removeAllViews();
                    }
                });

                paste.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ClipData.Item data=clipboardManager.getPrimaryClip().getItemAt(0);
                        if(data.getText()==null)
                            return;
                        browser_urlBar.setText(data.getText().toString());
                        Toast.makeText(context,"url pasted",Toast.LENGTH_SHORT).show();
                        menubar_longpress_container.removeAllViews();
                    }
                });

                cut.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(browser_urlBar.getText()==null)
                            return;
                        ClipData clip=ClipData.newPlainText("url",browser_urlBar.getText().toString());
                        clipboardManager.setPrimaryClip(clip);
                        browser_urlBar.setText("");
                        Toast.makeText(context,"url cut",Toast.LENGTH_SHORT).show();
                        menubar_longpress_container.removeAllViews();
                    }
                });


                return true;
            }
        });

        browser_backbutton=(ImageView)browser_layout.findViewById(R.id.back_button);
        browser_backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url=history.pop();
                browser_webView.loadUrl(url);
                browser_urlBar.setText(url);
                history.showHistory();
            }
        });
        browser_searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url_string=browser_urlBar.getText().toString().toLowerCase();
                if(url_string.isEmpty()){
                    url_string=browser_urlBar.getHint().toString().toLowerCase();
                }else{
                    url_string = browser_urlBar.getText().toString().toLowerCase();
                }
                    Log.i("nischit: ",url_string);
                    if (url_string.startsWith("www")) {
                        url_string = "https://" + url_string;
                    }else if((!url_string.startsWith("http.")||!url_string.startsWith("https."))&&url_string.endsWith(".com")){
                        url_string = "https://www." + url_string;
                    }else{
                        url_string = "https://www.google.com/search?q="+browser_urlBar.getText().toString();
                    }
                    browser_webView.loadUrl(url_string);
            }
        });
        browser_webView.getSettings().setJavaScriptEnabled(true);
        browser_webView.getSettings().setSupportZoom(true);
        browser_webView.getSettings().setLoadWithOverviewMode(true);//override default browser from opening when opening links
        browser_webView.getSettings().setUseWideViewPort(true);
        browser_webView.getSettings().setLoadsImagesAutomatically(true);
        browser_webView.getSettings().setSaveFormData(true);
        browser_webView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //Utils.changeLayoutParams(bubble_layout,true);
                if(Variables.LAYOUT_PARAMS!=Variables.FOCUSABLE_LAYOUT_PARAMS)
                    Utils.changeLayoutParams(Variables.base_layout,true);
                return false;
            }
        });
        browser_webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                browser_urlBar.setText(url);
                return false;
            }
            @Override
            public void onPageFinished(WebView view, final String url) {
                //changeSearchButton(true);
                history.push(url);
                Animation fade = AnimationUtils.loadAnimation(context,R.anim.fade);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    fade.setAnimationListener(new Animation.AnimationListener(){
                        @Override
                        public void onAnimationStart(Animation animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            Utils.ProgressBar(bubble_progressBar,0,0);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                    bubble_progressBar.startAnimation(fade);
                }
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);

                Toast.makeText(context,"Woops! Some thing went wrong!\n"+error+"\n cannot connect to "+request,Toast.LENGTH_SHORT).show();
                //changeSearchButton(false);
                Log.i("nischit: ","error");
            }
        });

        browser_webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                Utils.ProgressBar(bubble_progressBar,bubble_imageView.getWidth(),newProgress);
            }

            @Override
            public void onReceivedIcon(WebView view, Bitmap icon) {
                super.onReceivedIcon(view, icon);
                Variables.BUBBLE_BITMAP=Utils.circleCropBitmap(icon);
            }

        });
    }
}
