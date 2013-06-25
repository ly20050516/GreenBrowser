package com.eebbk.greenbrowser.item;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebBackForwardList;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.eebbk.greenbrowser.util.JLog;
import com.eebbk.senior.greenbrowser.R;

/**
 * 
 * 说明：TAB单项<br>
 * 公司名称 ：步步高教育电子<br>
 * 
 * @author 李修金
 * @version 1.0
 */
public class TabItem extends LinearLayout implements OnClickListener {

	/**
	 * 
	 * 说明： TAB关闭接口<br>
	 * 公司名称 ：步步高教育电子<br>
	 * 
	 * @author 李修金
	 * @version 1.0
	 */
	public interface OnTabCloseListener {
		public void onTabClose(View v);
	}

	/** 加载进度 */
	private ProgressBar mProgressBar;

	/** TAB关闭 */
	private Button mTabClose;

	/** TAB图标 */
	private ImageView mTabImage;

	/** TAB文本 */
	private TextView mTabText;

	/** TAB关闭监听器 */
	private OnTabCloseListener onTabCloseListener;

	public TabItem(Context context) {
		this(context, null);
	}

	public TabItem(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater.from(context).inflate(R.layout.tabitem, this);
		mProgressBar = (ProgressBar) findViewById(R.id.tabProgress);
		mTabImage = (ImageView) findViewById(R.id.tabImage);
		mTabText = (TextView) findViewById(R.id.tabText);
		mTabClose = (Button) findViewById(R.id.tabClose);
		mTabClose.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		onTabCloseListener.onTabClose(this);
	}

	/**
	 * 设置TAB关闭监听器
	 * 
	 * @param onTabCloseListener
	 */
	public void setOnTabCloseListener(OnTabCloseListener onTabCloseListener) {
		this.onTabCloseListener = onTabCloseListener;
	}

	/**
	 * TAB项选中
	 */
	@Override
	public void setSelected(boolean selected) {
		if (selected) {
			setTabCloseVisiblity(View.VISIBLE);
		} else {
			setTabCloseVisiblity(View.INVISIBLE);
		}

		super.setSelected(selected);
	}

	/**
	 * 设置TAB项关闭按键是否显示
	 * 
	 * @param v
	 */
	public void setTabCloseVisiblity(int v) {
		mTabClose.setVisibility(v);
	}

	/**
	 * 设置TAB项的图标
	 * 
	 * @param icon
	 */
	public void setTabImage(Bitmap icon) {
		mTabImage.setVisibility(View.VISIBLE);
		mTabImage.setImageBitmap(icon);
	}

	/**
	 * 设置TAB项的图标
	 * 
	 * @param resId
	 */
	public void setTabImage(int resId) {
		mTabImage.setVisibility(View.VISIBLE);
		mTabImage.setImageResource(resId);
	}

	/**
	 * 设置TAB项的进度条及显示进度
	 * 
	 * @param progress
	 */
	public void setTabProgressBar(int progress) {
		mProgressBar.setVisibility(View.VISIBLE);
		mProgressBar.setProgress(progress * 100);

		if (progress == 100) {
			mProgressBar.setVisibility(View.GONE);
			mProgressBar.setProgress(0);
		}
	}

	/**
	 * 设置TAB项显示文本
	 * 
	 * @param text
	 */
	public void setTabText(String text) {
		mTabText.setText(text);

		currentTitle = text;
	}

	static final String CURRURL = "CURRURL";
	static final String CURRTITLE = "CURRTITLE";

	public void setUrl(String url) {
		this.currentUrl = url;
	}

	public String getUrl() {
		return this.currentUrl;
	}

	public void setTitle(String title) {
		this.currentTitle = title;
	}

	private String currentUrl;
	private String currentTitle;

	private Bundle mSavedState;
	private WebView mMainView;

	public void setMainView(WebView webView) {
		this.mMainView = webView;
	}

	public void setMainView(WebView webView, Bundle inState) {
		this.mMainView = webView;

		restoreState(inState);
	}

	public WebView getMainView() {
		return this.mMainView;
	}

	public Bundle saveState() {
		// TODO Auto-generated method stub
		if (mMainView == null) {
			return mSavedState;
		}

		if (TextUtils.isEmpty(currentUrl)) {
			return null;
		}

		mSavedState = new Bundle();

		WebBackForwardList savedList = mMainView.saveState(mSavedState);
		if (savedList == null || savedList.getSize() == 0) {
			JLog.w("---", "Failed to save back/forward list for ");
		}

		mSavedState.putString(CURRURL, currentUrl);
		mSavedState.putString(CURRTITLE, currentTitle);

		return mSavedState;
	}

	private void restoreState(Bundle inState) {
		mSavedState = inState;
		if (mSavedState == null) {
			return;
		}

		String url = inState.getString(CURRURL);
		String title = inState.getString(CURRTITLE);
		restoreUserAgent();
		setTabText(title);

		if (mMainView == null) {
			return;
		}

		WebBackForwardList restoredState = mMainView.restoreState(mSavedState);
		if (restoredState == null || restoredState.getSize() == 0) {
			JLog.w("---", "Failed to restore WebView state!");
			if (mMainView != null) {
				mMainView.loadUrl(url, null);
			}
		}
		mSavedState = null;
	}

	private void restoreUserAgent() {
		// TODO Auto-generated method stub

	}

	public void freeMemory() {
		if (mMainView != null) {
			mMainView.freeMemory();
		}
	}

	private boolean isDestroyed = false;

	public boolean IsDestroyed() {
		return this.isDestroyed;
	}

	public void destroy() {
		if (mMainView != null) {
			mMainView.destroy();
			isDestroyed = true;
		}
	}
}
