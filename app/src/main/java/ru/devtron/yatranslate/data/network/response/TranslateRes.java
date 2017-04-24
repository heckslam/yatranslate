package ru.devtron.yatranslate.data.network.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Arrays;
import java.util.List;

import ru.devtron.yatranslate.data.storage.Translation;


public class TranslateRes {
	@SerializedName("code")
	@Expose
	private int code;
	@SerializedName("lang")
	@Expose
	private String lang;
	@SerializedName("text")
	@Expose
	private List<String> text;
	@Expose
	private transient boolean isFavorite;


	public int getCode() {
		return code;
	}

	public String getLang() {
		switch (lang) {
			case "en":
				return "en-ru";
			case "ru":
				return "ru-en";
			default:
				return lang;
		}
	}

	public void setFavorite(Boolean favorite) {
		isFavorite = favorite;
	}

	public List<String> getText() {
		return text;
	}

	public boolean isFavorite() {
		return isFavorite;
	}
}