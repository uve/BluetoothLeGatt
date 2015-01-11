package com.example.android.bluetoothlegatt;

//import android.content.pm.PackageManager;

import android.content.Context;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


import android.os.Handler;


public class WirelessService {


    private static final String TAG = "WirelessService";


    private static final String URL_SERVER = "http://192.168.0.102:8080/";

    public static final String URL_LOCATION = "location";
    public static final String URL_POSITION = "position";
    public static final String URL_HEIGHT   = "height";


    private Context mContext;


    private Handler mHandler;


    public WirelessService(Context mContext){
        this.mContext = mContext;

        mHandler = new Handler();
    }


    public void Send(final String url, final  List<NameValuePair> postParams) {



        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                try {

                    // Create a new HttpClient and Post Header
                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost(URL_SERVER + url);

                    UrlEncodedFormEntity entity = new UrlEncodedFormEntity(postParams);

                    httpPost.setEntity(entity);
                    // Execute HTTP Post Request
                    HttpResponse response = httpclient.execute(httpPost);

                    //Log.d(TAG, response.toString() );

                } catch (ClientProtocolException e) {
                    // TODO Auto-generated catch block
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                }/* catch (Exception e) {
                    // TODO Auto-generated catch block
                }*/




            }
        };

        new Thread(runnable).start();



    }


}
