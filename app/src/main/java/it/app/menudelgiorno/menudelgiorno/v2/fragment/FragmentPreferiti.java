package it.app.menudelgiorno.menudelgiorno.v2.fragment;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import it.app.menudelgiorno.menudelgiorno.v2.ListViewMenuAdapter;
import it.app.menudelgiorno.menudelgiorno.v2.ListViewPreferitiAdapter;
import it.app.menudelgiorno.menudelgiorno.v2.R;
import it.app.menudelgiorno.menudelgiorno.v2.utility.Utility;

public class FragmentPreferiti extends Fragment {

    public static FragmentManager fragmentManager;
    private ListView myListView;
    private ListViewPreferitiAdapter myAdapter;
    private ProgressDialog progressdialog;
    private View rootView;

    public FragmentPreferiti() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_menu_preferiti,
                container, false);

        Utility.creaDataBase(getActivity());

        ListViewMenuAdapter.fragmentManager = requireActivity().getSupportFragmentManager();

        new ScaricaLocaliPreferiti().execute();

        return rootView;
    }

    class ScaricaLocaliPreferiti extends AsyncTask<Void, Void, Void> {
        ScaricaLocaliPreferiti() {
        }

        @Override
        protected Void doInBackground(Void... params) {

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    myListView.setAdapter(myAdapter);
                }
            });

            return null;
        }

        @Override
        protected void onPostExecute(Void v) {

            // getActivity().runOnUiThread(new Runnable() {
            // @Override
            // public void run() {
            // progress.dismiss();
            // }
            // });
            progressdialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            // getActivity().runOnUiThread(new Runnable() {
            // @Override
            // public void run() {
            // progress = ProgressDialog.show(getActivity(), "",
            // "Caricamento dati in corso...", true);
            //
            // myAdapter = new ListViewPreferitiAdapter(getActivity(),
            // Utility.scaricaLocali(Utility.getPreferiti()));
            // myListView = (ListView) rootView
            // .findViewById(R.id.listaLocaliPreferiti);
            // }
            // });

            progressdialog = new ProgressDialog(
                    FragmentPreferiti.this.getActivity());
            progressdialog.setMessage("Caricamento in corso.....");
            progressdialog.setIndeterminate(false);
            progressdialog.setMax(100);
            progressdialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressdialog.setCancelable(false);
            progressdialog.show();

            myAdapter = new ListViewPreferitiAdapter(getActivity(),
                    Utility.scaricaLocali(Utility.getPreferiti()));
            myListView = rootView
                    .findViewById(R.id.listaLocaliPreferiti);

        }

        protected void onProgressUpdate(String... progress) {
            progressdialog.setProgress(Integer.parseInt(progress[0]));
        }

    }

}
