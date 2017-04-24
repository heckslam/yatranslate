package ru.devtron.yatranslate.ui.screens.root;

import android.support.annotation.StringRes;

import ru.devtron.yatranslate.base.BaseView;

/**
 * View contract for {@link RootActivity}
 */
public interface IRootView extends BaseView {
	void showMessage(String message);

	void showMessage(@StringRes int message);

	void showIntro();

	void showError(Throwable e);

	void showTranslateTab();
}
