package ru.devtron.yatranslate.data;

import android.support.annotation.VisibleForTesting;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.Single;
import nl.nl2312.rxcupboard2.DatabaseChange;
import retrofit2.Retrofit;
import ru.devtron.yatranslate.data.managers.DbManager;
import ru.devtron.yatranslate.data.managers.PreferencesManager;
import ru.devtron.yatranslate.data.network.RestService;
import ru.devtron.yatranslate.data.network.RestTransformer;
import ru.devtron.yatranslate.data.network.response.DictionaryRes;
import ru.devtron.yatranslate.data.network.response.TranslateRes;
import ru.devtron.yatranslate.data.storage.Translation;
import ru.devtron.yatranslate.di.DaggerService;
import ru.devtron.yatranslate.di.components.DaggerDataManagerComponent;
import ru.devtron.yatranslate.di.components.DataManagerComponent;
import ru.devtron.yatranslate.di.modules.LocalModule;
import ru.devtron.yatranslate.di.modules.NetworkModule;
import ru.devtron.yatranslate.App;
import ru.devtron.yatranslate.utils.ConstantManager;

import static android.support.annotation.VisibleForTesting.NONE;

/**
 * DataManager - Общий класс который объединяет работу с Network, Database, SharedPreferences
 */
public class DataManager {
	private static DataManager ourInstance = null;
	@Inject
	RestService mRestService;
	@Inject
	Retrofit mRetrofit;
	@Inject
	PreferencesManager mPreferencesManager;
	@Inject
	DbManager mDbManager;
	private final RestTransformer mRestTransformer;

	public DataManager() {
		DataManagerComponent component = DaggerService.getComponent(DataManagerComponent.class);
		if (component == null) {
			component = DaggerDataManagerComponent.builder()
					.appComponent(App.getAppComponent())
					.networkModule(new NetworkModule())
					.localModule(new LocalModule())
					.build();
			DaggerService.registerComponent(DataManagerComponent.class, component);
		}
		component.inject(this);
		mRestTransformer = new RestTransformer<>();
	}

	public static DataManager getInstance() {
		if (ourInstance == null) {
			ourInstance = new DataManager();
		}
		return ourInstance;
	}

	@VisibleForTesting(otherwise = NONE)
	DataManager(RestService restService, DbManager dbManager) {
		mRestService = restService;
		mDbManager = dbManager;
		mRestTransformer = new RestTransformer();
		mRestTransformer.setTestMode();
		ourInstance = this;
	}

	/**
	 * Определяет язык на основе введенного текста и отправляет на перевод
	 *
	 * @param text the text to translate
	 * @return the observable
	 */
	public Observable<TranslateRes> detectLanguageAndTranslate(String text) {
		return mRestService.detectLang(ConstantManager.TRANSLATE_KEY, text, "en,ru")
				.compose(((RestTransformer<TranslateRes>) mRestTransformer))
				.switchMap(translateRes -> translate(translateRes.getLang(), text));
	}

	public Observable<TranslateRes> detectLanguageTest(String text) {
		return mRestService.detectLang(ConstantManager.TRANSLATE_KEY, text, "en,ru")
				.compose(((RestTransformer<TranslateRes>) mRestTransformer));
	}

	/**
	 * Метод проверяет существует ли такой перевод уже в базе данных для проверки является ли
	 * данный перевод избранным. И делает запрос в сеть для перевода.
	 *
	 * @param lang язык с какого на какой
	 * @param text текст для перевода
	 * @return the observable
	 */
	public Observable<TranslateRes> translate(String lang, String text) {
		Observable<Translation> localObs = mDbManager.getSavedTranslation(text)
				.toObservable();

		Observable<TranslateRes> networkObs = mRestService.translate(
				ConstantManager.TRANSLATE_KEY, lang, text)
				.compose(((RestTransformer<TranslateRes>) mRestTransformer))
				.doOnNext(translateRes -> saveTranslation(translateRes, text));
		return Observable.zip(localObs, networkObs, (translation, translateRes) -> {
			if (translation != null) {
				translateRes.setFavorite(translation.isFavorite());
			}
			return translateRes;
		}).onErrorResumeNext(throwable -> networkObs);
	}

	public Observable<DictionaryRes> dictionary(String lang, String text) {
		return mRestService.dictionary(ConstantManager.DICTIONARY_KEY, lang, text)
				.compose(((RestTransformer<DictionaryRes>) mRestTransformer));
	}

	private void saveTranslation(TranslateRes translateRes, String text) {
		mDbManager.saveTranslation(translateRes, text);
	}

	public Single<List<Translation>> getHistoryFromDb() {
		return mDbManager.getHistoryFromDb();
	}

	public Single<List<Translation>> getFavoritesFromDb() {
		return mDbManager.getFavoritesFromDb();
	}

	public Flowable<DatabaseChange<Translation>> observeChangesTranslationTbl() {
		return mDbManager.observeTranslationChanges();
	}

	public void updateHistoryItem(Translation history) {
		mDbManager.updateHistoryItem(history);
	}

	public Completable clearHistory() {
		return mDbManager.clearHistory();
	}

	public Completable clearFavoriteFromDb() {
		return mDbManager.clearFavorites();
	}

	public boolean isIntroShown() {
		return mPreferencesManager.isIntroShown();
	}

	public void setIntroShown(boolean b) {
		mPreferencesManager.setIntroShown(b);
    }

	public void updateLastItem() {
		mDbManager.updateLastItem();
	}

	public Single<Long> clearUnusedRows() {
		return mDbManager.clearUnusedRows();
	}

	public Single<List<Translation>> getAllItems() {
		return mDbManager.getAllItems();
	}
}
