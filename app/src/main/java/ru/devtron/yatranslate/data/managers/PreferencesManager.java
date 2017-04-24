package ru.devtron.yatranslate.data.managers;


import android.content.Context;
import android.content.SharedPreferences;

/**
 * Менеджер для работы c SharedPreferences. Он используется для сохранения настроек и состояний
 * самого приложения, нежели для сохранения данных с API
 */
public class PreferencesManager {
	private SharedPreferences mSharedPreferences;

	static final String IS_INTRO_SHOWN = "IS_INTRO_SHOWN";

	/**
	 * Instantiates a new Preferences manager.
	 * @param context the context
	 */
	public PreferencesManager(Context context) {
		mSharedPreferences = context.getSharedPreferences("yatranslate", Context.MODE_PRIVATE);
	}

	/**
	 * Возвращает boolean прошел ли юзер вводное ознакомление или нет
	 * {@link ru.devtron.yatranslate.ui.screens.intro.IntroActivity}
	 * @return the boolean
	 */
	public boolean isIntroShown() {
		return mSharedPreferences.getBoolean(IS_INTRO_SHOWN, false);
	}

	/**
	 * Sets intro shown.
	 * @param isShown the is shown
	 */
	public void setIntroShown(boolean isShown) {
		SharedPreferences.Editor editor = mSharedPreferences.edit();
		editor.putBoolean(IS_INTRO_SHOWN, isShown);
		editor.apply();
	}
}
