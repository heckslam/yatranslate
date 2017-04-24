package ru.devtron.yatranslate.ui.screens.root;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.ViewPager;
import android.widget.Toast;

import javax.inject.Inject;

import ru.devtron.yatranslate.BuildConfig;
import ru.devtron.yatranslate.R;
import ru.devtron.yatranslate.base.BaseActivity;
import ru.devtron.yatranslate.di.DaggerService;
import ru.devtron.yatranslate.di.modules.RootModule;
import ru.devtron.yatranslate.ui.screens.intro.IntroActivity;

public class RootActivity extends BaseActivity implements IRootView {
	@Inject
	RootPresenter mPresenter;
	private TabLayout tabLayout;
	private int[] tabIcons = {
			R.drawable.ic_tab_translate,
			R.drawable.ic_tab_bookmark,
			R.drawable.ic_tab_settings
	};

	ViewPager mTabPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTheme(R.style.AppTheme);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_root);

		createDaggerComponent();

		mPresenter.takeView(this);
		mPresenter.initView();

		mTabPager = $(R.id.main_tabs_pager);
		mTabPager.setAdapter(new BottomBarPagerAdapter(getSupportFragmentManager()));

		tabLayout = (TabLayout) findViewById(R.id.tab_layout);
		tabLayout.setupWithViewPager(mTabPager);
		setupTabIcons();

		tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
			@SuppressWarnings("ConstantConditions")
			@Override
			public void onTabSelected(TabLayout.Tab tab) {
				int blackColor = ContextCompat.getColor(RootActivity.this, R.color.black);
				changeColorTabIcon(blackColor, tab.getIcon());
			}

			@SuppressWarnings("ConstantConditions")
			@Override
			public void onTabUnselected(TabLayout.Tab tab) {
				int inactiveColor = ContextCompat.getColor(RootActivity.this, R.color.tab_inactive);
				changeColorTabIcon(inactiveColor, tab.getIcon());
			}

			@Override
			public void onTabReselected(TabLayout.Tab tab) {

			}
		});
	}

	@Override
	public void showIntro() {
		Intent intent = new Intent(this, IntroActivity.class);
		startActivity(intent);
		finish();
	}

	@Override
	public void onBackPressed() {
		if (mTabPager.getCurrentItem() > 0) {
			mTabPager.setCurrentItem(0);
		} else {
			super.onBackPressed();
		}
	}

	private void changeColorTabIcon(int color, Drawable tabIcon) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			DrawableCompat.setTint(tabIcon, color);
		} else {
			tabIcon.mutate().setColorFilter(color, PorterDuff.Mode.SRC_IN);
		}
	}

	private void setupTabIcons() {
		for (int i = 0; i < tabIcons.length; i++) {
			if (tabLayout.getTabAt(i) != null) {
				//noinspection ConstantConditions
				tabLayout.getTabAt(i).setIcon(tabIcons[i]);
			}
		}
	}

	//region ==================== IRootView ====================
	@Override
	public void showMessage(String message) {
		Toast.makeText(this, message, Toast.LENGTH_LONG).show();
	}

	@Override
	public void showMessage(@StringRes int message) {
		Toast.makeText(this, message, Toast.LENGTH_LONG).show();
	}

	@Override
	public void showError(Throwable e) {
		if (BuildConfig.DEBUG) {
			showMessage(e.getMessage());
			e.printStackTrace();
		} else {
			showMessage(getString(R.string.error_msg));
			// TODO: send error stacktrace to Crashlytics
		}
	}

	@Override
	public void showTranslateTab() {
		if (mTabPager.getCurrentItem() > 0) {
			mTabPager.setCurrentItem(0);
		}
	}
	//endregion


	//region ==================== DI ====================
	@dagger.Component(modules = RootModule.class)
	@RootScope
	public interface Component {
		void inject(RootActivity activity);
		RootPresenter getRootPresenter();
	}

	private void createDaggerComponent() {
		Component component = DaggerService.getComponent(Component.class);
		if (component == null) {
			component = DaggerRootActivity_Component.builder()
					.rootModule(new RootModule())
					.build();
			DaggerService.registerComponent(Component.class, component);
		}
		component.inject(this);
	}
	//endregion

	@Override
	protected void onDestroy() {
		if (isFinishing()) {
			mPresenter.dropView();
			DaggerService.unregisterScope(RootScope.class);
		}
		super.onDestroy();
	}
}
