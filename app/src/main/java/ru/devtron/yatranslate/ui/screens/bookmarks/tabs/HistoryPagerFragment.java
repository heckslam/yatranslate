package ru.devtron.yatranslate.ui.screens.bookmarks.tabs;

import dagger.Provides;
import ru.devtron.yatranslate.R;
import ru.devtron.yatranslate.di.DaggerService;
import ru.devtron.yatranslate.ui.screens.bookmarks.AbstractTabFragment;

/**
 * Фрагмент для истории является табом
 */
public class HistoryPagerFragment extends AbstractTabFragment {
	public static HistoryPagerFragment newInstance() {
		HistoryPagerFragment fragment = new HistoryPagerFragment();
		fragment.setTitle("История");
		return fragment;
	}

	@Override
	public int getType() {
		return TAB_HISTORY;
	}

	@Override
	public void onDestroyView() {
		if (isRemoving()) {
			DaggerService.unregisterScope(HistoryScope.class);
		}
		super.onDestroyView();
	}


	//region ============================= DI =============================
	@dagger.Module
	class Module {
		@Provides
		@HistoryScope
		HistoryPresenter provideHistoryPresenter() {
			return new HistoryPresenter();
		}
	}

	@dagger.Component(modules = Module.class)
	@HistoryScope
	interface Component {
		void inject(HistoryPagerFragment fragment);
	}

	@Override
	protected void createDaggerComponent() {
		Component component = DaggerService.getComponent(Component.class);
		if (component == null) {
			component = DaggerHistoryPagerFragment_Component.builder()
					.module(new Module())
					.build();
			DaggerService.registerComponent(Component.class, component);
		}
		component.inject(this);
	}

	@Override
	protected int getSearchHint() {
		return R.string.history_search_hint;
	}
	//endregion
}
