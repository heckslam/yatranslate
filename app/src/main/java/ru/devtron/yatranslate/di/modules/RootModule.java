package ru.devtron.yatranslate.di.modules;

import dagger.Module;
import dagger.Provides;
import ru.devtron.yatranslate.ui.screens.root.RootPresenter;
import ru.devtron.yatranslate.ui.screens.root.RootScope;


@Module
public class RootModule {
	@Provides
	@RootScope
	RootPresenter provideRootPresenter() {
		return new RootPresenter();
	}
}