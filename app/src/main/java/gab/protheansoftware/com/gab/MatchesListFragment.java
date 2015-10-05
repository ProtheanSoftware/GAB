package gab.protheansoftware.com.gab;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.AdapterView;
import android.widget.Toast;

import gab.protheansoftware.com.gab.model.MySQLDatabaseHandler;
import gab.protheansoftware.com.gab.model.Profile;
import gab.protheansoftware.com.gab.adapter.MatchesListAdapter;


/**
 * Implement match list here
 */
public class MatchesListFragment extends android.support.v4.app.ListFragment implements AdapterView.OnItemClickListener {
MySQLDatabaseHandler dbh;


    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
    }

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

        getListView().setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(getActivity(), String.valueOf(parent.getItemAtPosition(position)),Toast.LENGTH_SHORT);
    }
}
