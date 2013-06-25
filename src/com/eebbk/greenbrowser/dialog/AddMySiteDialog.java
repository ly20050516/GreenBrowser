package com.eebbk.greenbrowser.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;

import com.eebbk.senior.greenbrowser.R;

/**
 * 
 * 说明：自定义对话框<br>
 * 公司名称 ：步步高教育电子<br>
 * 
 * @author 李修金
 * @version 1.0
 */
public class AddMySiteDialog extends Dialog {

	public static class Builder {

		private Context context;
		private String negativeButtonText;
		private DialogInterface.OnClickListener positiveButtonClickListener,
				negativeButtonClickListener;

		private String positiveButtonText;

		public Builder(Context context) {
			this.context = context;
		}

		/**
		 * 创建自定义对话框
		 */
		public AddMySiteDialog create() {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			final AddMySiteDialog dialog = new AddMySiteDialog(context,
					R.style.AddMyDialog);
			View layout = inflater.inflate(R.layout.addmysite, null);
			dialog.addContentView(layout, new LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

			if (positiveButtonText != null) {
				((Button) layout.findViewById(R.id.positiveButton))
						.setText(positiveButtonText);
				if (positiveButtonClickListener != null) {
					((Button) layout.findViewById(R.id.positiveButton))
							.setOnClickListener(new View.OnClickListener() {
								@Override
								public void onClick(View v) {
									positiveButtonClickListener.onClick(dialog,
											DialogInterface.BUTTON_POSITIVE);
								}
							});
				}
			} else {
				layout.findViewById(R.id.positiveButton).setVisibility(
						View.GONE);
			}

			if (negativeButtonText != null) {
				((Button) layout.findViewById(R.id.negativeButton))
						.setText(negativeButtonText);
				if (negativeButtonClickListener != null) {
					((Button) layout.findViewById(R.id.negativeButton))
							.setOnClickListener(new View.OnClickListener() {
								@Override
								public void onClick(View v) {
									positiveButtonClickListener.onClick(dialog,
											DialogInterface.BUTTON_NEGATIVE);
								}
							});
				}
			} else {
				layout.findViewById(R.id.negativeButton).setVisibility(
						View.GONE);
			}

			dialog.setContentView(layout);
			return dialog;
		}

		/**
		 * 设置取消按钮
		 * 
		 * @param negativeButtonText
		 * @param listener
		 * @return Builder
		 */
		public Builder setNegativeButton(int negativeButtonText,
				DialogInterface.OnClickListener listener) {
			this.negativeButtonText = (String) context
					.getText(negativeButtonText);
			this.negativeButtonClickListener = listener;
			return this;
		}

		/**
		 * 设置取消按钮
		 * 
		 * @param negativeButtonText
		 * @param listener
		 * @return Builder
		 */
		public Builder setNegativeButton(String negativeButtonText,
				DialogInterface.OnClickListener listener) {
			this.negativeButtonText = negativeButtonText;
			this.negativeButtonClickListener = listener;
			return this;
		}

		/**
		 * 设置确定按钮
		 * 
		 * @param positiveButtonText
		 * @param listener
		 * @return Builder
		 */
		public Builder setPositiveButton(int positiveButtonText,
				DialogInterface.OnClickListener listener) {
			this.positiveButtonText = (String) context
					.getText(positiveButtonText);
			this.positiveButtonClickListener = listener;
			return this;
		}

		/**
		 * 设置确定按钮
		 * 
		 * @param positiveButtonText
		 * @param listener
		 * @return Builder
		 */
		public Builder setPositiveButton(String positiveButtonText,
				DialogInterface.OnClickListener listener) {
			this.positiveButtonText = positiveButtonText;
			this.positiveButtonClickListener = listener;
			return this;
		}
	}

	public AddMySiteDialog(Context context) {
		super(context);
	}

	public AddMySiteDialog(Context context, int theme) {
		super(context, theme);
	}
}
