package it.app.menudelgiorno.menudelgiorno.v2.fragment;

import androidx.fragment.app.Fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import it.app.menudelgiorno.menudelgiorno.v2.R;
import it.app.menudelgiorno.menudelgiorno.v2.core.User;
import it.app.menudelgiorno.menudelgiorno.v2.utility.Utility;

public class FragmentLogin extends Fragment {

    private TextView titolo;
    private ImageView user_picture;
    private CheckBox glutine, latticini;
    private Spinner spinnerFasceSpesa, spinnerAziende, spinnerBuoniPasto;
    private View rootView;

    private static SharedPreferences settings;
    private static SharedPreferences.Editor editor;

    public FragmentLogin() {
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        editor.putBoolean("latticini", latticini.isChecked());
        editor.putBoolean("glutine", glutine.isChecked());
        editor.putString("fascia_spesa", spinnerFasceSpesa.getSelectedItem()
                .toString());
        editor.putString("azienda", spinnerAziende.getSelectedItem().toString());
        editor.putString("buono_pasto", spinnerBuoniPasto.getSelectedItem()
                .toString());
        editor.commit();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_menu_login, container,
                false);
        titolo = rootView.findViewById(R.id.title);
        user_picture = rootView.findViewById(R.id.icon);
        glutine = rootView.findViewById(R.id.glutine);
        latticini = rootView.findViewById(R.id.latticini);
        spinnerFasceSpesa = rootView
                .findViewById(R.id.spinnerFasceSpesa);
        spinnerAziende = rootView.findViewById(R.id.spinnerAziende);
        spinnerBuoniPasto = rootView
                .findViewById(R.id.spinnerBuoniPasto);

        settings = getActivity().getSharedPreferences("login", 0);
        editor = settings.edit();

        if (Utility.isThereUser(rootView.getContext())) {
            User user = Utility.getUser(rootView.getContext());

            titolo.setText(user.getNome() + " " + user.getCognome());

            user_picture.setImageBitmap(user.getFoto());

            latticini.setChecked(settings.getBoolean("latticini", false));
            glutine.setChecked(settings.getBoolean("glutine", false));

            for (int i = 0; i < spinnerFasceSpesa.getCount(); i++)
                if (settings.getString("fascia_spesa", "").equals(
                        spinnerFasceSpesa.getItemAtPosition(i).toString())) {
                    spinnerFasceSpesa.setSelection(i);
                    break;
                }

            for (int i = 0; i < spinnerAziende.getCount(); i++)
                if (settings.getString("azienda", "").equals(
                        spinnerAziende.getItemAtPosition(i).toString())) {
                    spinnerAziende.setSelection(i);
                    break;
                }

            for (int i = 0; i < spinnerBuoniPasto.getCount(); i++)
                if (settings.getString("buono_pasto", "").equals(
                        spinnerBuoniPasto.getItemAtPosition(i).toString())) {
                    spinnerBuoniPasto.setSelection(i);
                    break;
                }

        }

        return rootView;
    }

}