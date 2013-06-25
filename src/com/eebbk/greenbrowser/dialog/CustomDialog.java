package com.eebbk.greenbrowser.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.eebbk.senior.greenbrowser.R;

/**
 * 
 * 说明：自定义对话框<br>
 * 公司名称 ：步步高教育电子<br>
 * 
 * @author 李修金
 * @version 1.0
 */
public class CustomDialog extends Dialog {

	public static class Builder {

		private View contentView;
		private Context context;
		private String message;
		private String negativeButtonText;
		private DialogInterface.OnClickListener positiveButtonClickListener,
				negativeButtonClickListener;
		private String positiveButtonText;

		private String title;

		public Builder(Context context) {
			this.context = context;
		}

		/**
		 * 创建自定义对话框
		 */
		public CustomDialog create() {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			final CustomDialog dialog = new CustomDialog(context,
					R.style.Dialog);
			View layout = inflater.inflate(R.layout.dialog_hint, null);
			dialog.addContentView(layout, new LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

			((TextView) layout.findViewById(R.id.title)).setText(title);

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

			if (message != null) {
				((TextView) layout.findViewById(R.id.message)).setText(message);
			} else if (contentView != null) {
				((LinearLayout) layout.findViewById(R.id.content))
						.removeAllViews();
				((LinearLayout) layout.findViewById(R.id.content)).addView(
						contentView, new LayoutParams(
								LayoutParams.WRAP_CONTENT,
								LayoutParams.WRAP_CONTENT));
			}
			dialog.setContentView(layout);
			return dialog;
		}

		/**
		 * 设置ContentView
		 * 
		 * @param v
		 * @return Builder
		 */
		public Builder setContentView(View v) {
			this.contentView = v;
			return this;
		}

		/**
		 * 设置对话框消息文本
		 * 
		 * @param message
		 * @return Builder
		 */
		public Builder setMessage(int message) {
			this.message = (String) context.getText(message);
			return this;
		}

		/**
		 * 设置对话框消息文本
		 * 
		 * @param message
		 * @return Builder
		 */
		public Builder setMessage(String message) {
			this.message = message;
			return this;
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

		/**
		 * 设置对话框标题
		 * 
		 * @param title
		 * @return Builder
		 */
		public Builder setTitle(int title) {
			this.title = (String) context.getText(title);
			return this;
		}

		/**
		 * 设置对话框标题
		 * 
		 * @param title
		 * @return Builder
		 */
		public Builder setTitle(String title) {
			this.title = title;
			return this;
		}
	}

	public CustomDialog(Context context) {
		super(context);
	}

	public CustomDialog(Context context, int theme) {
		super(context, theme);
	}
}
