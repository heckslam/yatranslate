package ru.devtron.yatranslate.di.components;

import javax.inject.Singleton;

import dagger.Component;
import ru.devtron.yatranslate.base.BaseModel;
import ru.devtron.yatranslate.di.modules.ModelModule;

@Component(modules = ModelModule.class)
@Singleton
public interface ModelComponent {
	void inject(BaseModel baseModel);
}