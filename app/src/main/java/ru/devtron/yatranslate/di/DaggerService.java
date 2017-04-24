package ru.devtron.yatranslate.di;

import android.support.annotation.Nullable;
import android.util.Log;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class DaggerService {
	private static final String TAG = "DaggerService";

	private static Map<Class, Object> sComponentMap = new HashMap<>();

	/**
	 * Регистрирует компонент даггера в мапе. Это нужно для того чтобы презентеры не были завязаны
	 * на жизненном цикле фрагментов и активностей. При повороте экрана презентер не будет пересоздаваться
	 * а будет вытаскиваться из этой мапы
	 * @param componentClass  класс даггер компонента
	 * @param daggerComponent сам объект даггер компонента
	 */
	public static void registerComponent(Class componentClass, Object daggerComponent) {
		sComponentMap.put(componentClass, daggerComponent);
	}

	/**
	 * Возвращает компонент если он уже был зарегистрирован
	 * @param <T>            the type parameter
	 * @param componentClass класс даггер компонента
	 * @return объект даггер компонента
	 */
	@Nullable
	@SuppressWarnings("unchecked")
	public static <T> T getComponent(Class<T> componentClass) {
		Object component = sComponentMap.get(componentClass);
		return (T) component;
	}

	/**
	 * Unregister scope when there is no need for it anymore
	 * @param scopeAnnotation the scope annotation
	 */
	public static void unregisterScope(Class<? extends Annotation> scopeAnnotation) {
		Iterator<Map.Entry<Class, Object>> iterator = sComponentMap.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry<Class, Object> entry = iterator.next();
			if (entry.getKey().isAnnotationPresent(scopeAnnotation)) {
				iterator.remove();
			}
		}
	}
}