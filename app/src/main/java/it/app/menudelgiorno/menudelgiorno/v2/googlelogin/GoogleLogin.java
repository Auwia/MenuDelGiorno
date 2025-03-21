package it.app.menudelgiorno.menudelgiorno.v2.googlelogin;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.GooglePlayServicesAvailabilityException;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.AccountPicker;
import com.google.android.gms.common.GooglePlayServicesUtil;

import it.app.menudelgiorno.menudelgiorno.v2.R;
import it.app.menudelgiorno.menudelgiorno.v2.core.User;
import it.app.menudelgiorno.menudelgiorno.v2.utility.Utility;

import androidx.fragment.app.Fragment;

/**
 * The TokenInfoActivity is a simple app that allows users to acquire, inspect
 * and invalidate authentication tokens for a different accounts and scopes.
 * <p>
 * In addition see implementations of {@link AbstractGetNameTask} for an
 * illustration of how to use the {@link GoogleAuthUtil}.
 */
public class GoogleLogin extends Fragment {
    private static final String TAG = "PlayHelloActivity";
    private static final String SCOPE = "oauth2:https://www.googleapis.com/auth/userinfo.profile";
    public static final String EXTRA_ACCOUNTNAME = "extra_accountname";

    static final int REQUEST_CODE_PICK_ACCOUNT = 1000;
    static final int REQUEST_CODE_RECOVER_FROM_AUTH_ERROR = 1001;
    static final int REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR = 1002;

    private String mEmail;
    private View rootView;

    public TextView titolo;
    public ImageView user_picture;

    public static Handler handler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_menu_login, container,
                false);

        titolo = rootView.findViewById(R.id.title);
        user_picture = rootView.findViewById(R.id.icon);

        if (!Utility.isThereUser(rootView.getContext())) {
            getUsername();
        } else {
            User user = Utility.getUser(rootView.getContext());
            titolo.setText(user.getNome() + " " + user.getCognome());
            user_picture.setImageBitmap(user.getFoto());
        }

        return rootView;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_PICK_ACCOUNT) {
            getActivity();
            if (resultCode == Activity.RESULT_OK) {
                mEmail = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                getUsername();
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(rootView.getContext(),
                        "You must pick an account", Toast.LENGTH_SHORT).show();
            }
        } else if ((requestCode == REQUEST_CODE_RECOVER_FROM_AUTH_ERROR || requestCode == REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR)
                && resultCode == Activity.RESULT_OK) {
            handleAuthorizeResult(resultCode, data);
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void handleAuthorizeResult(int resultCode, Intent data) {
        if (data == null) {
            show("Unknown error, click the button again");
            return;
        }
        if (resultCode == Activity.RESULT_OK) {
            Log.i(TAG, "Retrying");
            getTask(this, mEmail, SCOPE).execute();
            return;
        }
        if (resultCode == Activity.RESULT_CANCELED) {
            show("User rejected authorization.");
            return;
        }
        show("Unknown error, click the button again");
    }

    /**
     * Attempt to get the user name. If the email address isn't known yet, then
     * call pickUserAccount() method so the user can pick an account.
     */
    private void getUsername() {
        if (mEmail == null) {
            pickUserAccount();
        } else {
            if (Utility.isNetworkAvailable(rootView.getContext())) {
                getTask(GoogleLogin.this, mEmail, SCOPE).execute();
            } else {
                Toast.makeText(rootView.getContext(),
                                "No network connection available", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    /**
     * Starts an activity in Google Play Services so the user can pick an
     * account
     */
    private void pickUserAccount() {
        String[] accountTypes = new String[]{"com.google"};
        Intent intent = AccountPicker.newChooseAccountIntent(null, null,
                accountTypes, false, null, null, null, null);
        startActivityForResult(intent, REQUEST_CODE_PICK_ACCOUNT);
    }

    /**
     * This method is a hook for background threads and async tasks that need to
     * update the UI. It does this by launching a runnable under the UI thread.
     */
    public void show(final String message) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                System.out.println(message);
            }
        });
    }

    /**
     * This method is a hook for background threads and async tasks that need to
     * provide the user a response UI when an exception occurs.
     */
    public void handleException(final Exception e) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (e instanceof GooglePlayServicesAvailabilityException) {
                    // The Google Play services APK is old, disabled, or not
                    // present.
                    // Show a dialog created by Google Play services that allows
                    // the user to update the APK
                    int statusCode = ((GooglePlayServicesAvailabilityException) e)
                            .getConnectionStatusCode();
                    Dialog dialog = GooglePlayServicesUtil.getErrorDialog(
                            statusCode, getActivity(),
                            REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR);
                    dialog.show();
                } else if (e instanceof UserRecoverableAuthException) {
                    // Unable to authenticate, such as when the user has not yet
                    // granted
                    // the app access to the account, but the user can fix this.
                    // Forward the user to an activity in Google Play services.
                    Intent intent = ((UserRecoverableAuthException) e)
                            .getIntent();
                    startActivityForResult(intent,
                            REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR);
                }
            }
        });
    }

    /**
     * Note: This approach is for demo purposes only. Clients would normally not
     * get tokens in the background from a Foreground activity.
     */
    private AbstractGetNameTask getTask(GoogleLogin activity, String email,
                                        String scope) {
        return new GetNameInForeground(activity, email, scope);

    }
}