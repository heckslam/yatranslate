package ru.devtron.yatranslate.data.network.error;

/**
 *  Ошибка возникает если язык который хочет ввести пользователь не поддерживается
 *  либо данное направление перевода не поддерживается
 */
public class NoLanguageError extends Throwable {
	public NoLanguageError() {
		super("Этот язык не поддерживается");
	}
}