package com.protheansoftware.gab;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.mysql.jdbc.StringUtils;
import com.protheansoftware.gab.model.BusHandler;
import com.protheansoftware.gab.model.JdbcDatabaseHandler;
import com.protheansoftware.gab.model.Match;

import java.util.ArrayList;


/**
 * Shows the different matches retrieved
 */
public class MatchScreenFragment extends Fragment implements View.OnClickListener {
    private JdbcDatabaseHandler jdb = JdbcDatabaseHandler.getInstance();
    private BusHandler bh = BusHandler.getInstance();
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

        bh.startSessionIfNeeded(this.getContext());
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
