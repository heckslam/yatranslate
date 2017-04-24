package ru.devtron.yatranslate.ui.screens.root;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.SparseArray;

import ru.devtron.yatranslate.base.BaseFragment;
import ru.devtron.yatranslate.ui.screens.bookmarks.BookmarksFragment;
import ru.devtron.yatranslate.ui.screens.translate.TranslateFragment;

class BottomBarPagerAdapter extends FragmentStatePagerAdapter {
	private SparseArray<BaseFragment> tabs;

	BottomBarPagerAdapter(FragmentManager fm) {
		super(fm);
		initTabsMap();
	}

	@Override
	public BaseFragment getItem(int position) {
		return tabs.get(position);
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return null;
	}

	@Override
	public int getCount() {
		return tabs.size();
	}

	private void initTabsMap() {
		tabs = new SparseArray<>();
		tabs.put(0, TranslateFragment.newInstance());
		tabs.put(1, BookmarksFragment.newInstance());
	}
}