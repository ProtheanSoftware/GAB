package com.protheansoftware.gab.handlers;

import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;

import com.protheansoftware.gab.model.Profile;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * This class handles the bulk of the data the application uses
 * @author Tobias Alldén
 */
public class DataHandler {
    private static final String TAG = "DATAHANDLER";
    private ArrayList<Profile> matches = new ArrayList<Profile>();
    private Profile me;
    private PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    private StrictMode.ThreadPolicy oldPolicy;

    private JdbcDatabaseHandler jdb;

    private Handler handler;

    /**
     * Initializes the DataHandler
     */
    public void init() {
        //Fix for mysql(jdbc)
        //Mysql cannot be reached through threads using the default threadpolicy
        //Here we save the oldpolicy so as to not change any settings on the device by mistake
        if (android.os.Build.VERSION.SDK_INT > 9) {
            oldPolicy = StrictMode.getThreadPolicy();
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        jdb = JdbcDatabaseHandler.getInstance();
        try {
            me = jdb.getUser(getMyDbId());
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e(TAG, "Could not generate me match");
        }
        handler = new Handler();

    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.removePropertyChangeListener(listener);
    }

    /**
     * Returns the profile of the current user
     * @return
     */
    public Profile getMyProfile() {
        return me;
    }

    private Runnable searchMatches = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "Searching for matches");
            try {
                matches = jdb.getPotentialMatches();
            } catch (SQLException e) {
                Log.e(TAG,"Error: "+e);
                e.printStackTrace();
            }
            Log.d(TAG, matches.toString());
            if(matches == null || matches.isEmpty()){
                pcs.firePropertyChange("NoMatches",null,null);
            }else{
                sortMatches(matches);
                pcs.firePropertyChange("MatchList",null,matches);
            }
        }
    };

    /**
     * Starts a thread that search for matches and then returns an arraylist with matches
     */
    public void searchForMatches() {
        Log.d(TAG, "Searching for matches...");

        //Start session and search for matches
        if(jdb.getSessiondgwByUserId(jdb.getMyId())!=null) {
            handler.post(searchMatches);
        }
    }



    /**
     * Returns the matches
     */
    public ArrayList<Profile> getMatches() {
        return this.matches;
    }

    /**
     * Sorts the matchlist after number of simular interests between you and the match
     * @param unsortedMatches
     * @return
     */
    private void sortMatches(ArrayList<Profile> unsortedMatches) {
        boolean flag = true;
        Profile temp;
        ArrayList<Profile> matchesWithnoSimularInterests = new ArrayList<Profile>();

        while (flag) {
            flag = false;
            for (int i = 0; i < unsortedMatches.size() - 1; i++) {
                if (me.getNumberOfSimularInterests(unsortedMatches.get(i).getInterests()) != 0) {
                    if (me.getNumberOfSimularInterests(unsortedMatches.get(i).getInterests()) <
                            me.getNumberOfSimularInterests(unsortedMatches.get(i + 1).getInterests())) {
                        temp = unsortedMatches.get(i);
                        unsortedMatches.set(i, unsortedMatches.get(i + 1));
                        unsortedMatches.set(i + 1, temp);
                        flag = true;
                    }
                } else {
                    matchesWithnoSimularInterests.add(unsortedMatches.get(i));
                }
            }
            if (!matchesWithnoSimularInterests.isEmpty()) {
                for (Profile m : matchesWithnoSimularInterests) {
                    unsortedMatches.add(m);
                }
            }
        }
    }




    /**
     * Returns the current users db id.
     * @return
     */
    public int getMyDbId() throws SQLException {
        return JdbcDatabaseHandler.getInstance().getMyId();
    }

    public void destroy(){
        StrictMode.setThreadPolicy(oldPolicy);
    }

}
