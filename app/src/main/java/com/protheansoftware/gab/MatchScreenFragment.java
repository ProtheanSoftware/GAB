package com.protheansoftware.gab;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import java.util.ArrayList;


/**
 * Shows the different matches retrieved
 */
public class MatchScreenFragment extends Fragment implements View.OnClickListener {

    // list of matches
    private ArrayList<Match> matches;


        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);


        }
    public void setmatches(ArrayList<Match> matches) {
        this.matches = matches;
    }




    @Override
    public void onResume() {
        super.onResume();

    }

    //Fills out the fragment with the user.
    public void setUser() {

    }

    /**
     * Declines the other user
     */
    public void decline() {

    }

    /**
     * Sets  the list of potential matches
     */
    public void setPotentialMatches() {

    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {

        
    }

}
