package com.protheansoftware.gab;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.telephony.CellLocation;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.AttributeSet;
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
    private TelephonyManager telephonyManager;
    private ArrayList<Profile> matches;
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        matches = new ArrayList<Profile>();


    }

    /**
     * Sets the list of matches
     * @param matches
     */
    public void setMatches(ArrayList<Profile> matches) {
        this.matches = matches;
    }

    /**
     * Shows the first match
     */
    public void showMatches() {
        if(matches.size() != 0) {
            setMatch(matches.get(0));
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_match_screen, container, false);
            return rootView;


    }
    public void onInflate(Activity activity, AttributeSet attrs, Bundle savedInstanceState) {
        super.onInflate(activity,attrs,savedInstanceState);
        showMatches();
    }



    @Override
    public void onResume() {
        super.onResume();
//        bh.startSessionIfNeeded(this.getContext(), (GsmCellLocation) telephonyManager.getCellLocation());
    }

    //Fills out the fragment with the match.
    public void setMatch(final Profile match){
        Log.d(TAG,Boolean.toString(getView()==null));
        ((TextView)getView().findViewById(R.id.nameTag)).setText(match.getName());
        ListView list = (ListView)getView().findViewById(R.id.centerContentList);
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

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.removePropertyChangeListener(listener);
    }


    /**
     * Loads the next match in the list
     * @param currentMatch
     */
    private void loadNextMatch(Profile currentMatch) {
        this.matches.remove(currentMatch);
        if(matches.isEmpty()) {
            pcs.firePropertyChange("No matches",null,null);
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
