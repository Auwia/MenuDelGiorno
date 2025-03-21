package it.app.menudelgiorno.menudelgiorno.v2.fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import it.app.menudelgiorno.menudelgiorno.v2.ListaReportRecensioniIndice;
import it.app.menudelgiorno.menudelgiorno.v2.R;
import it.app.menudelgiorno.menudelgiorno.v2.ReportRecensioniGridViewAdapter;
import it.app.menudelgiorno.menudelgiorno.v2.core.Menu;
import it.app.menudelgiorno.menudelgiorno.v2.core.User;
import it.app.menudelgiorno.menudelgiorno.v2.utility.Utility;


public class FragmentDetailMenu extends Fragment {

    private static final String RETRIEVE_PIATTI = "http://menudelgiornomecc.altervista.org/select_piatto.php?id_menu=";
    private static final String RETRIEVE_RATING = "http://menudelgiornomecc.altervista.org/select_rating_menu.php?id_menu=";
    private static final String RETRIEVE_RECENSIONI = "http://menudelgiornomecc.altervista.org/select_recensioni_menu.php?id_menu=";
    private static final String INSERT_RECENSIONE_MENU = "http://menudelgiornomecc.altervista.org/insert_recensione_menu.php";

    private ArrayAdapter<ListaReportRecensioniIndice> myAdapterListaReporRecensioni;
    private ArrayList<ListaReportRecensioniIndice> myListaReportRecensioni;
    private GridView myGridView;
    private RatingBar voto, rating_menu;
    private EditText commento;
    private Dialog dialog;
    private TextView counter;

    private Bundle savedInstanceState;
    private final Menu menu = new Menu();
    private User user;

    private Dialog progress;

    public FragmentDetailMenu() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceStateB) {

        View rootView = inflater.inflate(R.layout.dettaglio_menu, container,
                false);

        if (Utility.isNetworkAvailable(getActivity()))
            if (Utility.isThereUser(getActivity()))
                user = Utility.getUser(rootView.getContext());

        savedInstanceState = getArguments();

        menu.setId(savedInstanceState.getInt("id_menu"));
        menu.setKm((float) savedInstanceState.getDouble("km"));
        menu.setNomeMenu(savedInstanceState.getString("nome_menu"));
        menu.setPrezzo(savedInstanceState.getDouble("prezzo"));
        menu.setRatingMenu((float) savedInstanceState.getDouble("rating"));

        TextView nome_menu = rootView.findViewById(R.id.nome_menu);
        nome_menu.setText(menu.getNomeMenu());

        TextView prezzo_menu = rootView
                .findViewById(R.id.prezzoMenu);
        prezzo_menu.setText(menu.getPrezzo() + " �");

        counter = rootView.findViewById(R.id.counter);

        rating_menu = rootView.findViewById(R.id.myRatingBar);
        rating_menu.setRating(menu.getRatingMenu());
        rating_menu.setIsIndicator(true);

        scaricaRecensioni();

        rating_menu.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (Integer.parseInt(Utility.getCounter(counter.getText()
                            .toString())) > 0) {
                        new VisualizzaRecensioniRating().execute();
                    } else {
                        Toast.makeText(getActivity(),
                                        "Nessun feedback presente", Toast.LENGTH_LONG)
                                .show();
                    }
                }
                return true;
            }
        });

        ListView lista_piatti = rootView
                .findViewById(R.id.listaPiatti);

        String[] array = null;
        JSONArray jsonArray;
        try {
            jsonArray = new JSONArray(
                    Utility.getWebServerResponse(RETRIEVE_PIATTI + menu.getId()));

            array = new String[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                array[i] = jsonObject.getString("descrizione");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                rootView.getContext(), R.layout.list_view, array);

        lista_piatti.setAdapter(arrayAdapter);

        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        LayoutParams lp = lista_piatti.getLayoutParams();
        lp.height = size.y * 78 / 100;
        lista_piatti.setLayoutParams(lp);

        final ImageView scriviRecensioneBtn = rootView
                .findViewById(R.id.iconaScriviRecensione);
        scriviRecensioneBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    if (Utility.isNetworkAvailable(getActivity())) {

                        if (Utility.isThereUser(v.getContext())) {
                            aggiungiRecensione();
                        } else {
                            Toast.makeText(getActivity(),
                                            R.string.ERRORE_LOGIN, Toast.LENGTH_LONG)
                                    .show();
                        }
                    } else {
                        Toast.makeText(
                                getActivity(),
                                getActivity().getResources().getString(
                                        R.string.ERRORE_NETWORK),
                                Toast.LENGTH_LONG).show();
                    }

                } catch (Exception e) {
                    Log.e("inserisci recensione",
                            "impossibile inserire recensione menu", e);
                }
            }
        });

        final TextView scriviRecensioneLabel = rootView
                .findViewById(R.id.labelScriviRecensione);
        scriviRecensioneLabel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    if (Utility.isNetworkAvailable(getActivity())) {
                        if (Utility.isThereUser(v.getContext())) {
                            aggiungiRecensione();
                        } else {
                            Toast.makeText(getActivity(),
                                            R.string.ERRORE_LOGIN, Toast.LENGTH_LONG)
                                    .show();
                        }
                    } else {
                        Toast.makeText(
                                getActivity(),
                                getActivity().getResources().getString(
                                        R.string.ERRORE_NETWORK),
                                Toast.LENGTH_LONG).show();
                    }

                } catch (Exception e) {
                    Log.e("inserisci recensione",
                            "impossibile inserire recensione menu", e);
                }
            }
        });

        return rootView;
    }

    protected void aggiungiRecensione() {
        dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.custom_dialog_recensioni);
        dialog.setTitle(getActivity().getResources().getString(R.string.labelScriviRecensione));

        voto = dialog.findViewById(R.id.valutazioneLocale);
        commento = dialog.findViewById(R.id.insertTestoRecensioneText);

        Button sendRecensioneBtn = dialog.findViewById(R.id.sendRecensioneBtn);
        sendRecensioneBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    URL url = new URL(INSERT_RECENSIONE_MENU);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setDoOutput(true);
                    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                    String data = "id_menu=" + URLEncoder.encode(String.valueOf(menu.getId()), "UTF-8") +
                            "&ranking=" + URLEncoder.encode(String.valueOf(voto.getRating()), "UTF-8") +
                            "&commento=" + URLEncoder.encode(commento.getText().toString(), "UTF-8") +
                            "&user=" + URLEncoder.encode(user.getNome() + " " + user.getCognome(), "UTF-8");

                    OutputStream os = conn.getOutputStream();
                    os.write(data.getBytes());
                    os.flush();
                    os.close();

                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String inputLine;
                    StringBuilder response = new StringBuilder();
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();

                    String responseString = response.toString();
                    if (responseString.equals("OK")) {
                        Toast.makeText(getActivity(), "Inserimento OK", Toast.LENGTH_LONG).show();
                        scaricaRecensioni();
                    } else {
                        Toast.makeText(getActivity(), responseString, Toast.LENGTH_LONG).show();
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

    private void scaricaRecensioni() {
        JSONArray jsonArray;

        try {
            jsonArray = new JSONArray(
                    Utility.getWebServerResponse(RETRIEVE_RATING + menu.getId()));

            JSONObject jsonObject = jsonArray.getJSONObject(0);

            rating_menu.setRating((float) jsonObject.getDouble("ranking"));
            counter.setText("(" + jsonObject.getInt("nVoti")
                    + ")");

        } catch (JSONException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

    }

    class VisualizzaRecensioniRating extends AsyncTask<Void, Void, Void> {
        VisualizzaRecensioniRating() {
        }

        @Override
        protected Void doInBackground(Void... params) {

            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    dialog = new Dialog(getActivity());
                    dialog.setContentView(R.layout.custom_dialog_leggi_recensioni);
                    dialog.setTitle(getActivity().getResources().getString(
                            R.string.labelLeggiRecensione));
                    try {
                        JSONArray jsonArray = new JSONArray(Utility
                                .getWebServerResponse(RETRIEVE_RECENSIONI
                                        + menu.getId()));

                        JSONObject jsonObject;
                        myListaReportRecensioni = new ArrayList<ListaReportRecensioniIndice>();

                        for (int i = 0; i < jsonArray.length(); i++) {
                            jsonObject = jsonArray.getJSONObject(i);
                            myListaReportRecensioni
                                    .add(new ListaReportRecensioniIndice(
                                            jsonObject.getString("user"),
                                            jsonObject.getString("commento"),
                                            jsonObject.getDouble("ranking")));
                        }

                    } catch (JSONException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }

                    myAdapterListaReporRecensioni = new                                                                                                     ReportRecensioniGridViewAdapter(
                            getActivity(), myListaReportRecensioni);
                    myGridView = dialog
                            .findViewById(R.id.listaRecensioniIndice);
                    myGridView.setAdapter(myAdapterListaReporRecensioni);

                    dialog.show();
                }
            });

            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    progress.dismiss();
                }
            });
        }

        @Override
        protected void onPreExecute() {
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    progress = ProgressDialog.show(getActivity(), "",
                            "Caricamento dati in corso...", true);
                }
            });

        }

    }

}
