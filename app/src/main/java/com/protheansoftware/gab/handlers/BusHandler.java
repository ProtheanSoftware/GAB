package com.protheansoftware.gab.handlers;

import android.util.Base64;
import android.util.Log;

import com.protheansoftware.gab.model.Secrets;
import com.protheansoftware.gab.model.Session;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

/**
 * @author oskar, david
 * Created by oskar on 09/10/15.
 * Handles communicating with the icomera and electricity api's.
 */
public class BusHandler {
    private final String TAG = "BusHandler";
    private static BusHandler instance;
    private IDatabaseHandler jdb = JdbcDatabaseHandler.getInstance();

    private BusHandler(){}

    public static BusHandler getInstance() {
        if(instance == null){
            instance = new BusHandler();
        }
        return instance;
    }

    /**
     * @return The bus DGW on bus/network with icomera api or null if failed to retrieve.
     */
    public String getBusDGW() {
        Log.i(TAG, "Trying to retrieve the bus VIN.");
        String sa = null;

        //String rawXML = getXML("http://www.ombord.info/api/xml/system/");
        //Used temporarily since else the user has to be on a real bus.
        String rawXML = "" +
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<system>\n" +
                "    <system_id type=\"integer\">2501069301</system_id>\n" +
                "</system>"
                ;

        if(rawXML == null) return null;
        Log.d(TAG, "The raw xml retrieved from icomera local api: "+rawXML);

        Log.d(TAG, "Trying to extract the system_id from the rawXML using regex.");
        Matcher m = Pattern.compile("(?<=<system_id type=\"integer\">)([^<]*)").matcher(rawXML);
        while (m.find()) {sa = m.group();}

        //Couldn't extract the system_id from the rawXML
        if (sa == null) return null;

        return jdb.getdgwFromSystemId(sa);
    }

    /**
     * Tries to retrieve xml from target url using GET with a timeout of 3s.
     * @param target The url to get the xml from.
     * @return The result xml or null if unable to retrieve xml.
     */
    private String getXML(String target) {
        Log.i(TAG, "Retrieving raw xml from target url.");
        try {
            URL url = new URL(target);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(3000);
            connection.connect();

            int status = connection.getResponseCode();
            Log.d(TAG, "XML GET request status code: " + status);

            InputStream is = connection.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);

            switch (status) {
                case 200:
                    int numCharsRead;
                    char[] charArray = new char[1024];
                    StringBuffer sb = new StringBuffer();
                    while ((numCharsRead = isr.read(charArray)) > 0) {
                        sb.append(charArray, 0, numCharsRead);
                    }
                    return sb.toString();
            }
        } catch (ConnectException e) {
            Log.d(TAG, "Couldn't connect: " + e);
        } catch(Exception e) {
            Log.e(TAG, "ERROR: "+e);
        }
        return null;
    }

    /**
     * Gets from Electricitys API using target URL
     * @param target Target url to get from
     * @return String of response, in JSON form.
     */
    private String getJSON(String target){
        Log.i(TAG, "Retrieving JSON from target url.");
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

    /**
     * Starts a session if the user is connected to a bus network with icomera api installed.
     * Updates the session DGW if the user is on another bus than the session.
     * @return True if a session was started or updated, and the user is currently on a bus with
     * icomera api. Else returns false.
     */
    public boolean startSessionIfNeeded() {
        String bus_dgw = getBusDGW();

        Log.d(TAG, "Not on a bus network or cant access local icomera api.");
        if (bus_dgw == null) {return false;}

        Log.d(TAG, "My bus DGW: "+bus_dgw);

        Session session = jdb.getSessiondgwByUserId();
        if (session != null) {
            if (session.dgw.equals(bus_dgw)) {
                Log.i(TAG, "Session running.");
            } else {
                Log.i(TAG, "Updating session DGW.");
                Log.d(TAG, "User is on a bus but not the same as the session. " +
                        "My DGW: " + bus_dgw + ". Session DGW: " + session.dgw);
                jdb.updateSession(bus_dgw);
            }
        } else {
            Log.i(TAG, "Starting a new session...");
            jdb.sessionStart(bus_dgw);
        }
        return true;
    }

    /**
     * Checks if the doors have been opened on the users bus within deltaTime
     * @param deltaTime Time to check from
     * @return True if the bus have been opened at anytime in the deltaTime, False else.
     */
    public boolean hasDoorsOpened(int deltaTime){
        long newtime = System.currentTimeMillis();
        long oldtime = newtime - deltaTime; //2 * 60 * 1000/10
        String busVin = jdb.getSessiondgwByUserId().dgw;
        String tmp = getJSON("https://ece01.ericsson.net:4443/ecity?dgw=" + busVin + "&resourceSpec=Ericsson$Open_Door_Value&t1=" + oldtime + "&t2=" + newtime);

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
