package gab.protheansoftware.com.gab.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import gab.protheansoftware.com.gab.R;

/**
 * Created by Oscar Hall on 01/10/15.
 */
public class MatchesListAdapter extends ArrayAdapter<String>{



    public MatchesListAdapter(Context context, String[] profiles)
    {
        super(context, R.layout.matches_list_row_template , profiles);
    }

    //TODO replace the inflater with view holder pattern
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View custom_row = inflater.inflate(R.layout.matches_list_row_template, parent, false);

        String SingleMatchItem = getItem(position);
        TextView matchedNameText = (TextView) custom_row.findViewById(R.id.matchedNameText);
        ImageView matchedPicture = (ImageView) custom_row.findViewById(R.id.matchedPicture);

        matchedNameText.setText(SingleMatchItem);
        matchedPicture.setImageResource(R.drawable.oskar);

        return custom_row;
    }
}
