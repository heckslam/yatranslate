package ru.devtron.yatranslate.ui.screens.bookmarks;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.Provides;
import ru.devtron.yatranslate.R;
import ru.devtron.yatranslate.base.BaseFragment;
import ru.devtron.yatranslate.di.DaggerService;
import ru.devtron.yatranslate.ui.screens.root.RootActivity;
import ru.devtron.yatranslate.ui.screens.root.RootPresenter;
import ru.devtron.yatranslate.ui.widget.CustomViewPager;
import ru.devtron.yatranslate.utils.ViewUtils;


/**
 * Фрагмент для закладок и избранных который держит их в tablayout
 */
public class BookmarksFragment extends BaseFragment implements IBookmarkView {
	private static final String TAG = BookmarksFragment.class.getSimpleName();

	public static BookmarksFragment newInstance() {
		return new BookmarksFragment();
	}

	@BindView(R.id.toolbar)
	Toolbar mToolbar;
	@BindView(R.id.tab_layout)
	TabLayout mTabLayout;
	@BindView(R.id.view_pager)
	CustomViewPager mViewPager;
	@Inject
	BookmarkPresenter mPresenter;
	BookmarkPagerAdapter mPagerAdapter;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_bookmarks, container, false);
		unbinder = ButterKnife.bind(this, v);
		createDaggerComponent();

		mPresenter.takeView(this);
		mPresenter.initView();

		initUI();

		return v;
	}

	private void initUI() {
		mToolbar.setTitle("");
		((RootActivity) getActivity()).setSupportActionBar(mToolbar);
		mViewPager.setPagingEnabled(true);
		mPagerAdapter = new BookmarkPagerAdapter(getChildFragmentManager());
		mViewPager.setAdapter(mPagerAdapter);
		mTabLayout.setupWithViewPager(mViewPager);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.menu_bookmark, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.clear_history_btn:
				onClearClicked();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * On clear clicked.
	 */
	public void onClearClicked() {
		ViewUtils.showAlertDialog(getContext(),
				getString(R.string.bookmark_dialog_clear_msg, mViewPager.getCurrentItem() == 0 ? "историю" : "избранное"),
				R.string.bookmark_dialog_action_ok,
				R.string.bookmark_dialog_action_cancel,
				(dialog, which) -> mPresenter.clearHistoryOrFavoritesFromDb(mViewPager.getCurrentItem()),
				null);
	}

	@Override
	public void onDestroyView() {
		mPresenter.dropView();
		if (isRemoving()) {
			DaggerService.unregisterScope(BookmarkScope.class);
			DaggerService.unregisterScope(BookmarkModelScope.class);
		}
		super.onDestroyView();
	}

	//region ==================== DI ====================
	@dagger.Module
	public static class Module {
		@Provides
		@BookmarkScope
		BookmarkPresenter provideBookmarkPresenter() {
			return new BookmarkPresenter();
		}
	}

	@dagger.Component(modules = Module.class, dependencies = RootActivity.Component.class)
	@BookmarkScope
	public interface Component {
		void inject(BookmarksFragment fragment);
		RootPresenter getRootPresenter();
	}

	private void createDaggerComponent() {
		Component component = DaggerService.getComponent(Component.class);
		if (component == null) {
			component = DaggerBookmarksFragment_Component.builder()
					.component(DaggerService.getComponent(RootActivity.Component.class))
					.module(new Module())
					.build();
			DaggerService.registerComponent(Component.class, component);
		}
		component.inject(this);
	}
	//endregion
}
