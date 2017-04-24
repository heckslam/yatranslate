package ru.devtron.yatranslate;

import android.app.Application;

import com.facebook.stetho.Stetho;

import ru.devtron.yatranslate.di.components.AppComponent;
import ru.devtron.yatranslate.di.components.DaggerAppComponent;
import ru.devtron.yatranslate.di.modules.AppModule;

public class App extends Application {

	private static AppComponent sAppComponent;


	@Override
	public void onCreate() {
		super.onCreate();
		createAppComponent();

		if (BuildConfig.DEBUG) {
			Stetho.initializeWithDefaults(this);
		}
	}

	private void createAppComponent() {
		sAppComponent = DaggerAppComponent.builder()
				.appModule(new AppModule(getApplicationContext()))
				.build();
	}

	public static AppComponent getAppComponent() {
		return sAppComponent;
	}
}
