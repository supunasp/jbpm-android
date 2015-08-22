package org.jbpm.mobileclient;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.jbpm.mobileclient.connection.ClientRestEngine;


/**
 * A login screen that offers login via username/password.
 */
public class LoginActivity extends Activity {

    private UserLoginTask authTask = null;


    // UI references.
    private EditText mUserView;
    private EditText mPasswordView;

    // Server Address
    private final String serverAddress ="http://10.0.2.2:8080/jbpm-console";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Set up the login form.
        mUserView = (EditText) findViewById(R.id.userName);
        mPasswordView = (EditText) findViewById(R.id.password);

        //
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button UserLoginButton = (Button) findViewById(R.id.user_sign_in_button);
        UserLoginButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

    }

    public void attemptLogin() {
        if (authTask != null) {
            return;
        }

        // Reset errors.
        mUserView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String userName = mUserView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid userName address.
        if (TextUtils.isEmpty(userName)) {
            mUserView.setError(getString(R.string.error_field_required));
            focusView = mUserView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.

            authTask = new UserLoginTask(userName, password);
            authTask.execute((Void) null);
        }
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    private class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String userName;
        private final String mPassword;
        String authHeader = "";


        UserLoginTask(String userNme, String password) {
            userName = userNme;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // set Authentication Header which will be used for later connections
            authHeader = "Basic " + ClientRestEngine.Base64Util.encode(userName + ":" + mPassword);
            return true;
        }
        @Override
        protected void onPostExecute(final Boolean success) {
            authTask = null;
            if (!success) {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            } else {
                Intent i = new Intent(LoginActivity.this, MenuActivity.class);
                i.putExtra("username", userName);
                i.putExtra("AuthHeader", authHeader);
                i.putExtra("ServerAddress",serverAddress);
                startActivity(i);
                finish();
            }
        }

        @Override
        protected void onCancelled() {
            authTask = null;

        }
    }
}

