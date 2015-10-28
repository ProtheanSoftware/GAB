package com.protheansoftware.gab.model;


import java.util.ArrayList;


/**
 * This class is a representation of a User in the application.
 * @author Tobias Allden
 * @author Oskar Jedvert
 */
public class Profile {
    private int databaseId;
    private long facebookId;
    private String name;
    private ArrayList<String> interests;

    public Profile(int databaseId, long facebookId, String name, ArrayList<String> interests) {
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
        if(interests == null) return new ArrayList<String>();
        return interests;
    }

    public void setInterests(ArrayList<String> interests) {
        this.interests = interests;
    }



    /**
     * Returns number of simular interests between profiles.
     * @return
     */
    public Integer getNumberOfSimularInterests(ArrayList<String> yourInterests) {
        Integer number = 0;
        if(interests == null) return 0;
        for(String s:yourInterests) {
            if(this.interests.contains(s)) {
                number +=1;
            }
        }
        return number;
    }

    /**
     * Returns an arraylist of simular interest between this match and another match
     * @param matchInterests
     * @return
     */
    public ArrayList<String> getSimularInterestList(ArrayList<String> matchInterests) {
        ArrayList<String> list = new ArrayList<>();
        if(interests == null || matchInterests == null) return new ArrayList<String>();
        if(!matchInterests.isEmpty()) {
            for(String interest:this.interests) {
                if(matchInterests.contains(interest)) {
                    list.add(interest);
                }
            }

        }
        return list;
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
