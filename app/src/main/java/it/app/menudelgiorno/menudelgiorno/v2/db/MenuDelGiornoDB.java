package it.app.menudelgiorno.menudelgiorno.v2.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.concurrent.CountDownLatch;

public class MenuDelGiornoDB extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "MenuDelGiorno.db";
	private static final int DATABASE_VERSION = 2;

	public static CountDownLatch latch;

	public static final String TABLE_PREFERITI = "PREFERITI";

	public static final String COLUMN_ID_LOCALE = "ID_LOCALE";
	public static final String COLUMN_IMAGE = "IMAGE";

	private static final String CREATE_TABLE_DEFAULT = "CREATE TABLE android_metadata (locale TEXT DEFAULT 'it_IT')";
	private static final String CREATE_TABLE_PREFERITI = "create table "
			+ TABLE_PREFERITI + "(" + COLUMN_ID_LOCALE + " INTEGER  NOT NULL, "
			+ COLUMN_IMAGE + " BLOB" + " );";

	public MenuDelGiornoDB(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {

		Cursor cursor = database.rawQuery(
				"select DISTINCT tbl_name from sqlite_master where tbl_name = '"
						+ "android_metadata" + "'", null);
		if (cursor != null) {
			if (cursor.getCount() > 0) {
				cursor.close();
			} else {
				cursor.close();
				database.execSQL(CREATE_TABLE_DEFAULT);
				database.execSQL("INSERT INTO android_metadata VALUES ('it_IT')");
			}
		}
		System.out.println(CREATE_TABLE_PREFERITI);
		database.execSQL(CREATE_TABLE_PREFERITI);
	}

	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion,
			int newVersion) {
		database.execSQL("ALTER TABLE " + TABLE_PREFERITI + " ADD COLUMN "
				+ COLUMN_IMAGE + " BLOB;");

	}

	@Override
	public void onDowngrade(SQLiteDatabase database, int oldVersion,
			int newVersion) {
	}
}
