package com.protheansoftware.gab;
import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pManager;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;
import android.widget.Toast;

import java.sql.SQLException;
import java.util.ArrayList;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.appevents.AppEventsLogger;
import com.protheansoftware.gab.adapter.TabsPagerAdapter;
import com.protheansoftware.gab.chat.MessagingFragment;
import com.protheansoftware.gab.model.Match;
import com.protheansoftware.gab.chat.MessageService;
import com.protheansoftware.gab.model.JdbcDatabaseHandler;
import com.protheansoftware.gab.model.JdbcDatabaseHandler;
import com.protheansoftware.gab.model.JsonParser;
import com.protheansoftware.gab.model.Match;
import com.protheansoftware.gab.model.Profile;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * This is the main activity, it holds the chat, the list of matches and the matchscreen fragments.
 * @author Tobias Allden
 */
public class MainActivity extends FragmentActivity implements ActionBar.TabListener {

    private static final String TAG = "MAINACTIVITY";
    private ViewPager viewPager;
    private TabsPagerAdapter tabsAdapter;
    private ActionBar actionBar;
    private ArrayList<Match> matches;
    private Match me;
    private FacebookParser fbParser;


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
        fbParser = new FacebookParser();
        try {
            me = fbParser.generateMatchFromUserID(getMyDbId(),getMyFacebookId());
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e(TAG,"Could not generate me match");
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
        matches = new ArrayList<Match>();

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
        Thread searchThread = new Thread() {
            public void run() {

            //Fills a arraylist with profiles wich are then converted to matches.
            ArrayList<Profile> matchesFromDb = new ArrayList<>();
            //Generate matches if db returned profiles
        }
        };
    }

    /**
     * Sorts the matchlist after number of simular interests between you and the match
     * @param unsortedMatches
     * @return
     */
    private void sortMatches(ArrayList<Match> unsortedMatches){
        boolean flag = true;
        Match temp;
        ArrayList<Match> matchesWithnoSimularInterests = new ArrayList<Match>();

        while(flag) {
            flag = false;
            for (int i = 0; i < unsortedMatches.size() - 1; i++) {
                if (me.getNumberOfSimularInterests(unsortedMatches.get(i).getInterests()) != 0) {
                    if (me.getNumberOfSimularInterests(unsortedMatches.get(i).getInterests()) <
                            me.getNumberOfSimularInterests(unsortedMatches.get(i + 1).getInterests())) {
                        temp = unsortedMatches.get(i);
                        unsortedMatches.set(i, unsortedMatches.get(i + 1));
                        unsortedMatches.set(i + 1, temp);
                        flag = true;
                    }
                } else {
                    matchesWithnoSimularInterests.add(unsortedMatches.get(i));
                }
            }
            if (!matchesWithnoSimularInterests.isEmpty()) {
                for (Match m : matchesWithnoSimularInterests) {
                    unsortedMatches.add(m);
                }
            }
        }


    }

    /**
     * Returns the facebook id for the current user
     * @return
     */
    private Long getMyFacebookId() {
        return Long.parseLong(AccessToken.getCurrentAccessToken().getUserId());
    }


    /**
     * Generates matches from an arrayList from server
     */
    public void generateMatches(ArrayList<Profile> dbMatches) {
        if(!dbMatches.isEmpty())
        {
            for (Profile p : dbMatches) {
                this.matches.add(fbParser.generateMatchFromUserID(p.getId(), p.getFbId()));
            }
        }

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

    /**
     * Inner class for parsing the facebook sdk responses
     */
    class FacebookParser {

        /**
         * Gets the user name and likes from the facebook sdk and returns a new Match
         *
         * @param dbId
         * @param fbId
         * @return
         */
        public Match generateMatchFromUserID(Integer dbId, Long fbId) {
            String name = getNameFromFacebookId(fbId);
            ArrayList<String> likes = getLikeListFromFacebookId(fbId);
            return new Match(dbId, fbId, name, likes);
        }

        /**
         * Returns an array of things that the user with the specified id has liked on facebook.
         *
         * @param fbId
         * @return
         */
        public ArrayList<String> getLikeListFromFacebookId(final long fbId) {
            final ArrayList<String> likeList = new ArrayList<String>();
            final long facebookId = fbId;

            Thread t = new Thread() {
                public void run() {
                    synchronized (likeList) {
                        new GraphRequest(AccessToken.getCurrentAccessToken(), "/"+fbId+"/likes", null, HttpMethod.GET, new GraphRequest.Callback() {
                            public void onCompleted(GraphResponse response) {
                                JSONObject object = response.getJSONObject();
                                try {
                                    JSONArray array = object.getJSONArray("data");
                                    for (int i = 0; i < array.length(); i++) {
                                        JSONObject obj = array.getJSONObject(i);
                                        String name = obj.get("name").toString();
                                        likeList.add(name);
                                    }
                                        likeList.notify();

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        ).executeAndWait();
                    }
                }

            };
            synchronized (likeList) {
                t.start();
                while (t.isAlive()) {
                    try {
                        likeList.wait(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                return likeList;
            }
        }


        /**
         * Returns the name of the user with the specified facebook id.
         *
         * @param fbId
         * @return
         */
        private String getNameFromFacebookId(long fbId) {
            final StringBuffer buffer = new StringBuffer();
            final long facebookId = fbId;
            Thread t = new Thread() {
                public void run() {
                    synchronized (buffer) {
                        new GraphRequest(AccessToken.getCurrentAccessToken(), "/" + facebookId, null, HttpMethod.GET, new GraphRequest.Callback() {
                            public void onCompleted(GraphResponse response) {
                                try {
                                    String returnName = response.getJSONObject().get("name").toString();
                                    buffer.append(returnName);
                                    buffer.notifyAll();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        ).executeAndWait();
                    }
                }

            };
            synchronized (buffer) {
                t.start();
                while (t.isAlive()) {
                    try {
                        buffer.wait(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                return buffer.toString();
            }
        }
    }


        @Override
        public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
        }

    }
