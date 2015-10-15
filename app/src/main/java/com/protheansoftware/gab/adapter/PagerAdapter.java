package com.protheansoftware.gab.adapter;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import com.protheansoftware.gab.Main2Activity;
import com.protheansoftware.gab.MatchScreenFragment;
import com.protheansoftware.gab.MatchesListFragment;
import com.protheansoftware.gab.SearchforMatches;
import com.protheansoftware.gab.TabFragment2;
import com.protheansoftware.gab.TabFragment3;
import com.protheansoftware.gab.chat.MessagingFragment;

public class PagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;
    boolean hasMatches = false;
    Main2Activity main;
    MatchScreenFragment matchScreenFragment;
    MessagingFragment messagingFragment;
    MatchesListFragment matchesListFragment;

    public PagerAdapter(FragmentManager fm, int NumOfTabs, Main2Activity main) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        this.main = main;
    }
    public void setHasMatches(boolean value) {
        hasMatches = value;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                if(matchScreenFragment == null) {
                    matchScreenFragment = new MatchScreenFragment();
                    matchScreenFragment.setMain(main);
                }
                return matchScreenFragment;
            case 1:
                if(matchesListFragment == null) {
                    matchesListFragment = new MatchesListFragment();
                    matchesListFragment.setMain(main);
                }
                return matchesListFragment;
            case 2:
                if(messagingFragment == null){
                    messagingFragment = new MessagingFragment();
                }
                return messagingFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
    public void setCount(int i){
        this.mNumOfTabs = i;
        notifyDataSetChanged();
    }
}