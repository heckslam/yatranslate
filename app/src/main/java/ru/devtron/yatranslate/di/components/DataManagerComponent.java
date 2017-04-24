package ru.devtron.yatranslate.di.components;

import javax.inject.Singleton;

import dagger.Component;
import ru.devtron.yatranslate.data.DataManager;
import ru.devtron.yatranslate.di.modules.LocalModule;
import ru.devtron.yatranslate.di.modules.NetworkModule;

@Component(dependencies = AppComponent.class, modules = {NetworkModule.class, LocalModule.class})
@Singleton
public interface DataManagerComponent {
	void inject(DataManager dataManager);
}
