package com.protheansoftware.gab.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.telephony.TelephonyManager;

import com.protheansoftware.gab.chat.MessagingFragment;
import com.protheansoftware.gab.MatchScreenFragment;
import com.protheansoftware.gab.MatchesListFragment;
import com.protheansoftware.gab.SearchforMatches;

import java.util.ArrayList;

/**
 * This adapter returns fragments for the main activity.
 * @author Tobias Alldén
 */
public class TabsPagerAdapter extends FragmentPagerAdapter {
    private int count;
    private ArrayList<Fragment> fragments;

    public TabsPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
        fragments = new ArrayList<Fragment>();
        fragments.add(new MatchScreenFragment());
        fragments.add(new SearchforMatches());
        fragments.add(new MatchesListFragment());
        fragments.add(new MessagingFragment());
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
                return fragments.get(0);
            case 4:
                return fragments.get(1);
            case 1:
                return fragments.get(2);
            case 2:
                return fragments.get(3);
        }
        return null;
    }

    @Override
    public int getCount() {
        return count;
    }
    public void setCount(int i){
        if(count!=i) {
            count = i;
            notifyDataSetChanged();
        }
    }
}