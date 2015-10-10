package com.protheansoftware.gab.model;
import android.content.Intent;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

/**
 * This class is used for parsing the json objects returned from facebook into java objects using gson.
 * @author Tobias Allden
 */
public class JsonParser {
    String name;


    /**
     * Gets the user name and likes from the facebook sdk and returns a new Match
     * @param dbId
     * @param fbId
     * @return
     */
    public Match generateMatchFromUserID(Integer dbId, Long fbId) {

        final ArrayList<String> likeList = new ArrayList<String>();

        //Get name from facebook api
        new GraphRequest(AccessToken.getCurrentAccessToken(),"/" + fbId,null, HttpMethod.GET,new GraphRequest.Callback() {
            public void onCompleted(GraphResponse response) {
                Gson g = new Gson();
                try {
                   name= (response.getJSONObject().get("name").toString());
                    Log.d("JSONS", name);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        ).executeAsync();

        //get likes
        new GraphRequest(AccessToken.getCurrentAccessToken(),"/" + fbId+"/likes",null, HttpMethod.GET,new GraphRequest.Callback() {
            public void onCompleted(GraphResponse response) {
                Gson g = new Gson();
                JSONArray jsonArray = response.getJSONArray();
                if(jsonArray != null) {
                    for(int i=0;i<jsonArray.length();i++) {
                        try {
                            likeList.add(jsonArray.get(i).toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    Log.d("JSONS lenght", Integer.toString(likeList.size()));

                }
            }
        }
        ).executeAsync();



        return new Match(dbId,fbId,name,likeList);

    }




}
