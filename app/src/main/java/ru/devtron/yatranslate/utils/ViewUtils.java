package ru.devtron.yatranslate.utils;


import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;

import ru.devtron.yatranslate.R;

public class ViewUtils {
	/**
	 * Показывает диалог
	 *
	 * @param context        the context
	 * @param message        the message
	 * @param positiveButton the positive button string res
	 * @param negativeButton the negative button string res
	 * @param positiveAction the positive action callback listener
	 * @param negativeAction the negative action callback listener
	 * @return the alert dialog
	 */
	public static AlertDialog showAlertDialog(Context context,
	                                          CharSequence message,
	                                          @StringRes int positiveButton,
	                                          @StringRes int negativeButton,
	                                          DialogInterface.OnClickListener positiveAction,
	                                          DialogInterface.OnClickListener negativeAction) {
		AlertDialog dialog = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.dialogAlertTheme))
				.setCancelable(true)
				.setMessage(message)
				.setPositiveButton(positiveButton, positiveAction)
				.setNegativeButton(negativeButton, negativeAction)
				.create();
		dialog.show();
		return dialog;
	}
}
