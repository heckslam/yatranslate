package ru.devtron.yatranslate.di.modules;

import android.content.Context;

import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.devtron.yatranslate.data.network.RestService;
import ru.devtron.yatranslate.data.network.interceptor.CacheInterceptor;
import ru.devtron.yatranslate.utils.ConstantManager;

@Module
public class NetworkModule {

	@Provides
	@Singleton
	OkHttpClient provideOkHttpClient(Context context) {
		return createClient(context);
	}

	@Provides
	@Singleton
	Retrofit provideRetrofit(OkHttpClient okHttpClient) {
		return createRetrofit(okHttpClient);
	}

	@Provides
	@Singleton
	RestService provideRestService(Retrofit retrofit) {
		return retrofit.create(RestService.class);
	}

	private OkHttpClient createClient(Context context) {
		int cacheSize = 10 * 1024 * 1024; // 10 MiB
		return new OkHttpClient.Builder()
				.addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
				.addNetworkInterceptor(new CacheInterceptor())
				.cache(new Cache(context.getCacheDir(), cacheSize))
				//.addInterceptor(new StethoInterceptor())
				.connectTimeout(5000, TimeUnit.MILLISECONDS)
				.readTimeout(5000, TimeUnit.MILLISECONDS)
				.writeTimeout(5000, TimeUnit.MILLISECONDS)
				.build();
	}

	private Retrofit createRetrofit(OkHttpClient okHttpClient) {
		return new Retrofit.Builder()
				.baseUrl(ConstantManager.BASE_URL)
				.addConverterFactory(GsonConverterFactory.create())
				.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
				.client(okHttpClient)
				.build();
	}

}