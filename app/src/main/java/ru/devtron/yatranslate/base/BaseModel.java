package ru.devtron.yatranslate.base;

import javax.inject.Inject;

import ru.devtron.yatranslate.data.DataManager;
import ru.devtron.yatranslate.di.DaggerService;
import ru.devtron.yatranslate.di.components.DaggerModelComponent;
import ru.devtron.yatranslate.di.components.ModelComponent;
import ru.devtron.yatranslate.di.modules.ModelModule;


/**
 * The type Base model.
 */
public abstract class BaseModel {
	/**
	 * DataManager для работы с данными (network, local) который инжектится в каждую модель
	 */
	@Inject
	public DataManager mDataManager;

	/**
	 * Instantiates a new Base model with dagger initialization
	 */
	public BaseModel() {
		ModelComponent component = DaggerService.getComponent(ModelComponent.class);
		if (component == null) {
			component = DaggerModelComponent.builder()
					.modelModule(new ModelModule())
					.build();
			DaggerService.registerComponent(ModelComponent.class, component);
		}
		component.inject(this);
	}
}