package ru.devtron.yatranslate.data.storage;

import nl.qbusict.cupboard.annotation.Column;
import nl.qbusict.cupboard.annotation.Index;

public class Translation {

	private Long _id;
	@Index(unique = true)
	@Column("to_translate")
	private String toTranslate;
	@Column("translated_str")
	private String translatedStr;
	private String lang;
	private boolean favorite;
	private boolean history;

	public Translation() {
	}

	public Translation(String toTranslate, String translatedStr, String lang) {
		this.toTranslate = toTranslate;
		this.translatedStr = translatedStr;
		this.lang = lang;
		this.history = true;
		this.favorite = false;
	}

	public Long get_id() {
		return _id;
	}

	public String getToTranslate() {
		return toTranslate;
	}

	public void setToTranslate(String toTranslate) {
		this.toTranslate = toTranslate;
	}

	public String getTranslatedStr() {
		return translatedStr;
	}

	public void setTranslatedStr(String translatedStr) {
		this.translatedStr = translatedStr;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public boolean isFavorite() {
		return favorite;
	}

	public void setFavorite(boolean favorite) {
		this.favorite = favorite;
	}

	public boolean isHistory() {
		return history;
	}

	public void setHistory(boolean history) {
		this.history = history;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Translation that = (Translation) o;
		return toTranslate.equals(that.toTranslate);
	}

	@Override
	public int hashCode() {
		int result = _id != null ? _id.hashCode() : 0;
		result = 31 * result + toTranslate.hashCode();
		return result;
	}

	@Override
	public String toString() {
		return "Translation{" +
				"_id=" + _id +
				", toTranslate='" + toTranslate + '\'' +
				", favorite=" + favorite +
				", history=" + history +
				'}';
	}
}
