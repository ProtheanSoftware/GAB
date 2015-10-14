package com.protheansoftware.gab;

import android.support.v4.app.*;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuItem;
import com.protheansoftware.gab.adapter.PagerAdapter;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;


public class Main2Activity extends AppCompatActivity implements PropertyChangeListener{
    private DataHandler handler;
    private PagerAdapter adapter;
    private ViewPager viewPager;
    private boolean hasMatches = false;
    private MatchScreenFragment matchScreen;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        handler = new DataHandler();
        handler.init();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.app_name);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Matchescreen"));
        tabLayout.addTab(tabLayout.newTab().setText("Matches"));
        tabLayout.addTab(tabLayout.newTab().setText("Chat"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

         viewPager = (ViewPager) findViewById(R.id.pager);

        adapter = new PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        handler.addPropertyChangeListener(this);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(tab.getPosition() == 0 && hasMatches) {
                    if(adapter.getItem(tab.getPosition()) instanceof MatchScreenFragment) {
                        matchScreen = (MatchScreenFragment) adapter.getItem(0);
                        matchScreen.setMatches(handler.getMatches());
                        viewPager.setCurrentItem(tab.getPosition());
                    }
                } else {
                    searchFormatches();
                }
                viewPager.setCurrentItem(tab.getPosition());

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                if(tab.getPosition() == 0 && hasMatches) {
                    adapter.setHasMatches(hasMatches);
                    matchScreen = (MatchScreenFragment)adapter.getItem(0);
                    matchScreen.setMatches(handler.getMatches());

                } else {
                    searchFormatches();
                }
                viewPager.setCurrentItem(tab.getPosition());

            }
        });
    }

    /**
     * Tells datahandler to search for matches
     */
    private void searchFormatches() {
        handler.searchForMatches();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
        if(propertyChangeEvent.getPropertyName().equals("MatchList")) {
            this.hasMatches = true;

        }
    }
}