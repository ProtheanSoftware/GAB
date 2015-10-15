package com.protheansoftware.gab;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.protheansoftware.gab.model.BusHandler;
import com.protheansoftware.gab.model.JdbcDatabaseHandler;
import com.protheansoftware.gab.model.Profile;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.sql.SQLException;
import java.util.ArrayList;


/**
 * Shows the different matches retrieved
 */
public class MatchScreenFragment extends Fragment implements View.OnClickListener {
    public static final String TAG = "MatchScreen";
    private JdbcDatabaseHandler jdb = JdbcDatabaseHandler.getInstance();
    private BusHandler bh = BusHandler.getInstance();
    private ArrayList<Profile> matches;
    Main2Activity main;


    private Thread updateWhenDoorsOpenedThread;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        matches = new ArrayList<Profile>();


    }
    public void setMain(Main2Activity main) {
        this.main = main;
    }

    /**
     * Sets the list of matches
     * @param matches
     */
    public void setMatches(ArrayList<Profile> matches) {
        this.matches = matches;
        setMatch(matches.get(0));
    }

    /**
     * Is called if no matches could be retrieved from the server
     */
    public void setNoMatches() {
        setMessage("Kunde ej hitta matchningar");
    }

    /**
     * When all the potential matches has been shown, show the searchscreen again.
     */
    private void noMoreMatches() {
        clearMatchScreen();
        getActivity().findViewById(R.id.searchScreen).setVisibility(View.VISIBLE);
        getActivity().findViewById(R.id.mainMatchScreen).setVisibility(View.GONE);
        main.setHasMatches(false);
        main.searchFormatches();


    }

    /**
     * Clears the matchscreen
     */
    private void clearMatchScreen() {
        ((TextView)getActivity().findViewById(R.id.nameTag)).setText("");
    }


    /**
     * Hides the searchscreen and shows the matchscreen
     */
    public void setHasMatches() {
        getActivity().findViewById(R.id.searchScreen).setVisibility(View.GONE);
        getActivity().findViewById(R.id.mainMatchScreen).setVisibility(View.VISIBLE);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //Thread that, when the doors have been opened on your bus, reload our matches.
        //If something goes wrong, wait 30 seconds before trying again
        updateWhenDoorsOpenedThread = new Thread(new Runnable() {
            @Override
            public void run() {
                boolean waitLong = false;
                boolean running = true;
                int waitTime = 24000; // 24 sec
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
                            if (BusHandler.getInstance().hasDoorsOpened("Vin_Num_001", waitTime)) {
                                //Searchmatches
                                Toast toast = new Toast(getContext());
                                toast.setDuration(Toast.LENGTH_LONG);
                                toast.setText("Doors have been opened, reloading potential matches..");
                                toast.show();
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
//        bh.startSessionIfNeeded(this.getContext(), (GsmCellLocation) telephonyManager.getCellLocation());
        if(!updateWhenDoorsOpenedThread.isAlive()) {
            updateWhenDoorsOpenedThread.start();
        }
        if (bh.startSessionIfNeeded(this.getContext()) == false) {
            setMessage("You need to be on a buss network to match with other people!");
        }
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
            noMoreMatches();
        } else {
            setMatch(this.matches.get(0));
        }

    }

    /**
     * Dislike the match
     */
    public void dislike(int id, String name) {
        try {
            JdbcDatabaseHandler.getInstance().addDislike(JdbcDatabaseHandler.getInstance().getUser(id).getDatabaseId(), name);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Likes the match
     */
    public void like(int id, String name){
        try {
            JdbcDatabaseHandler.getInstance().addLike(JdbcDatabaseHandler.getInstance().getUser(id).getDatabaseId(), name);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets the searchmessage
     */
    private void setMessage(String message) {
        ((TextView)getActivity().findViewById(R.id.message)).setText(message);
    }

    @Override
    public void onPause() {
        super.onPause();
        if(updateWhenDoorsOpenedThread.isAlive()) updateWhenDoorsOpenedThread.interrupt();
    }


    @Override
    public void onStart() {
        super.onStart();
        Button search = ((Button)getActivity().findViewById(R.id.searchbtn));
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setMessage("Söker efter matchningar...");
                main.searchFormatches();
            }
        });
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
