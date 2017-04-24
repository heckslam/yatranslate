package ru.devtron.yatranslate.base;

import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

/**
 * Base activity which contains common methods. Each activity should extend this class
 */
public class BaseActivity extends AppCompatActivity {
	/**
	 * Метод избавляет нас от необходимости дублировать findViewById и cast'инга
	 * Синтаксис позаимствован из JQuery
	 * Это лучше чем использовать ButterKnife
	 *
	 * @param <T> generic для cast'инга
	 * @param id  для поиска в layout
	 * @return view t
	 */
	protected <T extends View> T $(@IdRes int id) {
		return (T) findViewById(id);
	}

	public void showMessage(String message) {
		Toast.makeText(this, message, Toast.LENGTH_LONG).show();
	}
}
