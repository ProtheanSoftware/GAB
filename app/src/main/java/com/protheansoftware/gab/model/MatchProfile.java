package com.protheansoftware.gab.model;

import java.util.ArrayList;

/**
 * Created by isoa3 on 21/10/15.
 */
public class MatchProfile extends Profile {
    private String dgw;

    public MatchProfile(int databaseId, long facebookId, String name, ArrayList<String> interests, String dgw) {
        super(databaseId, facebookId, name, interests);
        this.dgw = dgw;
    }

    public String getDgw() {
        return dgw;
    }
}
