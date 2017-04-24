package ru.devtron.yatranslate.ui.screens.bookmarks.tabs;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import ru.devtron.yatranslate.R;
import ru.devtron.yatranslate.data.storage.Translation;


/**
 * Адаптер для истории и избранных
 */
public class HistoryAdapter extends RecyclerView.Adapter<HistoryItemViewHolder> {

	private List<Translation> mTranslationList = new ArrayList<>();
	private List<Translation> mTranslationsCopy = new ArrayList<>();
	private HistoryItemViewHolder.OnFavoriteCheckListener mOnFavoriteCheckListener;

	public void addList(List<Translation> translations) {
		mTranslationList = translations;
		mTranslationsCopy.addAll(translations);
	}

	public void reloadList(List<Translation> translations) {
		mTranslationList.clear();
		mTranslationsCopy.clear();
		mTranslationList.addAll(translations);
		mTranslationsCopy.addAll(translations);
	}

	void addItem(Translation item) {
		mTranslationList.add(0, item);
		mTranslationsCopy.add(0, item);
		notifyItemInserted(0);
	}

	void removeItem(Translation entity) {
		mTranslationList.remove(entity);
		mTranslationsCopy.remove(entity);
		notifyDataSetChanged();
	}

	void updateFavorite(Translation item) {
		for (int i = 0; i < mTranslationList.size(); i++) {
			if (mTranslationList.get(i).get_id().equals(item.get_id())) {
				mTranslationList.get(i).setFavorite(item.isFavorite());
				notifyItemChanged(i);
				return;
			}
		}
	}

	@Override
	public long getItemId(int position) {
		//if (mTranslationList.get(position) != null)
			return mTranslationList.get(position).get_id();
		//else
		//	return RecyclerView.NO_ID;
	}

	@Override
	public HistoryItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_favorite_hist, parent, false);
		return new HistoryItemViewHolder(v, mOnFavoriteCheckListener);
	}

	@Override
	public void onBindViewHolder(HistoryItemViewHolder holder, int position) {
		Translation history = mTranslationList.get(position);

		holder.mToTranslateTv.setText(history.getToTranslate());
		holder.mTranslatedTv.setText(history.getTranslatedStr());
		holder.mLanguageTv.setText(history.getLang());
		holder.mFavoriteCb.setChecked(history.isFavorite());
	}

	@Override
	public int getItemCount() {
		return mTranslationList.size();
	}

	/**
	 * Возвращает перевод по позиции в адаптере
	 *
	 * @param position the position
	 * @return the item
	 */
	public Translation getItem(int position) {
		return mTranslationList.get(position);
	}

	/**
	 * Фильтрует отображаемые пользователю данные при вводе текста в поиск
	 *
	 * @param query the query
	 */
	void filter(String query) {
		mTranslationList.clear();
		if (query.isEmpty()) {
			mTranslationList.addAll(mTranslationsCopy);
		} else {
			query = query.toLowerCase();
			for (Translation history : mTranslationsCopy) {
				if (history.getToTranslate() != null) {
					if (history.getToTranslate().toLowerCase().contains(query)) {
						mTranslationList.add(history);
					}
				}
				if (history.getTranslatedStr() != null) {
					if (history.getTranslatedStr().toLowerCase().contains(query)) {
						mTranslationList.add(history);
					}
				}
			}
		}
		notifyDataSetChanged();
	}

	public void setCheckListener(HistoryItemViewHolder.OnFavoriteCheckListener actionListener) {
		this.mOnFavoriteCheckListener = actionListener;
	}
}