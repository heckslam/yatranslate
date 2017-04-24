package ru.devtron.yatranslate.ui.screens.translate;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.OnClick;
import dagger.Provides;
import ru.devtron.yatranslate.R;
import ru.devtron.yatranslate.base.BaseFragment;
import ru.devtron.yatranslate.di.DaggerService;
import ru.devtron.yatranslate.ui.screens.root.RootActivity;
import ru.devtron.yatranslate.ui.screens.translate.dictionary.BaseItem;
import ru.devtron.yatranslate.ui.screens.translate.dictionary.DictionaryAdapter;
import ru.devtron.yatranslate.utils.Utils;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static ru.devtron.yatranslate.ui.screens.translate.TranslatePresenter.RU_EN;

public class TranslateFragment extends BaseFragment implements ITranslateView, View.OnClickListener {
	private static final String TAG = "TranslateFragment";
	public static final int REQUEST_PERMISSION_CODE = 1;
	@Inject
	TranslatePresenter mPresenter;
	TranslatePanel mTranslatePanel;
	TextView translatedText;
	RecyclerView mRecyclerView;
	Toolbar mToolbar;
	DictionaryAdapter mDictionaryAdapter;
	TextView fromTranslateLangTv, toTranslateLangTv;
	ImageButton swapLangBtn;
	TextView transcriptionTv;
	LinearLayout translatedBtnsWrapper;
	CheckBox favoriteCheckbox;

	ObjectAnimator mRotateObjectAnimator;

	public static TranslateFragment newInstance() {
		return new TranslateFragment();
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		createDaggerComponent();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_translate, container, false);

		unbinder = ButterKnife.bind(this, view);

		mTranslatePanel = $(view, R.id.translate_panel);
		translatedText = $(view, R.id.translated_tv);
		translatedText.setMovementMethod(new ScrollingMovementMethod());
		mRecyclerView = $(view, R.id.dictionary_recycler);
		mToolbar = $(view, R.id.toolbar);
		fromTranslateLangTv = $(view, R.id.from_translate_lang_tv);
		toTranslateLangTv = $(view, R.id.to_translate_lang_tv);
		swapLangBtn = $(view, R.id.swap_language_btn);
		transcriptionTv = $(view, R.id.transcription_tv);
		translatedBtnsWrapper = $(view, R.id.translated_buttons_wrapper);
		favoriteCheckbox = $(view, R.id.favorite_checkbox);

		mRotateObjectAnimator = ObjectAnimator.ofFloat(swapLangBtn, "rotation", 180);

		initUI();

		swapLangBtn.setOnClickListener(this);

		mPresenter.takeView(this);
		mPresenter.initView();

		return view;
	}

	/**
	 * Озвучить переведенный текст
	 */
	@OnClick(R.id.play_translated_btn)
	public void playTranslatedText() {
		mPresenter.playTextToSpeech(translatedText.getText().toString());
	}

	@OnClick(R.id.favorite_checkbox)
	public void addFavoriteClicked() {
		mPresenter.addLastTranslateToFavorites();
	}

	@Override
	public TranslatePanel getTranslatePanel() {
		return mTranslatePanel;
	}

	@Override
	public RootActivity getRootActivity() {
		return ((RootActivity) getActivity());
	}

	public void initUI() {
		mToolbar.setTitle("");
		getRootActivity().setSupportActionBar(mToolbar);

		mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
		mDictionaryAdapter = new DictionaryAdapter();
		mRecyclerView.setAdapter(mDictionaryAdapter);
	}

	@Override
	public void showDictionary(List<BaseItem> baseItems, String transcription) {
		mDictionaryAdapter.addAll(baseItems);
		if (transcription != null)
			transcriptionTv.setText(getString(R.string.transcription_tv, transcription));
	}

	@Override
	public void showTranslated(String text) {
		translatedBtnsWrapper.setVisibility(View.VISIBLE);
		translatedText.setText(text);
	}

	@Override
	public void setIdleState() {
		translatedText.setText("");
		transcriptionTv.setText("");
		mDictionaryAdapter.clear();
		translatedBtnsWrapper.setVisibility(View.GONE);
		favoriteCheckbox.setChecked(false);
	}

	@Override
	public boolean checkAudioPermission() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			if (ContextCompat.checkSelfPermission(getContext(), RECORD_AUDIO) != PERMISSION_GRANTED) {
				requestPermissions(new String[]{RECORD_AUDIO}, REQUEST_PERMISSION_CODE);
				return false;
			}
		}
		return true;
	}

	@Override
	public void updateCurrentLang() {
		showSwappingAnimation();
	}

	@Override
	public void setFavorite(boolean isFavorite) {
		favoriteCheckbox.setChecked(isFavorite);
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		mPresenter.onRequestPermissionsResultHandler(requestCode, grantResults);
	}

	private void swapLangClick() {
		showSwappingAnimation();
		mPresenter.swapLanguage();
	}

	private void showSwappingAnimation() {
		ObjectAnimator moveLeftLangToCircleAnim = ObjectAnimator.ofFloat(fromTranslateLangTv, "translationX", 44 * Utils.getDensity(getContext()));
		ObjectAnimator returnLeftLangToStartPosAnim = ObjectAnimator.ofFloat(fromTranslateLangTv, "translationX", 0f);
		ObjectAnimator moveRightLangToCircleAnim = ObjectAnimator.ofFloat(toTranslateLangTv, "translationX", -44 * Utils.getDensity(getContext()));
		ObjectAnimator returnRightLangToStartPosAnim = ObjectAnimator.ofFloat(toTranslateLangTv, "translationX", 0f);
		returnLeftLangToStartPosAnim.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationStart(Animator animation) {
				super.onAnimationStart(animation);
				fromTranslateLangTv.setText(mPresenter.getCurrentLanguage().equals(RU_EN) ?
						R.string.lang_russian : R.string.lang_english);
				toTranslateLangTv.setText(mPresenter.getCurrentLanguage().equals(RU_EN) ?
						R.string.lang_english : R.string.lang_russian);
			}
		});
		AnimatorSet leftLangAnimSet = new AnimatorSet();
		leftLangAnimSet.playSequentially(moveLeftLangToCircleAnim, returnLeftLangToStartPosAnim);
		AnimatorSet rightLangAnimSet = new AnimatorSet();
		rightLangAnimSet.playSequentially(moveRightLangToCircleAnim, returnRightLangToStartPosAnim);
		AnimatorSet resultSet = new AnimatorSet();
		resultSet.playTogether(leftLangAnimSet, rightLangAnimSet);
		resultSet.setDuration(100);
		resultSet.setInterpolator(new FastOutSlowInInterpolator());
		resultSet.start();
		mRotateObjectAnimator.start();
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.swap_language_btn:
				swapLangClick();
				break;
		}
	}

	//region ============================= DI =============================
	@dagger.Module
	class Module {
		@Provides
		@TranslateScope
		TranslatePresenter provideTranslatePresenter() {
			return new TranslatePresenter();
		}
	}

	@dagger.Component(modules = Module.class)
	@TranslateScope
	interface Component {
		void inject(TranslateFragment fragment);
		void inject(TranslatePanel panel);
	}

	private void createDaggerComponent() {
		Component component = DaggerService.getComponent(Component.class);
		if (component == null) {
			component = DaggerTranslateFragment_Component.builder()
					.module(new Module())
					.build();
			DaggerService.registerComponent(Component.class, component);
		}
		component.inject(this);
	}
	//endregion
}
