package ru.devtron.yatranslate.data.network;

import android.support.annotation.VisibleForTesting;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.annotations.NonNull;
import retrofit2.Response;
import ru.devtron.yatranslate.data.network.error.ErrorUtils;
import ru.devtron.yatranslate.data.network.error.NetworkAvailableError;
import ru.devtron.yatranslate.data.network.error.NoLanguageError;
import ru.devtron.yatranslate.data.network.error.OutOfSymbolLengthError;
import ru.devtron.yatranslate.utils.NetworkStatusChecker;

import static android.support.annotation.VisibleForTesting.NONE;


/**
 * Класс для обработки типичных ситуаций при запросе в сеть (применяется в каждом запросе)
 * возвращает response либо ошибку
 */
public class RestTransformer<R> implements ObservableTransformer<Response<R>, R> {
	private boolean mTestMode;

	@Override
	public ObservableSource<R> apply(@NonNull Observable<Response<R>> responseObservable) {
		Observable<Boolean> networkStatus;
		if (mTestMode) {
			networkStatus = Observable.just(true);
		} else {
			networkStatus = NetworkStatusChecker.isInternetAvailable();
		}
		return networkStatus
				.flatMap(aBoolean -> aBoolean ? responseObservable : Observable.error(new NetworkAvailableError())) // кидаем ошибку если инернет недоступен
				// если убрать предыдущую строку то приложение будет работать без интернета с введенными ранее запросами, т.к. включено кэширование
				.flatMap(rResponse -> {
					switch (rResponse.code()) {
						case 200:
							return Observable.just(rResponse.body());
						case 304:
							return Observable.empty();
						case 413: // пользователь ввел слишком много символов
							return Observable.error(new OutOfSymbolLengthError());
						case 422: // язык не поддерживается
							return Observable.error(new NoLanguageError());
						case 501:
							return Observable.error(new NoLanguageError());
						default:
							return Observable.error(ErrorUtils.parseError(rResponse));
					}
				});
	}
	@VisibleForTesting(otherwise = NONE)
	public void setTestMode() {
		mTestMode = true;
	}
}