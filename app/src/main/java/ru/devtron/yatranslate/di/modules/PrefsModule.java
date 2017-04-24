package ru.devtron.yatranslate.di.modules;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import ru.devtron.yatranslate.data.managers.PreferencesManager;

@Module
public class PrefsModule {
	@Provides
	@Singleton
	PreferencesManager providePreferencesManager(Context context) {
		return new PreferencesManager(context);
	}
}
