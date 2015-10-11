package com.protheansoftware.gab.model;


import java.util.ArrayList;


/**
 * A class containing all tha data for a potential match
 */
public class Match {
    private int databaseId;
    private long facebookId;
    private String name;
    private ArrayList<String> interests;

    public Match(int databaseId,long facebookId,String name,ArrayList<String> interests) {
        this.databaseId = databaseId;
        this.facebookId = facebookId;
        this.name = name;
        this.interests = interests;

    }

    public int getDatabaseId() {
        return databaseId;
    }
    public long getFacebookId() {
        return facebookId;
    }

    public String getName() {
        return name;
    }

    public ArrayList<String> getInterests() {
        return interests;
    }



    /**
     * Returns number of simular interests between profiles.
     * @return
     */
    private Integer getNumberOfSimularInterests(ArrayList<String> yourInterests) {
        Integer number = 0;
        for(String s:yourInterests) {
            if(this.interests.contains(s)) {
                number +=1;
            }
        }
        return number;
    }

    @Override
    public String toString() {
        return "Name: " + name + " Likes " + interests.get(0) + " " + interests.get(1);
    }

    public long getFbId() {
        return facebookId;
    }
    public int getDbId() {
        return this.databaseId;
    }
}
