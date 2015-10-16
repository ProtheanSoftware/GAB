package com.protheansoftware.gab;

import android.util.Log;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import com.protheansoftware.gab.adapter.PagerAdapter;
import com.protheansoftware.gab.chat.MessageService;
import com.protheansoftware.gab.chat.MessagingFragment;
import com.protheansoftware.gab.model.JdbcDatabaseHandler;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.SQLException;


/**
 * The main activity for the application
 * @author Tobias Alldén
 */
public class MainActivity extends AppCompatActivity implements PropertyChangeListener{
    private DataHandler handler;
    private PagerAdapter adapter;
    private ViewPager viewPager;
    private boolean hasMatches = false;
    private MatchScreenFragment matchScreen;

    private Intent serviceIntent;

    private final String TAG = "MainActivity";

    @Override
    protected void onStart() {
        super.onStart();
        serviceIntent = new Intent(getApplicationContext(), MessageService.class);
        startService(serviceIntent);
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopService(serviceIntent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        handler = new DataHandler();
        handler.init();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.app_name);

        final TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Matchescreen"));
        tabLayout.addTab(tabLayout.newTab().setText("Matches"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        viewPager = (ViewPager) findViewById(R.id.pager);

        adapter = new PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount(), this);
        viewPager.setAdapter(adapter);
        handler.addPropertyChangeListener(this);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0 && matchScreen == null) {
                    matchScreen = (MatchScreenFragment) adapter.getItem(0);
                    matchScreen.setMain(MainActivity.this);
                }
                viewPager.setCurrentItem(tab.getPosition());

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    if(matchScreen == null) {
                        matchScreen = (MatchScreenFragment) adapter.getItem(0);
                        matchScreen.setMain(MainActivity.this);
                    }
                    if (!hasMatches) {
                        searchFormatches();
                    }
                }
                viewPager.setCurrentItem(tab.getPosition());

                //viewPager.setCurrentItem(tab.getPosition());
                Log.d(TAG, "Tab Reselected");
            }
        });
    }

    /**
     * Tells datahandler to search for matches
     */
    public void searchFormatches() {
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

    /**
     * Sets if there are matches
     * @param value
     */
    public void setHasMatches(boolean value) {
        hasMatches  =value;
    }

    @Override
    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
        if(propertyChangeEvent.getPropertyName().equals("MatchList")) {
            Log.d("Swag","hello");
            this.hasMatches = true;
            if(matchScreen == null) {
                matchScreen = (MatchScreenFragment)adapter.getItem(0);
            }
            matchScreen.setMatches(handler.getMatches());
            matchScreen.setHasMatches();


        }
        if(propertyChangeEvent.getPropertyName().equals("NoMatches")) {
            if(matchScreen == null) {
                matchScreen = (MatchScreenFragment)adapter.getItem(0);
            }
            matchScreen.setNoMatches();
        }
    }
    public void openChat(){
        adapter.setCount(3);
        Log.d(TAG, "Chat opened");
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);

        //If chat is already opened remove current and reload
        if(tabLayout.getTabCount() == 3){
            tabLayout.removeTabAt(2);
        }
        String recipient = "null";
        try{
            recipient = JdbcDatabaseHandler.getInstance().getUser(Integer.parseInt(MessagingFragment.getRecipientId())).getName();
        }catch (SQLException e){
        }
        tabLayout.addTab(tabLayout.newTab().setText(recipient));

        viewPager.setCurrentItem(2);
    }

    @Override
    protected void onDestroy() {
        handler.destroy();
        super.onDestroy();
    }

    public void closeChat() {
        //Make sure chat is opened
        if(adapter.getCount() > 2){
            TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
            tabLayout.removeTabAt(2);
            adapter.setCount(adapter.getCount()-1);
        }
    }
}