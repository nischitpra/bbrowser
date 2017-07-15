package com.nhuchhe.nischit.bbrowser;

import android.util.Log;

import java.util.Vector;

/**
 * Created by Nischit on 12/23/2016.
 */
public class History {
    Vector<String> url_stack;
    boolean should_push=false;
    public History (){
        url_stack=new Vector<String>();
    }
    public void push(String url){
        if(url_stack.isEmpty()||(should_push && url_stack.lastElement().equals(url))){
            url_stack.add(url);
        }
        should_push=true;
    }
    public String pop(){
        should_push=false;
        if(url_stack.size()>1)
            url_stack.remove(url_stack.size()-1);
        else
            return "https://www.google.com";
        String url=url_stack.lastElement();
        return url;
    }
    public void showHistory(){
        Log.d("nischit_history:","________________________");
        for(int i=0;i<url_stack.size();i++){
            Log.d("nischit_history: ",url_stack.elementAt(i));
        }
    }
}
