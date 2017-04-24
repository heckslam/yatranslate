package ru.devtron.yatranslate.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

public class Utils {
	private static final String TAG = "Utils";

	/**
	 * Возвращает плотность пикселей на текущем устройстве
	 *
	 * @param context the context
	 * @return the density
	 */
	public static int getDensity(Context context) {
		DisplayMetrics displayMetrics = new DisplayMetrics();
		((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(displayMetrics);
		return (int) displayMetrics.density;
	}
}
