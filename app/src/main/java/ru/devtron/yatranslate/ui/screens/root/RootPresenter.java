package ru.devtron.yatranslate.ui.screens.root;


import java.util.List;

import javax.inject.Inject;

import dagger.Provides;
import io.reactivex.subjects.PublishSubject;
import ru.devtron.yatranslate.base.BasePresenter;
import ru.devtron.yatranslate.data.storage.Translation;
import ru.devtron.yatranslate.di.DaggerService;

public class RootPresenter extends BasePresenter<IRootView> implements IRootPresenter {
	@Inject
	RootModel mModel;

	private PublishSubject<List<Translation>> mClearEventSubject = PublishSubject.create();
	private PublishSubject<Translation> mTranslateAgainSubject = PublishSubject.create();

	@Override
	public void initView() {
		if (getView() != null) {
			if (!mModel.isIntroShown()) {
				getView().showIntro();
			}
		}
	}

	public PublishSubject<List<Translation>> getClearEventSubject() {
		return mClearEventSubject;
	}

	public PublishSubject<Translation> getTranslateAgainSubject() {
		return mTranslateAgainSubject;
	}

	public RootPresenter() {
		Component component = DaggerService.getComponent(Component.class);
		if (component == null) {
			component = DaggerRootPresenter_Component.builder()
					.module(new Module())
					.build();
			DaggerService.registerComponent(Component.class, component);
		}
		component.inject(this);
	}

	@Override
	public void showTranslateTab() {
		if (getView() != null) {
			getView().showTranslateTab();
		}
	}

	//region ============================= DI =============================
	@dagger.Module
	class Module {
		@Provides
		@RootScope
		RootModel provideTranslateModel() {
			return new RootModel();
		}
	}

	@dagger.Component(modules = Module.class)
	@RootScope
	interface Component {
		void inject(RootPresenter presenter);
	}
	//endregion
}