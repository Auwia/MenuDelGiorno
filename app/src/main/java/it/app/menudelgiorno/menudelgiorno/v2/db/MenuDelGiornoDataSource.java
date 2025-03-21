package it.app.menudelgiorno.menudelgiorno.v2.db;

import android.content.Context;
import android.database.SQLException;

public class MenuDelGiornoDataSource {

	private final MenuDelGiornoDB dbHelper;
	
	public MenuDelGiornoDataSource(Context context) 
	{
	    dbHelper = new MenuDelGiornoDB(context);
	}

	public void open() throws SQLException 
	{
		dbHelper.getWritableDatabase();
	}

	public void close() 
	{
		dbHelper.close();
	}
}
