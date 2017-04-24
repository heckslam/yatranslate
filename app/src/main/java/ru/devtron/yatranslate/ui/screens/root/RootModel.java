package ru.devtron.yatranslate.ui.screens.root;

import ru.devtron.yatranslate.base.BaseModel;

class RootModel extends BaseModel {
	/**
	 * Is intro shown boolean.
	 *
	 * @return the boolean
	 */
	boolean isIntroShown() {
		return mDataManager.isIntroShown();
	}
}
