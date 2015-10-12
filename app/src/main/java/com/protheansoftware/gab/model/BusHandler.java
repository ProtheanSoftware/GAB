package com.protheansoftware.gab.model;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.util.Base64;
import android.util.Log;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Marcus on 09/10/15.
 */
public class BusHandler{
    private final String TAG = "GAB";
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
        long newtime = System.currentTimeMillis();
        long old = newtime - 100000;
        return getJSON("https://ece01.ericsson.net:4443/ecity?resourceSpec=Ericsson$Cell_Id_Value&t1="+old+"&t2="+newtime);
    }
    public String getMyWifi(Context context){
        WifiManager mngr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        String wifi = mngr.getConnectionInfo().getSSID();
        return wifi;
    }
    private String getJSON(String target){
        try {
            byte[] authEncBytes = Base64.encode(Secrets.API.getBytes(), Base64.DEFAULT);
            String authEncStr = new String(authEncBytes);

            URL url = new URL (target);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestProperty("Authorization", "Basic " + authEncStr);
            connection.setRequestMethod("GET");
            connection.connect();

            int status = connection.getResponseCode();
            Log.d(TAG, "JSON GET request status code: "+status);

            InputStream is = connection.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);

            switch (status) {
                case 200:
                case 201:
                    String line;
                    int numCharsRead;
                    char[] charArray = new char[1024];
                    StringBuffer sb = new StringBuffer();
                    while ((numCharsRead = isr.read(charArray))>0) {
                        sb.append(charArray, 0, numCharsRead);
                    }
                    return sb.toString();
            }
        } catch(Exception e) {
            Log.e(TAG, "ERROR: "+e);
            e.printStackTrace();
        }
        return null;
    }

    public void startSessionIfNeeded(Context context) {
        JdbcDatabaseHandler jdb = JdbcDatabaseHandler.getInstance(); //Maybe pass this class around instead?
        int user_id = 237; //Will be passed to this function as a param
        ArrayList<String> wifis = new ArrayList<String>(); //Will be retrieved by getWifis function
        wifis.add("NETGEAR20");
        String my_wifi = getMyWifi(context).replaceAll("\"", "");

        Session session;
        Log.d(TAG, "My network: "+my_wifi+" Bussnetworks: "+wifis);
        if ((session = jdb.getSessionSSIDByUserId(user_id)) != null) {
            if (session.ssid.equals(my_wifi)) {
                Log.i(TAG, "Session running. Fetch and display matches.");
                //already on this bus network and session is started
                //Display my matches
            } else if (wifis.contains(my_wifi)){
                Log.i(TAG, "Updating session ssid.");
                Log.d(TAG, "User wifi is a bus wifi but not the same as the session wifi. " +
                        "My ssid: "+my_wifi+". Session ssid: "+session.ssid);
                //Im on a another bus network, update the session
                jdb.updateSession(my_wifi, user_id);
            }
        } else if (wifis.contains(my_wifi)){
            Log.e(TAG, "Starting a new session...");
            //Im on a bus network and no session is started, start a new session
            jdb.sessionStart(my_wifi);
        } else {
            Log.d(TAG, "IM NOT ON A BUS NETWORK!");
            //Im not currently on any buss network!
            //Display fragment prompting the user to connect to the buss network.
        }
    }
}
