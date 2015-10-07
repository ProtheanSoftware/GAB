package gab.protheansoftware.com.gab;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.Toast;

import gab.protheansoftware.com.gab.chat.MessagingFragment;
import gab.protheansoftware.com.gab.model.IDatabaseHandler;
import gab.protheansoftware.com.gab.model.Profile;
import gab.protheansoftware.com.gab.adapter.MatchesListAdapter;
import gab.protheansoftware.com.gab.model.JdbcDatabaseHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;


/**
 * Implement match list here
 */
public class MatchesListFragment extends android.support.v4.app.ListFragment implements AdapterView.OnItemClickListener {

    private static final String TAG = "MatchesListFragment";
    private IDatabaseHandler dbh;

    public Observable notifier;


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

        getListView().setOnItemClickListener(this);
    }

    public void setDbh(IDatabaseHandler dbh){
        this.dbh = dbh;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        int r_id = 0;

        //Switch tab
        ViewPager pager = (ViewPager) getActivity().findViewById(R.id.pager);
        pager.setCurrentItem(2);



        Toast.makeText(getActivity(), String.valueOf(parent.getItemAtPosition(position)), Toast.LENGTH_SHORT).show();

        if(parent.getItemAtPosition(position) instanceof Profile) {
            r_id = ((Profile)parent.getItemAtPosition(position)).getId();
        }
        Log.d(TAG, "Opening chat with: " + r_id);

        MessagingFragment.setRecipientId(String.valueOf(r_id));

    }
}