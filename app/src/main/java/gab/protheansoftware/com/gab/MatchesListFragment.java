package gab.protheansoftware.com.gab;

import android.os.Bundle;
import android.widget.ListAdapter;

import gab.protheansoftware.com.gab.model.MySQLDatabaseHandler;
import gab.protheansoftware.com.gab.model.Profile;
import gab.protheansoftware.com.gab.adapter.MatchesListAdapter;


/**
 * Implement match list here
 */
public class MatchesListFragment extends android.support.v4.app.ListFragment {
MySQLDatabaseHandler dbh;


    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        dbh = new MySQLDatabaseHandler();

        Profile[] matches = new Profile[0];
        try {
            matches = (Profile[]) dbh.getMatches().toArray();
        } catch (Exception e) {
            e.printStackTrace();
        }


        ListAdapter matchesListAdapter = new MatchesListAdapter(getActivity(), matches);

        setListAdapter(matchesListAdapter);
    }

}
