package ru.devtron.yatranslate.ui.screens.intro;

import javax.inject.Inject;

import dagger.Provides;
import ru.devtron.yatranslate.base.BasePresenter;
import ru.devtron.yatranslate.di.DaggerService;

class IntroPresenter extends BasePresenter<IIntroView> implements IIntroPresenter{
	@Inject
	IntroModel mModel;

	@Override
	public void initView() {
	}

	IntroPresenter() {
		createDaggerComponent();
	}

	@Override
	public void onSkipClicked() {
		if (getView() != null) {
			if (getView().getCurrentItemIndex() == 2) {
				mModel.setIntroShown();
				getView().openRootActivity();
			} else {
				getView().nextPage();
			}
		}
	}


	//region ============================= DI =============================
	@dagger.Module
	class Module {
		@Provides
		@IntroScope
		IntroModel provideIntroModel() {
			return new IntroModel();
		}
	}

	@dagger.Component(modules = Module.class)
	@IntroScope
	interface Component {
		void inject(IntroPresenter presenter);
	}

	private void createDaggerComponent() {
		Component component = DaggerService.getComponent(Component.class);
		if (component == null) {
			component = DaggerIntroPresenter_Component.builder()
					.module(new IntroPresenter.Module())
					.build();
			DaggerService.registerComponent(Component.class, component);
		}
		component.inject(this);
	}
	//endregion
}
