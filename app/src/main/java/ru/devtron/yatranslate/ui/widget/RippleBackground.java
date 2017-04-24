package ru.devtron.yatranslate.ui.widget;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import ru.devtron.yatranslate.R;

/**
 * Анимация в стиле Ripple Effect. Используется для кнопки микрофон в качестве background
 */
public class RippleBackground extends RelativeLayout {

	private static final int DEFAULT_RIPPLE_COUNT = 4;
	private static final int DEFAULT_DURATION_TIME = 1500;
	private static final float DEFAULT_SCALE = 2.5f;

	private float rippleStrokeWidth;
	private Paint paint;
	private boolean animationRunning = false;
	private AnimatorSet animatorSet;
	private ArrayList<RippleView> rippleViewList = new ArrayList<>();


	public RippleBackground(Context context) {
		super(context);
	}

	public RippleBackground(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public RippleBackground(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}

	private void init(final Context context) {
		if (isInEditMode())
			return;
		int rippleColor = ContextCompat.getColor(context, R.color.colorAccent);
		rippleStrokeWidth = getResources().getDimension(R.dimen.rippleStrokeWidth);
		float rippleRadius = getResources().getDimension(R.dimen.rippleRadius);
		int rippleDurationTime = DEFAULT_DURATION_TIME;
		int rippleAmount = DEFAULT_RIPPLE_COUNT;
		float rippleScale = DEFAULT_SCALE;

		int rippleDelay = rippleDurationTime / rippleAmount;

		paint = new Paint();
		paint.setAntiAlias(true);
		rippleStrokeWidth = 0;
		paint.setStyle(Paint.Style.FILL);
//		paint.setStyle(Paint.Style.STROKE);
		paint.setColor(rippleColor);

		LayoutParams rippleParams = new LayoutParams((int) (2 * (rippleRadius + rippleStrokeWidth)), (int) (2 * (rippleRadius + rippleStrokeWidth)));
		rippleParams.addRule(ALIGN_PARENT_RIGHT, TRUE);
		rippleParams.addRule(ALIGN_PARENT_TOP, TRUE);

		animatorSet = new AnimatorSet();
		animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
		ArrayList<Animator> animatorList = new ArrayList<>();

		for (int i = 0; i < rippleAmount; i++) {
			RippleView rippleView = new RippleView(getContext());
			addView(rippleView, rippleParams);
			rippleViewList.add(rippleView);
			final ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(rippleView, "ScaleX", 1.0f, rippleScale);
			scaleXAnimator.setRepeatCount(ObjectAnimator.INFINITE);
			scaleXAnimator.setRepeatMode(ObjectAnimator.RESTART);
			scaleXAnimator.setStartDelay(i * rippleDelay);
			scaleXAnimator.setDuration(rippleDurationTime);
			animatorList.add(scaleXAnimator);
			final ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(rippleView, "ScaleY", 1.0f, rippleScale);
			scaleYAnimator.setRepeatCount(ObjectAnimator.INFINITE);
			scaleYAnimator.setRepeatMode(ObjectAnimator.RESTART);
			scaleYAnimator.setStartDelay(i * rippleDelay);
			scaleYAnimator.setDuration(rippleDurationTime);
			animatorList.add(scaleYAnimator);
			final ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(rippleView, "Alpha", 1.0f, 0f);
			alphaAnimator.setRepeatCount(ObjectAnimator.INFINITE);
			alphaAnimator.setRepeatMode(ObjectAnimator.RESTART);
			alphaAnimator.setStartDelay(i * rippleDelay);
			alphaAnimator.setDuration(rippleDurationTime);
			animatorList.add(alphaAnimator);
		}

		animatorSet.playTogether(animatorList);
	}

	private class RippleView extends View {

		/**
		 * Instantiates a new Ripple view.
		 *
		 * @param context the context
		 */
		public RippleView(Context context) {
			super(context);
			this.setVisibility(View.INVISIBLE);
		}

		@Override
		protected void onDraw(Canvas canvas) {
			int radius = (Math.min(getWidth(), getHeight())) / 2;
			canvas.drawCircle(radius, radius, radius - rippleStrokeWidth, paint);
		}
	}

	/**
	 * Start ripple animation.
	 */
	public void startRippleAnimation() {
		if (!isRippleAnimationRunning()) {
			for (RippleView rippleView : rippleViewList) {
				rippleView.setVisibility(VISIBLE);
			}
			animatorSet.start();
			animationRunning = true;
		}
	}

	/**
	 * Stop ripple animation.
	 */
	public void stopRippleAnimation() {
		if (isRippleAnimationRunning()) {
			animatorSet.end();
			animationRunning = false;
		}
	}

	/**
	 * Is ripple animation running boolean.
	 *
	 * @return the boolean
	 */
	public boolean isRippleAnimationRunning() {
		return animationRunning;
	}
}