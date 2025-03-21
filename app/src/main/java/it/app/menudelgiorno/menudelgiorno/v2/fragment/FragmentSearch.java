package it.app.menudelgiorno.menudelgiorno.v2.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.util.ArrayList;

import it.app.menudelgiorno.menudelgiorno.v2.FiltraFragment;
import it.app.menudelgiorno.menudelgiorno.v2.ListViewLocaliAdapter;
import it.app.menudelgiorno.menudelgiorno.v2.R;
import it.app.menudelgiorno.menudelgiorno.v2.core.LocaleC;

public class FragmentSearch extends Fragment {

    public static FragmentManager fragmentManager;
    private View rootView;

    public FragmentSearch() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public void onPostCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ListView myListView = rootView
                .findViewById(R.id.listaLocali);

        myListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long arg3) {
                Fragment fragment = new FragmentDetailLocali();
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                // DetailFragment.locale = myLocale.get(position);
                fragmentManager.beginTransaction()
                        .replace(R.id.content_frame, fragment)
                        .addToBackStack(null).commit();

            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_menu_cerca, container,
                false);

        ListViewLocaliAdapter.fragmentManager = requireActivity().getSupportFragmentManager();

        final ArrayList<LocaleC> myLocale = new ArrayList<LocaleC>();
        // myLocale.add(new LocaleC(BitmapFactory.decodeResource(
        // rootView.getResources(), R.drawable.ic_photos), "Ristorante",
        // "Max", "+393282658862", 12.2, 2.5F, true, "via passo", "98",
        // 83038, "montemiletto", "(AV)", "Ristorantino bellisso",
        // "massimo.manganiello@gmail.com", "www.sito.com", 1));
        // myLocale.add(new LocaleC(BitmapFactory.decodeResource(
        // rootView.getResources(), R.drawable.ic_photos), "Pub", "Luigi",
        // "+393382648262", 201.9, 3F, false, "via passo", "98", 83038,
        // "montemiletto", "(AV)", "Pub bellissimo",
        // "massimo.manganiello@gmail.com", "www.sito.com", 1));
        ListViewLocaliAdapter myAdapter = new ListViewLocaliAdapter(
                getActivity(), myLocale);
        ListView myListView = rootView
                .findViewById(R.id.listaLocali);
        myListView.setAdapter(myAdapter);

        myListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long arg3) {
                Fragment fragment = new FragmentDetailLocali();
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                FragmentDetailLocali.locale = myLocale.get(position);
                fragmentManager.beginTransaction()
                        .replace(R.id.content_frame, fragment)
                        .addToBackStack(null).commit();

            }
        });
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO Add your menu entries here

        inflater.inflate(R.menu.filtra, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.filtra) {
            Fragment fragment = new Fragment();
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, fragment).addToBackStack(null)
                    .commit();
        }
        return super.onOptionsItemSelected(item);
    }

}
