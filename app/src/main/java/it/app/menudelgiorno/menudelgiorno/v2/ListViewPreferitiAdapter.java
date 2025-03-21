package it.app.menudelgiorno.menudelgiorno.v2;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RatingBar;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;

import it.app.menudelgiorno.menudelgiorno.v2.core.LocaleC;
import it.app.menudelgiorno.menudelgiorno.v2.fragment.FragmentDetailLocali;

public class ListViewPreferitiAdapter extends ArrayAdapter<LocaleC> {
    private final ArrayList<LocaleC> listaLocali;
    private final int resLayout;
    private final Activity context;
    private LocaleC locale;

    public static FragmentManager fragmentManager;
    public View row;

    public ListViewPreferitiAdapter(Activity context,
                                    ArrayList<LocaleC> listaLocali) {
        super(context, R.layout.custom_list_preferiti_view, listaLocali);
        this.listaLocali = listaLocali;
        resLayout = R.layout.custom_list_preferiti_view;
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

        locale = listaLocali.get(position);

        if (locale != null) {
            ImageView fotoLocale = row
                    .findViewById(R.id.imageLocalePref);
            TextView nomeLocale = row
                    .findViewById(R.id.nomeLocalePref);
            TextView telefonoLocale = row
                    .findViewById(R.id.labelTelefonoPref);
            TextView km = row.findViewById(R.id.labelKMPref);
            RatingBar valutazioneLocale = row
                    .findViewById(R.id.valutazioneLocalePref);
            TextView counter = row.findViewById(R.id.counter);
            valutazioneLocale.setOnTouchListener(new OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });

            if (fotoLocale != null) {
                fotoLocale.setImageBitmap(locale.getFotoLocale());
                fotoLocale.setScaleType(ScaleType.FIT_XY);
            }

            if (nomeLocale != null) {
                nomeLocale.setText(locale.getNomeLocale());
            }

            if (telefonoLocale != null) {
                telefonoLocale.setText(locale.getTelefonoLocale());
            }

            if (km != null) {
                DecimalFormat df = new DecimalFormat("###,##0.00");
                km.setText(df.format(locale.getKm()));
            }

            if (valutazioneLocale != null) {
                valutazioneLocale.setRating(locale.getRatingLocale());
            }

            if (counter != null) {
                counter.setText("(" + locale.getVoti() + ")");
            }

            fotoLocale.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle data = new Bundle();
                    data.putParcelable("locale", listaLocali.get(pos));
                    Fragment fragment = new Fragment();
                    fragment.setArguments(data);
                    FragmentManager fragmentManager = context
                            .getFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.content_frame, fragment)
                            .addToBackStack(null).commit();
                }
            });

        }

        return row;
    }
}
