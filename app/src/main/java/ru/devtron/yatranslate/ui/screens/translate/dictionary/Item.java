package ru.devtron.yatranslate.ui.screens.translate.dictionary;

public class Item extends BaseItem {

	private int pos;
	private String translate;
	private String mean;
	private String example;

	public Item(int pos, String translate, String mean, String example) {
		super(ITEM);
		this.pos = pos;
		this.translate = translate;
		this.mean = mean;
		this.example = example;
	}

	int getPos() {
		return pos;
	}

	public String getTranslate() {
		return translate;
	}

	String getMean() {
		return mean;
	}

	String getExample() {
		return example;
	}
}