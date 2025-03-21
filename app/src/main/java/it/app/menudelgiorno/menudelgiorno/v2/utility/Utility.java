package it.app.menudelgiorno.menudelgiorno.v2.utility;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.StrictMode;
import android.provider.Settings;
import android.util.Base64;

import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.HttpEntity;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.HttpResponse;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.StatusLine;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.ClientProtocolException;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.HttpClient;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.methods.HttpGet;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.impl.client.DefaultHttpClient;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

import it.app.menudelgiorno.menudelgiorno.v2.R;
import it.app.menudelgiorno.menudelgiorno.v2.core.LocaleC;
import it.app.menudelgiorno.menudelgiorno.v2.core.User;
import it.app.menudelgiorno.menudelgiorno.v2.db.MenuDelGiornoDataSource;

public class Utility {

    // VARIABILI DATA BASE
    private static final String DATABASE_NAME = "MenuDelGiorno.db";
    private static SQLiteDatabase database;
    private static MenuDelGiornoDataSource datasource;
    private static Cursor cur;

    // WEBSERVICE
    private final static String DOWNLOAD_FOTO_LOCALE = "http://menudelgiornomecc.altervista.org/select_foto_locale.php?id=";
    private final static String SELECT_LOCALE = "http://menudelgiornomecc.altervista.org/select_locale.php";

    private static final Hashtable<String, String> valori = new Hashtable<String, String>();

    public static SQLiteDatabase creaDataBase(Context context) {
        datasource = new MenuDelGiornoDataSource(context);
        datasource.open();
        database = context.openOrCreateDatabase(DATABASE_NAME,
                Context.MODE_PRIVATE, null);
        datasource.close();
        return database;
    }

    public static String getWebServerResponse(String url) {
        System.out.println(url);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);

        StringBuilder builder = new StringBuilder();
        HttpClient client = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(url);
        try {
            HttpResponse response = client.execute(httpGet);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode == 200) {
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(content));
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
            } else {
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return builder.toString();
    }

    public static String whatAccount(Context context) {
        SharedPreferences settings = context.getSharedPreferences("login", 0);
        return settings.getString("account", null);

    }

    public static User getUser(Context context) {
        SharedPreferences settings = context.getSharedPreferences("login", 0);

        byte[] b = Base64.decode(settings.getString("foto", null),
                Base64.DEFAULT);
        InputStream is = new ByteArrayInputStream(b);
        Bitmap foto = BitmapFactory.decodeStream(is);

        return new User(settings.getString("id", null), settings.getString(
                "nome", null), settings.getString("cognome", null),
                settings.getString("account", null), foto);

    }

    public static boolean isThereUser(Context context) {
        SharedPreferences settings = context.getSharedPreferences("login", 0);
        return settings.getString("id", null) != null;
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static boolean isGPSAvailable(Context context) {
        LocationManager locationManager = (LocationManager) context
                .getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public static boolean isWIFIAvailable(Context context) {
        WifiManager wifiManager = (WifiManager) context
                .getSystemService(Context.WIFI_SERVICE);
        return wifiManager.isWifiEnabled();
    }

    public static int[] getPreferiti() {
        cur = database.query("PREFERITI", new String[]{"ID_LOCALE"}, null,
                null, null, null, null, null);

        cur.moveToFirst();
        int[] a = new int[cur.getCount()];
        for (int i = 0; i < cur.getCount(); i++) {
            a[i] = cur.getInt(0);
            cur.moveToNext();
        }

        cur.close();

        return a;
    }

    public static boolean isTherePreferiti() {
        cur = database.query("PREFERITI", new String[]{"ID_LOCALE"}, null,
                null, null, null, null, null);

        cur.moveToFirst();
        for (int i = 0; i < cur.getCount(); ) {
            return true;
        }

        cur.close();

        return false;
    }

    public static ArrayList<LocaleC> scaricaLocali(int[] id_locali) {
        Bitmap foto = null;
        ArrayList<LocaleC> myLocale = new ArrayList<LocaleC>();
        JSONArray jsonArray;
        JSONObject jsonObject;

        for (int i = 0; i < id_locali.length; i++) {

            cur = database.query("PREFERITI", new String[]{"IMAGE"},
                    "ID_LOCALE=?",
                    new String[]{String.valueOf(id_locali[i])}, null, null,
                    null, null);

            cur.moveToFirst();
            byte[] a = cur.getBlob(0);
            cur.close();

            if (a != null) {
                foto = BitmapFactory.decodeByteArray(a, 0, a.length);
            } else {
                try {
                    jsonArray = new JSONArray(
                            Utility.getWebServerResponse(DOWNLOAD_FOTO_LOCALE
                                    + id_locali[i]));

                    jsonObject = jsonArray.getJSONObject(0);
                    byte[] res1 = Base64.decode(
                            jsonObject.getString("immagine"), 0);
                    InputStream ins = new ByteArrayInputStream(res1);
                    BitmapFactory.Options opts = new BitmapFactory.Options();
                    opts.inSampleSize = 4;

                    foto = BitmapFactory.decodeStream(ins, null, opts);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            try {
                valori.clear();

                valori.put("latitudine", "40.85699");
                valori.put("longitudine", "14.282045");
                valori.put("id", String.valueOf(id_locali[i]));

                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                        .permitAll().build();
                StrictMode.setThreadPolicy(policy);

                String max = insertWebService(SELECT_LOCALE, valori);

                jsonArray = new JSONArray(max);

                jsonObject = jsonArray.getJSONObject(0);
                LocaleC locale = new LocaleC();

                locale.setFotoLocale(foto);
                locale.setTipologiaLocale(jsonObject.getString("tipologia"));
                locale.setNomeLocale(jsonObject.getString("nome_locale"));
                locale.setTelefonoLocale(jsonObject.getString("telefono"));
                locale.setKm((float) jsonObject.getDouble("distance"));
                locale.setRatingLocale((float) jsonObject.getDouble("rating"));
                locale.setOffertaSpeciale(false);
                locale.setVia(jsonObject.getString("indirizzo"));
                locale.setNumero(jsonObject.getString("numero"));
                locale.setCAP(jsonObject.getInt("cap"));
                locale.setComune(jsonObject.getString("comune"));
                locale.setProvincia(jsonObject.getString("provincia"));
                locale.setDescrizione(jsonObject.getString("descrizione"));
                locale.setEmail(jsonObject.getString("email"));
                locale.setSitoWeb(jsonObject.getString("sito_web"));
                locale.setId(jsonObject.getInt("id"));
                locale.setVoti(jsonObject.getInt("nVoti"));

                myLocale.add(locale);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return myLocale;
    }

    public static LocaleC scaricaLocale(int id) {

        LocaleC locale = new LocaleC();

        try {
            JSONArray jsonArray = new JSONArray(
                    Utility.getWebServerResponse(DOWNLOAD_FOTO_LOCALE + id));

            JSONObject jsonObject = jsonArray.getJSONObject(0);
            byte[] res1 = Base64.decode(jsonObject.getString("immagine"), 0);
            InputStream ins = new ByteArrayInputStream(res1);
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inSampleSize = 4;

            Bitmap foto = BitmapFactory.decodeStream(ins, null, opts);

            valori.clear();

            valori.put("latitudine", "40.85699");
            valori.put("longitudine", "14.282045");
            valori.put("id", String.valueOf(id));

            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);

            String max = insertWebService(SELECT_LOCALE, valori);
            jsonArray = new JSONArray(max);
            jsonObject = jsonArray.getJSONObject(0);

            locale.setFotoLocale(foto);
            locale.setTipologiaLocale(jsonObject.getString("tipologia"));
            locale.setNomeLocale(jsonObject.getString("nome_locale"));
            locale.setTelefonoLocale(jsonObject.getString("telefono"));
            locale.setKm((float) jsonObject.getDouble("distance"));
            locale.setRatingLocale((float) jsonObject.getDouble("rating"));
            locale.setOffertaSpeciale(false);
            locale.setVia(jsonObject.getString("indirizzo"));
            locale.setNumero(jsonObject.getString("numero"));
            locale.setCAP(jsonObject.getInt("cap"));
            locale.setComune(jsonObject.getString("comune"));
            locale.setProvincia(jsonObject.getString("provincia"));
            locale.setDescrizione(jsonObject.getString("descrizione"));
            locale.setEmail(jsonObject.getString("email"));
            locale.setSitoWeb(jsonObject.getString("sito_web"));
            locale.setId(jsonObject.getInt("id"));
            locale.setVoti(jsonObject.getInt("nVoti"));

        } catch (Exception e) {
            e.printStackTrace();
        }

        return locale;

    }

    public static void showSettingsAlert(final String provider,
                                         final Activity context) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

        alertDialog.setTitle(provider + " SETTINGS");

        alertDialog.setMessage(R.string.ERRORE_NETWORK);

        alertDialog.setPositiveButton("Settings",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent;
                        if (provider.equals("GPS")) {
                            intent = new Intent(
                                    Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        } else {
                            intent = new Intent(
                                    Settings.ACTION_WIRELESS_SETTINGS);
                        }

                        context.startActivity(intent);
                    }
                });

        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alertDialog.show();
    }

    public static String insertWebService(String url, Hashtable<String, String> valori) {
        System.out.println(url + "?=" + valori);
        try {
            URL obj = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            StringBuilder data = new StringBuilder();
            Enumeration<String> enumKey = valori.keys();
            while (enumKey.hasMoreElements()) {
                String key = enumKey.nextElement();
                String val = valori.get(key);
                if (data.length() != 0) data.append("&");
                data.append(URLEncoder.encode(key, "UTF-8"))
                        .append("=")
                        .append(URLEncoder.encode(val, "UTF-8"));
            }

            OutputStream os = conn.getOutputStream();
            os.write(data.toString().getBytes());
            os.flush();
            os.close();

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    public static ArrayList<LocaleC> scaricaLocaliSenzaFoto(int[] id_locali) {

        ArrayList<LocaleC> myLocale = new ArrayList<LocaleC>();

        for (int i = 0; i < id_locali.length; i++) {

            try {

                valori.clear();

                valori.put("latitudine", "40.85699");
                valori.put("longitudine", "14.282045");
                valori.put("id", String.valueOf(id_locali[i]));

                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                        .permitAll().build();
                StrictMode.setThreadPolicy(policy);

                String max = insertWebService(SELECT_LOCALE, valori);

                JSONArray jsonArray = new JSONArray(max);

                JSONObject jsonObject = jsonArray.getJSONObject(0);
                LocaleC locale = new LocaleC();

                locale.setTipologiaLocale(jsonObject.getString("tipologia"));
                locale.setNomeLocale(jsonObject.getString("nome_locale"));
                locale.setTelefonoLocale(jsonObject.getString("telefono"));
                locale.setKm((float) jsonObject.getDouble("distance"));
                locale.setRatingLocale((float) jsonObject.getDouble("rating"));
                locale.setOffertaSpeciale(false);
                locale.setVia(jsonObject.getString("indirizzo"));
                locale.setNumero(jsonObject.getString("numero"));
                locale.setCAP(jsonObject.getInt("cap"));
                locale.setComune(jsonObject.getString("comune"));
                locale.setProvincia(jsonObject.getString("provincia"));
                locale.setDescrizione(jsonObject.getString("descrizione"));
                locale.setEmail(jsonObject.getString("email"));
                locale.setSitoWeb(jsonObject.getString("sito_web"));
                locale.setId(jsonObject.getInt("id"));
                locale.setVoti(jsonObject.getInt("nVoti"));

                myLocale.add(locale);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return myLocale;
    }

    public static String getCounter(String counter) {
        return counter.substring(1, counter.length() - 1);
    }

}
