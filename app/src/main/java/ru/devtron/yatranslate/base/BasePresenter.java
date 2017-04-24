package ru.devtron.yatranslate.base;

import android.support.annotation.Nullable;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;
import ru.devtron.yatranslate.ui.screens.root.IRootView;
import ru.devtron.yatranslate.ui.screens.root.RootPresenter;

/**
 * Base presenter, each presenter should extend this class
 *
 * @param <T> the view which extends BaseView
 * @author Ruslan Aliev
 */
public abstract class BasePresenter<T extends BaseView> {
	private static final String TAG = "BasePresenter";
	private T mView;
	/**
	 * CompositeDisposable содержит всех подписчиков и очищает их при вызове {@link #dropView()}"
	 */
	protected CompositeDisposable mCoSubs;

	/**
	 * Принимает интерфейс вьюхи при инициализации презентера
	 * @param view the view
	 */
	public void takeView(T view) {
		mCoSubs = new CompositeDisposable();
		mView = view;
	}

	/**
	 * Отцепляет вьюху от презентера и отписывается от всех подписчиков
	 */
	public void dropView() {
		if (mCoSubs != null) {
			mCoSubs.clear();
		}
		mView = null;
	}

	/**
	 * Вызывается после получения вьюхи для первоначальной инициализации данных из презентера
	 */
	public abstract void initView();

	/**
	 * Возвращает текущую вью
	 * @return the view
	 */
	@Nullable
	public T getView() {
		return mView;
	}
}