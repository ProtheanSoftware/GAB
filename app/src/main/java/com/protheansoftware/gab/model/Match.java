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
    public Integer getNumberOfSimularInterests(ArrayList<String> matchInterests) {
        Integer number = 0;
        for(String s:matchInterests) {
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
        if(!matchInterests.isEmpty()) {
            for(String interest:this.interests) {
                if(matchInterests.contains(interest)) {
                    list.add(interest);
                }
            }

        }
            return list;
    }

    /**
     * Sets interests
     * @param interests
     */
    public void setInterests(ArrayList<String> interests) {
        this.interests = interests;
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
