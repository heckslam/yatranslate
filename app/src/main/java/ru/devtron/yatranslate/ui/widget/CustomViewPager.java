package ru.devtron.yatranslate.ui.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Viewpager который имеет возможность блокировать свайпы
 */
public class CustomViewPager extends ViewPager {

	private boolean isPagingEnabled = false;

	public CustomViewPager(Context context) {
		super(context);
	}

	public CustomViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return this.isPagingEnabled && super.onTouchEvent(event);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
		return this.isPagingEnabled && super.onInterceptTouchEvent(event);
	}

	/**
	 * Переключатель блокировать ли свайпы или нет
	 * @param enabled - включены свайпы или нет
	 */
	public void setPagingEnabled(boolean enabled) {
		this.isPagingEnabled = enabled;
	}
}