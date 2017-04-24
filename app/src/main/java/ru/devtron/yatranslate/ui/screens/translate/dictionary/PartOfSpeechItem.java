package ru.devtron.yatranslate.ui.screens.translate.dictionary;

public class PartOfSpeechItem extends BaseItem {

	private String header;
	public PartOfSpeechItem(String header) {
		super(PART_OF_SPEECH_ITEM);
		this.header = header;
	}
	String getHeader() {
		return header;
	}
}