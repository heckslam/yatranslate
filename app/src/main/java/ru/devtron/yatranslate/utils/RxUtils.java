package ru.devtron.yatranslate.utils;

import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.schedulers.Schedulers;

/**
 * Util класс для работы с RX
 */
public class RxUtils {
	private RxUtils() {
	}

	/**
	 * Трансформирует observable в асинхронный поток
	 *
	 * @param <T> the type parameter
	 * @return the observable transformer
	 */
	@NonNull
	public static <T> ObservableTransformer<T, T> async() {
		return upstream -> upstream
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread());
	}

}
