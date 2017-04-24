package ru.devtron.yatranslate.ui.screens.intro;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.Window;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;

import com.pixelcan.inkpageindicator.InkPageIndicator;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.Provides;
import ru.devtron.yatranslate.R;
import ru.devtron.yatranslate.base.BaseActivity;
import ru.devtron.yatranslate.di.DaggerService;
import ru.devtron.yatranslate.ui.screens.root.RootActivity;

/**
 * Активити вызывается при запуске приложения в первый раз. Показывает пользователю главные
 * функции приложения с красивой анимацией Circular reveal
 */
public class IntroActivity extends BaseActivity implements IIntroView {
	private static final String TAG = "IntroActivity";
	private ViewPager viewPager;
	private ImageView mIntroImage;
	private IntroAdapter mAdapter;
	private int lastPage = 0;

	Button skipIntroBtn;
	@Inject
	IntroPresenter mPresenter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.intro_layout);

		createDaggerComponent();

		viewPager = $(R.id.intro_view_pager);
		skipIntroBtn = $(R.id.skip_intro_btn);
		mIntroImage = $(R.id.intro_image);
		mAdapter = new IntroAdapter();
		viewPager.setAdapter(mAdapter);
		InkPageIndicator inkPageIndicator = $(R.id.indicator);
		inkPageIndicator.setViewPager(viewPager);
		viewPager.setPageMargin(0);
		viewPager.setOffscreenPageLimit(1);

		initViewPagerAnimation();

		skipIntroBtn.setOnClickListener(view -> mPresenter.onSkipClicked());

		mPresenter.takeView(this);
		mPresenter.initView();

	}

	private void initViewPagerAnimation() {
		viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
			}

			@Override
			public void onPageSelected(int position) {
				if (position == 2) {
					skipIntroBtn.setText(R.string.intro_guide_start_btn);
				} else {
					skipIntroBtn.setText(R.string.intro_action_further);
				}
			}

			@Override
			public void onPageScrollStateChanged(int state) {
				if (state == ViewPager.SCROLL_STATE_IDLE || state == ViewPager.SCROLL_STATE_SETTLING) {
					startNextImageRevealAnimation();
				}
			}
		});
	}

	public void startNextImageRevealAnimation() {
		if (lastPage != viewPager.getCurrentItem()) {
			lastPage = viewPager.getCurrentItem();

			final int cx = (mIntroImage.getMeasuredWidth()) / 2; // центр вью по X
			final int cy = (mIntroImage.getMeasuredHeight()) / 2; // центр вью по Y
			final int radius = Math.max(mIntroImage.getWidth(), mIntroImage.getHeight()); // радиус

			final Animator hideCircleAnim;
			final Animator showCircleAnim;
			ObjectAnimator showColorAnim = null;
			ObjectAnimator hideColorAnim = null;

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
				hideCircleAnim = ViewAnimationUtils.createCircularReveal(mIntroImage, cx, cy, radius, 0); // анимация с максимального радиуса до 0
				showCircleAnim = ViewAnimationUtils.createCircularReveal(mIntroImage, cx, cy, 0, radius); // анимация с 0 радиуса до максимального

			} else {
				hideCircleAnim = ObjectAnimator.ofFloat(mIntroImage, "alpha", 0);
				showCircleAnim = ObjectAnimator.ofFloat(mIntroImage, "alpha", 1);
			}


			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
				ColorDrawable cdr = (ColorDrawable) mIntroImage.getForeground();
				hideColorAnim = ObjectAnimator.ofArgb(cdr, "color",
						getResources().getColor(R.color.transparent, null),
						getResources().getColor(R.color.colorAccent, null));
				showColorAnim = ObjectAnimator.ofArgb(cdr, "color",
						getResources().getColor(R.color.transparent, null));
			}

			hideCircleAnim.addListener(new AnimatorListenerAdapter() { // слушатель на окончание анимации
				@Override
				public void onAnimationEnd(Animator animation) {
					super.onAnimationEnd(animation);
					mIntroImage.setImageResource(mAdapter.icons[lastPage]);
					mIntroImage.setVisibility(View.INVISIBLE);
				}
			});

			showCircleAnim.addListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationStart(Animator animation) {
					super.onAnimationStart(animation);
					mIntroImage.setVisibility(View.VISIBLE);
				}
			});
			AnimatorSet hideSet = new AnimatorSet();
			AnimatorSet showSet = new AnimatorSet();
			AnimatorSet resultSet = new AnimatorSet();
			addAnimatorTogetherInSet(hideSet, hideCircleAnim, hideColorAnim);
			addAnimatorTogetherInSet(showSet, showCircleAnim, showColorAnim);
			hideSet.setDuration(300);
			hideSet.setInterpolator(new FastOutSlowInInterpolator());

			showSet.setDuration(300);
			showSet.setInterpolator(new FastOutSlowInInterpolator());

			resultSet.playSequentially(hideSet, showSet);
			resultSet.start();

			//showCircleAnim.setStartDelay(100);
			//hideSet.playSequentially(hideCircleAnim, showCircleAnim);
		}

	}

	private void addAnimatorTogetherInSet(AnimatorSet set, Animator... animators) {
		List<Animator> animatorList = new ArrayList<>();
		for (Animator animator : animators) {
			if (animator != null) {
				animatorList.add(animator);
			}
		}
		set.playTogether(animatorList);
	}

	@Override
	public void openRootActivity() {
		Intent rootIntent = new Intent(IntroActivity.this, RootActivity.class);
		startActivity(rootIntent);
		finish();
	}

	@Override
	public int getCurrentItemIndex() {
		return viewPager.getCurrentItem();
	}

	@Override
	public void nextPage() {
		viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
	}


	//region ============================= DI =============================
	@dagger.Module
	class Module {
		@Provides
		@IntroScope
		IntroPresenter provideIntroPresenter() {
			return new IntroPresenter();
		}
	}

	@dagger.Component(modules = Module.class)
	@IntroScope
	interface Component {
		void inject(IntroActivity activity);
	}

	private void createDaggerComponent() {
		Component component = DaggerService.getComponent(Component.class);
		if (component == null) {
			component = DaggerIntroActivity_Component.builder()
					.module(new Module())
					.build();
			DaggerService.registerComponent(Component.class, component);
		}
		component.inject(this);
	}

	@Override
	protected void onDestroy() {
		if (isFinishing()) {
			mPresenter.dropView();
			DaggerService.unregisterScope(IntroScope.class);
		}
		super.onDestroy();
	}
	//endregion
}
