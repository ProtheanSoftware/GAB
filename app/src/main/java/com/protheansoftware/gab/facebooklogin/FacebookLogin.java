package com.protheansoftware.gab.facebooklogin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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

import com.protheansoftware.gab.MainActivity;
import com.protheansoftware.gab.R;

import java.util.Arrays;

public class FacebookLogin extends Activity {
    LoginButton loginButton;
    CallbackManager callbackManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("FacebookLogin", "FacebookLogin activity started");



        //initialize facebook sdk
        FacebookSdk.sdkInitialize(getApplicationContext());
        Log.i("FacebookLogin", "Facebook sdk initialized");

        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("user_likes"));


        //Check if logged in, if tre, starts main activity
        if(AccessToken.getCurrentAccessToken() != null) {
            Log.i("FacebookLogin", "Logged in sucessfully");
        }




        setContentView(R.layout.activity_facebook_login);
        callbackManager=CallbackManager.Factory.create();
        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("user_likes");
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.i("FacebookActivity", "Login sucessful");
                startMainActivity();

            }

            @Override
            public void onCancel() {
                Log.i("FacebookActivity", "login cancelled");
                Toast.makeText(getApplicationContext(), "Facebook login interrupted", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(FacebookException e) {
                Log.i("FacebookActivity", "FacebookLogin error");
                Toast.makeText(getApplicationContext(), "There was an error, please try again", Toast.LENGTH_LONG).show();

            }

        });
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }


    /**
     * Starts the main activity is user successfully logged in to facebook.
     */
    public void startMainActivity() {
        Intent mainActivity = new Intent(this,MainActivity.class);
        Log.i("DEBUG","INTENT STARTED");
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
}
