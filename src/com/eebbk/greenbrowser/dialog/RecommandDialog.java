package com.eebbk.greenbrowser.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.eebbk.greenbrowser.util.JUpload;
import com.eebbk.senior.greenbrowser.R;

/**
 * 
 * 说明：自定义对话框<br>
 * 公司名称 ：步步高教育电子<br>
 * 
 * @author 李修金
 * @version 1.0
 */
public class RecommandDialog extends Dialog {

	final class RecommandDialogOnClickListener implements
			android.view.View.OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.negativeButton:
				break;

			case R.id.positiveButton:
				// 提交推荐信息上传至服务器
				String contentString = mEditText.getText().toString();
				if (contentString != null && contentString.length() > 0) {
					new JUpload(mContext).uploader(contentString);
				}

				break;
			}
			mEditText.setText("");
			dismiss();
		}
	}

	private Context mContext;

	private EditText mEditText;

	private Button negativeButton;
	private Button positiveButton;

	public RecommandDialog(Context context) {
		super(context);
		this.mContext = context;
	}

	public RecommandDialog(Context context, int theme) {
		super(context, theme);
		this.mContext = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.recommendsite);

		mEditText = (EditText) findViewById(R.id.person_recommend_edit);
		negativeButton = (Button) findViewById(R.id.negativeButton);
		positiveButton = (Button) findViewById(R.id.positiveButton);
		negativeButton.setOnClickListener(new RecommandDialogOnClickListener());
		positiveButton.setOnClickListener(new RecommandDialogOnClickListener());
	}
}
