package ru.devtron.yatranslate.data.network.error;

import retrofit2.Response;

public class ErrorUtils {
	/**
	 * Превращает ошибку с Response в {@link ApiError}, если ни одна из кастомных ошибок не попадает
	 * под условие
	 * @param response - респонс с сервера
	 * @return ApiError для отображения пользователю
	 */
	public static ApiError parseError(Response<?> response) {
		return new ApiError(response.code());
	}
}
