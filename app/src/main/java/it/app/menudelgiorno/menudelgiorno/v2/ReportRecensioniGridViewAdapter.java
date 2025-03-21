package it.app.menudelgiorno.menudelgiorno.v2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.ArrayList;

public class ReportRecensioniGridViewAdapter extends
		ArrayAdapter<ListaReportRecensioniIndice> {

	private final ArrayList<ListaReportRecensioniIndice> myLista;
	private final Context context;
	public View row;

	public ReportRecensioniGridViewAdapter(Context context,
			ArrayList<ListaReportRecensioniIndice> myLista) {
		super(context, R.layout.grid_view_custom_recensioni, myLista);
		this.myLista = myLista;
		this.context = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		row = convertView;
		if (row == null) {
			LayoutInflater ll = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row = ll.inflate(R.layout.grid_view_custom_recensioni, parent,
					false);
		}

		ListaReportRecensioniIndice item = myLista.get(position);

		if (item != null) {

			RatingBar voto = row
					.findViewById(R.id.valutazioneLocale);

			TextView recensionista = row != null ? row
					.findViewById(R.id.recensionista) : null;

			TextView recensione = row.findViewById(R.id.recensione);

			if (recensionista != null) {
				recensionista.setText(item.getRecensionista());
				recensionista.setTextSize(12);
			}

			if (recensione != null) {
				recensione.setText(String.valueOf(item.getRecensione()));
				recensione.setTextSize(12);
			}

			if (voto != null) {
				voto.setRating(Float.valueOf(item.getVoto().toString()));
			}

		}

		return row;
	}
}
