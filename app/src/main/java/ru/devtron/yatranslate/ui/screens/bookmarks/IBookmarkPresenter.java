package ru.devtron.yatranslate.ui.screens.bookmarks;

/**
 * The interface Bookmark presenter.
 */
public interface IBookmarkPresenter {
	/**
	 * Clear history or favorites from db.
	 *
	 * @param constHistoryOrFav the const history or fav
	 */
	void clearHistoryOrFavoritesFromDb(int constHistoryOrFav);
}
