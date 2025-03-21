package it.app.menudelgiorno.menudelgiorno.v2.fragment;

import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Hashtable;

import it.app.menudelgiorno.menudelgiorno.v2.ListViewMenuAdapter;
import it.app.menudelgiorno.menudelgiorno.v2.R;
import it.app.menudelgiorno.menudelgiorno.v2.core.Menu;
import it.app.menudelgiorno.menudelgiorno.v2.utility.Utility;

public class FragmentMenuDelGiorno extends Fragment {

    private static final String RETRIEVE_MENU = "http://menudelgiornomecc.altervista.org/select_menu.php";
    public static FragmentManager fragmentManager;

    private final Hashtable<String, String> valori = new Hashtable<String, String>();

    private Menu menu;
    private final ArrayList<Menu> myMenu = new ArrayList<Menu>();

    public FragmentMenuDelGiorno() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceStateB) {

        View rootView = inflater.inflate(R.layout.fragment_menu_del_giorno,
                container, false);

        Bundle savedInstanceState = getArguments();

        ListViewMenuAdapter.fragmentManager = requireActivity().getSupportFragmentManager();

        if (savedInstanceState != null) {

            if (savedInstanceState.getInt("id_locale") > -1) {
                JSONArray jsonArray;
                valori.clear();

                try {
                    // valori.put("latitudine",
                    // String.valueOf(mLocationClient.getLastLocation().getLatitude()));
                    // valori.put("longitudine",
                    // String.valueOf(mLocationClient.getLastLocation().getLongitude()));

                    valori.put("latitudine", "40.85699");
                    valori.put("longitudine", "14.282045");
                    valori.put("id_locale", String.valueOf(savedInstanceState
                            .getInt("id_locale")));

                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                            .permitAll().build();
                    StrictMode.setThreadPolicy(policy);

                    String max = Utility
                            .insertWebService(RETRIEVE_MENU, valori);

                    jsonArray = new JSONArray(max);

                    myMenu.clear();

                    for (int i = 0; i < jsonArray.length(); i++) {

                        menu = new Menu();
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        menu.setId(jsonObject.getInt("id"));
                        menu.setNomeMenu(jsonObject.getString("descrizione"));
                        menu.setPrezzo(jsonObject.getDouble("prezzo"));
                        menu.setKm((float) jsonObject.getDouble("distance"));
                        menu.setRatingMenu((float) jsonObject
                                .getDouble("rating"));
                        menu.setNomeLocale(null);
                        myMenu.add(menu);
                    }

                    ListViewMenuAdapter myAdapter = new ListViewMenuAdapter(
                            getActivity(), myMenu);
                    ListView myListView = rootView
                            .findViewById(R.id.listaMenuDelGiorno);
                    myListView.setAdapter(myAdapter);
                    myListView
                            .setOnItemClickListener(new OnItemClickListener() {

                                @Override
                                public void onItemClick(AdapterView<?> arg0,
                                                        View arg1, int position, long arg3) {
                                    Bundle bundle = new Bundle();
                                    bundle.putString("nome_menu",
                                            myMenu.get(position).getNomeMenu());
                                    bundle.putDouble("prezzo",
                                            myMenu.get(position).getPrezzo());
                                    bundle.putDouble("rating",
                                            myMenu.get(position)
                                                    .getRatingMenu());
                                    bundle.putInt("id_menu",
                                            myMenu.get(position).getId());

                                    Fragment fragment = new FragmentDetailMenu();
                                    FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                                    fragment.setArguments(bundle);
                                    fragmentManager
                                            .beginTransaction()
                                            .replace(R.id.content_frame,
                                                    fragment)
                                            .addToBackStack(null).commit();

                                }
                            });

                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else {
                System.out.println("ERROR");
            }
        }

        return rootView;
    }
}
