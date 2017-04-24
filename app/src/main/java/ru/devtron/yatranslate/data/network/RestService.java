package ru.devtron.yatranslate.data.network;

import io.reactivex.Observable;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;
import ru.devtron.yatranslate.data.network.response.DictionaryRes;
import ru.devtron.yatranslate.data.network.response.TranslateRes;
import ru.devtron.yatranslate.utils.ConstantManager;

/**
 * RestService описывающий методы работы с API
 */
public interface RestService {
	/**
	 * Метод для получения перевода с API
	 *
	 * @param key  ключ от Translate API
	 * @param lang язык с какого на какой (например ru-en либо en-ru)
	 * @param text текст для перевода
	 * @return the observable
	 */
	@GET("translate")
	Observable<Response<TranslateRes>> translate(@Query("key") String key,
	                                             @Query("lang") String lang,
	                                             @Query("text") String text);

	/**
	 * Метод для получения словаря и примеров с Dictionary API
	 *
	 * @param key  ключ от Dictionary API
	 * @param lang язык
	 * @param text слово
	 * @return the observable
	 */
	@GET("https://dictionary.yandex.net/api/v1/dicservice.json/lookup")
	Observable<Response<DictionaryRes>> dictionary(@Query("key") String key,
	                                               @Query("lang") String lang,
	                                               @Query("text") String text);

	/**
	 * Метод для определения языка на основе введенного пользователем текста
	 *
	 * @param key  ключ от Translate API
	 * @param text введенный пользователем текст
	 * @return the observable
	 */
	@GET("detect")
	Observable<Response<TranslateRes>> detectLang(@Query("key") String key,
	                                              @Query("text") String text,
	                                              @Query("hint") String hint);

}
