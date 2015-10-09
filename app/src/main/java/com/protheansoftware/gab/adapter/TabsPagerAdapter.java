package com.protheansoftware.gab.adapter;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.protheansoftware.gab.chat.MessagingFragment;
import com.protheansoftware.gab.MatchScreenFragment;
import com.protheansoftware.gab.MatchesListFragment;
import com.protheansoftware.gab.SearchforMatches;

/**
 * This adapter returns fragments for the main activity.
 * @author Tobias Alldén
 */
public class TabsPagerAdapter extends FragmentPagerAdapter {
    private int count;

    public TabsPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
        count=2;
    }

    /**
     * Returns a fragment sepcified by index (eg. matchscreen is id 0 and so on).
     * @param i
     * @return fragment
     */
    @Override
    public Fragment getItem(int i) {
        switch(i) {
            case 0:
                return new MatchScreenFragment();
            case 11:
                return new SearchforMatches();
            case 1:
                return new MatchesListFragment();
            case 2:
                return new MessagingFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return count;
    }
    public void setCount(int i){
        count = i;
        notifyDataSetChanged();
    }
}