package ru.devtron.yatranslate.di.modules;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import ru.devtron.yatranslate.data.DataManager;

@Module
public class ModelModule {
	@Provides
	@Singleton
	DataManager provideDataManager() {
		return new DataManager();
	}
}