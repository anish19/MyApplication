package com.example.anish.myapplication;

import android.os.AsyncTask;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by anish on 21/8/14.
 */
public class SendData extends AsyncTask<UserLoc, Void, Void>{

    protected Void doInBackground(UserLoc... userInstance){

        String postParams = "lat="+userInstance[0].lat +"&lng=" +userInstance[0].lng +"&userId="+ userInstance[0].userId;
        URL url = null;
        try {
            url = new URL("http://www.mylatlong.cf/addUserLoc.php");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setFixedLengthStreamingMode(postParams.getBytes().length);
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
            DataOutputStream out = new DataOutputStream(urlConnection.getOutputStream());
            out.write(postParams.getBytes());
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    return null;
    }
}
