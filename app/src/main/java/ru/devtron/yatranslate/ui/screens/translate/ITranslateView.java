package ru.devtron.yatranslate.ui.screens.translate;

import java.util.List;

import ru.devtron.yatranslate.base.BaseView;
import ru.devtron.yatranslate.ui.screens.root.RootActivity;
import ru.devtron.yatranslate.ui.screens.translate.dictionary.BaseItem;

interface ITranslateView extends BaseView {
	RootActivity getRootActivity();
	TranslatePanel getTranslatePanel();
	void showDictionary(List<BaseItem> baseItems, String transcription);
	void showTranslated(String text);

	/**
	 * Возвращает все вью в исходное состояние
	 */
	void setIdleState();

	boolean checkAudioPermission();
	void updateCurrentLang();
	void setFavorite(boolean isFavorite);
}
