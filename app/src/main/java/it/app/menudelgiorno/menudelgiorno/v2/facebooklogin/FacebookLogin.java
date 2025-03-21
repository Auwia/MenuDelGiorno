package it.app.menudelgiorno.menudelgiorno.v2.facebooklogin;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;

import it.app.menudelgiorno.menudelgiorno.v2.R;


public class FacebookLogin {

    private static ProgressDialog progressDialog;
    public static Handler handler;

    public static void login(final Context context) {
        LoginManager.getInstance().logInWithReadPermissions(
                (android.app.Activity) context,
                Arrays.asList("public_profile", "email")
        );

        LoginManager.getInstance().registerCallback(it.app.menudelgiorno.menudelgiorno.v2.facebooklogin.FacebookLoginModern.getCallbackManager(),
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        fetchUserData(context, loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {
                        Log.d("FacebookLogin", "Login cancelled.");
                    }

                    @Override
                    public void onError(FacebookException error) {
                        Log.e("FacebookLogin", "Login error: ", error);
                    }
                });
    }

    private static void fetchUserData(final Context context, AccessToken accessToken) {
        progressDialog = ProgressDialog.show(context, "", context.getString(R.string.loading), true);

        GraphRequest request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                try {
                    String id = object.getString("id");
                    String firstName = object.getString("first_name");
                    String lastName = object.getString("last_name");

                    // Scarica immagine profilo
                    URL imageURL = new URL("https://graph.facebook.com/" + id + "/picture?type=large");
                    InputStream inputStream = imageURL.openConnection().getInputStream();
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] imageBytes = baos.toByteArray();

                    SharedPreferences settings = context.getSharedPreferences("login", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("account", "facebook");
                    editor.putString("id", id);
                    editor.putString("nome", firstName);
                    editor.putString("cognome", lastName);
                    editor.putString("foto", Base64.encodeToString(imageBytes, Base64.DEFAULT));
                    editor.apply();

                    if (handler != null) {
                        handler.sendEmptyMessage(0);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    progressDialog.dismiss();
                }
            }
        });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,first_name,last_name,email");
        request.setParameters(parameters);
        request.executeAsync();
    }
}
