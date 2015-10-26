package com.protheansoftware.gab.handlers;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.google.gson.Gson;

import com.protheansoftware.gab.model.Profile;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

/**
 * This singleton is used for parsing the json objects returned from facebook into java objects.
 * @author Tobias Allden
 */
public class JsonParser{

    //Class variables to be able to acess within inner functions
    String name;
    ArrayList<String> likeList = new ArrayList<String>();

    private static JsonParser instance = null;

    public static synchronized JsonParser getInstance() {
        if(instance == null) {
            instance = new JsonParser();
        }
            return instance;

    }



    /**
     * Gets the user name and likes from the facebook sdk and returns a new Match
     * @param dbId
     * @param fbId
     * @return
     */
    public Profile generateMatchFromUserID(Integer dbId, Long fbId) {
        String name = getNameFromFacebookId(fbId);
        ArrayList<String> likes = getLikeListFromFacebookId(fbId);
        return new Profile(dbId,fbId,name,likes);
    }

    /**
     * Returns an array of things that the user with the specified id has liked on facebook.
     * @param fbId
     * @return
     */
    public ArrayList<String> getLikeListFromFacebookId(long fbId) {
        new GraphRequest(AccessToken.getCurrentAccessToken(),"/" + fbId+"/likes",null, HttpMethod.GET,new GraphRequest.Callback() {
            public void onCompleted(GraphResponse response) {
                Gson g = new Gson();
                JSONArray jsonArray = response.getJSONArray();
                ArrayList<String> likeList = new ArrayList<String>();
                if(jsonArray != null) {
                    for(int i=0;i<jsonArray.length();i++) {
                        try {
                            likeList.add(jsonArray.get(i).toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }
                JsonParser.this.likeList = likeList;
            }
        }
        ).executeAsync();
        ArrayList<String> tmpLikeList = likeList;
        this.likeList = new ArrayList<String>();
        return tmpLikeList;
    }

    /**
     * Returns the name of the user with the specified facebook id.
     * @param fbId
     * @return
     */
    public String getNameFromFacebookId(long fbId) {
        new GraphRequest(AccessToken.getCurrentAccessToken(),"/" + fbId,null, HttpMethod.GET,new GraphRequest.Callback() {
            public void onCompleted(GraphResponse response) {
                Gson g = new Gson();
                try {
                    JsonParser.this.name = (response.getJSONObject().get("name").toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        ).executeAsync();

        String tmpName = this.name;
        this.name = "";
        return tmpName;
    }




}
