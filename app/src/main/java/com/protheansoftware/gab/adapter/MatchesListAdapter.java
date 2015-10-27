package com.protheansoftware.gab.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.Objects;

import com.protheansoftware.gab.R;
import com.protheansoftware.gab.fragments.MatchScreenFragment;
import com.protheansoftware.gab.handlers.BusHandler;
import com.protheansoftware.gab.handlers.JdbcDatabaseHandler;
import com.protheansoftware.gab.model.MatchProfile;
import com.protheansoftware.gab.model.Profile;
import com.protheansoftware.gab.model.Session;

/**
 * @author Oscar Hall
 * Created by Oscar Hall on 01/10/15.
 */
public class MatchesListAdapter extends ArrayAdapter<MatchProfile>{

    private Session mySession;

    public MatchesListAdapter(Context context, List<MatchProfile> profiles)
    {
        super(context, R.layout.matches_list_row_template , profiles);
    }

    //TODO replace the inflater with view holder pattern
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View custom_row = inflater.inflate(R.layout.matches_list_row_template, parent, false);

        final MatchProfile singleMatchItem = getItem(position);

        TextView matchedNameText = (TextView) custom_row.findViewById(R.id.matchedNameText);
        ImageView matchedPicture = (ImageView) custom_row.findViewById(R.id.matchedPicture);
        ImageView status = (ImageView) custom_row.findViewById(R.id.status);

        matchedNameText.setText(singleMatchItem.getName());
        matchedPicture.setImageResource(R.drawable.noprofpic);

        if(singleMatchItem.getDgw() != null) {

            if(mySession == null) {
                mySession = JdbcDatabaseHandler.getInstance().getSessiondgwByUserId(JdbcDatabaseHandler.getInstance().getMyId());
            }

            if (singleMatchItem.getDgw().equals(mySession.dgw)) {
                status.setImageResource(R.drawable.circle_green);
            }else{
                status.setImageResource(R.drawable.circle_blue);
            }
        }else{
            status.setImageResource(R.drawable.circle_gray);
        }
        return custom_row;
    }
}
