package com.example.anish.myapplication;

import android.app.Activity;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


/**
 * Created by groover on 30/8/14.
 */
public class ThreatChecker {
    MyActivity activity;
    public static int checkInterval=2000;       // interval in ms

    ThreatChecker(MyActivity activity){
        this.activity = activity;
    }

    public void startChecking() {
        UserLoc location= activity.getUserLoc() ;
        final Handler myHandler = new Handler();
        Runnable runnable = new Runnable()
        {
            public void run()
            {
                myHandler.postDelayed(this, checkInterval);
                Thread t = new Thread(){
                    public void run() {
                        ThreatChecker checker = new ThreatChecker(activity);
                        checker.check() ;
                    }
                };
                t.start() ;
            }
        };
        myHandler.postDelayed(runnable, 0);
    }

    public void check() {
        UserLoc location = activity.getUserLoc();
        if(location.lat==0)
            return;
        String postParams = "lat="+location.lat +"&lng=" +location.lng +"&id="+ location.userId+"&speed="+location.speed;
        URL url = null;
        try {
            url = new URL("http://www.mylatlong.cf/serverProcess2.php");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Content-Length", "" +Integer.toString(postParams.getBytes().length));
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
            DataOutputStream out = new DataOutputStream(urlConnection.getOutputStream());
            out.write(postParams.getBytes());
            out.flush();
            out.close();
            DataInputStream in = new DataInputStream(urlConnection.getInputStream());
            byte[] response = new byte[1024];
            String temp=new String();
            int bytesRead=0;
            while((bytesRead=in.read(response))>=0){
                temp+=new String(response,0,bytesRead);
            }
            final String VUCdata = temp;


            String[] responses=temp.split(";");
            int green = Color.parseColor("#5659d620"), red = Color.parseColor("#7fff0010");
            int color=red;
            temp = "Overspeeding Vehicles\n\n" ;

            for(String str : responses){
                String[] arr = str.split(",");
                int id = Integer.parseInt(arr[0]);
                Double speed=Double.parseDouble(arr[1]), lat=Double.parseDouble(arr[2]), lng=Double.parseDouble(arr[3]);

                if(id==0) {
                    activity.runOnUiThread(new Runnable(){
                        public void run(){
                            activity.notifyUser("noOSV");
                        }
                    });
                    color = green;
                    temp+="None";
                    ThreatChecker.checkInterval = 2000;
                    break;
                }
                else{
                    activity.runOnUiThread(new Runnable(){
                        public void run(){
                            activity.notifyUser(VUCdata);
                        }
                    });
                    ThreatChecker.checkInterval = 1000;
                    temp+="Id: "+id+" Speed: "+speed+"\n";

                }
            }
            final String dispStr = temp;
            final int newColor = color;
            activity.runOnUiThread(new Runnable(){
                public void run(){
                    activity.updateLowerBox(dispStr, newColor);
                }
            });
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
