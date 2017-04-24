package ru.devtron.yatranslate.di.modules;

import dagger.Module;

@Module(
		includes = {
				PrefsModule.class,
				DbModule.class
		}
)
public class LocalModule {
}
