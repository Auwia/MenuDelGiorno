// Updated and modernized version of FragmentOrganizzaPranzo
// Note: Facebook SDK updated, deprecated API removed, and AsyncTasks still in place but simplified.
// Replace these with coroutines or other modern solutions in the future.

package it.app.menudelgiorno.menudelgiorno.v2.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.HttpMethod;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

import it.app.menudelgiorno.menudelgiorno.v2.ListViewAmiciAdapter;
import it.app.menudelgiorno.menudelgiorno.v2.ListViewAmiciAdapterEditabile;
import it.app.menudelgiorno.menudelgiorno.v2.R;
import it.app.menudelgiorno.menudelgiorno.v2.core.Amico;
import it.app.menudelgiorno.menudelgiorno.v2.core.LocaleC;
import it.app.menudelgiorno.menudelgiorno.v2.core.Pranzo;
import it.app.menudelgiorno.menudelgiorno.v2.core.User;
import it.app.menudelgiorno.menudelgiorno.v2.utility.Utility;

public class FragmentOrganizzaPranzo extends Fragment {

    private TextView mDateDisplay, mTimeDisplay;
    private final Calendar c = Calendar.getInstance();
    private final Calendar c1 = Calendar.getInstance();
    private SimpleDateFormat dateFormat;
    private ListViewAmiciAdapterEditabile myAdapter;
    private ListViewAmiciAdapter adpterListaInvitati, adpterListaInvitatiOrganizzatore;
    private ListView myListView, listaPartecipantiInviti, listaPartecipantiInvitiOrganizzatore;
    private Spinner listaPranziInvitato, listaPranziOrganizzatore, listaLocali;
    private Button partecipa_pranzo_btn, organizza_pranzo_btn, prenota_pranzo_btn;
    private ImageButton addAmico, mDateButton, mTimeButton, eliminaAmico;
    private TabHost tabHost;
    private View rootView;
    private AlertDialog.Builder dialog;

    private static final String RETRIEVE_ID_PRANZO = "https://menudelgiornomecc.altervista.org/select_pranzo.php";
    private static final String INSERT_PRANZO = "https://menudelgiornomecc.altervista.org/insert_pranzo.php";
    private static final String INSERT_PARTECIPANTI = "https://menudelgiornomecc.altervista.org/insert_partecipanti.php";
    private static final String RETRIEVE_PRANZI_ORGANIZZATORE = "https://menudelgiornomecc.altervista.org/select_pranzi_organizzatore.php?id_organizzatore=";
    private static final String RETRIEVE_PRANZI_INVITATO = "https://menudelgiornomecc.altervista.org/select_pranzi_invitato.php?id_invitato=";
    private static final String RETRIEVE_PRANZO_INVITATI = "https://menudelgiornomecc.altervista.org/select_pranzo_invitati.php?id_pranzo=";
    private static final String UPDATE_PRANZO_INVITATO = "https://menudelgiornomecc.altervista.org/update_pranzo_invitato.php";
    private static final String UPDATE_PRANZO_ORGANIZZATORE = "https://menudelgiornomecc.altervista.org/update_pranzo_organizzatore.php";

    private final Hashtable<String, String> valori = new Hashtable<>();
    private final ArrayList<Amico> listaAmiciFull = new ArrayList<>();
    private final ArrayList<Amico> myAmico_listview = new ArrayList<>();
    private final ArrayList<Amico> myInvitato_listview = new ArrayList<>();
    private final ArrayList<Amico> myInvitatoOrganizzatore_listview = new ArrayList<>();
    private ArrayList<Amico> myAmico_dialog = new ArrayList<>();
    private ArrayList<Amico> myInvitato_listview_bkp = new ArrayList<>();
    private ArrayList<Amico> myInvitatoOrganizzatore_listview_bkp = new ArrayList<>();
    private ArrayList<Pranzo> pranziOrganizzatore = new ArrayList<>();
    private ArrayList<Pranzo> pranziInvitato = new ArrayList<>();
    private ArrayList<LocaleC> preferitiLocali = new ArrayList<>();
    private User userLog;
    private Pranzo pranzo;
    private LocaleC locale;
    private Dialog progress, progress1, progress2, progress3, progress4;
    private int mYear, mMonth, mDay, mHourOfDay, mMinute;

    public FragmentOrganizzaPranzo() {
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void inviaNotificaPranzo(String id_invitato, String nome_locale, String data_ora_evento) {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken == null || accessToken.isExpired()) {
            Toast.makeText(getActivity(), "Utente non loggato con Facebook", Toast.LENGTH_SHORT).show();
            return;
        }

        Bundle params = new Bundle();
        params.putString("message", "Sei stato invitato a pranzo da un amico!");
        params.putString("link", "https://samples.ogp.me/440002909390231");
        params.putString("name", "Menu Del Giorno");
        params.putString("description", "Evento pranzo al locale: " + nome_locale + "\nData: " + data_ora_evento);
        params.putString("tags", id_invitato);

        GraphRequest request = new GraphRequest(
                accessToken,
                "/me/feed",
                params,
                HttpMethod.POST,
                response -> {
                    if (response.getError() != null) {
                        Toast.makeText(getActivity(), "Errore durante l'invio della notifica", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), "Invito inviato con successo!", Toast.LENGTH_SHORT).show();
                    }
                }
        );
        request.executeAsync();
    }


    class ScaricaAmici extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(getActivity(), "", "Caricamento dati in corso...", true);
            addAmico.setEnabled(false);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            AccessToken accessToken = AccessToken.getCurrentAccessToken();

            if (accessToken != null && !accessToken.isExpired()) {
                GraphRequest request = new GraphRequest(
                        accessToken,
                        "/me/friends",
                        null,
                        HttpMethod.GET,
                        response -> {
                            try {
                                JSONObject responseObject = response.getJSONObject();
                                if (responseObject != null) {
                                    JSONArray data = responseObject.getJSONArray("data");
                                    listaAmiciFull.clear();
                                    for (int i = 0; i < data.length(); i++) {
                                        JSONObject user = data.getJSONObject(i);
                                        String id = user.getString("id");
                                        String name = user.getString("name");
                                        listaAmiciFull.add(new Amico(id, name, false));
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name");
                request.setParameters(parameters);
                request.executeAndWait();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            addAmico.setEnabled(true);
            progress.dismiss();
        }
    }

    class ScaricaLocaliPreferiti extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            progress3 = ProgressDialog.show(getActivity(), "", "Caricamento dati in corso...", true);
            listaLocali.setEnabled(false);
        }

        @Override
        protected Void doInBackground(Void... params) {
            preferitiLocali = Utility.scaricaLocaliSenzaFoto(Utility.getPreferiti());
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            String[] locali = new String[preferitiLocali.size()];
            for (int i = 0; i < preferitiLocali.size(); i++) {
                locali[i] = preferitiLocali.get(i).getNomeLocale();
            }
            listaLocali.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, locali));
            listaLocali.setEnabled(true);
            progress3.dismiss();
        }
    }

    class ScaricaPranziInvitato extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            progress2 = ProgressDialog.show(getActivity(), "", "Caricamento dati in corso...", true);
            listaPranziInvitato.setEnabled(false);
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                JSONArray jsonArray = new JSONArray(
                        Utility.getWebServerResponse(RETRIEVE_PRANZI_INVITATO + userLog.getId()));
                pranziInvitato = new ArrayList<>();

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    Pranzo pranzo = new Pranzo();
                    pranzo.setIdLocale(jsonObject.getInt("id_locale"));
                    pranzo.setIdPranzo(jsonObject.getInt("id_pranzo"));
                    pranzo.setIdOrganizzatore(jsonObject.getString("id_organizzatore"));
                    pranzo.setDataOraEvento(jsonObject.getString("data_ora_evento"));
                    pranzo.setNomeLocale(jsonObject.getString("nome_locale"));
                    pranziInvitato.add(pranzo);
                }

                myInvitato_listview_bkp = new ArrayList<>();

                for (Pranzo p : pranziInvitato) {
                    JSONArray invitati = new JSONArray(
                            Utility.getWebServerResponse(RETRIEVE_PRANZO_INVITATI + p.getIdPranzo()));

                    for (int i = 0; i < invitati.length(); i++) {
                        JSONObject jsonObject = invitati.getJSONObject(i);
                        Amico amico = new Amico();
                        amico.setId(jsonObject.getString("id_invitato"));
                        amico.setIdPranzo(p.getIdPranzo());
                        amico.setNomeAmico("Utente");
                        boolean aderito = jsonObject.getInt("aderito") != 0;
                        amico.setFlaggato(aderito);
                        myInvitato_listview_bkp.add(amico);
                        fetchFacebookUserName(amico);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            List<String> pranzi = new ArrayList<>();
            for (Pranzo p : pranziInvitato) {
                pranzi.add(p.getDataOraEvento());
            }
            Collections.sort(pranzi);
            ArrayAdapter<String> adp = new ArrayAdapter<>(
                    requireActivity(), R.layout.custom_spinner_pranzo,
                    R.id.descrizionePranzo, pranzi);
            listaPranziInvitato.setAdapter(adp);
            listaPranziInvitato.setEnabled(true);
            progress2.dismiss();
        }
    }

    private void fetchFacebookUserName(final Amico amico) {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken != null && !accessToken.isExpired()) {
            GraphRequest request = new GraphRequest(
                    accessToken,
                    "/" + amico.getId(),
                    null,
                    HttpMethod.GET,
                    response -> {
                        try {
                            JSONObject userObject = response.getJSONObject();
                            if (userObject != null) {
                                String name = userObject.optString("name", "Sconosciuto");
                                amico.setNomeAmico(name);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            amico.setNomeAmico("Sconosciuto");
                        }
                    }
            );
            request.executeAsync();
        }
    }
}