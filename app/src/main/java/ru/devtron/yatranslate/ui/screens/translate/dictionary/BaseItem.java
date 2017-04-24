package ru.devtron.yatranslate.ui.screens.translate.dictionary;

import android.support.annotation.IntDef;

public abstract class BaseItem {

	@IntDef({PART_OF_SPEECH_ITEM, ITEM})
	@interface ItemType {}

	static final int PART_OF_SPEECH_ITEM = 0;
	static final int ITEM = 1;

	private int itemType;

	BaseItem(@ItemType int itemType) {
		this.itemType = itemType;
	}

	int getItemType() {
		return itemType;
	}
}
