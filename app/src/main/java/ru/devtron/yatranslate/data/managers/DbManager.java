package ru.devtron.yatranslate.data.managers;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.BiConsumer;
import io.reactivex.schedulers.Schedulers;
import nl.nl2312.rxcupboard2.DatabaseChange;
import nl.nl2312.rxcupboard2.RxDatabase;
import nl.qbusict.cupboard.Cupboard;
import ru.devtron.yatranslate.data.network.response.TranslateRes;
import ru.devtron.yatranslate.data.storage.Translation;
import ru.devtron.yatranslate.ui.screens.bookmarks.tabs.HistoryAdapter;

/**
 * Db manager - менеджер для работы с локальной БД SQlite
 */
public class DbManager {

	private Cupboard cupboard;
	private SQLiteDatabase database;
	private RxDatabase rxDatabase;

	private Flowable<DatabaseChange<Translation>> mDatabaseChangeFlowable;

	/**
	 * Instantiates a new Db manager.
	 *
	 * @param cupboard   the cupboard - обертка для SQLite (всего < 400 методов)
	 * @param database   the database
	 * @param rxDatabase the rx database - надстройка над cupboard для работы в стиле rx
	 */
	public DbManager(Cupboard cupboard, SQLiteDatabase database,
	                 RxDatabase rxDatabase) {
		this.cupboard = cupboard;
		this.database = database;
		this.rxDatabase = rxDatabase;
	}

	/**
	 * Метод для сохранение перевода в базу данных
	 * @param translateRes ответ от сервера с переведенным предложением и дополнительными данными
	 * @param toTransate   слово для перевода
	 */
	public void saveTranslation(TranslateRes translateRes, String toTransate) {
		rxDatabase.put(new Translation(
				toTransate,
				translateRes.getText().get(0),
				translateRes.getLang()
		)).subscribe((translation, throwable) -> {
			if (throwable != null) {
				throwable.printStackTrace();
			}
		});
	}

	/**
	 * Получение истории переводов из локальной БД
	 * @return the history from db
	 */
	public Single<List<Translation>> getHistoryFromDb() {
		return rxDatabase.query(rxDatabase.buildQuery(Translation.class)
				.withSelection("history = ?", "1")
				.orderBy("_id desc")).toList();
	}

	/**
	 * Получение избранных переводов из локальной БД
	 * @return the favorites from db
	 */
	public Single<List<Translation>> getFavoritesFromDb() {
		return rxDatabase.query(rxDatabase.buildQuery(Translation.class)
				.withSelection("favorite = ?", "1")
				.orderBy("_id desc")).toList();
	}

	/**
	 * Получение конретного перевода из БД на основе введенного пользователем слова для перевода
	 * @param toTranslate Слово или предложение для перевода
	 * @return the saved translation
	 */
	public Single<Translation> getSavedTranslation(String toTranslate) {
		return rxDatabase.query(rxDatabase.buildQuery(Translation.class)
				.withSelection("to_translate = ?", toTranslate)
				.limit(1))
				.firstOrError();
	}

	/**
	 * Наблюдатель за изменением таблицы переводов (TRANSLATIONS) может слать события
	 * {@link nl.nl2312.rxcupboard2.DatabaseChange.DatabaseInsert}
	 * {@link nl.nl2312.rxcupboard2.DatabaseChange.DatabaseUpdate}
	 * {@link nl.nl2312.rxcupboard2.DatabaseChange.DatabaseDelete}
	 * @return the flowable
	 */
	public Flowable<DatabaseChange<Translation>> observeTranslationChanges() {
		if (mDatabaseChangeFlowable == null) {
			mDatabaseChangeFlowable = rxDatabase.changes(Translation.class);
		}
		return mDatabaseChangeFlowable;
	}

	/**
	 * Принимает перевод из {@link HistoryAdapter} и либо удаляет его если его
	 * нет в истории и избранном либо обновляет
	 * @param translation the translation
	 */
	public void updateHistoryItem(Translation translation) {
		if (!translation.isFavorite() && !translation.isHistory()) {
			rxDatabase.delete(translation)
					.subscribeOn(Schedulers.io())
					.observeOn(AndroidSchedulers.mainThread())
					.subscribe(mTranslationThrowableBiConsumer);
		} else {
			rxDatabase.put(translation)
					.subscribeOn(Schedulers.io())
					.observeOn(AndroidSchedulers.mainThread())
					.subscribe(mTranslationThrowableBiConsumer);
		}
	}

	private BiConsumer<Translation, Throwable> mTranslationThrowableBiConsumer =
			(BiConsumer<Translation, Throwable>) (translation1, throwable) -> {
		if (throwable != null) {
			throwable.printStackTrace();
		}
	};

	/**
	 * Метод устанавливает всем переводам в базе флаг isHistory на false
	 * @return the completable
	 */
	public Completable clearHistory() {
		return Completable.create(e -> {
			ContentValues contentValues = new ContentValues();
			contentValues.put("history", 0);
			cupboard.withDatabase(database).update(Translation.class, contentValues);
			e.onComplete();
		}).subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread());
	}

	/**
	 * Метод устанавливает всем избранным флаг isFavorite на false
	 * @return the completable
	 */
	public Completable clearFavorites() {
		return Completable.create(e -> {
			ContentValues contentValues = new ContentValues();
			contentValues.put("favorite", 0);
			cupboard.withDatabase(database).update(Translation.class, contentValues);
			e.onComplete();
		}).subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread());
	}

	/**
	 * Метод вызывается после вызова {@link #clearHistory()} или {@link #clearFavorites()}
	 * которой удаляет элементы не относящиеся ни к истории, ни к избранным
	 * @return the single
	 */
	public Single<Long> clearUnusedRows() {
		return rxDatabase.delete(Translation.class, "favorite = ? AND history = ?", "0", "0")
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread());
	}

	/**
	 * Возвращает все переводы из базы
	 * @return the all items
	 */
	public Single<List<Translation>> getAllItems() {
		return rxDatabase.query(rxDatabase.buildQuery(Translation.class)
				.orderBy("_id desc")).toList();
	}

	/**
	 * Меняет флаг isFavorite на противоположный у последнего перевода в БД
	 * Вызывается из {@link ru.devtron.yatranslate.ui.screens.translate.TranslateFragment}
	 * при добавлении в избранное
	 */
	public void updateLastItem() {
		rxDatabase.query(Translation.class)
				.takeLast(1)
				.subscribe(translation -> {
					translation.setFavorite(!translation.isFavorite());
					rxDatabase.put(translation)
							.subscribeOn(Schedulers.io())
							.observeOn(AndroidSchedulers.mainThread())
							.subscribe(mTranslationThrowableBiConsumer);
				});
	}
}
