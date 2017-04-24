package ru.devtron.yatranslate.data.network.error;


/**
 * Ошибка возникает если пользователь ввел слишком много символов для перевода
 */
public class OutOfSymbolLengthError extends Throwable {
	public OutOfSymbolLengthError() {
		super("Слишком много символов, попробуйте уменьшить текст");
	}
}
