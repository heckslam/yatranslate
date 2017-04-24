package ru.devtron.yatranslate.ui.screens.bookmarks.tabs;

import ru.devtron.yatranslate.data.storage.Translation;

/**
 * The interface History presenter.
 */
public interface IHistoryPresenter {
	/**
	 * Load history.
	 */
	void loadHistory();

	/**
	 * Load favorite.
	 */
	void loadFavorite();

	/**
	 * Update history item.
	 *
	 * @param history   the history
	 * @param isChecked the is checked
	 */
	void updateHistoryItem(Translation history, boolean isChecked);

	/**
	 * Translate word again.
	 *
	 * @param item the item
	 */
	void translateWordAgain(Translation item);
}