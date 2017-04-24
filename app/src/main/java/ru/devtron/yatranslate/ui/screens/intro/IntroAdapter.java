package ru.devtron.yatranslate.ui.screens.intro;

import android.database.DataSetObserver;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ru.devtron.yatranslate.R;

class IntroAdapter extends PagerAdapter {
	final int[] icons = new int[]{
            R.drawable.intro1,
            R.drawable.intro2,
            R.drawable.intro3
    };
    private int[] titles = new int[]{
            R.string.intro_page1_heading,
            R.string.intro_page2_heading,
            R.string.intro_page3_heading
    };
    private int[] messages = new int[]{
            R.string.intro_page1_description,
            R.string.intro_page2_description,
            R.string.intro_page3_description
    };

    @Override
    public int getCount() {
        return icons.length;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = View.inflate(container.getContext(), R.layout.intro_item_layout, null);
        TextView headerTextView = (TextView) view.findViewById(R.id.header_text);
        TextView messageTextView = (TextView) view.findViewById(R.id.message_text);
        container.addView(view, 0);

        headerTextView.setText(container.getContext().getString(titles[position]));
        messageTextView.setText(container.getContext().getString(messages[position]));

        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        if (observer != null) {
            super.unregisterDataSetObserver(observer);
        }
    }
}