package com.eebbk.senior.greenbrowser.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebSettings.RenderPriority;

/**
 * 
 * 说明：WEBVIEW 设置<br>
 * 公司名称 ：步步高教育电子<br>
 * 
 * @author 李修金
 * @version 1.0
 */
@SuppressLint({ "NewApi", "SetJavaScriptEnabled" })
public class WebViewSettings {

	private Context mContext;

	public static void initialize(final Context context) {
		sInstance = new WebViewSettings(context);
	}

	private static WebViewSettings sInstance;

	public static WebViewSettings getInstance() {
		return sInstance;
	}

	/**
	 * 
	 * @param context
	 */
	private WebViewSettings(Context context) {
		mContext = context.getApplicationContext();
	}

	/**
	 * 设置
	 * 
	 * @param settings
	 */
	public synchronized void startSettings(WebSettings settings) {
		CookieManager.getInstance().setAcceptCookie(true);
		syncStaticSettings(settings);
		syncSetting(settings);
	}

	private String mAppCachePath;

	private String getAppCachePath() {
		if (mAppCachePath == null) {
			mAppCachePath = mContext.getDir("appcache", 0).getPath();
		}
		return mAppCachePath;
	}

	/**
	 * 设置
	 * 
	 * @param settings
	 */
	private void syncStaticSettings(WebSettings settings) {
		settings.setTextZoom(93);
		settings.setNeedInitialFocus(false);

		settings.setSupportMultipleWindows(true);
		settings.setEnableSmoothTransition(true);

		settings.setAllowContentAccess(false);

		// HTML5 API flags
		settings.setAppCacheEnabled(true);
		settings.setDatabaseEnabled(true);
		settings.setDomStorageEnabled(true); // 是否开启Dom存储Api

		// HTML5 configuration parametersettings.
		settings.setAppCacheMaxSize(10 * 1024 * 1024);
		settings.setAppCachePath(getAppCachePath());
		settings.setDatabasePath(mContext.getDir("databases", 0).getPath());
		settings.setGeolocationDatabasePath(mContext.getDir("geolocation", 0)
				.getPath());
	}

	// User agent strings.
	private static final String DESKTOP_USERAGENT = "Mozilla/5.0 (X11; "
			+ "Linux x86_64) AppleWebKit/534.24 (KHTML, like Gecko) "
			+ "Chrome/11.0.696.34 Safari/534.24";

	private static final String IPHONE_USERAGENT = "Mozilla/5.0 (iPhone; U; CPU iPhone OS 3_0 like Mac OS X; en-us)"
			+ " AppleWebKit/528.18 (KHTML, like Gecko) Version/4.0"
			+ " Mobile/7A341 Safari/528.16";

	/**
	 * 设置
	 * 
	 * @param settings
	 */
	private void syncSetting(WebSettings settings) {
		settings.setRenderPriority(RenderPriority.HIGH); // 设置更高的渲染级别
		settings.setGeolocationEnabled(true);
		settings.setJavaScriptEnabled(true);// 是否支持javascript
		// settings.setJavaScriptCanOpenWindowsAutomatically(true);
		settings.setPluginState(PluginState.ON);// 开启插件支持
		settings.setLoadsImagesAutomatically(true);// WebView是否应当装入图像资源
		// settings.setBlockNetworkImage(true);　// 不从网络加载图像

		// WebView自适应屏幕
		settings.setUseWideViewPort(true);
		settings.setLoadWithOverviewMode(true);

		settings.setDefaultTextEncodingName("GBK");

		settings.setSavePassword(true);
		settings.setSaveFormData(true);

		if (userAgentString == null || userAgentString.length() == 0) {
			userAgentString = DESKTOP_USERAGENT;
		} else {
			userAgentString = IPHONE_USERAGENT;
		}
		settings.setUserAgentString(userAgentString);
	}

	private String userAgentString;

	public String getUserAgentString() {
		return userAgentString;
	}

	public void setUserAgentString(String userAgentString) {
		this.userAgentString = userAgentString;
	}

}
