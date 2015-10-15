package com.protheansoftware.gab;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;

import com.protheansoftware.gab.model.IDatabaseHandler;
import com.protheansoftware.gab.model.Profile;
import com.protheansoftware.gab.adapter.MatchesListAdapter;
import com.protheansoftware.gab.model.JdbcDatabaseHandler;
import com.protheansoftware.gab.chat.MessagingFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;


/**
 * Implement match list here
 */
public class MatchesListFragment extends android.support.v4.app.ListFragment implements AdapterView.OnItemClickListener {

    private static final String TAG = "MatchesListFragment";
    private IDatabaseHandler dbh;
    private Main2Activity main;

    public Observable notifier;



    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setDbh(JdbcDatabaseHandler.getInstance());
        List<Profile> matches = new ArrayList<Profile>();
        try {
            matches = dbh.getMatches();
        } catch (Exception e) {
            e.printStackTrace();
        }

        ListAdapter matchesListAdapter = new MatchesListAdapter(getActivity(), matches);
        setListAdapter(matchesListAdapter);

        getListView().setOnItemClickListener(this);
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_item_list,container,false);
        return rootView;
    }

    public void setDbh(IDatabaseHandler dbh){
        this.dbh = dbh;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        int r_id = 0;

        if(parent.getItemAtPosition(position) instanceof Profile) {
            r_id = ((Profile)parent.getItemAtPosition(position)).getDatabaseId();
        }
        Log.d(TAG, "Opening chat with: " + r_id);

        MessagingFragment.setRecipientId(String.valueOf(r_id));

        //Switch tab and open chat
        main.openChat();
    }

    public void setMain(Main2Activity main) {
        this.main = main;
    }
}