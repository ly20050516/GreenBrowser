package com.eebbk.senior.greenbrowser.view;

import android.content.Context;
import android.content.pm.PackageManager;
import android.view.View;
import android.webkit.WebView;

/**
 * 说明：WEBVIEW创建 <br>
 * 公司名称 ：步步高教育电子<br>
 * 
 * @author 李修金
 * @version 1.0
 */
public class WebViewFactory {

	private Context mContext;

	/**
	 * 
	 * @param context
	 */
	public WebViewFactory(Context context) {
		super();
		this.mContext = context;
	}

	/**
	 * 创建WEBVIEW
	 * @return
	 */
	public WebView createSubWebView() {
		return createWebView();
	}

	/**
	 * 创建WEBVIEW
	 * @return
	 */
	public WebView createWebView() {
		WebView w = instantiateWebView();
		initWebViewSettings(w);
		return w;
	}

	/**
	 * 
	 * @return
	 */
	protected WebView instantiateWebView() {
		return new WebView(mContext);
	}

	/**
	 * 初始化
	 * 
	 * @param w
	 */
	protected void initWebViewSettings(WebView w) {
		w.setDrawingCacheEnabled(true); //
		w.setScrollbarFadingEnabled(true); // 设置隐藏滚动条
		w.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY); // 设置滚动条样式
		w.setMapTrackballToArrowKeys(false); // 使用轨迹球直接
		w.getSettings().setBuiltInZoomControls(true); // 启用内置变焦
		final PackageManager pm = mContext.getPackageManager();
		boolean supportsMultiTouch = pm
				.hasSystemFeature(PackageManager.FEATURE_TOUCHSCREEN_MULTITOUCH)
				|| pm.hasSystemFeature(PackageManager.FEATURE_FAKETOUCH_MULTITOUCH_DISTINCT);
		w.getSettings().setDisplayZoomControls(!supportsMultiTouch);
		// w.setInitialScale(25);
		w.requestFocus();
		w.requestFocusFromTouch();
		// w.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

		final WebViewSettings s = WebViewSettings.getInstance();
		s.startSettings(w.getSettings());
	}
}
