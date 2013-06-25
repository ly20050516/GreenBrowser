package com.eebbk.greenbrowser.dialog;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;

import com.eebbk.senior.greenbrowser.R;

public class MenuPopupWindow {
	public interface onMenuKeyListener {
		public void onClickMenu(int type);
	}

	public static final int OPERATE_GUIDE_BTN = 1;// 操作指南按钮
	public static final int OPERATE_SETTING_TYPE = 1;// 操作指南和设置

	public static final int OPERATE_TYPE = 0;// 只有操作指南
	public static final int SETTING_BTN = 2;// 设置按钮

	private Context mContext;

	private onMenuKeyListener mMenuKeyListener;

	private Button mOperateGuideBtn;
	private PopupWindow mPopupWindow;

	private Button mSettingBtn;
	private int mType = OPERATE_TYPE;

	private View mView;

	/**
	 * 构造方法
	 */
	public MenuPopupWindow(View view, Context mContext, int mType) {
		this.mContext = mContext;
		this.mView = view;
		this.mType = mType;
		initPopupWindow();
	}

	/**
	 * 撤销弹出框
	 */
	public void dismiss() {
		if (null != mPopupWindow) {
			mPopupWindow.dismiss();
		}
	}

	/**
	 * 实例化PopupWindow创建
	 */
	@SuppressWarnings("static-access")
	private void initPopupWindow() {
		View mLayout = null;

		// 获取LayoutInflater实例
		LayoutInflater mInflater = (LayoutInflater) mContext
				.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);

		// 获取弹出菜单的布局
		if (OPERATE_TYPE == mType) {
			mLayout = mInflater.inflate(R.layout.menu_popupwindow_again, null);
		} else {
			mLayout = mInflater.inflate(R.layout.menu_popupwindow, null);
		}
		mLayout.setFocusableInTouchMode(true);
		mLayout.setFocusable(true);
		mLayout.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
				if ((keyCode == KeyEvent.KEYCODE_MENU)
						&& (mPopupWindow.isShowing())
						&& event.getAction() == KeyEvent.ACTION_UP) {
					mPopupWindow.dismiss();// 这里写明模拟menu的PopupWindow退出就行
					return true;
				}
				return false;
			}
		});

		mOperateGuideBtn = (Button) mLayout
				.findViewById(R.id.OperateGuideButton);
		mOperateGuideBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
				if (null != mMenuKeyListener) {
					mMenuKeyListener.onClickMenu(OPERATE_GUIDE_BTN);
				}
			}
		});

		if (OPERATE_TYPE != mType) {
			mSettingBtn = (Button) mLayout.findViewById(R.id.SettingButton);
			mSettingBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					dismiss();
					if (null != mMenuKeyListener) {
						mMenuKeyListener.onClickMenu(SETTING_BTN);
					}
				}
			});
		}

		// 设置popupWindow的布局
		mPopupWindow = new PopupWindow(mLayout,
				WindowManager.LayoutParams.MATCH_PARENT,
				WindowManager.LayoutParams.WRAP_CONTENT);
		mPopupWindow.setFocusable(true);
		mPopupWindow.setOutsideTouchable(true);
		mPopupWindow.setBackgroundDrawable(new BitmapDrawable());

		/* 设置系统动画 */
		mPopupWindow.setAnimationStyle(R.style.PopupWindowAnimation);
	}

	public void setonMenuKeyListener(onMenuKeyListener mMenuKeyListener) {
		this.mMenuKeyListener = mMenuKeyListener;
	}

	/**
	 * 显示菜单
	 */
	public void show() {
		mPopupWindow.showAtLocation(mView, Gravity.BOTTOM, 0, 0); // 设置在屏幕中的显示位置
	}
}
