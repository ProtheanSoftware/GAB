package com.protheansoftware.gab.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import com.protheansoftware.gab.R;
import com.protheansoftware.gab.handlers.JdbcDatabaseHandler;
import com.protheansoftware.gab.model.Profile;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * This activity handles the login functionality required to sign in to facebook. Also contains a method for logging in as a developer.
 * @author Tobias Allden
 */
public class FacebookLogin extends Activity {
    LoginButton loginButton;
    CallbackManager callbackManager;
    public static final String TAG = "FacebookLogin";

    private Boolean userExists;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userExists = false;
        Log.d(TAG, "FacebookLogin activity started");

        //initialize facebook sdk
        FacebookSdk.sdkInitialize(getApplicationContext());
        Log.d(TAG, "Facebook sdk initialized");

        Log.d(TAG, "Tries to log in with previous user...");
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("user_likes"));

        //Check if logged in, if true, starts main activity
        if(AccessToken.getCurrentAccessToken() != null) {
            Log.d(TAG, "User logged in sucessfully");
            checkLoginAndStart();
        } else {
            Log.d(TAG, "Could not log in previous user, will now show facebooklogin...");
        }

        setContentView(R.layout.activity_facebook_login);
        callbackManager=CallbackManager.Factory.create();
        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("user_likes");
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "Login sucessful");
                checkLoginAndStart();
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "login cancelled");
                Toast.makeText(getApplicationContext(), "Facebook login interrupted", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(FacebookException e) {
                Log.d(TAG, "FacebookLogin error");
                Toast.makeText(getApplicationContext(), "There was an error, please try again", Toast.LENGTH_LONG).show();

            }

        });
    }

    /**
     * This method is called when the "developer login" button is pressed, and allows for using the
     * app without the need for a facebook account (anly accessable to developers)
     */
    public void developerLogin(View view) {
        //makeshift facebook id
        long fb_id = 11235813;
        addTempUserIfNotExists(fb_id);
        synchronized (userExists) {
            while(!userExists) {
                try {
                    userExists.wait(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        startMainActivity();

    }

    /**
     * Waits with starting the main application until we have confirmed the user
     * exists.
     */
    private void checkLoginAndStart() {
        addUserIfNotExists();
        synchronized (userExists) {
            while(!userExists) {
                try {
                    userExists.wait(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        startMainActivity();
    }

    /**
     * Checks if the user exists in database with Facebook id from Facebook SDK
     * Flags userExists true when done, either done adding or done checking if exists
     */
    private void addUserIfNotExists() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (userExists) {
                    long fb_id = Long.parseLong(AccessToken.getCurrentAccessToken().getUserId());
                    Profile user = null;

                    try {
                        user = JdbcDatabaseHandler.getInstance(fb_id).getUserFromFBID(fb_id);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                    FacebookParser parser = new FacebookParser();
                    if (user == null) {
                        Log.d(TAG, "User doesn't exist, creating user..");
                        JdbcDatabaseHandler.getInstance().addUser(parser.getNameFromFacebookId(fb_id), fb_id, parser.getLikeListFromFacebookId(fb_id));
                        userExists = true;
                        return;
                    } else {
                        userExists = true;
                        return;
                    }
                }
            }
        });
        thread.start();
    }

    /**
     * Simular to addUserIfNotExists, to be used with a makeshift facebook id (Developer login)
     */
    private void addTempUserIfNotExists(final long fb_id) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (userExists) {
                    Profile user = null;

                    try {
                        user = JdbcDatabaseHandler.getInstance(fb_id).getUserFromFBID(fb_id);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    ArrayList<String> likes = new ArrayList<String>();
                    likes.add("Netflix");
                    likes.add("Snubbar med slips");
                    likes.add("Chalmers University of Technology");
                    if (user == null) {
                        Log.d(TAG, "Temp User doesn't exist, creating user..");
                        JdbcDatabaseHandler.getInstance().addUser("Gilfoyle", fb_id, likes);
                        userExists = true;
                        return;
                    } else {
                        userExists = true;
                        return;
                    }
                }
            }
        });
        thread.start();
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }


    /**
     * Starts the main activity.
     */
    public void startMainActivity() {
        Intent mainActivity = new Intent(this,MainActivity.class);
        startActivity(mainActivity);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_facebook_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Inner class for parsing the facebook sdk responses
     */
    class FacebookParser {
        /**
         * Returns an array of things that the user with the specified id has liked on facebook.
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
}
