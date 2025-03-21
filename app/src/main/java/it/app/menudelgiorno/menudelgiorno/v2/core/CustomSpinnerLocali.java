package it.app.menudelgiorno.menudelgiorno.v2.core;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import it.app.menudelgiorno.menudelgiorno.v2.R;

public class CustomSpinnerLocali extends ArrayAdapter<String> {
    private final String[] objects;
    private final Activity context;

    public CustomSpinnerLocali(Activity context, String[] objects) {
        super(context, R.layout.custom_spinner_locali, objects);

        this.objects = objects;
        this.context = context;
    }

    @Override
    public String getItem(int pos) {
        // TODO Auto-generated method stub
        return objects[pos];
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, parent);
    }

    View getCustomView(int position, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        final View row = inflater.inflate(R.layout.custom_spinner_locali,
                parent, false);

        TextView label = row != null ? row
                .findViewById(R.id.descrizioneLocale) : null;
        label.setText(objects[position]);

        return row;
    }
}
