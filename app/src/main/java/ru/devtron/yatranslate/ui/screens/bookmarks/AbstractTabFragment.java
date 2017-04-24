package ru.devtron.yatranslate.ui.screens.bookmarks;

import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import ru.devtron.yatranslate.R;
import ru.devtron.yatranslate.base.BaseFragment;
import ru.devtron.yatranslate.data.storage.Translation;
import ru.devtron.yatranslate.ui.screens.bookmarks.tabs.HistoryAdapter;
import ru.devtron.yatranslate.ui.screens.bookmarks.tabs.HistoryItemViewHolder;
import ru.devtron.yatranslate.ui.screens.bookmarks.tabs.HistoryPresenter;
import ru.devtron.yatranslate.ui.screens.bookmarks.tabs.IHistoryView;
import ru.devtron.yatranslate.ui.widget.RxEditText;

/**
 * Абстрактный фрагмент для табов (истории и избранных)
 */
public abstract class AbstractTabFragment extends BaseFragment implements IHistoryView {
	private String title;

	/**
	 * Получает title для отображения в табах
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}


	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * The constant TAB_HISTORY.
	 */
	public static final int TAB_HISTORY = 0;
	/**
	 * The constant TAB_FAVORITE.
	 */
	public static final int TAB_FAVORITE = 1;

	@Inject
	HistoryPresenter mPresenter;
	@BindView(R.id.history_search)
	RxEditText mRxSearch;
	@BindView(R.id.history_recycler)
	RecyclerView mRecycler;

	private HistoryAdapter mAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_history, container, false);
		unbinder = ButterKnife.bind(this, v);
		createDaggerComponent();

		mPresenter.takeView(this);
		mPresenter.initView();

		mRxSearch.setHint(getSearchHint());

		return v;
	}

	@Override
	public void onDestroyView() {
		mPresenter.dropView();
		mAdapter.setCheckListener(null);
		super.onDestroyView();
	}


	public void onClearCallback(List<Translation> translations) {
		mRxSearch.setText(null);
		Observable.fromIterable(translations)
				.filter(translation -> {
					if (getType() == TAB_FAVORITE) {
						return translation.isFavorite();
					} else {
						return translation.isHistory();
					}
				})
				.toList()
				.subscribe(translations1 -> mAdapter.reloadList(translations1));
	}

	//region ==================== IHistoryView ====================
	@Override
	public HistoryAdapter getHistoryAdapter() {
		return mAdapter;
	}

	@Override
	public void initRecyclerView() {
		mAdapter = new HistoryAdapter();
		mAdapter.setHasStableIds(true);
		mAdapter.setCheckListener(new HistoryItemViewHolder.OnFavoriteCheckListener(){
			@Override
			public void onItemClicked(int position) {
				mPresenter.translateWordAgain(mAdapter.getItem(position));
			}

			@Override
			public void onCheckedChanged(boolean isChecked, int position) {
				mPresenter.updateHistoryItem(mAdapter.getItem(position), isChecked);
			}
		});

		LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
		DividerItemDecoration decoration = new DividerItemDecoration(getContext(), linearLayoutManager.getOrientation());
		decoration.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.recycler_divider));
		mRecycler.setLayoutManager(linearLayoutManager);
		mRecycler.addItemDecoration(decoration);
		mRecycler.setAdapter(mAdapter);
	}

	@Override
	public void showHistoryList(List<Translation> history) {
		mAdapter.addList(history);
	}

	@Override
	public Flowable<CharSequence> getSearchFlowable() {
		return mRxSearch.observeChanges();
	}
	//endregion

	protected abstract void createDaggerComponent();

	/**
	 * Получает hint из каждого фрагмента для {@link #mRxSearch}
	 * @return the search hint
	 */
	@StringRes
	protected abstract int getSearchHint();
}
