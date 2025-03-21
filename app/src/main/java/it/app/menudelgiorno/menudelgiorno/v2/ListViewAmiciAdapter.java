package it.app.menudelgiorno.menudelgiorno.v2;

import android.app.FragmentManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;

import it.app.menudelgiorno.menudelgiorno.v2.core.Amico;

public class ListViewAmiciAdapter extends ArrayAdapter<Amico> {
	private final ArrayList<Amico> listaAmici;
	int resLayout;
	Context context;
	public static FragmentManager fragmentManager;

	private CheckBox flagAmico;

	public View row;

	public ListViewAmiciAdapter(Context context, ArrayList<Amico> listaAmici) {
		super(context, R.layout.custom_list_amici_view, listaAmici);
		this.listaAmici = listaAmici;
		resLayout = R.layout.custom_list_amici_view;
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

		Amico amico = listaAmici.get(position);

		if (amico != null) {
			TextView nomeAmico = row
					.findViewById(R.id.singoloAmicoLista);
			flagAmico = row.findViewById(R.id.flaggatAmicoCustom);

			flagAmico.setEnabled(false);

			if (nomeAmico != null) {
				nomeAmico.setText(amico.getNomeAmico());
			}

			flagAmico.setChecked(amico.isFlaggato());
			flagAmico.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					listaAmici.set(
							pos,
							new Amico(listaAmici.get(pos).getId(), listaAmici
									.get(pos).getNomeAmico(), ((CheckBox) v)
									.isChecked()));
				}
			});
		}

		return row;
	}
}
