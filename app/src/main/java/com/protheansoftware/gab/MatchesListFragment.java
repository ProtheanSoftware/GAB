package com.protheansoftware.gab;

import android.os.Bundle;
import android.widget.ListAdapter;
import com.protheansoftware.gab.model.IDatabaseHandler;
import com.protheansoftware.gab.model.Profile;
import com.protheansoftware.gab.adapter.MatchesListAdapter;
import com.protheansoftware.gab.model.JdbcDatabaseHandler;

import java.util.ArrayList;
import java.util.List;


/**
 * Implement match list here
 */
public class MatchesListFragment extends android.support.v4.app.ListFragment {

    private IDatabaseHandler dbh;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);



        setDbh(new JdbcDatabaseHandler());
        List<Profile> matches = new ArrayList<Profile>();
        try {
            matches = dbh.getMatches();
        } catch (Exception e) {
            e.printStackTrace();
        }


        ListAdapter matchesListAdapter = new MatchesListAdapter(getActivity(), matches);

        setListAdapter(matchesListAdapter);
    }
    public void setDbh(IDatabaseHandler dbh){
        this.dbh = dbh;
    }

}
