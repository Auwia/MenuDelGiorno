package it.app.menudelgiorno.menudelgiorno.v2.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Hashtable;

import it.app.menudelgiorno.menudelgiorno.v2.ListViewMenuAdapter;
import it.app.menudelgiorno.menudelgiorno.v2.R;
import it.app.menudelgiorno.menudelgiorno.v2.core.Menu;
import it.app.menudelgiorno.menudelgiorno.v2.utility.Utility;

public class FragmentOffertaGiorno extends Fragment {

    private static View rootView;

    private TabHost tabHost;
    private double rangeMax = -1;

    private final Hashtable<String, String> valori = new Hashtable<String, String>();

    private ListViewMenuAdapter myAdapter;
    private ListView myListView;
    private final ArrayList<Menu> myMenu = new ArrayList<Menu>();

    private static final String RETRIEVE_MENU_TUTTI = "http://menudelgiornomecc.altervista.org/select_menu_tutti.php";

    private static final long MIN_TIME_BW_UPDATES = 1; // 1 minute

    private LocationRequest mLocationRequest;
    boolean mUpdatesRequested = false;

    public FragmentOffertaGiorno() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_menu_offerta_giorno, container, false);

        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());

        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                double lat = location.getLatitude();
                double lng = location.getLongitude();
            }
        });

        if (!Utility.isNetworkAvailable(getActivity())) {
            Utility.showSettingsAlert("INTERNET", getActivity());
        }

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(MIN_TIME_BW_UPDATES);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        mLocationRequest.setFastestInterval(MIN_TIME_BW_UPDATES);
        mUpdatesRequested = false;

        tabHost = rootView.findViewById(R.id.tabhost);
        tabHost.setup();

        TabSpec spec1 = tabHost.newTabSpec(getString(R.string.labelVicinanza));
        spec1.setIndicator(getString(R.string.labelVicinanza));
        spec1.setContent(R.id.tab1);

        TabSpec spec2 = tabHost.newTabSpec(getString(R.string.labelRanking));
        spec2.setIndicator(getString(R.string.labelRanking));
        spec2.setContent(R.id.tab2);

        TabSpec spec3 = tabHost.newTabSpec(getString(R.string.labelRange));
        spec3.setContent(R.id.tab3);
        spec3.setIndicator(getString(R.string.labelRange));

        tabHost.addTab(spec1);
        tabHost.addTab(spec2);
        tabHost.addTab(spec3);

        ListViewMenuAdapter.fragmentManager = requireActivity().getSupportFragmentManager();

        myMenu.clear();

        JSONArray jsonArray;
        valori.clear();

        try {
            // valori.put("latitudine",
            // String.valueOf(mLocationClient.getLastLocation().getLatitude()));
            // valori.put("longitudine",
            // String.valueOf(mLocationClient.getLastLocation().getLongitude()));

            valori.put("latitudine", "40.85699");
            valori.put("longitudine", "14.282045");

            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            String max = Utility.insertWebService(RETRIEVE_MENU_TUTTI, valori);

            jsonArray = new JSONArray(max);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                myMenu.add(new Menu(jsonObject.getString("descrizione"), jsonObject.getString("nome_locale"), jsonObject.getDouble("prezzo"), (float) jsonObject.getDouble("distance"), (float) jsonObject.getDouble("rating"), jsonObject.getInt("id"), jsonObject.getInt("numtot")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        myListView = rootView.findViewById(R.id.listaMenu);
        myAdapter = new ListViewMenuAdapter(getActivity(), myMenu);
        myAdapter.sort(new Comparator<Menu>() {
            @Override
            public int compare(Menu lhs, Menu rhs) {
                if (lhs.getKm() >= rhs.getKm()) {
                    return 1;
                } else {
                    return -1;
                }
            }
        });
        myListView.setAdapter(myAdapter);

        myListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                Fragment fragment = new FragmentDetailMenu();
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                Bundle bundle = new Bundle();
                bundle.putString("nome_menu", myAdapter.getItem(position).getNomeMenu());
                bundle.putDouble("prezzo", myAdapter.getItem(position).getPrezzo());
                bundle.putDouble("rating", myAdapter.getItem(position).getRatingMenu());
                bundle.putInt("id_menu", myAdapter.getItem(position).getId());

                fragment.setArguments(bundle);
                fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).addToBackStack(null).commit();

            }
        });

        tabHost.getTabWidget().getChildAt(0).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                myAdapter = new ListViewMenuAdapter(getActivity(), myMenu);
                myAdapter.sort(new Comparator<Menu>() {
                    @Override
                    public int compare(Menu lhs, Menu rhs) {
                        if (lhs.getKm() >= rhs.getKm()) {
                            return 1;
                        } else {
                            return -1;
                        }
                    }
                });

                myListView.setAdapter(myAdapter);

                tabHost.setCurrentTab(0);

            }
        });

        tabHost.getTabWidget().getChildAt(1).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                myAdapter = new ListViewMenuAdapter(getActivity(), myMenu);
                myAdapter.sort(new Comparator<Menu>() {
                    @Override
                    public int compare(Menu lhs, Menu rhs) {
                        if (lhs.getRatingMenu() >= rhs.getRatingMenu()) {
                            return -1;
                        } else {
                            return 1;
                        }
                    }
                });

                myListView.setAdapter(myAdapter);

                tabHost.setCurrentTab(1);

            }
        });

        tabHost.getTabWidget().getChildAt(2).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                final CharSequence[] items = {"Fino a 5�", "da 5� a 10�", "Oltre 10�"};
                AlertDialog.Builder builder3 = new AlertDialog.Builder(rootView.getContext());
                builder3.setTitle("Scelti il range di prezzo").setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int rangeMin = -1;
                        if (which == 0) {
                            rangeMax = 5;
                            rangeMin = -1;
                        } else if (which == 1) {
                            rangeMax = 10;
                            rangeMin = 5;
                        } else if (which == 2) {
                            rangeMax = -1;
                            rangeMin = -1;
                        }

                        ArrayList<Menu> myMenuBkp = new ArrayList<Menu>();
                        for (int i = 0; i < myMenu.size(); i++) {
                            myMenuBkp.add(myMenu.get(i));
                        }

                        if (rangeMax != -1) {

                            for (int i = 0; i < myMenuBkp.size(); i++) {

                                if (myMenuBkp.get(i).getPrezzo() > rangeMax || myMenuBkp.get(i).getPrezzo() < rangeMin) {
                                    myMenuBkp.remove(i);
                                    i--;
                                }
                            }
                        }
                        myAdapter = new ListViewMenuAdapter(getActivity(), myMenuBkp);

                        myAdapter.sort(new Comparator<Menu>() {
                            @Override
                            public int compare(Menu lhs, Menu rhs) {
                                if (lhs.getPrezzo() <= rhs.getPrezzo()) {
                                    return -1;
                                } else {
                                    return 1;
                                }
                            }
                        });

                        myListView.setAdapter(myAdapter);

                    }
                });

                builder3.show();

                tabHost.setCurrentTab(2);

            }
        });

        tabHost.setOnTabChangedListener(new OnTabChangeListener() {

            @Override
            public void onTabChanged(String tabId) {

                if (tabHost.getCurrentTab() == 2) {
                    ArrayList<Menu> myMenuBkp = new ArrayList<Menu>();
                    for (int i = 0; i < myMenu.size(); i++) {
                        myMenuBkp.add(myMenu.get(i));
                    }

                    if (rangeMax != -1) {
                        for (int i = 0; i < myMenuBkp.size(); i++) {

                            if (myMenuBkp.get(i).getPrezzo() > rangeMax) {
                                myMenuBkp.remove(i);
                            }
                        }

                    }

                    // if (myAdapter != null) {
                    // myAdapter.clear();
                    // }
                    myAdapter = new ListViewMenuAdapter(getActivity(), myMenuBkp);
                    myListView.setAdapter(myAdapter);

                } else {

                    // if (myAdapter != null) {
                    // myAdapter.clear();
                    // }
                    myAdapter = new ListViewMenuAdapter(getActivity(), myMenu);
                    myListView.setAdapter(myAdapter);

                }
            }
        });

        return rootView;
    }

}
