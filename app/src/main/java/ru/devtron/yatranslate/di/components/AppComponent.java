package ru.devtron.yatranslate.di.components;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import dagger.Component;
import ru.devtron.yatranslate.di.modules.AppModule;
import ru.devtron.yatranslate.di.modules.DbModule;

@Component(modules = {AppModule.class, DbModule.class})
public interface AppComponent {
	Context getContext();
}