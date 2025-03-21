package it.app.menudelgiorno.menudelgiorno.v2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.fragment.app.FragmentManager;

import java.text.DecimalFormat;
import java.util.ArrayList;

import it.app.menudelgiorno.menudelgiorno.v2.core.Menu;

public class ListViewMenuAdapter extends ArrayAdapter<Menu> {
    private final ArrayList<Menu> listaMenu;
    int resLayout;
    Context context;
    public static FragmentManager fragmentManager;

    public View row;

    public ListViewMenuAdapter(Context context, ArrayList<Menu> listaMenu) {
        super(context, R.layout.custom_list_menu_view, listaMenu);
        this.listaMenu = listaMenu;
        resLayout = R.layout.custom_list_menu_view;
        this.context = context;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        row = convertView;
        if (row == null) {
            LayoutInflater ll = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = ll.inflate(resLayout, parent, false);
        }

        Menu menu = listaMenu.get(position);

        if (menu != null) {
            TextView nomeMenu = row.findViewById(R.id.nomeMenu);
            TextView prezzo = row.findViewById(R.id.prezzoMenu);
            TextView km = row.findViewById(R.id.kmMenu);
            TextView nomeLocale = row.findViewById(R.id.nomeLocale);
            RatingBar valutazioneMenu = row
                    .findViewById(R.id.valutazioneMenu);
            valutazioneMenu.setIsIndicator(true);
            TextView counter = row.findViewById(R.id.counter);

            if (nomeMenu != null) {
                nomeMenu.setText(menu.getNomeMenu());
            }

            if (prezzo != null) {
                DecimalFormat df = new DecimalFormat("###,##0.00");
                prezzo.setText(df.format(menu.getPrezzo()));
            }

            if (km != null) {
                DecimalFormat df = new DecimalFormat("###,##0.00");
                km.setText(df.format(menu.getKm()));
            }

            if (valutazioneMenu != null) {
                valutazioneMenu.setRating(menu.getRatingMenu());
            }

            if (nomeLocale != null) {
                nomeLocale.setText(menu.getNomeLocale());
            }

            if (counter != null) {
                counter.setText("(" + menu.getCounter() + ")");
            }
        }

        return row;
    }
}
