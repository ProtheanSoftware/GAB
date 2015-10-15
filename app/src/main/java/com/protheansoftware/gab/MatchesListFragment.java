package com.protheansoftware.gab;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
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

    private ListAdapter matchesListAdapter;
    private SwipeRefreshLayout swipeContainer;
    private Handler handler = new Handler();

    public Observable notifier;
    private boolean refreshing;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setDbh(JdbcDatabaseHandler.getInstance());
        reloadMatches();

        swipeContainer = (SwipeRefreshLayout) getActivity().findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                setRefreshing(true);
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        reloadMatches();
                        setRefreshing(false);
                    }
                });
                thread.start();
                handler.post(refresh);
            }
        });
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

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
    private final Runnable refresh = new Runnable() {
        @Override
        public void run() {
            if (isRefreshing()) {
                handler.postDelayed(this, 500);
            } else {
                swipeContainer.setRefreshing(false);
            }
        }
    };

    private void reloadMatches(){
        List<Profile> matches = new ArrayList<Profile>();
        try {
            matches = dbh.getMatches();
        } catch (Exception e) {
            e.printStackTrace();
        }

        matchesListAdapter = new MatchesListAdapter(getActivity(), matches);
        setListAdapter(matchesListAdapter);
    }

    public void setMain(Main2Activity main) {
        this.main = main;
    }

    public boolean isRefreshing() {
        return refreshing;
    }

    public void setRefreshing(boolean refreshing) {
        this.refreshing = refreshing;
    }
}