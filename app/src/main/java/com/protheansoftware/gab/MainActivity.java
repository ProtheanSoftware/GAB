package com.protheansoftware.gab;
import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.sql.SQLException;
import java.util.ArrayList;

import com.facebook.appevents.AppEventsLogger;
import com.protheansoftware.gab.adapter.TabsPagerAdapter;
import com.protheansoftware.gab.chat.MessagingFragment;
import com.protheansoftware.gab.model.Profile;
import com.protheansoftware.gab.chat.MessageService;
import com.protheansoftware.gab.model.JdbcDatabaseHandler;
import java.util.HashMap;



/**
 * This is the main activity, it holds the chat, the list of matches and the matchscreen fragments.
 * @author Tobias Allden
 */
public class MainActivity extends FragmentActivity implements ActionBar.TabListener {

    private static final String TAG = "MAINACTIVITY";
    private ViewPager viewPager;
    private TabsPagerAdapter tabsAdapter;
    private ActionBar actionBar;
    private ArrayList<Profile> matches;
    private Profile me;

    //Reference to matchscreen to be able to build with user profile
    private MatchScreenFragment match;
    private String name;

    //Tab titles
    private String[] tabs = {"Match Screen","Matches"};

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayShowTitleEnabled(false);
        setContentView(R.layout.activity_tabbed);


        //Fix for mysql(jdbc)
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        try {
            me = JdbcDatabaseHandler.getInstance().getUser(getMyDbId());
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e(TAG,"Could not generate me match");
        }



        ArrayList<String> list = new ArrayList<>();
        list.add("Netflix");
        Profile m1 = new Profile(1,11,"Karl",new ArrayList<String>());
        Profile m2 = new Profile(1,1,"Anders",new ArrayList<String>());
        Profile m3 = new Profile(1,144,"Sven",new ArrayList<String>());
        m1.setInterests(list);
        ArrayList<String> list2 = new ArrayList<>();
        list2.add("Netflix");

        list2.add("REACT");
        m2.setInterests(list2);
        ArrayList<String> list3 = new ArrayList<>();
        list3.add("Netflix");
        list3.add("REACT");
        list3.add("Tofsen - Chalmers Kårtidning");
        list3.add("Swag");
        m3.setInterests(list3);
        ArrayList<Profile> unsortedList = new ArrayList<>();
        unsortedList.add(m1);
        unsortedList.add(m2);
        unsortedList.add(m3);
        ArrayList<Profile> sortedList = sortMatches(unsortedList);
        for(Profile m:sortedList) {
            Log.d(TAG,m.getName() + "Simular interests: " + m.getSimularInterestList(me.getInterests()).toString());

        }
        initTabStructure();



    }

    /**
     * Initializes the tab structure
     */
    private void initTabStructure() {

        //Set my id based on fb
        JdbcDatabaseHandler.getInstance();

        //Initialize
        viewPager = (ViewPager) findViewById(R.id.pager);
        actionBar = getActionBar();
        tabsAdapter = new TabsPagerAdapter(getSupportFragmentManager(), (TelephonyManager) getSystemService(this.TELEPHONY_SERVICE));

        viewPager.setAdapter(tabsAdapter);
        actionBar.setHomeButtonEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        matches = new ArrayList<Profile>();

        //add tabs
        for (String tab_name : tabs) {
            actionBar.addTab(actionBar.newTab().setText(tab_name).setTabListener(this));
        }


        /**
         * on swiping the viewpager make respective tab selected
         * */
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                if (position == 2) {
                    Log.d(TAG, "chat opened");
                    if (actionBar.getTabCount() == 3) {
                        actionBar.removeTabAt(2);
                    }
                    String recipient = "null";
                    try {
                        recipient = JdbcDatabaseHandler.getInstance().getUser(Integer.parseInt(MessagingFragment.getRecipientId())).getName();
                    } catch (SQLException e) {
                    }
                    actionBar.addTab(actionBar.newTab().setText(recipient).setTabListener(MainActivity.this));
                }
                actionBar.setSelectedNavigationItem(position);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        AppEventsLogger.activateApp(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        AppEventsLogger.deactivateApp(this);
    }

    /**
     * Returns the current users db id.
     * @return
     */
    public int getMyDbId() throws SQLException {
       return JdbcDatabaseHandler.getInstance().getMyId();
    }


    /**
     * Starts a thread that search for matches and then returns an arraylist with matches
     */
    private void searchForMatches() {
        //Start session and search for matches
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    matches = JdbcDatabaseHandler.getInstance().getMatches();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.run();
    }

    /**
     * Sorts an arraylist after simular interest with your interests.
     * @param unsortedMatches
     * @return
     */
    private ArrayList<Profile> sortMatches(ArrayList<Profile> unsortedMatches) {
        //Generate keymap of numofinterest as keys and matches as values
        HashMap<Integer, ArrayList<Profile>> simularInterestMap = new HashMap<Integer, ArrayList<Profile>>();

        ArrayList<Profile> sortedMatches = new ArrayList<Profile>();

        //generate keymap
        for (Profile match : unsortedMatches) {
            Integer numofsiminterest = me.getNumberOfSimularInterests(match.getInterests());
            Log.d(TAG, match.getName() + "HAs " + numofsiminterest + "interests ");
            if (simularInterestMap.get(numofsiminterest) == null) {
                ArrayList<Profile> matchList = new ArrayList<Profile>();
                matchList.add(match);
                simularInterestMap.put(numofsiminterest, matchList);
            } else {
                simularInterestMap.get(numofsiminterest).add(match);
            }
        }
        for (Integer siminterest : simularInterestMap.keySet()) {
            if (!(siminterest == 0)) {
                for (Profile m : simularInterestMap.get(siminterest)) {
                    sortedMatches.add(m);
                }
            }

        }
        //Add users with 0 interest last
        if (simularInterestMap.get(0) != null) {
            for (Profile m : simularInterestMap.get(0)) {
                sortedMatches.add(m);
            }

        }
        return sortedMatches;

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
    }

    //Returns the fragment for the specified tag
    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        if(tab.getPosition() == 0) {
            if(matches.size() == 0) {
                viewPager.setCurrentItem(11);
                searchForMatches();
            } else {
                viewPager.setCurrentItem(tab.getPosition());
                this.match = (MatchScreenFragment)tabsAdapter.getItem(0);
                match.setMatches(matches);
            }
           // if(matches.isEmpty()) {
           //     //returns searchformatches
           //     viewPager.setCurrentItem(11);
           //     searchForMatches();
           // } else {
           //     viewPager.setCurrentItem(tab.getPosition());
           //     this.match = (MatchScreenFragment)tabsAdapter.getItem(0);
           //     match.setmatches(matches);
           // }
        }
        viewPager.setCurrentItem(tab.getPosition());
    }
    @Override
    public void onStart(){
        super.onStart();
        final Intent serviceIntent = new Intent(getApplicationContext(), MessageService.class);
        startService(serviceIntent);
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
    }

}
