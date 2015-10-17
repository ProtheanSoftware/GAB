package com.protheansoftware.gab.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.facebook.login.widget.LoginButton;
import com.protheansoftware.gab.R;
import com.protheansoftware.gab.handlers.JdbcDatabaseHandler;

import java.sql.SQLException;


/**
 * Created by boking on 2015-10-13.
 */
public class ProfileFragment extends android.support.v4.app.Fragment{

    private TextView profileName;
    private JdbcDatabaseHandler dbh = JdbcDatabaseHandler.getInstance();
    private LoginButton logoutBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile,container,false);
        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        profileName = (TextView)getActivity().findViewById(R.id.profileName);
        logoutBtn = (LoginButton)getActivity().findViewById(R.id.logoutBtn);

        try {
            profileName.setText(dbh.getUser(dbh.getMyId()).getName());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logOut();
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
}
