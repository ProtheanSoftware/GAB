package com.protheansoftware.gab;
import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.appevents.AppEventsLogger;
import com.protheansoftware.gab.adapter.TabsPagerAdapter;
import com.protheansoftware.gab.model.JdbcDatabaseHandler;
import com.protheansoftware.gab.model.JsonParser;
import com.protheansoftware.gab.model.Match;
import com.protheansoftware.gab.model.Profile;
import java.util.Collections;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * This is the main activity, it holds the chat, the list of matches and the matchscreen fragments.
 * @author Tobias Allden
 */
public class MainActivity extends FragmentActivity implements ActionBar.TabListener {

    private ViewPager viewPager;
    private TabsPagerAdapter tabsAdapter;
    private ActionBar actionBar;
    private ArrayList<Match> matches;

    //Reference to matchscreen to be able to build with user profile
    private MatchScreenFragment match;
    private String name;

    //Tab titles
    private String[] tabs = {"Match Screen", "Matches", "Chat"};

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabbed);


        //Fix for mysql(jdbc)
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }



        initTabStructure();

    }

    /**
     * Initializes the tab structure
     */
    private void initTabStructure() {
        viewPager = (ViewPager) findViewById(R.id.pager);
        actionBar = getActionBar();
        tabsAdapter = new TabsPagerAdapter(getSupportFragmentManager());

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
     * Starts a thread that search for matches
     */
    private void searchForMatches() {
        //Start session and search for matches

        //Fills a arraylist with profiles wich are then converted to matches.
        ArrayList<Profile> matchesFromDb = new ArrayList<>();

        //Generate matches if db returned profiles
        if (!matchesFromDb.isEmpty()) {
            for (Profile p : matchesFromDb) {
                JsonParser parser = JsonParser.getInstance();
                this.matches.add(parser.generateMatchFromUserID(p.getId(), p.getFbId()));
            }
        }
    }

    /**
     * Generates matches from an arrayList from server
     */
    public void generateMatches() {

    }


    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
    }

    //Returns the fragment for the specified tag
    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        if (tab.getPosition() == 0) {
            if (matches.size() == 1) {
                viewPager.setCurrentItem(11);
                searchForMatches();
            } else {
                viewPager.setCurrentItem(tab.getPosition());
                this.match = (MatchScreenFragment) tabsAdapter.getItem(0);
                match.setmatches(matches);
            }
        }
        viewPager.setCurrentItem(tab.getPosition());
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
