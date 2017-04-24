package ru.devtron.yatranslate.ui.screens.bookmarks.tabs;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import nl.nl2312.rxcupboard2.OnDatabaseChange;
import ru.devtron.yatranslate.base.BasePresenter;
import ru.devtron.yatranslate.data.storage.Translation;
import ru.devtron.yatranslate.di.DaggerService;
import ru.devtron.yatranslate.ui.screens.bookmarks.AbstractTabFragment;
import ru.devtron.yatranslate.ui.screens.bookmarks.BookmarkModel;
import ru.devtron.yatranslate.ui.screens.bookmarks.BookmarkPresenter;
import ru.devtron.yatranslate.ui.screens.bookmarks.BookmarksFragment;
import ru.devtron.yatranslate.ui.screens.bookmarks.DaggerBookmarkPresenter_Component;
import ru.devtron.yatranslate.ui.screens.root.RootPresenter;

public class HistoryPresenter extends BasePresenter<IHistoryView> implements IHistoryPresenter {
	private static final String TAG = "HistoryPresenter";

	@Inject
	BookmarkModel mModel;
	@Inject
	RootPresenter mRootPresenter;

	public HistoryPresenter() {
		createDaggerComponent();
	}

	@Override
	public void initView() {
		if (getView() != null) {
			getView().initRecyclerView();
			subscribeOnSearchEt();
			if (getView().getType() == HistoryPagerFragment.TAB_HISTORY) {
				loadHistory();
			} else {
				loadFavorite();
			}
			subscribeOnDatabaseChanges();
		}
	}

	private void subscribeOnDatabaseChanges() {
		//noinspection ConstantConditions
		if (getView().getType() == AbstractTabFragment.TAB_HISTORY) {
			mCoSubs.add(
					mModel.observeChangesTranslationTbl()
							.subscribe(new OnDatabaseChange<Translation>() {
								@Override
								public void onInsert(Translation entity) {
									getView().getHistoryAdapter().addItem(entity);
								}

								@Override
								public void onDelete(Translation entity) {
									getView().getHistoryAdapter().removeItem(entity);
								}

								@Override
								public void onUpdate(Translation entity) {
									if (!entity.isHistory()) {
										getView().getHistoryAdapter().removeItem(entity);
									} else {
										getView().getHistoryAdapter().updateFavorite(entity);
									}
								}
							})
			);
		} else if (getView().getType() == AbstractTabFragment.TAB_FAVORITE) {
			mCoSubs.add(
					mModel.observeChangesTranslationTbl()
							.subscribe(new OnDatabaseChange<Translation>() {
								@Override
								public void onDelete(Translation entity) {
									getView().getHistoryAdapter().removeItem(entity);
								}

								@Override
								public void onUpdate(Translation entity) {
									if (entity.isFavorite()) {
										getView().getHistoryAdapter().addItem(entity);
									} else {
										getView().getHistoryAdapter().removeItem(entity);
									}
								}
							})
			);
		}
		subscribeOnClearEvent();
	}

	private void subscribeOnClearEvent() {
		mRootPresenter.getClearEventSubject()
				.subscribe(translations -> {
					if (getView() != null) {
						getView().onClearCallback(translations);
					}
				});
	}

	private void subscribeOnSearchEt() {
		if (getView() != null) {
			mCoSubs.add(
					getView().getSearchFlowable()
							.debounce(250, TimeUnit.MILLISECONDS)
							.map(charSequence -> charSequence.toString().trim())
							.observeOn(AndroidSchedulers.mainThread())
							.subscribe(string -> {
								if (getView().getHistoryAdapter() != null) {
									getView().getHistoryAdapter().filter(string);
								}
							})
			);
		}
	}

	@Override
	public void loadHistory() {
		if (getView() != null) {
			mCoSubs.add(mModel.getHistoryFromDb()
					.subscribe(translationList -> getView().showHistoryList(translationList),
							Throwable::printStackTrace)
			);
		}
	}

	@Override
	public void loadFavorite() {
		if (getView() != null) {
			mCoSubs.add(mModel.getFavoriteFromDb()
					.subscribe(historyEntities -> getView().showHistoryList(historyEntities),
							Throwable::printStackTrace)
			);
		}
	}

	@Override
	public void updateHistoryItem(Translation translation, boolean isChecked) {
		if (isChecked) {
			translation.setFavorite(true);
		} else {
			translation.setFavorite(false);
		}
		mModel.updateHistoryItem(translation);
	}

	@Override
	public void translateWordAgain(Translation item) {
		if (mRootPresenter.getTranslateAgainSubject().hasObservers()) {
			mRootPresenter.getTranslateAgainSubject().onNext(item);
		}
	}

	//region ============================= DI =============================
	private void createDaggerComponent() {
		BookmarkPresenter.Component component = DaggerService.getComponent(
				BookmarkPresenter.Component.class);
		if (component == null) {
			component = DaggerBookmarkPresenter_Component.builder()
					.component(DaggerService.getComponent(BookmarksFragment.Component.class))
					.module(new BookmarkPresenter.Module())
					.build();
			DaggerService.registerComponent(BookmarkPresenter.Component.class, component);
		}
		component.inject(this);
	}
	//endregion
}