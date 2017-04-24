package ru.devtron.yatranslate.ui.screens.bookmarks;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import nl.nl2312.rxcupboard2.DatabaseChange;
import ru.devtron.yatranslate.base.BaseModel;
import ru.devtron.yatranslate.data.storage.Translation;

/**
 * Модель для истории и избранных
 */
public class BookmarkModel extends BaseModel {
	/**
	 * Gets history from db.
	 * @return the history from db
	 */
	public Single<List<Translation>> getHistoryFromDb() {
		return mDataManager.getHistoryFromDb()
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread());
	}

	/**
	 * Observe changes in translation table Sqlite
	 * @return the flowable
	 */
	public Flowable<DatabaseChange<Translation>> observeChangesTranslationTbl() {
		return mDataManager.observeChangesTranslationTbl()
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread());
	}

	/**
	 * Gets favorite from db.
	 *
	 * @return the favorite from db
	 */
	public Single<List<Translation>> getFavoriteFromDb() {
		return mDataManager.getFavoritesFromDb()
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread());
	}

	/**
	 * Update history item.
	 *
	 * @param history the history
	 */
	public void updateHistoryItem(Translation history) {
		mDataManager.updateHistoryItem(history);
	}

	/**
	 * Clear history or favorites from db.
	 *
	 * @param constHistoryOrFav the const history or fav
	 * @return the completable
	 */
	Completable clearHistoryOrFavoritesFromDb(int constHistoryOrFav) {
		if (constHistoryOrFav == AbstractTabFragment.TAB_HISTORY) {
			return mDataManager.clearHistory();
		} else {
			return mDataManager.clearFavoriteFromDb();
		}
	}

	/**
	 * Clear unused rows.
	 *
	 * @return the single
	 */
	Single<Long> clearUnusedRows() {
		return mDataManager.clearUnusedRows();
	}

	/**
	 * Gets all items.
	 *
	 * @return the all items
	 */
	Single<List<Translation>> getAllItems() {
		return mDataManager.getAllItems();
	}
}