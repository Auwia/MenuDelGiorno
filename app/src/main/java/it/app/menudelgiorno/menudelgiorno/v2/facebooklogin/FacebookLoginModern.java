package it.app.menudelgiorno.menudelgiorno.v2.facebooklogin;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Base64;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.Arrays;

import it.app.menudelgiorno.menudelgiorno.v2.R;

public class FacebookLoginModern {

    private static Context context;
    private static ProgressDialog progressDialog;
    private static SharedPreferences settings;
    private static CallbackManager callbackManager;
    public static Handler handler;

    public static void login(Context ctx) {
        context = ctx;
        callbackManager = CallbackManager.Factory.create();

        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(context.getString(R.string.loading));
        progressDialog.setCancelable(false);
        progressDialog.show();

        LoginManager.getInstance().logInWithReadPermissions((android.app.Activity) context, Arrays.asList("email", "public_profile"));
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                fetchUserInfo(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                progressDialog.dismiss();
            }

            @Override
            public void onError(FacebookException error) {
                progressDialog.dismiss();
                Log.e("FBLogin", "Error: " + error.getMessage());
            }
        });
    }

    private static void fetchUserInfo(AccessToken accessToken) {
        GraphRequest request = GraphRequest.newMeRequest(
                accessToken,
                (JSONObject object, GraphResponse response) -> {
                    try {
                        String id = object.getString("id");
                        String firstName = object.optString("first_name");
                        String lastName = object.optString("last_name");

                        // Carica immagine profilo
                        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                        StrictMode.setThreadPolicy(policy);

                        URL img_url = new URL("https://graph.facebook.com/" + id + "/picture?type=large");
                        Bitmap bitmap = BitmapFactory.decodeStream(img_url.openConnection().getInputStream());

                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        byte[] b = baos.toByteArray();

                        settings = context.getSharedPreferences("login", 0);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString("account", "facebook");
                        editor.putString("id", id);
                        editor.putString("nome", firstName);
                        editor.putString("cognome", lastName);
                        editor.putString("foto", Base64.encodeToString(b, Base64.DEFAULT));
                        editor.apply();

                        handler.sendEmptyMessage(0);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        progressDialog.dismiss();
                    }
                });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,first_name,last_name,email");
        request.setParameters(parameters);
        request.executeAsync();
    }

    public static CallbackManager getCallbackManager() {
        return callbackManager;
    }
}
