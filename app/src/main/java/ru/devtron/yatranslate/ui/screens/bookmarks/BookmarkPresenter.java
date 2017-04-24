package ru.devtron.yatranslate.ui.screens.bookmarks;

import javax.inject.Inject;

import dagger.Provides;
import ru.devtron.yatranslate.base.BasePresenter;
import ru.devtron.yatranslate.di.DaggerService;
import ru.devtron.yatranslate.ui.screens.bookmarks.tabs.HistoryPresenter;
import ru.devtron.yatranslate.ui.screens.root.RootPresenter;

public class BookmarkPresenter extends BasePresenter<IBookmarkView> implements IBookmarkPresenter{
	@Inject
	BookmarkModel mModel;
	@Inject
	RootPresenter mRootPresenter;

	@Override
	public void initView() {

	}
	@Override
	public void clearHistoryOrFavoritesFromDb(int constHistoryOrFav) {
		mCoSubs.add(
				mModel.clearHistoryOrFavoritesFromDb(constHistoryOrFav)
						.subscribe(this::clearUnusedRows, Throwable::printStackTrace)
		);
	}

	private void clearUnusedRows() {
		mCoSubs.add(
				mModel.clearUnusedRows()
					.subscribe((aLong, throwable) -> reloadAllItems())
		);
	}

	private void reloadAllItems() {
		mCoSubs.add(
				mModel.getAllItems()
					.subscribe(translations -> {
						if (getView() != null) {
							if (mRootPresenter.getClearEventSubject().hasObservers()) {
								mRootPresenter.getClearEventSubject().onNext(translations);
							}
						}
					})
		);
	}

	BookmarkPresenter() {
		createDaggerComponent();
	}


	//region ============================= DI =============================
	@dagger.Module
	public static class Module {
		@Provides
		@BookmarkModelScope
		BookmarkModel provideBookmarkModel() {
			return new BookmarkModel();
		}
	}

	@dagger.Component(dependencies = BookmarksFragment.Component.class, modules = Module.class)
	@BookmarkModelScope
	public interface Component {
		void inject(BookmarkPresenter presenter);
		void inject(HistoryPresenter presenter);
	}

	private void createDaggerComponent() {
		Component component = DaggerService.getComponent(Component.class);
		if (component == null) {
			component = DaggerBookmarkPresenter_Component.builder()
					.component(DaggerService.getComponent(BookmarksFragment.Component.class))
					.module(new BookmarkPresenter.Module())
					.build();
			DaggerService.registerComponent(Component.class, component);
		}
		component.inject(this);
	}
	//endregion
}
