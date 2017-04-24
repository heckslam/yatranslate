package ru.devtron.yatranslate.data.storage;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import nl.qbusict.cupboard.Cupboard;

/**
 * The type Db open helper.
 */
public class DbOpenHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "YaTranslate.db";
	private static final int DATABASE_VERSION = 1;

	/**
	 * The Cupboard.
	 */
	protected Cupboard cupboard;

	/**
	 * Instantiates a new Db open helper.
	 *
	 * @param context  the context
	 * @param cupboard the cupboard
	 */
	public DbOpenHelper(Context context, Cupboard cupboard) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.cupboard = cupboard;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		cupboard.withDatabase(db).createTables();
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		cupboard.withDatabase(db).upgradeTables();
	}
}

