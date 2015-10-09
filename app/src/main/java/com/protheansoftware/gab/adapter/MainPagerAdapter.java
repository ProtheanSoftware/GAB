package com.protheansoftware.gab.adapter;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by Marcus on 09/10/15.
 */
public class MainPagerAdapter extends PagerAdapter {
    //Holds all current views
    private ArrayList<View> views = new ArrayList<View>();

    //"Object" is the page, method tells where to put item,  if item nolonger exists, return POSITION_NONE
    @Override
    public int getItemPosition (Object object)
    {
        int index = views.indexOf(object);
        if (index == -1)
            return POSITION_NONE;
        else
            return index;
    }

    //Called when ViewPager wants an item, we get from ou list
    @Override
    public Object instantiateItem (ViewGroup container, int position)
    {
        View v = views.get(position);
        container.addView(v);
        return v;
    }

    // Remove view from list
    @Override
    public void destroyItem (ViewGroup container, int position, Object object)
    {
        container.removeView(views.get(position));
    }

    @Override
    public int getCount() {
        return views.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    /**
     * Add "view" to end of "views".
     **/
    public int addView (View v)
    {
        return addView (v, views.size());
    }

    /**
     * Add "view" at "position" to "views".
     * Returns position of new view.
     **/
    public int addView (View v, int position)
    {
        views.add (position, v);
        return position;
    }

    /**
     * Removes "view" from "views".
     * Retuns position of removed view.
     * */
    public int removeView (ViewPager pager, View v)
    {
        return removeView (pager, views.indexOf (v));
    }
    /**
     * Removes the "view" at "position" from "views".
     * Retuns position of removed view.
     * The app should call this to remove pages; not used by ViewPager.
     **/
    public int removeView (ViewPager pager, int position)
    {
        pager.setAdapter (null);
        views.remove (position);
        pager.setAdapter (this);

        return position;
    }

    /**
     * Returns the "view" at "position".
     * The app should call this to retrieve a view; not used by ViewPager.
     **/
    public View getView (int position)
    {
        return views.get (position);
    }
}
