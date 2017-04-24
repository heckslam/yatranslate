package ru.devtron.yatranslate.ui.screens.bookmarks;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.SparseArray;

import ru.devtron.yatranslate.ui.screens.bookmarks.tabs.FavoritesPagerFragment;
import ru.devtron.yatranslate.ui.screens.bookmarks.tabs.HistoryPagerFragment;

/**
 * Адаптер для табов истории и избранных
 */
class BookmarkPagerAdapter extends FragmentStatePagerAdapter {
	private SparseArray<AbstractTabFragment> tabs;

	BookmarkPagerAdapter(FragmentManager fm) {
		super(fm);
		initTabsMap();
	}

	@Override
	public AbstractTabFragment getItem(int position) {
		return tabs.get(position);
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return tabs.get(position).getTitle();
	}

	@Override
	public int getCount() {
		return tabs.size();
	}

	private void initTabsMap() {
		tabs = new SparseArray<>();
		tabs.put(0, HistoryPagerFragment.newInstance());
		tabs.put(1, FavoritesPagerFragment.newInstance());
	}
}
