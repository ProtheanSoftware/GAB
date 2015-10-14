package com.protheansoftware.gab.model;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.telephony.CellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.util.Base64;
import android.util.Log;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

/**
 * @author oskar
 * Created by oskar on 09/10/15.
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


    public ArrayList<String> getWifis() {
        long newtime = System.currentTimeMillis();
        long oldtime = newtime - 100000;
        String tmp = getJSON("https://ece01.ericsson.net:4443/ecity?resourceSpec=Ericsson$Cell_Id_Value&t1=" + oldtime + "&t2=" + newtime);

        ArrayList<String> sa = new ArrayList<>();
        Matcher m = Pattern.compile("(?<=\"value\":\")([^\"]*)").matcher(tmp);

        while (m.find()) {sa.add(m.group());}

        return sa;
    }

    /**
     * Gets the device wifi
     * @param context The devices context
     * @return SSID of wifi
     */
    public String getMyWifi(Context context){
        WifiManager mngr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        String wifi = mngr.getConnectionInfo().getSSID();
        return wifi;
    }

    /**
     * Gets from Electricitys API using target URL
     * @param target Target url to get from
     * @return String of response, in JSON form.
     */
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

    public void startSessionIfNeeded(Context context, GsmCellLocation cellLocation) {
        JdbcDatabaseHandler jdb = JdbcDatabaseHandler.getInstance(); //Maybe pass this class around instead?
        int user_id = 237; //Will be passed to this function as a param
        ArrayList<String> wifis = getWifis();
        String my_wifi = Integer.toHexString(cellLocation.getCid()).toUpperCase();

        if (my_wifi.length() < 8) {
            my_wifi = "0" + my_wifi;
        }

        wifis.add(my_wifi);

        Session session;
        Log.e(TAG, "My network: "+my_wifi+" Bussnetworks: "+wifis);
        if ((session = jdb.getSessionSSIDByUserId(user_id)) != null) {
            if (session.ssid.equals(my_wifi)) {
                Log.e(TAG, "Session running. Fetch and display matches.");
                //already on this bus network and session is started
                //Display my matches
            } else if (wifis.contains(my_wifi)){
                Log.e(TAG, "Updating session ssid.");
                Log.e(TAG, "User wifi is a bus wifi but not the same as the session wifi. " +
                        "My ssid: "+my_wifi+". Session ssid: "+session.ssid);
                //Im on a another bus network, update the session
                jdb.updateSession(my_wifi, user_id);
            } else {
                //I have a session running but is currently not on a buss network.
                //Dont allow matches.
            }
        } else if (wifis.contains(my_wifi)){
            Log.e(TAG, "Starting a new session...");
            //Im on a bus network and no session is started, start a new session
            jdb.sessionStart(my_wifi);
        } else {
            Log.e(TAG, "IM NOT ON A BUS NETWORK!");
            //Im not currently on any buss network!
            //Display fragment prompting the user to connect to the buss network.
        }
    }

    /**
     * Checks if the doors have been opened on the target bus within deltaTime
     * @param busVin Target bus
     * @param deltaTime Time to check from
     * @return True if the bus have been opened at anytime in the deltaTime, False else.
     */
    public boolean hasDoorsOpened(String busVin, int deltaTime){
        long newtime = System.currentTimeMillis();
        long oldtime = newtime - deltaTime; //2 * 60 * 1000/10
        String tmp = getJSON("https://ece01.ericsson.net:4443/ecity?dgw=Ericsson$" + busVin + "&resourceSpec=Ericsson$Open_Door_Value&t1=" + oldtime + "&t2=" + newtime);

        Log.d(TAG, tmp);

        //Search for value using regex, gets the string next to value, in this case either true or false.
        ArrayList<String> sa = new ArrayList<>();
        Matcher m = Pattern.compile("(?<=\"value\":\")([^\"]*)").matcher(tmp);

        while (m.find()) {
            if(m.group().equals("true")) return true;
        }

        return false;
    }
}
