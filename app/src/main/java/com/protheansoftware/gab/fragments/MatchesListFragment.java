package com.protheansoftware.gab.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.flipboard.bottomsheet.BottomSheetLayout;
import com.protheansoftware.gab.R;
import com.protheansoftware.gab.activities.MainActivity;
import com.protheansoftware.gab.handlers.IDatabaseHandler;
import com.protheansoftware.gab.model.Profile;
import com.protheansoftware.gab.adapter.MatchesListAdapter;
import com.protheansoftware.gab.handlers.JdbcDatabaseHandler;

import java.util.ArrayList;
import java.util.Observable;


/**
 * Implement match list here
 */
public class MatchesListFragment extends android.support.v4.app.ListFragment implements AdapterView.OnItemClickListener {

    private static final String TAG = "MatchesListFragment";
    private IDatabaseHandler dbh;
    private MainActivity main;

    private ArrayList<Profile> matches;

    private ListAdapter matchesListAdapter;
    private SwipeRefreshLayout swipeContainer;
    private Handler handler = new Handler();
    private Profile currentProfile;

    public Observable notifier;
    private boolean refreshing;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setDbh(JdbcDatabaseHandler.getInstance());
        reloadMatches();

        matchesListAdapter = new MatchesListAdapter(getActivity(), matches);
        setListAdapter(matchesListAdapter);

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
        initBottomSheetLayout();

        //Start a thread that check if you have been matched with someone
        //Thread handler.post(matchCheckThread);
        //end matchThread

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
                matchesListAdapter = new MatchesListAdapter(getActivity(), matches);
                setListAdapter(matchesListAdapter);
                initBottomSheetLayout();
            }
        }
    };

    private void reloadMatches(){
        matches = new ArrayList<Profile>();
        try {
            matches = dbh.getMatches();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initBottomSheetLayout() {
        getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int pos, long id) {
                if (arg0.getItemAtPosition(pos) instanceof Profile) {
                    currentProfile = null;
                    currentProfile = ((Profile) arg0.getItemAtPosition(pos));
                   BottomSheetLayout bottomSheetLayout = (BottomSheetLayout) getActivity().findViewById(R.id.bottomsheet);
                    bottomSheetLayout.showWithSheetView(getActivity().getLayoutInflater().inflate(R.layout.remove_menu, bottomSheetLayout, false));
                    ((TextView) getActivity().findViewById(R.id.deleteNameTag)).setText(currentProfile.getName());

                    ((Button) getActivity().findViewById(R.id.btn_remove)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            JdbcDatabaseHandler.getInstance().removeLike(currentProfile.getDbId());
                            reloadMatches();
                            ((BottomSheetLayout)getActivity().findViewById(R.id.bottomsheet)).dismissSheet();
                            matchesListAdapter = new MatchesListAdapter(getActivity(), matches);
                            handler.post(refresh);
                        }
                    });
                }
                    return true;

                }
        });

    }

    public void setMain(MainActivity main) {
        this.main = main;
    }

    public boolean isRefreshing() {
        return refreshing;
    }

    public void setRefreshing(boolean refreshing) {
        this.refreshing = refreshing;
    }


  /**  This thread have been cut out of the demo product. .OH
   * private Runnable matchCheckThread = new Runnable() {
        @Override
        public void run() {
            int waitTime = 15000; // 15 sec
            //Create a new instance of thread to check again after waitTime .OH
            handler.postDelayed(matchCheckThread, waitTime);
            try {
                  ArrayList<Profile> oldList = matches;
                  reloadMatches();
                  if (oldList.size() != matches.size()) {
                    //move this code to a custom handler instead. Should only send a msg to handler here. Buuuut will be debuged right now. .OH
                    MatchPopup mp = new MatchPopup();
                    mp.show(getFragmentManager(), "New match");
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    };*/
}