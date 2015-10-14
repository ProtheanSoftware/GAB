package com.protheansoftware.gab;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.protheansoftware.gab.model.BusHandler;
import com.protheansoftware.gab.model.JdbcDatabaseHandler;
import com.protheansoftware.gab.model.Profile;

import java.sql.SQLException;
import java.util.ArrayList;


/**
 * Shows the different matches retrieved
 */
public class MatchScreenFragment extends Fragment implements View.OnClickListener {
    private JdbcDatabaseHandler jdb = JdbcDatabaseHandler.getInstance();
    private BusHandler bh = BusHandler.getInstance();
    private TelephonyManager telephonyManager;
    // list of matches
    private ArrayList<Profile> matches;
    private Thread updateWhenDoorsOpenedThread;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    public void setMatches(ArrayList<Profile> matches) {
        this.matches = matches;
        setMatch(matches.get(0));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setPotentialMatches();
        //Thread that, when the doors have been opened on your bus, reload our matches.
        //If something goes wrong, wait 30 seconds before trying again
        updateWhenDoorsOpenedThread = new Thread(new Runnable() {
            @Override
            public void run() {
                boolean waitLong = false;
                boolean running = true;
                int waitTime = 240000; // 24 sec
                while(running) {
                    try {
                        if (waitLong) {
                            Thread.sleep(waitTime * 2);
                            waitLong = false;
                        } else {
                            Thread.sleep(waitTime);
                        }
                    }catch (InterruptedException e){

                    }
                    try {
                        if(!Thread.currentThread().isInterrupted()) {
                            if (BusHandler.getInstance().hasDoorsOpened("171330", waitTime)) {
                                //Searchmatches
                                Log.d("MatchScreen", "SEARCHING MATCHES  GATES HAVE BEEN OPENED");
                            }
                        }else{
                            running = false;
                        }
                    }catch (Exception e){
                        waitLong = true;
                    }
                }
            }
        });
        updateWhenDoorsOpenedThread.start();
        View rootView = inflater.inflate(R.layout.fragment_match_screen,container,false);
        return rootView;
    }



    @Override
    public void onResume() {
        super.onResume();
        if(!updateWhenDoorsOpenedThread.isAlive()){
            updateWhenDoorsOpenedThread.start();
        }
        bh.startSessionIfNeeded(this.getContext(), (GsmCellLocation) telephonyManager.getCellLocation());
    }

    //Fills out the fragment with the match.
    public void setMatch(final Profile match){
        ((TextView)getActivity().findViewById(R.id.nameTag)).setText(match.getName());
        ListView list = (ListView)getActivity().findViewById(R.id.centerContentList);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,match.getInterests());
        list.setAdapter(adapter);

        ((Button)getActivity().findViewById(R.id.dislikeButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dislike(match.getDatabaseId(), match.getName());
                loadNextMatch(match);
            }
        });
        ((Button)getActivity().findViewById(R.id.likeButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                like(match.getDatabaseId(), match.getName());
                loadNextMatch(match);
            }
        });
    }


    /**
     * Loads the next match in the list
     * @param currentMatch
     */
    private void loadNextMatch(Profile currentMatch) {
        this.matches.remove(currentMatch);
        if(matches.isEmpty()) {
            //notify main to change view
        } else {
            setMatch(this.matches.get(0));
        }

    }

    /**
     * Dislike the match
     */
    public void dislike(int id, String name) {
        try {
            JdbcDatabaseHandler.getInstance().addDislike(JdbcDatabaseHandler.getInstance().getUserFromFBID(id).getDatabaseId(), name);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Likes the match
     */
    public void like(int id, String name){
        try {
            JdbcDatabaseHandler.getInstance().addLike(JdbcDatabaseHandler.getInstance().getUserFromFBID(id).getDatabaseId(), name);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets  the list of potential matches
     */
    public void setPotentialMatches() {

    }

    @Override
    public void onPause() {
        super.onPause();
        if(updateWhenDoorsOpenedThread.isAlive()) updateWhenDoorsOpenedThread.interrupt();
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

    public void init(TelephonyManager telephonyManager) {
        this.telephonyManager = telephonyManager;
    }
}
