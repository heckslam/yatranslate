package ru.devtron.yatranslate.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import io.reactivex.Observable;
import ru.devtron.yatranslate.App;

/**
 * Проверяет доступен ли сейчас интернет
 */
public class NetworkStatusChecker {

	private static Boolean isNetworkAvailable() {
		ConnectivityManager cm = (ConnectivityManager)
				App.getAppComponent().getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = cm.getActiveNetworkInfo();
		return networkInfo != null && networkInfo.isConnectedOrConnecting();
	}

	public static Observable<Boolean> isInternetAvailable() {
		return Observable.just(isNetworkAvailable());
	}
}
