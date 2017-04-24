package ru.devtron.yatranslate.ui.screens.translate;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import nl.nl2312.rxcupboard2.DatabaseChange;
import ru.devtron.yatranslate.base.BaseModel;
import ru.devtron.yatranslate.data.network.response.DictionaryRes;
import ru.devtron.yatranslate.data.network.response.TranslateRes;
import ru.devtron.yatranslate.data.storage.Translation;
import ru.devtron.yatranslate.utils.RxUtils;

class TranslateModel extends BaseModel {

	/**
	 * Detect language and translate observable.
	 *
	 * @param text the text
	 * @return the observable
	 */
	Observable<TranslateRes> detectLanguageAndTranslate(String text) {
		return mDataManager.detectLanguageAndTranslate(text)
				.compose(RxUtils.async());
	}

	Observable<DictionaryRes> requestDictionary(String lang, String text) {
		return mDataManager.dictionary(lang, text)
				.compose(RxUtils.async());
	}

	void updateLastItem() {
		mDataManager.updateLastItem();
	}

	Flowable<DatabaseChange<Translation>> observeChangesTranslationTbl() {
		return mDataManager.observeChangesTranslationTbl()
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread());
	}

}