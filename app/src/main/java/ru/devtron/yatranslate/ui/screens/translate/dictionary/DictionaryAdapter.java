package ru.devtron.yatranslate.ui.screens.translate.dictionary;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.devtron.yatranslate.R;

public class DictionaryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

	private List<BaseItem> mItems = new ArrayList<>();

	public void addAll(List<BaseItem> items) {
		final int size = this.mItems.size();
		this.mItems.addAll(items);
		notifyItemRangeInserted(size, items.size());
	}

	public void clear() {
		mItems.clear();
		notifyDataSetChanged();
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View v;
		if (viewType == BaseItem.PART_OF_SPEECH_ITEM) {
			v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dictionary_header, parent, false);
			return new HeaderVH(v);
		} else {
			v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dictionary, parent, false);
			return new DictionaryVH(v);
		}
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
		if (getItemViewType(position) == BaseItem.PART_OF_SPEECH_ITEM) {
			PartOfSpeechItem header = (PartOfSpeechItem) mItems.get(position);
			HeaderVH vh = (HeaderVH) holder;

			vh.mHeader.setText(header.getHeader());
		} else {
			Item item = (Item) mItems.get(position);
			DictionaryVH vh = (DictionaryVH) holder;

			setData(vh.mPos, String.valueOf(item.getPos()));
			setData(vh.mTranslate, item.getTranslate());
			setData(vh.mMean, item.getMean());
			setData(vh.mExample, item.getExample());
		}
	}

	private void setData(TextView tv, String s) {
		if (!s.isEmpty()) {
			tv.setVisibility(View.VISIBLE);
			tv.setText(s);
		} else {
			tv.setVisibility(View.GONE);
		}
	}

	@Override
	public int getItemCount() {
		return mItems.size();
	}

	@Override
	public int getItemViewType(int position) {
		return mItems.get(position).getItemType() == BaseItem.PART_OF_SPEECH_ITEM
				? BaseItem.PART_OF_SPEECH_ITEM
				: BaseItem.ITEM;
	}


	class HeaderVH extends RecyclerView.ViewHolder {

		@BindView(R.id.header_tv)
		TextView mHeader;

		private HeaderVH(View itemView) {
			super(itemView);
			ButterKnife.bind(this, itemView);
		}
	}

	class DictionaryVH extends RecyclerView.ViewHolder {
		@BindView(R.id.pos_tv)
		TextView mPos;
		@BindView(R.id.translate_tv)
		TextView mTranslate;
		@BindView(R.id.meaning_tv)
		TextView mMean;
		@BindView(R.id.example_tv)
		TextView mExample;

		private DictionaryVH(View itemView) {
			super(itemView);
			ButterKnife.bind(this, itemView);
		}
	}
}
