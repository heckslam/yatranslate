package ru.devtron.yatranslate.ui.screens.translate;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.devtron.yatranslate.R;
import ru.devtron.yatranslate.di.DaggerService;
import ru.devtron.yatranslate.ui.widget.RippleBackground;
import ru.devtron.yatranslate.ui.widget.RxEditText;
import ru.devtron.yatranslate.utils.ConstantManager;
import ru.yandex.speechkit.SpeechKit;

/**
 * Панель для ввода текста и кнопки очистить, записать, проиграть
 */
public class TranslatePanel extends RelativeLayout implements View.OnFocusChangeListener {
	private static final String TAG = "TranslatePanel";
	@BindView(R.id.translate_et)
	RxEditText translateEt;
	@BindView(R.id.clear_btn)
	ImageButton clearBtn;
	@BindView(R.id.mic_btn)
	ImageButton micBtn;
	@BindView(R.id.play_sound_btn)
	ImageButton playSoundBtn;
	@BindView(R.id.mic_ripple_wrapper)
	RippleBackground micRippleWrapper;

	@Inject
	TranslatePresenter mPresenter;

	AnimatorSet mInvalidTextAnimatorSet;

	private boolean isVoiceModeEnabled;

	public TranslatePanel(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		if (isInEditMode()) {
			return;
		}
		createDaggerComponent();
		ButterKnife.bind(this);
		SpeechKit.getInstance().configure(getContext(), ConstantManager.YASPEECH_API_KEY);
		createDaggerComponent();
		initView();

		mInvalidTextAnimatorSet = (AnimatorSet) AnimatorInflater.loadAnimator(getContext(), R.animator.invalid_text_animator);
		mInvalidTextAnimatorSet.setTarget(TranslatePanel.this);
	}

	@OnClick(R.id.clear_btn)
	public void clearTranslateInput() {
		translateEt.setText(null);
	}

	private void initView() {
		translateEt.setOnFocusChangeListener(this);
	}

	public RxEditText getTranslateEt() {
		return translateEt;
	}

	@OnClick(R.id.mic_btn)
	public void listenWordsFromMic() {
		if (!isVoiceModeEnabled) {
			mPresenter.createAndStartRecognizer();
		} else {
			setVoiceMode(false);
		}
	}

	@OnClick(R.id.play_sound_btn)
	public void playSound() {
		mPresenter.playTextToSpeech();
	}

	void setVoiceMode(boolean enabled) {
		isVoiceModeEnabled = enabled;
		if (enabled) {
			micRippleWrapper.startRippleAnimation();
			this.translateEt.setFocusable(false);
			this.translateEt.setHint(R.string.translate_input_voice_enabled_hint);
			this.setBackgroundResource(R.drawable.input_text_border_container_bg_yellow_voice_enabled);
			hideKeyboard();
		} else {
			micRippleWrapper.stopRippleAnimation();
			this.translateEt.setFocusableInTouchMode(true);
			this.translateEt.setFocusable(true);
			this.translateEt.requestFocus();
			this.translateEt.setHint(R.string.enter_text_hint_et);
			this.setBackgroundResource(R.drawable.input_text_border_container_bg_yellow);
		}
		showOrHideClearBtn();
	}

	private void hideKeyboard() {
		InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(translateEt.getWindowToken(), 0);
	}


	private void showOrHideClearBtn() {
		if (translateEt.getText().length() > 0 && !isVoiceModeEnabled) {
			clearBtn.setVisibility(VISIBLE);
			playSoundBtn.setVisibility(VISIBLE);
		} else {
			clearBtn.setVisibility(GONE);
			playSoundBtn.setVisibility(GONE);
		}
	}
	public void showOrHideClearBtn(CharSequence text) {
		if (text.length() > 0 && !isVoiceModeEnabled) {
			clearBtn.setVisibility(VISIBLE);
			playSoundBtn.setVisibility(VISIBLE);
		} else {
			clearBtn.setVisibility(GONE);
			playSoundBtn.setVisibility(GONE);
		}
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		if (v.getId() == R.id.translate_et) {
			setInputFocusState(hasFocus);
		}
	}

	private void createDaggerComponent() {
		TranslateFragment.Component component = DaggerService.getComponent(TranslateFragment.Component.class);
		if (component != null)
			component.inject(this);
	}

	public void setInputFocusState(boolean hasFocus) {
		this.translateEt.setCursorVisible(hasFocus);
		this.setBackgroundResource(hasFocus ? R.drawable.input_text_border_container_bg_yellow :
				R.drawable.input_text_border_container_bg_grey);
	}

	public void showInvalidTextAnimation() {
		if (mInvalidTextAnimatorSet.isRunning()) {
			mInvalidTextAnimatorSet.end();
			mInvalidTextAnimatorSet.start();
		} else {
			mInvalidTextAnimatorSet.start();
		}
	}
}
