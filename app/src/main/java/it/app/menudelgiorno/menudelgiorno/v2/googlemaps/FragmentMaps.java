package it.app.menudelgiorno.menudelgiorno.v2.googlemaps;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Hashtable;

import it.app.menudelgiorno.menudelgiorno.v2.R;
import it.app.menudelgiorno.menudelgiorno.v2.fragment.FragmentDetailLocali;
import it.app.menudelgiorno.menudelgiorno.v2.fragment.FragmentOrganizzaPranzo;
import it.app.menudelgiorno.menudelgiorno.v2.utility.Utility;

public class FragmentMaps extends Fragment implements OnMapReadyCallback,
        GoogleMap.OnMapClickListener,
        GoogleMap.OnMarkerClickListener,
        GoogleMap.OnMapLongClickListener, LocationListener {

    private GoogleMap map;
    private static Marker marker;
    private View rootView;
    private LatLng local;
    private Hashtable<String, Integer> markers;
    private Dialog dialog, loadPoisDialog;

    private LocationRequest mLocationRequest;
    private boolean mUpdatesRequested = false;
    private Location loc;
    private FusedLocationProviderClient fusedLocationClient;

    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1; // 1 meter
    private static final long MIN_TIME_BW_UPDATES = 1000; // 1 second
    private static final int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    public FragmentMaps() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_menu_maps, container, false);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(MIN_TIME_BW_UPDATES);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        mLocationRequest.setFastestInterval(MIN_TIME_BW_UPDATES);

        return rootView;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            return;
        }

        map.setMyLocationEnabled(true);
        map.setOnMapClickListener(this);
        map.setOnMarkerClickListener(this);
        map.setOnMapLongClickListener(this);

        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(final Marker marker) {

                final CharSequence[] items = {"Dettagli",
                        "Organizza Pranzo", "Navigazione"};
                AlertDialog.Builder builder3 = new AlertDialog.Builder(
                        requireContext());
                builder3.setTitle("Scelti il metodo di login").setItems(
                        items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {

                                if (marker != null && markers != null) {

                                    if (which == 0) {
                                        // DETTAGLIO LOCALE
                                        try {

                                            Bundle data = new Bundle();
                                            data.putParcelable(
                                                    "locale",
                                                    Utility.scaricaLocale(markers.get(marker
                                                            .getId())));
                                            Fragment fragment = new FragmentDetailLocali();
                                            fragment.setArguments(data);
                                            FragmentManager fragmentManager = getFragmentManager();
                                            fragmentManager
                                                    .beginTransaction()
                                                    .replace(
                                                            R.id.content_frame,
                                                            fragment)
                                                    .addToBackStack(null)
                                                    .commit();
                                        } catch (Exception e) {
                                            Toast.makeText(
                                                            getActivity(),
                                                            "Caricamento in corso, riprova per favore",
                                                            Toast.LENGTH_LONG)
                                                    .show();
                                            Fragment fragment = new FragmentMaps();
                                            FragmentManager fragmentManager = getFragmentManager();
                                            fragmentManager
                                                    .beginTransaction()
                                                    .replace(
                                                            R.id.content_frame,
                                                            fragment)
                                                    .addToBackStack(null)
                                                    .commit();
                                        }

                                    } else if (which == 1) {
                                        // ORGANIZZA PRANZO

                                        try {
                                            Bundle data = new Bundle();
                                            data.putParcelable(
                                                    "locale",
                                                    Utility.scaricaLocale(markers.get(marker
                                                            .getId())));
                                            Fragment fragment = new FragmentOrganizzaPranzo();
                                            fragment.setArguments(data);
                                            FragmentManager fragmentManager = getFragmentManager();
                                            fragmentManager
                                                    .beginTransaction()
                                                    .replace(
                                                            R.id.content_frame,
                                                            fragment)
                                                    .addToBackStack(null)
                                                    .commit();
                                        } catch (Exception e) {
                                            Toast.makeText(
                                                            getActivity(),
                                                            "Caricamento in corso, riprova per favore",
                                                            Toast.LENGTH_LONG)
                                                    .show();
                                            Fragment fragment = new FragmentMaps();
                                            FragmentManager fragmentManager = getFragmentManager();
                                            fragmentManager
                                                    .beginTransaction()
                                                    .replace(
                                                            R.id.content_frame,
                                                            fragment)
                                                    .addToBackStack(null)
                                                    .commit();
                                        }

                                    } else if (which == 2) {
                                        // AVVIA NAVIGATORE
                                        FragmentMaps.marker = marker;
                                        new AlertDialog.Builder(
                                                getActivity())
                                                .setIcon(
                                                        android.R.drawable.ic_dialog_alert)
                                                .setTitle(
                                                        "Conferma navigazione")
                                                .setMessage(
                                                        "Vuoi avviare il navigatore?")
                                                .setPositiveButton(
                                                        "SI",
                                                        new DialogInterface.OnClickListener() {

                                                            @Override
                                                            public void onClick(
                                                                    DialogInterface dialog,
                                                                    int which) {

                                                                Intent localIntent = new Intent(
                                                                        "android.intent.action.VIEW");

                                                                localIntent
                                                                        .setData(Uri
                                                                                .parse("google.navigation:q="
                                                                                        + FragmentMaps.marker
                                                                                        .getPosition().latitude
                                                                                        + ","
                                                                                        + FragmentMaps.marker
                                                                                        .getPosition().longitude));
                                                                startActivity(localIntent);
                                                            }

                                                        })
                                                .setNegativeButton("NO",
                                                        null).show();
                                    }
                                } else {
                                    Toast.makeText(
                                            getActivity(),
                                            "Caricamento dei POI in corso, attendere prego...",
                                            Toast.LENGTH_LONG).show();
                                }
                            }
                        });

                builder3.show();
            }
        });

        if (Utility.isNetworkAvailable(requireContext())) {
            new LoadPois().execute();
        } else {
            Utility.showSettingsAlert("INTERNET", getActivity());
        }
    }

    @Override
    public void onMapClick(LatLng point) {
        map.animateCamera(CameraUpdateFactory.newLatLng(point));
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        marker.showInfoWindow();
        return true;
    }

    @Override
    public void onMapLongClick(LatLng point) {
    }

    @Override
    public void onLocationChanged(Location location) {
        loc = location;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (map != null) map.clear();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Utility.isNetworkAvailable(requireContext())) {
            new LoadPois().execute();
        }
    }

    class LoadPois extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            loadPoisDialog = ProgressDialog.show(getActivity(), "", "Caricamento POI\nAttendere...", true);
        }

        @Override
        protected Void doInBackground(Void... params) {
            PoisManager.readPoi(getActivity());
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            if (PoisManager.getSize() > 0) {
                markers = new Hashtable<>();
                for (int j = 0; j < PoisManager.getSize(); j++) {
                    local = new LatLng(PoisManager.getPoi(j).getLocation().getLatitude(), PoisManager.getPoi(j).getLocation().getLongitude());
                    Marker m = map.addMarker(new MarkerOptions()
                            .position(local)
                            .title(PoisManager.getPoi(j).getNomeLocale())
                            .snippet(PoisManager.getPoi(j).getAddress().getComponent("indirizzo") + " - " + PoisManager.getPoi(j).getTel())
                            .icon(BitmapDescriptorFactory.fromResource(PoisManager.getPoi(j).getResourseIconId())));
                    markers.put(m.getId(), PoisManager.getPoi(j).getIdLocale());
                }
            }
            loadPoisDialog.dismiss();
        }
    }
}
