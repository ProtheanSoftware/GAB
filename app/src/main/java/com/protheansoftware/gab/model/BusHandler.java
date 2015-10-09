package com.protheansoftware.gab.model;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.util.Base64;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Marcus on 09/10/15.
 */
public class BusHandler{
    private static BusHandler instance;

    private BusHandler(){

    }

    public static BusHandler getInstance() {
        if(instance == null){
            instance = new BusHandler();
        }
        return instance;
    }

    public String getWifis() {
        return getJSON("https://ece01.ericsson.net:4443/ecity?Ericsson$Cell_Id_Value");
    }
    public String getMyWifi(Context context){
        WifiManager mngr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        String wifi = mngr.getConnectionInfo().getSSID();
        return wifi;
    }
    private String getJSON(String target){
        try {
            URL url = new URL (target);
            String encoding = Base64.encode(Secrets.API.getBytes(), Base64.DEFAULT).toString();

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoOutput(true);
            connection.setRequestProperty("Authorization", "Basic " + encoding);
            connection.setRequestProperty("Content-length", "0");
            connection.setUseCaches(false);
            connection.setAllowUserInteraction(false);
            connection.connect();
            int status = connection.getResponseCode();

            switch (status) {
                case 200:
                case 201:
                    BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line+"\n");
                    }
                    br.close();
                    return sb.toString();
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
