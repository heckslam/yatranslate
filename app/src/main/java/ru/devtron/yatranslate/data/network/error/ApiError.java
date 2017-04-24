package ru.devtron.yatranslate.data.network.error;

/**
 * Универсальный класс для отображения ошибки с API
 * Используется если не подходит ни к одной из кастомный ошибок (например {@link NetworkAvailableError})
 */
public class ApiError extends Throwable {
	private int statusCode;

	public ApiError(int statusCode) {
		super("Ошибка: " + statusCode + ", попробуйте еще раз");
		this.statusCode = statusCode;
	}
}
