package com.protheansoftware.gab;

import android.media.Image;

import java.util.ArrayList;

import com.protheansoftware.gab.constants.Constants;

/**
 * A class containing all tha data for a potential match
 */
public class Match {
    private int id;
    private String name;
    private int age;
    private ArrayList<String> interests;
    private Image profileImage;
    private boolean hasSimularInterests;



    public Match(int id ,String name, int age,ArrayList<String> interests) {
        this.id = id;
        this.name = name;
        this.age = age;
        for(String s: interests) {
            this.interests.add(s);
        }

    }

    /**
     * Compares your interests with this persons interests to determine if match.
     * @param yourInterests
     */
    private void hasSimularInterests(ArrayList<String> yourInterests) {
        int numOfSimularInterests = 0;
        for(String s:yourInterests) {
            if(numOfSimularInterests > Constants.num_of_simular_interest_for_match) {
                hasSimularInterests =true;
                break;
            }
            if(this.interests.contains(s)) {
                numOfSimularInterests++;
            }
        }
    }

    /**
     * Returns all the simular interests.
     * @return
     */
    private ArrayList<String> getSimularInterests(ArrayList<String> yourInterests) {
        ArrayList<String> simularInterests = new ArrayList<String>();
        for(String s:yourInterests) {
            if(this.interests.contains(s)) {
                simularInterests.add(s);
            }
        }
        return simularInterests;
    }






}
