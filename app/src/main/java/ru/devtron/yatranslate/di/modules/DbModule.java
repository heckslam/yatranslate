package ru.devtron.yatranslate.di.modules;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;

import com.google.gson.Gson;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import nl.nl2312.rxcupboard2.RxCupboard;
import nl.nl2312.rxcupboard2.RxDatabase;
import nl.qbusict.cupboard.Cupboard;
import nl.qbusict.cupboard.CupboardBuilder;
import ru.devtron.yatranslate.data.managers.DbManager;
import ru.devtron.yatranslate.data.storage.DbOpenHelper;
import ru.devtron.yatranslate.data.storage.Translation;

@Module
public class DbModule {

	/**
	 * Provide cupboard
	 * @return the cupboard
	 */
	@Provides
	@Singleton
	Cupboard provideCupboard() {
		Cupboard cupboard = new CupboardBuilder()
				.useAnnotations()
				.build();
		cupboard.register(Translation.class);
		return cupboard;
	}

	/**
	 * Provide sqlite database.
	 * @param context  the context
	 * @param cupboard the cupboard
	 * @return the sq lite database
	 */
	@Provides
	@Singleton
	SQLiteDatabase provideDatabase(Context context, Cupboard cupboard) {
		return new DbOpenHelper(context, cupboard).getWritableDatabase();
	}

	/**
	 * Provide RxDatabase
	 * @param cupboard the cupboard
	 * @param database the database
	 * @return the rx database
	 */
	@Provides
	@Singleton
	RxDatabase provideRxDatabase(Cupboard cupboard, SQLiteDatabase database) {
		return RxCupboard.with(cupboard, database);
	}

	/**
	 * Provide db manager
	 * @param cupboard   the cupboard
	 * @param database   the database
	 * @param rxDatabase the rx database
	 * @return the db manager
	 */
	@Provides
	@Singleton
	DbManager provideDbManager(Cupboard cupboard,
	                                    SQLiteDatabase database, RxDatabase rxDatabase) {
		return new DbManager(cupboard, database, rxDatabase);
	}
}
