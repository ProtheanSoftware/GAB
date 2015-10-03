package gab.protheansoftware.com.gab;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListAdapter;


import gab.protheansoftware.com.gab.model.IDatabaseHandler;
import gab.protheansoftware.com.gab.adapter.MatchesListAdapter;
import gab.protheansoftware.com.gab.model.JdbcDatabaseHandler;
import gab.protheansoftware.com.gab.model.Profile;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;


/**
 * Implement match list here
 */
public class MatchesListFragment extends android.support.v4.app.ListFragment {
    private static final String TAG ="MATCH_LIST_FRAGMENT";
    private IDatabaseHandler dbh;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setDbh(new JdbcDatabaseHandler());
        ArrayList<Profile> profiles = null;
        try {
            profiles = dbh.getMatches();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        ArrayList<String> matches = new ArrayList<String>();
        if(profiles != null){
            for(Profile temp : profiles){
                matches.add(temp.getName());
            }
        }
        Log.v(TAG, "Matches: " + matches.size());
        String[] matchesArray = Arrays.copyOf(matches.toArray(), matches.size(), String[].class);
        ListAdapter matchesListAdapter = new MatchesListAdapter(getActivity(), matchesArray);
        setListAdapter(matchesListAdapter);
    }
    public void setDbh(IDatabaseHandler dbh){
        this.dbh = dbh;
    }

}
