package gab.protheansoftware.com.gab.adapter;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import gab.protheansoftware.com.gab.ChatFragment;
import gab.protheansoftware.com.gab.MatchScreenFragment;
import gab.protheansoftware.com.gab.MatchesListFragment;
import gab.protheansoftware.com.gab.SearchforMatches;

/**
 * This adapter returns fragments for the main activity.
 * @author Tobias Alldén
 */
public class TabsPagerAdapter extends FragmentPagerAdapter {


    public TabsPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    /**
     * Returns a fragment sepcified by index (eg. matchscreen is id 0 and so on).
     * @param i
     * @return
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
                return new ChatFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }
}
