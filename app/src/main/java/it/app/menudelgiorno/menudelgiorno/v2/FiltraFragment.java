package it.app.menudelgiorno.menudelgiorno.v2;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FiltraFragment extends Fragment {

	public FiltraFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_menu_filtra,
				container, false);

		return rootView;
	}
}
