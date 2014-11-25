package com.example.anish.myapplication;

import android.os.AsyncTask;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by anish on 21/8/14.
 */
public class checkOverspeed extends AsyncTask<MyActivity, Void, Void>{

    protected Void doInBackground(MyActivity... activity){
        UserLoc userInstance = activity[0].getUserLoc();
        final String postParams = "lat="+userInstance.lat +"&lng=" +userInstance.lng +"&id="+ userInstance.userId+"&speed="+userInstance.speed;

        URL url = null;
        try {
            url = new URL("http://www.mylatlong.cf/serverProcess1.php");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Content-Length", "" + Integer.toString(postParams.getBytes().length));
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
            DataOutputStream out = new DataOutputStream(urlConnection.getOutputStream());
            out.write(postParams.getBytes());
            out.flush();
            out.close();
            DataInputStream in = new DataInputStream(urlConnection.getInputStream());
            byte[] response = new byte[1024];
            String tmp=new String();
            int bytesRead=0;
            while((bytesRead=in.read(response))>=0){
                tmp+=new String(response,0,bytesRead);
            }
            String[] responses = tmp.split(",");
            int id = Integer.parseInt(responses[0]), alert = Integer.parseInt(responses[1]);
            Double speed = Double.parseDouble(responses[2]);

            final MyActivity myActivity = activity[0];
            if(alert==1){
                myActivity.setMinSpeedInterval(3f);
                myActivity.lastCheckedInterval = 10000;
            }
            else{
                myActivity.setMinSpeedInterval(5f);
                myActivity.lastCheckedInterval=15000;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    return null;
    }
}
