package ru.devtron.yatranslate.ui.screens.intro;

import ru.devtron.yatranslate.base.BaseView;

interface IIntroView extends BaseView {
	void openRootActivity();
	int getCurrentItemIndex();
	void nextPage();
}
