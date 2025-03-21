package it.app.menudelgiorno.menudelgiorno.v2.fragment;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.HttpEntity;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.HttpResponse;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.NameValuePair;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.entity.UrlEncodedFormEntity;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.methods.HttpPost;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.impl.client.DefaultHttpClient;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.message.BasicNameValuePair;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.util.EntityUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import it.app.menudelgiorno.menudelgiorno.v2.ListaReportRecensioniIndice;
import it.app.menudelgiorno.menudelgiorno.v2.R;
import it.app.menudelgiorno.menudelgiorno.v2.ReportRecensioniGridViewAdapter;
import it.app.menudelgiorno.menudelgiorno.v2.core.LocaleC;
import it.app.menudelgiorno.menudelgiorno.v2.core.User;
import it.app.menudelgiorno.menudelgiorno.v2.db.MenuDelGiornoDataSource;
import it.app.menudelgiorno.menudelgiorno.v2.googlemaps.FragmentMaps;
import it.app.menudelgiorno.menudelgiorno.v2.utility.Utility;

public class FragmentDetailLocali extends Fragment {

    public static LocaleC locale;
    private EditText commento;
    private Dialog dialog;

    private User user;

    private static final String RETRIEVE_RECENSIONI = "http://menudelgiornomecc.altervista.org/select_recensioni_locale.php?id_locale=";
    private static final String RETRIEVE_RATING = "http://menudelgiornomecc.altervista.org/select_rating_locale.php?id_locale=";
    private static final String INSERT_RECENSIONE_LOCALE = "http://menudelgiornomecc.altervista.org/insert_recensione_locale.php";

    private ArrayAdapter<ListaReportRecensioniIndice> myAdapterListaReporRecensioni;
    private ArrayList<ListaReportRecensioniIndice> myListaReportRecensioni;
    private GridView myGridView;
    private RatingBar voto, valutazioneLocale;
    private TextView counter;

    // Variabili database
    private static final String DATABASE_NAME = "MenuDelGiorno.db";
    private ContentValues row;
    private SQLiteDatabase database;
    private static MenuDelGiornoDataSource datasource;

    public FragmentDetailLocali() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.dettaglio_locale, container,
                false);

        datasource = new MenuDelGiornoDataSource(getActivity());
        datasource.open();
        database = getActivity().openOrCreateDatabase(DATABASE_NAME,
                Context.MODE_PRIVATE, null);

        final Button preferitiLocaleBtn = rootView
                .findViewById(R.id.preferitiLocale);
        locale = getArguments().getParcelable("locale");

        if (locale.setPreferito(isPreferito(locale.getId()))) {
            preferitiLocaleBtn.setCompoundDrawablesWithIntrinsicBounds(
                    null,
                    getActivity().getResources().getDrawable(
                            R.drawable.ic_action_important_yel), null, null);
        } else {
            preferitiLocaleBtn.setCompoundDrawablesWithIntrinsicBounds(
                    null,
                    getActivity().getResources().getDrawable(
                            R.drawable.ic_action_important), null, null);
        }

        if (Utility.isThereUser(rootView.getContext())) {

            user = Utility.getUser(rootView.getContext());

        }

        TextView nomeLocale = rootView
                .findViewById(R.id.nome_locale);
        // nomeLocale.setText(locale.getTipologiaLocale() + ": "
        // + locale.getNomeLocale());
        nomeLocale.setText(locale.getNomeLocale());

        TextView labelIndirizzo = rootView
                .findViewById(R.id.labelIndirizzo);

        labelIndirizzo.setText(locale.getVia() + ", " + locale.getNumero()
                + " - " + locale.getCAP() + " - " + locale.getComune() + ", "
                + locale.getProvincia());

        TextView descrizione_locale = rootView
                .findViewById(R.id.descrizione_locale);
        descrizione_locale.setText(locale.getDescrizione());

        TextView telefonoLocale = rootView
                .findViewById(R.id.labelTelefono);
        telefonoLocale.setText(locale.getTelefonoLocale());

        ImageView img_locale = rootView
                .findViewById(R.id.img_locale);

        img_locale.setImageBitmap(locale.getFotoLocale());

        counter = rootView.findViewById(R.id.counter);
        counter.setText("(" + locale.getVoti() + ")");

        valutazioneLocale = rootView
                .findViewById(R.id.valutazioneLocale);
        valutazioneLocale.setIsIndicator(true);
        valutazioneLocale.setRating(locale.getRatingLocale());
        valutazioneLocale.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (Integer.parseInt(Utility.getCounter(counter.getText()
                            .toString())) > 0) {
                        visualizzaRecensioni();
                    } else {
                        Toast.makeText(getActivity(),
                                        "Nessun feedback presente", Toast.LENGTH_LONG)
                                .show();
                    }

                }
                return true;
            }
        });

        TextView emailLocale = rootView
                .findViewById(R.id.labelEmail);
        emailLocale.setText(locale.getEmail());
        emailLocale.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    if (!locale.getEmail().equals("")) {
                        Intent intent = new Intent(Intent.ACTION_SENDTO);
                        intent.setType("plain/text");
                        intent.setData(Uri.parse("mailto:" + locale.getEmail()));
                        intent.putExtra(Intent.EXTRA_SUBJECT,
                                "APP Menu' del giorno - info");
                        intent.putExtra(Intent.EXTRA_TEXT,
                                "Salve " + locale.getNomeLocale() + ",\n");

                        startActivity(Intent.createChooser(intent,
                                "Invia email..."));
                    } else {
                        Toast.makeText(
                                getActivity(),
                                getActivity().getResources().getString(
                                        R.string.ERRORE_EMAIL),
                                Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    Log.e("chiamata all'invio mail",
                            "Impossibile inviare e-mail", e);
                }
            }
        });

        final Button chiamaLocaleBtn = rootView
                .findViewById(R.id.chiamaLocale);
        chiamaLocaleBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    if (!locale.getTelefonoLocale().equals("")) {
                        Intent callIntent = new Intent(Intent.ACTION_CALL, Uri
                                .parse("tel:" + locale.getTelefonoLocale()));
                        startActivity(callIntent);
                    } else {
                        Toast.makeText(
                                getActivity(),
                                getActivity().getResources().getString(
                                        R.string.ERRORE_TELEFONO),
                                Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    Log.e("chiamata", "Impossibile chiamare", e);
                }
            }
        });

        final Button menuLocaleBtn = rootView
                .findViewById(R.id.menuLocale);
        menuLocaleBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Bundle bundle = new Bundle();
                bundle.putInt("id_locale", locale.getId());
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                Fragment fragment = new FragmentMenuDelGiorno();
                fragment.setArguments(bundle);
                fragmentManager.beginTransaction()
                        .replace(R.id.content_frame, fragment)
                        .addToBackStack(null).commit();
            }
        });

        final Button mappaLocaleBtn = rootView
                .findViewById(R.id.mappaLocale);
        mappaLocaleBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    Fragment fragment = new FragmentMaps();
                    FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.content_frame, fragment)
                            .addToBackStack(null).commit();
                } catch (Exception e) {
                    Log.e("chiamata alla mappa",
                            "Impossibile connettersi alla mappa", e);
                }
            }
        });

        preferitiLocaleBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    database.beginTransaction();

                    if (locale.isPreferito()) {
                        // eliminare da preferiti
                        database.delete("PREFERITI",
                                "ID_LOCALE=" + locale.getId(), null);

                        locale.setPreferito(false);

                        preferitiLocaleBtn
                                .setCompoundDrawablesWithIntrinsicBounds(
                                        null,
                                        getActivity()
                                                .getResources()
                                                .getDrawable(
                                                        R.drawable.ic_action_important),
                                        null, null);

                    } else {
                        // aggiungere a preferiti
                        row = new ContentValues();
                        row.put("ID_LOCALE", locale.getId());

                        ByteArrayOutputStream bos = new ByteArrayOutputStream();
                        locale.getFotoLocale().compress(
                                Bitmap.CompressFormat.PNG, 100, bos);
                        byte[] bArray = bos.toByteArray();
                        row.put("IMAGE", bArray);

                        database.insert("PREFERITI", null, row);
                        locale.setPreferito(true);

                        preferitiLocaleBtn
                                .setCompoundDrawablesWithIntrinsicBounds(
                                        null,
                                        getActivity()
                                                .getResources()
                                                .getDrawable(
                                                        R.drawable.ic_action_important_yel),
                                        null, null);

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    database.setTransactionSuccessful();
                    database.endTransaction();
                }
            }
        });

        final TextView sitoLocaleLabel = rootView
                .findViewById(R.id.labelWWW);
        sitoLocaleLabel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    if (!locale.getSitoWeb().equals("")) {
                        String url = null;
                        if (!locale.getSitoWeb().contains("http")) {
                            url = "http://" + locale.getSitoWeb();
                        } else {
                            url = locale.getSitoWeb();
                        }

                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        startActivity(i);
                    } else {
                        Toast.makeText(
                                getActivity(),
                                getActivity().getResources().getString(
                                        R.string.ERRORE_SITO_WEB),
                                Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("Errore caricamento sito web", e.toString());
                }
            }
        });

        final ImageView scriviRecensioneBtn = rootView
                .findViewById(R.id.iconaScriviRecensione);
        scriviRecensioneBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    if (Utility.isThereUser(v.getContext())) {
                        aggiungiRecensione();
                    } else {
                        Toast.makeText(getActivity(), R.string.ERRORE_LOGIN,
                                Toast.LENGTH_LONG).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("aggiungi a preferiti",
                            "Impossibile aggiungere a preferiti", e);
                }
            }
        });

        final TextView scriviRecensioneLabel = rootView
                .findViewById(R.id.labelScriviRecensione);
        scriviRecensioneLabel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    if (Utility.isThereUser(v.getContext())) {
                        aggiungiRecensione();
                    } else {
                        Toast.makeText(getActivity(), R.string.ERRORE_LOGIN,
                                Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    Log.e("aggiungi a preferiti",
                            "Impossibile aggiungere a preferiti", e);
                }
            }
        });

        final ImageView sitoLocaleBtn = rootView
                .findViewById(R.id.iconaWWW);
        sitoLocaleBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(locale.getSitoWeb()));
                    startActivity(i);
                } catch (Exception e) {
                    Log.e("aggiungi a preferiti",
                            "Impossibile aggiungere a preferiti", e);
                }
            }
        });

        return rootView;
    }

    protected void aggiungiRecensione() {
        dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.custom_dialog_recensioni);
        dialog.setTitle(getActivity().getResources().getString(
                R.string.labelScriviRecensione));

        voto = dialog.findViewById(R.id.valutazioneLocale);
        commento = dialog
                .findViewById(R.id.insertTestoRecensioneText);

        Button sendRecensioneBtn = dialog
                .findViewById(R.id.sendRecensioneBtn);
        sendRecensioneBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                DefaultHttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(INSERT_RECENSIONE_LOCALE);

                try {
                    ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

                    nameValuePairs.add(new BasicNameValuePair("id_locale",
                            String.valueOf(locale.getId())));
                    nameValuePairs.add(new BasicNameValuePair("ranking", String
                            .valueOf(voto.getRating())));
                    nameValuePairs.add(new BasicNameValuePair("commento",
                            commento.getText().toString()));
                    nameValuePairs.add(new BasicNameValuePair("user", user
                            .getNome() + " " + user.getCognome()));

                    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    HttpResponse response = httpclient.execute(httppost);
                    HttpEntity entity = response.getEntity();
                    String responseString = EntityUtils.toString(entity,
                            "UTF-8");
                    if (responseString.equals("OK")) {
                        Toast.makeText(getActivity(), "Inserimento OK",
                                Toast.LENGTH_LONG).show();
                        scaricaRecensioni();
                    } else {
                        Toast.makeText(getActivity(),
                                        responseString, Toast.LENGTH_LONG)
                                .show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    dialog.dismiss();
                }
            }
        });

        dialog.show();
    }

    protected void visualizzaRecensioni() {
        dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.custom_dialog_leggi_recensioni);
        dialog.setTitle(getActivity().getResources().getString(
                R.string.labelLeggiRecensione));

        try {
            JSONArray jsonArray = new JSONArray(
                    Utility.getWebServerResponse(RETRIEVE_RECENSIONI
                            + locale.getId()));

            JSONObject jsonObject;
            myListaReportRecensioni = new ArrayList<ListaReportRecensioniIndice>();

            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObject = jsonArray.getJSONObject(i);
                myListaReportRecensioni.add(new ListaReportRecensioniIndice(
                        jsonObject.getString("user"), jsonObject
                        .getString("commento"), jsonObject
                        .getDouble("ranking")));
            }
        } catch (JSONException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        myAdapterListaReporRecensioni = new ReportRecensioniGridViewAdapter(
                getActivity(), myListaReportRecensioni);
        myGridView = dialog.findViewById(R.id.listaRecensioniIndice);
        myGridView.setAdapter(myAdapterListaReporRecensioni);

        dialog.show();
    }

    @Override
    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        state.putParcelable("locale", locale);
    }

    private void scaricaRecensioni() {
        JSONArray jsonArray;

        try {
            jsonArray = new JSONArray(
                    Utility.getWebServerResponse(RETRIEVE_RATING
                            + locale.getId()));

            JSONObject jsonObject = jsonArray.getJSONObject(0);

            valutazioneLocale
                    .setRating((float) jsonObject.getDouble("ranking"));
            counter.setText("(" + jsonObject.getInt("nVoti") + ")");

        } catch (JSONException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

    }

    public boolean isPreferito(int id) {
        Cursor cur = database.query("PREFERITI", new String[]{"ID_LOCALE"},
                "ID_LOCALE=" + id, null, null, null, null, null);
        cur.moveToFirst();

        if (cur.getCount() > 0) {
            cur.close();
            return true;
        } else {
            cur.close();
            return false;
        }

    }

    @Override
    public void onDestroyView() {
        datasource.close();
        database.close();
        super.onDestroyView();
    }

}
