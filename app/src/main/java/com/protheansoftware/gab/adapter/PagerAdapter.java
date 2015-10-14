package com.protheansoftware.gab.adapter;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import com.protheansoftware.gab.MatchScreenFragment;
import com.protheansoftware.gab.MatchesListFragment;
import com.protheansoftware.gab.SearchforMatches;
import com.protheansoftware.gab.TabFragment2;
import com.protheansoftware.gab.TabFragment3;

public class PagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;
    boolean hasMatches = false;

    public PagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }
    public void setHasMatches(boolean value) {
        hasMatches = value;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                if(!hasMatches) {
                    SearchforMatches searchforMatches = new SearchforMatches();
                    return searchforMatches;
                } else {
                    MatchScreenFragment matchScreenFragment = new MatchScreenFragment();
                    return matchScreenFragment;
                }
            case 1:
                MatchesListFragment tab2 = new MatchesListFragment();
                return tab2;
            case 2:
                TabFragment3 tab3 = new TabFragment3();
                return tab3;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}