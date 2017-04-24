package ru.devtron.yatranslate.ui.screens.bookmarks.tabs;

import dagger.Provides;
import ru.devtron.yatranslate.R;
import ru.devtron.yatranslate.di.DaggerService;
import ru.devtron.yatranslate.ui.screens.bookmarks.AbstractTabFragment;

/**
 * Фрагмент для избранных переводов
 */
public class FavoritesPagerFragment extends AbstractTabFragment {

	public static FavoritesPagerFragment newInstance() {
		FavoritesPagerFragment fragment = new FavoritesPagerFragment();
		fragment.setTitle("Избранное");
		return fragment;
	}

	@Override
	public void onDestroyView() {
		if (isRemoving()) {
			DaggerService.unregisterScope(FavoriteScope.class);
		}
		super.onDestroyView();
	}

	@Override
	public int getType() {
		return TAB_FAVORITE;
	}


	//region ============================= DI =============================
	@dagger.Module
	class Module {
		@Provides
		@FavoriteScope
		HistoryPresenter provideHistoryPresenter() {
			return new HistoryPresenter();
		}
	}

	@dagger.Component(modules = Module.class)
	@FavoriteScope
	interface Component {
		void inject(FavoritesPagerFragment fragment);
	}

	@Override
	protected void createDaggerComponent() {
		Component component = DaggerService.getComponent(Component.class);
		if (component == null) {
			component = DaggerFavoritesPagerFragment_Component.builder()
					.module(new Module())
					.build();
			DaggerService.registerComponent(Component.class, component);
		}
		component.inject(this);
	}

	@Override
	protected int getSearchHint() {
		return R.string.favorites_search_hint;
	}
	//endregion
}
