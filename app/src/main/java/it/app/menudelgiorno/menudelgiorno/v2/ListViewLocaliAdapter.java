package it.app.menudelgiorno.menudelgiorno.v2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.text.DecimalFormat;
import java.util.ArrayList;

import it.app.menudelgiorno.menudelgiorno.v2.core.LocaleC;
import it.app.menudelgiorno.menudelgiorno.v2.fragment.FragmentDetailLocali;

public class ListViewLocaliAdapter extends ArrayAdapter<LocaleC> {
    private final ArrayList<LocaleC> listaLocali;
    int resLayout;
    Context context;
    public static FragmentManager fragmentManager;

    public View row;

    public ListViewLocaliAdapter(Context context,
                                 ArrayList<LocaleC> listaLocali) {
        super(context, R.layout.custom_list_locali_view, listaLocali);
        this.listaLocali = listaLocali;
        resLayout = R.layout.custom_list_locali_view;
        this.context = context;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        row = convertView;
        final int pos = position;

        if (row == null) {
            LayoutInflater ll = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = ll.inflate(resLayout, parent, false);
        }

        LocaleC locale = listaLocali.get(position);

        if (locale != null) {
            ImageButton fotoLocale = row
                    .findViewById(R.id.imageLocale);
            TextView nomeLocale = row.findViewById(R.id.nomeLocale);
            TextView telefonoLocale = row
                    .findViewById(R.id.labelTelefono);
            TextView km = row.findViewById(R.id.labelKM);
            RatingBar valutazioneLocale = row
                    .findViewById(R.id.valutazioneLocale);
            valutazioneLocale.setOnTouchListener(new OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });

            ImageView offertaSpeciale = row
                    .findViewById(R.id.labelOfferta);

            if (fotoLocale != null) {
                fotoLocale.setImageBitmap(locale.getFotoLocale());
            }

            if (nomeLocale != null) {
                nomeLocale.setText(locale.getNomeLocale());
                nomeLocale.setTextSize(12);
            }

            if (telefonoLocale != null) {
                telefonoLocale.setText(locale.getTelefonoLocale());
                telefonoLocale.setTextSize(12);
            }

            if (km != null) {
                DecimalFormat df = new DecimalFormat("###,##0.00");
                km.setText(df.format(locale.getKm()));
                km.setTextSize(12);
            }

            if (valutazioneLocale != null) {
                valutazioneLocale.setRating(locale.getRatingLocale());
            }

            if (offertaSpeciale != null) {
                if (locale.haveOffertaSpeciale()) {
                    offertaSpeciale.setImageResource(R.drawable.specialoffer);
                }

            }

            fotoLocale.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Fragment fragment = new Fragment();
                    FragmentDetailLocali.locale = listaLocali.get(pos);

                    fragmentManager.beginTransaction()
                            .replace(R.id.content_frame, fragment).commit();
                }
            });

        }

        return row;
    }
}
