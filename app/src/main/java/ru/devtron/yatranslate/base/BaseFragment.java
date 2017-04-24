package ru.devtron.yatranslate.base;

import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Toast;

import butterknife.Unbinder;

/**
 * Base fragment which contains common methods. Each fragment should extend this class
 */
public class BaseFragment extends Fragment {
	/**
	 * An unbinder contract that will unbind views when called
	 */
	protected Unbinder unbinder;

	/**
	 * Метод избавляет нас от необходимости дублировать findViewById и cast'инга
	 * Синтаксис позаимствован из JQuery
	 * Это лучше чем использовать ButterKnife
	 *
	 * @param <T>  generic для cast'инга
	 * @param view the view
	 * @param id   для поиска в layout
	 * @return view t
	 */
	protected <T extends View> T $(View view, @IdRes int id) {
		return (T) view.findViewById(id);
	}

	public void showMessage(String s) {
		Toast.makeText(getContext(), s, Toast.LENGTH_LONG).show();
	}

	@Override public void onDestroyView() {
		super.onDestroyView();
		if (unbinder != null) unbinder.unbind();
	}
}
