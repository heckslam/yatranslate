package ru.devtron.yatranslate.ui.widget;

import android.content.Context;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;

import javax.annotation.Nullable;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.annotations.NonNull;

/**
 * Custom EditText with rxjava support
 *
 * @author RuslanAliev
 */
public class RxEditText extends AppCompatEditText {

	@Nullable
	private FlowableEmitter<CharSequence> mEmitter;

	public RxEditText(Context context) {
		super(context);
	}

	public RxEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	/**
	 * Возвращает flowable который будет эмитить введенный пользователем текст
	 * @return the flowable
	 */
	@NonNull
	public Flowable<CharSequence> observeChanges() {
		return Flowable.create((FlowableEmitter<CharSequence> emitter) -> {
			mEmitter = emitter;
			emitter.onNext(getText());
		}, BackpressureStrategy.LATEST);
	}

	@Override
	public void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
		super.onTextChanged(text, start, lengthBefore, lengthAfter);
		if (mEmitter != null && !mEmitter.isCancelled()) {
			mEmitter.onNext(text);
		}
	}
}