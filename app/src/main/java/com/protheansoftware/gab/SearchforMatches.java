package com.protheansoftware.gab;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class SearchforMatches extends android.support.v4.app.Fragment {
    TextView message;
    public static final String TAG = "SearchFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_searchfor_matches,container,false);
        message = (TextView)(getActivity().findViewById(R.id.messagetextview));
        return rootView;
    }

    public void setMessage(String message) {
        this.message.setText(message);
    }

}
