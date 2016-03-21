package com.voiceconf.voiceconf.ui.authentification;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;

import com.voiceconf.voiceconf.R;
import com.voiceconf.voiceconf.networking.services.LoginCallback;
import com.voiceconf.voiceconf.networking.services.LoginService;
import com.voiceconf.voiceconf.ui.main.MainActivity;

/**
 * This activity handles the user login and registration.
 */
public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final int RC_SIGN_IN = 9001;
    private static final String GOOGLE_USER_ID = "{\"GoogleUserID\" : \"";

    private GoogleApiClient mGoogleApiClient;
    private SignInButton mSignInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        mSignInButton = (SignInButton) findViewById(R.id.sign_in_button);
        mSignInButton.setScopes(gso.getScopeArray());
        mSignInButton.setSize(SignInButton.SIZE_WIDE);

        mSignInButton.setOnClickListener(v -> {
            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
            startActivityForResult(signInIntent, RC_SIGN_IN);
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                final GoogleSignInAccount acct = result.getSignInAccount();
                // Request was successful => checking if the user exists
                if (acct != null && acct.getId() != null && acct.getEmail() != null && acct.getDisplayName() != null && acct.getPhotoUrl() != null) {
                    LoginService.login(new LoginCallback() {
                        @Override
                        public void onSuccess() {
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        }

                        @Override
                        public void onFailure(Exception e) {
                            somethingWentWrong(e.toString());
                        }
                    }, GOOGLE_USER_ID, acct.getId(), acct.getEmail(), acct.getDisplayName(), acct.getPhotoUrl().toString());
                } else {
                    somethingWentWrong(getResources().getString(R.string.something_went_wrong));
                }
            } else {
                somethingWentWrong(getResources().getString(R.string.something_went_wrong));
            }
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Snackbar.make(mSignInButton, R.string.login_failed, Snackbar.LENGTH_LONG).show();
    }

    /**
     * Something went wrong notifying the user
     */
    private void somethingWentWrong(String msg) {
        Snackbar.make(mSignInButton, msg, Snackbar.LENGTH_LONG)
                .setAction(R.string.retry, v -> {
                    mSignInButton.performClick();
                })
                .show();
    }
}
