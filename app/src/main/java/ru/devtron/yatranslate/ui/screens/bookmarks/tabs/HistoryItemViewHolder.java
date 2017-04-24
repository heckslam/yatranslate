package ru.devtron.yatranslate.ui.screens.bookmarks.tabs;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.devtron.yatranslate.R;


/**
 * ViewHolder для истории и избранных переводов
 */
public class HistoryItemViewHolder extends RecyclerView.ViewHolder {
	@BindView(R.id.to_translate_tv)
	TextView mToTranslateTv;
	@BindView(R.id.translated_tv)
	TextView mTranslatedTv;
	@BindView(R.id.lang_tv)
	TextView mLanguageTv;
	@BindView(R.id.favorite_checkbox)
	CheckBox mFavoriteCb;

	private OnFavoriteCheckListener mOnFavoriteCheckListener;

	HistoryItemViewHolder(View itemView, OnFavoriteCheckListener onFavoriteCheckListener) {
		super(itemView);
		mOnFavoriteCheckListener = onFavoriteCheckListener;
		ButterKnife.bind(this, itemView);
	}

	@OnClick(R.id.favorite_checkbox)
	void onFavoriteCheckboxClicked() {
		if (mOnFavoriteCheckListener != null) {
			mOnFavoriteCheckListener.onCheckedChanged(mFavoriteCb.isChecked(), getAdapterPosition());
		}
	}

	@OnClick(R.id.history_item_wrapper)
	void onClickItem() {
		if (mOnFavoriteCheckListener != null) {
			mOnFavoriteCheckListener.onItemClicked(getAdapterPosition());
		}
	}

	/**
	 * Callback для элоементов в адаптере (истории и избранных)
	 */
	public interface OnFavoriteCheckListener {
		/**
		 * Вызывается при клике на элемент в адаптере для повторного перевода
		 * @param position the position
		 */
		void onItemClicked(int position);

		/**
		 * Вызывается при добавлении в избранное
		 * @param isChecked the is checked
		 * @param position  the position
		 */
		void onCheckedChanged(boolean isChecked, int position);
	}

}