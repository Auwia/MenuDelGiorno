/*
 * Copyright 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package it.app.menudelgiorno.menudelgiorno.v2.googlelogin;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.util.Base64;
import android.util.Log;

import com.google.android.gms.auth.GoogleAuthUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import it.app.menudelgiorno.menudelgiorno.v2.R;

/**
 * Display personalized greeting. This class contains boilerplate code to
 * consume the token but isn't integral to getting the tokens.
 */
public abstract class AbstractGetNameTask extends AsyncTask<Void, Void, Void> {
    private static final String TAG = "TokenInfoTask";
    protected GoogleLogin mActivity;
    private ProgressDialog progressDialog;

    protected String mScope;
    protected String mEmail;

    private String nominativo;
    private Bitmap foto;

    private SharedPreferences settings;
    private SharedPreferences.Editor editor;

    AbstractGetNameTask(GoogleLogin activity, String email, String scope) {
        this.mActivity = activity;
        this.mScope = scope;
        this.mEmail = email;
    }

    @Override
    protected void onPreExecute() {
        progressDialog = new ProgressDialog(mActivity.getActivity());
        progressDialog.setMessage(mActivity.getText(R.string.loading));
        progressDialog.show();
    }

    @Override
    protected void onPostExecute(Void v) {
        mActivity.titolo.setText(nominativo);
        mActivity.user_picture.setImageBitmap(foto);
        GoogleLogin.handler.sendEmptyMessage(0);
        progressDialog.dismiss();
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            fetchNameFromProfileServer();
        } catch (IOException ex) {
            onError("Following Error occured, please try again. "
                    + ex.getMessage(), ex);
        } catch (JSONException e) {
            onError("Bad response: " + e.getMessage(), e);
        }
        return null;
    }

    protected void onError(String msg, Exception e) {
        if (e != null) {
            Log.e(TAG, "Exception: ", e);
        }
        mActivity.show(msg); // will be run in UI thread
    }

    /**
     * Get a authentication token if one is not available. If the error is not
     * recoverable then it displays the error message on parent activity.
     */
    protected abstract String fetchToken() throws IOException;

    /**
     * Contacts the user info server to get the profile of the user and extracts
     * the first name of the user from the profile. In order to authenticate
     * with the user info server the method first fetches an access token from
     * Google Play services.
     *
     * @throws IOException   if communication with user info server failed.
     * @throws JSONException if the response from the server could not be parsed.
     */
    private void fetchNameFromProfileServer() throws IOException, JSONException {
        String token = fetchToken();
        if (token == null) {
            // error has already been handled in fetchToken()
            return;
        }
        URL url = new URL(
                "https://www.googleapis.com/oauth2/v1/userinfo?access_token="
                        + token);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        int sc = con.getResponseCode();
        if (sc == 200) {
            InputStream is = con.getInputStream();
            JSONObject profile = new JSONObject(readResponse(is));

            nominativo = profile.getString("given_name") + " "
                    + profile.getString("family_name");

            try {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                        .permitAll().build();
                StrictMode.setThreadPolicy(policy);
                URL img_value = new URL(profile.getString("picture"));
                foto = BitmapFactory.decodeStream(img_value.openConnection()
                        .getInputStream());

                // SALVATAGGIO DATI IN SHARED PREFERENCES
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                BitmapFactory.decodeStream(
                        img_value.openConnection().getInputStream()).compress(
                        Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] b = baos.toByteArray();

                settings = mActivity.getActivity().getSharedPreferences(
                        "login", 0);
                editor = settings.edit();
                editor.putString("account", "google+");
                editor.putString("id", profile.getString("id"));
                editor.putString("nome", profile.getString("given_name"));
                editor.putString("cognome", profile.getString("family_name"));
                editor.putString("foto",
                        Base64.encodeToString(b, Base64.DEFAULT));
                editor.commit();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            is.close();
        } else if (sc == 401) {
            GoogleAuthUtil.invalidateToken(mActivity.getActivity(), token);
            onError("Server auth error, please try again.", null);
            Log.i(TAG,
                    "Server auth error: " + readResponse(con.getErrorStream()));
        } else {
            onError("Server returned the following error code: " + sc, null);
        }
    }

    /**
     * Reads the response from the input stream and returns it as a string.
     */
    private static String readResponse(InputStream is) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] data = new byte[2048];
        int len = 0;
        while ((len = is.read(data, 0, data.length)) >= 0) {
            bos.write(data, 0, len);
        }
        return bos.toString(StandardCharsets.UTF_8);
    }
}
