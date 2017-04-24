package ru.devtron.yatranslate.ui.screens.bookmarks.tabs;

import java.util.List;

import io.reactivex.Flowable;
import ru.devtron.yatranslate.base.BaseView;
import ru.devtron.yatranslate.data.storage.Translation;

/**
 * The interface History view.
 */
public interface IHistoryView extends BaseView {
	/**
	 * Gets search flowable.
	 *
	 * @return the search flowable
	 */
	Flowable<CharSequence> getSearchFlowable();

	/**
	 * Init recycler view.
	 */
	void initRecyclerView();

	/**
	 * Show history list.
	 *
	 * @param history the history
	 */
	void showHistoryList(List<Translation> history);

	/**
	 * Gets type.
	 *
	 * @return the type
	 */
	int getType();

	/**
	 * Gets history adapter.
	 *
	 * @return the history adapter
	 */
	HistoryAdapter getHistoryAdapter();

	/**
	 * On clear callback.
	 *
	 * @param translations the translations
	 */
	void onClearCallback(List<Translation> translations);
}