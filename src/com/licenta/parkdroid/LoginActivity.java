/**
 * 
 */
package com.licenta.parkdroid;

import com.licenta.park.Park;
import com.licenta.parkdroid.preferences.Preferences;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author vladucu
 *
 */
public class LoginActivity extends Activity {
    
    private static final String TAG = "LoginActivity";
    private static final boolean DEBUG = true;
    
    //TODO drawable/main_logo.png trebuie facut 355*158
    private TextView mNewAccountTextView;
    private EditText mUserNameEditText;
    private EditText mPasswordEditText;
    
    private AsyncTask<Void, Void, Boolean> mLoginTask;

    private ProgressDialog mProgressDialog;

    /* Called when the activity is first created.
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.login_activity);

        // Set up the UI.
        ensureUi();
    }

    private ProgressDialog showProgressDialog() {
        if (mProgressDialog == null) {
            ProgressDialog dialog = new ProgressDialog(this);
            dialog.setTitle(R.string.login_dialog_title);
            dialog.setMessage(getString(R.string.login_dialog_message));
            dialog.setIndeterminate(true);
            dialog.setCancelable(true);
            mProgressDialog = dialog;
        }
        mProgressDialog.show();
        return mProgressDialog;
    }

    private void dismissProgressDialog() {
        try {
            mProgressDialog.dismiss();
        } catch (IllegalArgumentException e) {
            // We don't mind. android cleared it for us.
        }
    }
    
    private void ensureUi() {
        // TODO implement ensureUi LoginActivity
        final Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mLoginTask = new LoginTask().execute();
                if (DEBUG) Log.d(TAG, "onClick Fa login");
            }
        });
/*
        mNewAccountTextView = (TextView) findViewById(R.id.newAccountTextView);
        mNewAccountTextView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO implement "Need an account" link
                //startActivity(new Intent(Intent.ACTION_VIEW, Uri
                //        .parse(Foursquare.FOURSQUARE_MOBILE_SIGNUP)));              
            }
        });*/

        mUserNameEditText = ((EditText) findViewById(R.id.userNameEditText));
        mPasswordEditText = ((EditText) findViewById(R.id.passwordEditText));

        TextWatcher fieldValidatorTextWatcher = new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                button.setEnabled(phoneNumberEditTextFieldIsValid()
                        && passwordEditTextFieldIsValid());
            }

            private boolean phoneNumberEditTextFieldIsValid() {
                // This can be either a phone number or username so we don't
                // care too much about the
                // format.
                return !TextUtils.isEmpty(mUserNameEditText.getText());
            }

            private boolean passwordEditTextFieldIsValid() {
                return !TextUtils.isEmpty(mPasswordEditText.getText());
            }
        };

        mUserNameEditText.addTextChangedListener(fieldValidatorTextWatcher);
        mPasswordEditText.addTextChangedListener(fieldValidatorTextWatcher);        
    }    
    
    
    private class LoginTask extends AsyncTask<Void, Void, Boolean> {
        private static final String TAG = "LoginTask";
        private static final boolean DEBUG = true;

        private Exception mReason;

        @Override
        protected void onPreExecute() {
            if (DEBUG) Log.d(TAG, "onPreExecute()");
            showProgressDialog();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            if (DEBUG) Log.d(TAG, "doInBackground()");
            SharedPreferences prefs = PreferenceManager
                    .getDefaultSharedPreferences(LoginActivity.this);
            Editor editor = prefs.edit();
            ParkDroid parkDroid = (ParkDroid) getApplication();
            Park park = parkDroid.getPark();           
            try {
                String phoneNumber = mUserNameEditText.getText().toString();
                String password = mPasswordEditText.getText().toString();

                boolean loggedIn = Preferences.loginUser(park, phoneNumber, password,
                        editor);

                if (DEBUG) Log.d(TAG, "doInBackground() logged in ="+loggedIn);
                // Make sure prefs makes a round trip.
                String userId = Preferences.getUserId(prefs);
                if (DEBUG) Log.d(TAG, "doInBackground() userId="+userId);
                if (TextUtils.isEmpty(userId)) {
                    if (DEBUG) Log.d(TAG, "Preference store calls failed");
                    throw new Exception(getResources().getString(
                            R.string.login_failed_login_toast));
                }
                return loggedIn;

            } catch (Exception e) {
                if (DEBUG) Log.d(TAG, "Caught Exception logging in.", e);
                mReason = e;
                Preferences.logoutUser(park, editor);
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean loggedIn) {
            if (DEBUG) Log.d(TAG, "onPostExecute(): " + ParkDroid.INTENT_ACTION_LOGGED_IN);
            ParkDroid parkDroid = (ParkDroid) getApplication();

            if (loggedIn) {
                //we are logged in, send the message
                sendBroadcast(new Intent(ParkDroid.INTENT_ACTION_LOGGED_IN));
                Toast.makeText(LoginActivity.this, getString(R.string.login_welcome_toast),
                        Toast.LENGTH_LONG).show();

                // Launch the main activity to let the user do anything.
                Intent intent = new Intent(LoginActivity.this, ParkDroidActivity.class);
                //if the activity exists in the current task, deliver this intent 
                //and clear other activities on top of it 
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

                // Be done with the activity.
                finish();

            } else {
                sendBroadcast(new Intent(ParkDroid.INTENT_ACTION_LOGGED_OUT));
               // NotificationsUtil.ToastReasonForFailure(LoginActivity.this, mReason);
            }
            dismissProgressDialog();
        }

        @Override
        protected void onCancelled() {
            dismissProgressDialog();
        }
    }
}
