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
                MatchesListFragment tab2 = new MatchesListFragment();
                tab2.setMain(main);
                return tab2;
            case 2:
                MessagingFragment tab3 = new MessagingFragment();
                return tab3;
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