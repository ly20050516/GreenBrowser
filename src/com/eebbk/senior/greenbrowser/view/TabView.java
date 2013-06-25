package com.eebbk.senior.greenbrowser.view;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.app.DownloadManager.Query;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.ConsoleMessage;
import android.webkit.DownloadListener;
import android.webkit.GeolocationPermissions.Callback;
import android.webkit.HttpAuthHandler;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.URLUtil;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebChromeClient.CustomViewCallback;
import android.webkit.WebResourceResponse;
import android.webkit.WebStorage.QuotaUpdater;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.eebbk.greenbrowser.dialog.AddMySiteDialog;
import com.eebbk.greenbrowser.dialog.CustomDialog;
import com.eebbk.greenbrowser.dialog.LimitVisitDialog;
import com.eebbk.greenbrowser.item.TabItem;
import com.eebbk.greenbrowser.item.TabItem.OnTabCloseListener;
import com.eebbk.greenbrowser.model.BaseObject;
import com.eebbk.greenbrowser.provider.PagerContentProvider.HistorySites;
import com.eebbk.greenbrowser.util.JDownload;
import com.eebbk.greenbrowser.util.JFileUtil;
import com.eebbk.greenbrowser.util.JLog;
import com.eebbk.greenbrowser.util.JUtil;
import com.eebbk.senior.greenbrowser.R;
import com.eebbk.senior.greenbrowser.view.HomeView.OnSiteNavigationListener;

/**
 * 
 * 说明：TABVIEW显示<br>
 * 公司名称 ：步步高教育电子<br>
 * 
 * @author 李修金
 * @version 1.0
 */
@SuppressLint("NewApi")
public class TabView extends LinearLayout {

	/**
	 * 说明：添加或移除我的网站<br>
	 * 公司名称 ：步步高教育电子<br>
	 * 
	 * @author 李修金
	 * @version 1.0
	 */
	final class AddMySiteDialogOnClickListener implements
			DialogInterface.OnClickListener {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub
			switch (which) {
			case DialogInterface.BUTTON_POSITIVE:
				if (JFileUtil.getAvailableInternalMemorySize() < 1024) {
					Toast.makeText(mActivity,
							mActivity.getText(R.string.freesize_short),
							Toast.LENGTH_LONG).show();
				}

				WebView webView = getCurrentWebView();
				BaseObject baseObject = (BaseObject) webView.getTag();
				if (baseObject.getIsme() == 0 && !mHomeView.isExist(baseObject)) {
					baseObject.setIsme(1);
					mHomeView.addMySites(baseObject);
				} else {
					mHomeView.removeMySites(baseObject);
					baseObject.setIsme(0);
				}
				break;

			case DialogInterface.BUTTON_NEGATIVE:
				break;
			}
			dialog.dismiss();
		}
	}

	/**
	 * 
	 * 说明：自定义对话框监听器内部类<br>
	 * 公司名称 ：步步高教育电子<br>
	 * 
	 * @author 李修金
	 * @version 1.0
	 */
	class CustomDialogOnClickListener implements
			DialogInterface.OnClickListener {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub
			switch (which) {
			case DialogInterface.BUTTON_POSITIVE:
				dialog.dismiss();
				JUtil.startSetWifiEnabled(mActivity);
				break;

			case DialogInterface.BUTTON_NEGATIVE:
				dialog.dismiss();
				break;
			}
		}
	}

	static class FullscreenHolder extends FrameLayout {

		public FullscreenHolder(Context ctx) {
			super(ctx);
			setBackgroundColor(ctx.getResources().getColor(R.color.black));
		}

		@Override
		public boolean onTouchEvent(android.view.MotionEvent evt) {
			return true;
		}

	}

	/**
	 * 
	 * 说明：网址导航<br>
	 * 公司名称 ：步步高教育电子<br>
	 * 
	 * @author 李修金
	 * @version 1.0
	 */
	class HomeViewOnSiteNavigationListener implements OnSiteNavigationListener {

		@Override
		public void onSiteNavigation(View v) {
			// TODO Auto-generated method stub
			BaseObject baseObject = (BaseObject) v.getTag();
			addTab(baseObject);
		}
	}

	class MyDownloadListener implements DownloadListener {

		@Override
		public void onDownloadStart(String url, String userAgent,
				String contentDisposition, String mimeType, long contentLength) {
			// TODO Auto-generated method stub
			JLog.i("tag", "url=" + url);
			JLog.i("tag", "userAgent=" + userAgent);
			JLog.i("tag", "contentDisposition=" + contentDisposition);
			JLog.i("tag", "mimetype=" + mimeType);
			JLog.i("tag", "contentLength=" + contentLength);
			if (JDownload.DOWNID == 0) {
				JDownload.onDownloadStart(mActivity, url, userAgent,
						contentDisposition, mimeType, contentLength);
			} else {
				Toast.makeText(mActivity, "不能添加更多的下载", Toast.LENGTH_LONG)
						.show();
			}

			removeEmptyTab();
		}
	}

	class MyWebChromeClient extends WebChromeClient {
		static final String LOG_TAG = "MyWebChromeClient";

		private TabItem tab;

		public MyWebChromeClient(TabItem tab) {
			super();
			// TODO Auto-generated constructor stub
			this.tab = tab;
		}

		@Override
		public Bitmap getDefaultVideoPoster() {
			// TODO Auto-generated method stub
			return super.getDefaultVideoPoster();
		}

		@Override
		public View getVideoLoadingProgressView() {
			// TODO Auto-generated method stub
			return super.getVideoLoadingProgressView();
		}

		@Override
		public void getVisitedHistory(ValueCallback<String[]> callback) {
			// TODO Auto-generated method stub
			super.getVisitedHistory(callback);
		}

		@Override
		public void onCloseWindow(WebView window) {
			// TODO Auto-generated method stub
			JLog.d(LOG_TAG, "onCloseWindow");

			removeTab(getCurrentTab());

			super.onCloseWindow(window);
		}

		@Override
		public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
			// TODO Auto-generated method stub
			JLog.d(LOG_TAG,
					"onConsoleMessage lineNumber: "
							+ consoleMessage.lineNumber() + " message: "
							+ consoleMessage.message() + " messageLevel: "
							+ consoleMessage.messageLevel());

			return super.onConsoleMessage(consoleMessage);
		}

		@Override
		public boolean onCreateWindow(WebView view, boolean isDialog,
				boolean isUserGesture, Message resultMsg) {
			// TODO Auto-generated method stub
			JLog.d(LOG_TAG, "onCreateWindow isDialog: " + isDialog
					+ " isUserGesture: " + isUserGesture);

			WebView childView = addTab(view);
			WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
			transport.setWebView(childView);
			resultMsg.sendToTarget();

			return true;
		}

		@Override
		public void onExceededDatabaseQuota(String url,
				String databaseIdentifier, long currentQuota,
				long estimatedSize, long totalUsedQuota,
				QuotaUpdater quotaUpdater) {
			// TODO Auto-generated method stub
			super.onExceededDatabaseQuota(url, databaseIdentifier,
					currentQuota, estimatedSize, totalUsedQuota, quotaUpdater);
		}

		@Override
		public void onGeolocationPermissionsHidePrompt() {
			// TODO Auto-generated method stub
			super.onGeolocationPermissionsHidePrompt();
		}

		@Override
		public void onGeolocationPermissionsShowPrompt(String origin,
				Callback callback) {
			// TODO Auto-generated method stub
			super.onGeolocationPermissionsShowPrompt(origin, callback);

			callback.invoke(origin, true, false);
		}

		@Override
		public void onHideCustomView() {
			// TODO Auto-generated method stub
			JLog.d(LOG_TAG, "onHideCustomView  ");

			hideCustomView();

			super.onHideCustomView();
		}

		@Override
		public boolean onJsAlert(WebView view, String url, String message,
				JsResult result) {
			// TODO Auto-generated method stub
			return super.onJsAlert(view, url, message, result);
		}

		@Override
		public boolean onJsBeforeUnload(WebView view, String url,
				String message, JsResult result) {
			// TODO Auto-generated method stub
			return super.onJsBeforeUnload(view, url, message, result);
		}

		@Override
		public boolean onJsConfirm(WebView view, String url, String message,
				JsResult result) {
			// TODO Auto-generated method stub
			return super.onJsConfirm(view, url, message, result);
		}

		@Override
		public boolean onJsPrompt(WebView view, String url, String message,
				String defaultValue, JsPromptResult result) {
			// TODO Auto-generated method stub
			return super.onJsPrompt(view, url, message, defaultValue, result);
		}

		@Override
		public boolean onJsTimeout() {
			// TODO Auto-generated method stub
			return super.onJsTimeout();
		}

		@Override
		public void onProgressChanged(WebView view, int newProgress) {
			// TODO Auto-generated method stub
			tab.setTabProgressBar(newProgress);

			super.onProgressChanged(view, newProgress);
		}

		@Override
		public void onReachedMaxAppCacheSize(long requiredStorage, long quota,
				QuotaUpdater quotaUpdater) {
			// TODO Auto-generated method stub
			quotaUpdater.updateQuota(requiredStorage * 2);
			// super.onReachedMaxAppCacheSize(requiredStorage, quota,
			// quotaUpdater);
		}

		@Override
		public void onReceivedIcon(WebView view, Bitmap icon) {
			// TODO Auto-generated method stub
			tab.setTabImage(icon);

			super.onReceivedIcon(view, icon);
		}

		@Override
		public void onReceivedTitle(WebView view, String title) {
			// TODO Auto-generated method stub
			tab.setTabText(title);
			tab.setUrl(view.getUrl());

			if (onPageChangeListener != null) {
				onPageChangeListener.onPageChange(false);
			}

			super.onReceivedTitle(view, title);
		}

		@Override
		public void onReceivedTouchIconUrl(WebView view, String url,
				boolean precomposed) {
			// TODO Auto-generated method stub
			super.onReceivedTouchIconUrl(view, url, precomposed);
		}

		@Override
		public void onRequestFocus(WebView view) {
			// TODO Auto-generated method stub
			super.onRequestFocus(view);
		}

		@Override
		public void onShowCustomView(View view, CustomViewCallback callback) {
			// TODO Auto-generated method stub
			JLog.d(LOG_TAG, "onShowCustomView  ");

			// super.onShowCustomView(view, callback);

			onShowCustomView(view, mActivity.getRequestedOrientation(),
					callback);
		}

		@Override
		public void onShowCustomView(View view, int requestedOrientation,
				CustomViewCallback callback) {
			// TODO Auto-generated method stub
			super.onShowCustomView(view, requestedOrientation, callback);

			showCustomView(view, requestedOrientation, callback);
		}

	}

	protected static final FrameLayout.LayoutParams COVER_SCREEN_PARAMS = new FrameLayout.LayoutParams(
			ViewGroup.LayoutParams.MATCH_PARENT,
			ViewGroup.LayoutParams.MATCH_PARENT);

	public void showCustomView(View view, int requestedOrientation,
			WebChromeClient.CustomViewCallback callback) {
		// if a view already exists then immediately terminate the new one
		if (mCustomView != null) {
			callback.onCustomViewHidden();
			return;
		}

		mOriginalOrientation = mActivity.getRequestedOrientation();
		FrameLayout decor = (FrameLayout) mActivity.getWindow().getDecorView();
		mFullscreenContainer = new FullscreenHolder(mActivity);
		mFullscreenContainer.addView(view, COVER_SCREEN_PARAMS);
		decor.addView(mFullscreenContainer, COVER_SCREEN_PARAMS);
		mCustomView = view;
		setFullscreen(true);
		getCurrentWebView().setVisibility(View.INVISIBLE);
		mCustomViewCallback = callback;
		mActivity.setRequestedOrientation(requestedOrientation);
	}

	class MyWebViewClient extends WebViewClient {

		class DialogOnClickListener implements DialogInterface.OnClickListener {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		}

		static final String LOG_TAG = "MyWebViewClient";

		private String enterUrl;
		private int preset;

		public MyWebViewClient(String url, int preset) {
			this.enterUrl = url;
			this.preset = preset;
		}

		@Override
		public void doUpdateVisitedHistory(WebView view, String url,
				boolean isReload) {
			// TODO Auto-generated method stub
			JLog.d(LOG_TAG, "doUpdateVisitedHistory: " + url + " isReload: "
					+ isReload);

			super.doUpdateVisitedHistory(view, url, isReload);
		}

		@Override
		public void onFormResubmission(WebView view, Message dontResend,
				Message resend) {
			// TODO Auto-generated method stub
			JLog.d(LOG_TAG, "onFormResubmission: " + dontResend + " resend: "
					+ resend);

			resend.sendToTarget();

			super.onFormResubmission(view, dontResend, resend);
		}

		@Override
		public void onLoadResource(WebView view, String url) {
			// TODO Auto-generated method stub
			JLog.d(LOG_TAG, "onLoadResource url: " + url);

			super.onLoadResource(view, url);
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			// TODO Auto-generated method stub
			// settings.setBlockNetworkImage(false);
			JLog.d(LOG_TAG, "onPageFinished url: " + url);

			super.onPageFinished(view, url);

			writeHistory(view, url);
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			// TODO Auto-generated method stub
			JLog.d(LOG_TAG, "onPageStarted url: " + url);

			view.getSettings()
					.setUserAgentString(JUtil.getUserAgentString(url));

			JLog.i(LOG_TAG, "onPageStarted getUserAgentString: "
					+ view.getSettings().getUserAgentString());

			super.onPageStarted(view, url, favicon);
		}

		@Override
		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {
			// TODO Auto-generated method stub
			JLog.d(LOG_TAG, "onReceivedError: errorCode: " + errorCode
					+ "  description: " + description + "	failingUrl: "
					+ failingUrl);

			super.onReceivedError(view, errorCode, description, failingUrl);
		}

		@Override
		public void onReceivedHttpAuthRequest(WebView view,
				HttpAuthHandler handler, String host, String realm) {
			// TODO Auto-generated method stub
			JLog.d(LOG_TAG, "onReceivedHttpAuthRequest: ");

			super.onReceivedHttpAuthRequest(view, handler, host, realm);
		}

		@Override
		public void onReceivedLoginRequest(WebView view, String realm,
				String account, String args) {
			// TODO Auto-generated method stub
			JLog.d(LOG_TAG, "onReceivedLoginRequest: ");

			super.onReceivedLoginRequest(view, realm, account, args);
		}

		/**
		 * 重写此方法可以让webview处理https请求
		 */
		@Override
		public void onReceivedSslError(WebView view, SslErrorHandler handler,
				SslError error) {
			// TODO Auto-generated method stub
			JLog.d(LOG_TAG, "onReceivedSslError: ");

			super.onReceivedSslError(view, handler, error);

			handler.proceed();
		}

		@Override
		public void onScaleChanged(WebView view, float oldScale, float newScale) {
			// TODO Auto-generated method stub
			JLog.d(LOG_TAG, "onScaleChanged: ");

			super.onScaleChanged(view, oldScale, newScale);
		}

		@Override
		@Deprecated
		public void onTooManyRedirects(WebView view, Message cancelMsg,
				Message continueMsg) {
			// TODO Auto-generated method stub
			JLog.d(LOG_TAG, "onTooManyRedirects: ");

			super.onTooManyRedirects(view, cancelMsg, continueMsg);
		}

		@Override
		public void onUnhandledKeyEvent(WebView view, KeyEvent event) {
			// TODO Auto-generated method stub
			JLog.d(LOG_TAG, "onUnhandledKeyEvent: " + event);

			super.onUnhandledKeyEvent(view, event);
		}

		@Override
		public WebResourceResponse shouldInterceptRequest(WebView view,
				String url) {
			// TODO Auto-generated method stub
			JLog.d(LOG_TAG, "shouldInterceptRequest url: " + url);

			return super.shouldInterceptRequest(view, url);
		}

		@Override
		public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
			// TODO Auto-generated method stub
			JLog.d(LOG_TAG, "shouldOverrideKeyEvent url: " + event);

			return super.shouldOverrideKeyEvent(view, event);
		}

		/**
		 * 重写此方法表明点击网页里面的链接还是在当前的webview里跳转，不跳到浏览器那边
		 */
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			// TODO Auto-generated method stub
			JLog.d(LOG_TAG, "shouldOverrideUrlLoading url: " + url);

			if (url.startsWith("about:")) {
				return false;
			}

			if (!JUtil.canVisited(url, enterUrl) && preset != 1) {
				if (mNoVisitDialog == null) {
					mNoVisitDialog = new LimitVisitDialog.Builder(mActivity)
							.create();
				}
				if (!mNoVisitDialog.isShowing()) {
					mNoVisitDialog.show();
				}

				return true;
			}

			return false;
		}
	}

	/**
	 * 说明： 长按显示添加至我的网站<br>
	 * 公司名称 ：步步高教育电子<br>
	 * 
	 * @author 李修金
	 * @version 1.0
	 */
	final class MyWebViewOnLongClickListener implements OnLongClickListener {

		@Override
		public boolean onLongClick(View v) {
			// TODO Auto-generated method stub
			String addString = null;
			if (((BaseObject) v.getTag()).getIsme() == 1) {
				addString = mActivity.getString(R.string.btn_canceladdsite);
			} else {
				addString = mActivity.getString(R.string.btn_add);
			}

			new AddMySiteDialog.Builder(mActivity)
					.setPositiveButton(addString,
							new AddMySiteDialogOnClickListener())
					.setNegativeButton(R.string.btn_cancel,
							new AddMySiteDialogOnClickListener()).create()
					.show();

			return false;
		}

	}

	/**
	 * 
	 * 说明：页面改变监听接口<br>
	 * 公司名称 ：步步高教育电子<br>
	 * 
	 * @author 李修金
	 * @version 1.0
	 */
	public interface OnPageChangeListener {
		public void onPageChange(boolean isHome);
	}

	class SubWebChromeClient extends WebChromeClient {
		static final String LOG_TAG = "SubWebChromeClient";

		private TabItem tab;

		public SubWebChromeClient(TabItem tab) {
			super();
			// TODO Auto-generated constructor stub
			this.tab = tab;
		}

		@Override
		public Bitmap getDefaultVideoPoster() {
			// TODO Auto-generated method stub
			return super.getDefaultVideoPoster();
		}

		@Override
		public View getVideoLoadingProgressView() {
			// TODO Auto-generated method stub
			return super.getVideoLoadingProgressView();
		}

		@Override
		public void getVisitedHistory(ValueCallback<String[]> callback) {
			// TODO Auto-generated method stub
			super.getVisitedHistory(callback);
		}

		@Override
		public void onCloseWindow(WebView window) {
			// TODO Auto-generated method stub
			JLog.d(LOG_TAG, "onCloseWindow");

			removeTab(getCurrentTab());

			super.onCloseWindow(window);
		}

		@Override
		public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
			// TODO Auto-generated method stub
			JLog.d(LOG_TAG,
					"onConsoleMessage lineNumber: "
							+ consoleMessage.lineNumber() + " message: "
							+ consoleMessage.message() + " messageLevel: "
							+ consoleMessage.messageLevel());

			return super.onConsoleMessage(consoleMessage);
		}

		@Override
		public boolean onCreateWindow(WebView view, boolean isDialog,
				boolean isUserGesture, Message resultMsg) {
			// TODO Auto-generated method stub
			JLog.i(LOG_TAG, "isDialog: " + isDialog + " isUserGesture: "
					+ isUserGesture);

			WebView childView = addTab(view);
			WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
			transport.setWebView(childView);
			resultMsg.sendToTarget();

			return true;
		}

		@Override
		public void onExceededDatabaseQuota(String url,
				String databaseIdentifier, long currentQuota,
				long estimatedSize, long totalUsedQuota,
				QuotaUpdater quotaUpdater) {
			// TODO Auto-generated method stub
			super.onExceededDatabaseQuota(url, databaseIdentifier,
					currentQuota, estimatedSize, totalUsedQuota, quotaUpdater);
		}

		@Override
		public void onGeolocationPermissionsHidePrompt() {
			// TODO Auto-generated method stub
			super.onGeolocationPermissionsHidePrompt();
		}

		@Override
		public void onGeolocationPermissionsShowPrompt(String origin,
				Callback callback) {
			// TODO Auto-generated method stub
			super.onGeolocationPermissionsShowPrompt(origin, callback);

			callback.invoke(origin, true, false);
		}

		@Override
		public void onHideCustomView() {
			// TODO Auto-generated method stub
			JLog.d(LOG_TAG, "onHideCustomView  ");

			hideCustomView();

			super.onHideCustomView();
		}

		@Override
		public boolean onJsAlert(WebView view, String url, String message,
				JsResult result) {
			// TODO Auto-generated method stub
			return super.onJsAlert(view, url, message, result);
		}

		@Override
		public boolean onJsBeforeUnload(WebView view, String url,
				String message, JsResult result) {
			// TODO Auto-generated method stub
			return super.onJsBeforeUnload(view, url, message, result);
		}

		@Override
		public boolean onJsConfirm(WebView view, String url, String message,
				JsResult result) {
			// TODO Auto-generated method stub
			return super.onJsConfirm(view, url, message, result);
		}

		@Override
		public boolean onJsPrompt(WebView view, String url, String message,
				String defaultValue, JsPromptResult result) {
			// TODO Auto-generated method stub
			return super.onJsPrompt(view, url, message, defaultValue, result);
		}

		@Override
		public boolean onJsTimeout() {
			// TODO Auto-generated method stub
			return super.onJsTimeout();
		}

		@Override
		public void onProgressChanged(WebView view, int newProgress) {
			// TODO Auto-generated method stub
			tab.setTabProgressBar(newProgress);

			super.onProgressChanged(view, newProgress);
		}

		@Override
		public void onReachedMaxAppCacheSize(long requiredStorage, long quota,
				QuotaUpdater quotaUpdater) {
			// TODO Auto-generated method stub
			quotaUpdater.updateQuota(requiredStorage * 2);
			// super.onReachedMaxAppCacheSize(requiredStorage, quota,
			// quotaUpdater);
		}

		@Override
		public void onReceivedIcon(WebView view, Bitmap icon) {
			// TODO Auto-generated method stub
			tab.setTabImage(icon);

			super.onReceivedIcon(view, icon);
		}

		@Override
		public void onReceivedTitle(WebView view, String title) {
			// TODO Auto-generated method stub
			tab.setTabText(title);

			if (onPageChangeListener != null) {
				onPageChangeListener.onPageChange(false);
			}

			super.onReceivedTitle(view, title);
		}

		@Override
		public void onReceivedTouchIconUrl(WebView view, String url,
				boolean precomposed) {
			// TODO Auto-generated method stub
			super.onReceivedTouchIconUrl(view, url, precomposed);
		}

		@Override
		public void onRequestFocus(WebView view) {
			// TODO Auto-generated method stub
			super.onRequestFocus(view);
		}

		@Override
		public void onShowCustomView(View view, CustomViewCallback callback) {
			// TODO Auto-generated method stub
			JLog.d(LOG_TAG, "onShowCustomView  ");

			super.onShowCustomView(view, callback);

			onShowCustomView(view, mActivity.getRequestedOrientation(),
					callback);
		}

		@Override
		public void onShowCustomView(View view, int requestedOrientation,
				CustomViewCallback callback) {
			// TODO Auto-generated method stub
			// super.onShowCustomView(view, requestedOrientation, callback);

			showCustomView(view, requestedOrientation, callback);
		}
	}

	class SubWebViewClient extends WebViewClient {
		static final String LOG_TAG = "SubWebViewClient";

		private String enterUrl;
		private int preset;

		public SubWebViewClient(String url, int preset) {
			this.enterUrl = url;
			this.preset = preset;
		}

		@Override
		public void doUpdateVisitedHistory(WebView view, String url,
				boolean isReload) {
			// TODO Auto-generated method stub
			super.doUpdateVisitedHistory(view, url, isReload);
		}

		@Override
		public void onFormResubmission(WebView view, Message dontResend,
				Message resend) {
			// TODO Auto-generated method stub
			resend.sendToTarget();

			super.onFormResubmission(view, dontResend, resend);
		}

		@Override
		public void onLoadResource(WebView view, String url) {
			// TODO Auto-generated method stub
			JLog.i(LOG_TAG, "onLoadResource url: " + url);

			super.onLoadResource(view, url);
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			// TODO Auto-generated method stub
			// settings.setBlockNetworkImage(false);
			JLog.i(LOG_TAG, "onPageFinished url: " + url);

			super.onPageFinished(view, url);

			writeHistory(view, url);
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			// TODO Auto-generated method stub
			JLog.i(LOG_TAG, "onPageStarted url: " + url);

			view.getSettings()
					.setUserAgentString(JUtil.getUserAgentString(url));

			JLog.i(LOG_TAG, "onPageStarted getUserAgentString: "
					+ view.getSettings().getUserAgentString());

			super.onPageStarted(view, url, favicon);
		}

		@Override
		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {
			// TODO Auto-generated method stub
			JLog.d(LOG_TAG, "onReceivedError: errorCode: " + errorCode
					+ "  description: " + description + "	failingUrl: "
					+ failingUrl);
			super.onReceivedError(view, errorCode, description, failingUrl);
		}

		@Override
		public void onReceivedHttpAuthRequest(WebView view,
				HttpAuthHandler handler, String host, String realm) {
			// TODO Auto-generated method stub
			super.onReceivedHttpAuthRequest(view, handler, host, realm);
		}

		@Override
		public void onReceivedLoginRequest(WebView view, String realm,
				String account, String args) {
			// TODO Auto-generated method stub
			super.onReceivedLoginRequest(view, realm, account, args);
		}

		/**
		 * 重写此方法可以让webview处理https请求
		 */
		@Override
		public void onReceivedSslError(WebView view, SslErrorHandler handler,
				SslError error) {
			// TODO Auto-generated method stub
			super.onReceivedSslError(view, handler, error);

			handler.proceed();
		}

		@Override
		public void onScaleChanged(WebView view, float oldScale, float newScale) {
			// TODO Auto-generated method stub
			super.onScaleChanged(view, oldScale, newScale);
		}

		@Override
		@Deprecated
		public void onTooManyRedirects(WebView view, Message cancelMsg,
				Message continueMsg) {
			// TODO Auto-generated method stub
			JLog.i(LOG_TAG, "onTooManyRedirects: ");

			super.onTooManyRedirects(view, cancelMsg, continueMsg);
		}

		@Override
		public void onUnhandledKeyEvent(WebView view, KeyEvent event) {
			// TODO Auto-generated method stub
			JLog.i(LOG_TAG, "onUnhandledKeyEvent: " + event);

			super.onUnhandledKeyEvent(view, event);
		}

		@Override
		public WebResourceResponse shouldInterceptRequest(WebView view,
				String url) {
			// TODO Auto-generated method stub
			JLog.i(LOG_TAG, "shouldInterceptRequest url: " + url);

			return super.shouldInterceptRequest(view, url);
		}

		@Override
		public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
			// TODO Auto-generated method stub
			JLog.i(LOG_TAG, "shouldOverrideKeyEvent url: " + event);

			return super.shouldOverrideKeyEvent(view, event);
		}

		/**
		 * 重写此方法表明点击网页里面的链接还是在当前的webview里跳转，不跳到浏览器那边
		 */
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			// TODO Auto-generated method stub
			JLog.i(LOG_TAG, "shouldOverrideUrlLoading url: " + url);

			if (url.startsWith("about:")) {
				return false;
			}

			if (!JUtil.canVisited(url, enterUrl) && preset != 1) {
				removeEmptyTab();

				if (mNoVisitDialog == null) {
					mNoVisitDialog = new LimitVisitDialog.Builder(mActivity)
							.create();
				}
				if (!mNoVisitDialog.isShowing()) {
					mNoVisitDialog.show();
				}

				return true;
			}

			return false;
		}

	}

	/**
	 * 
	 * 说明：TABITE点击<br>
	 * 公司名称 ：步步高教育电子<br>
	 * 
	 * @author 李修金
	 * @version 1.0
	 */
	class TabItemOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			setCurrentTab((TabItem) v);
		}

	}

	/**
	 * 
	 * 说明：TABITE关闭<br>
	 * 公司名称 ：步步高教育电子<br>
	 * 
	 * @author 李修金
	 * @version 1.0
	 */
	class TabItemOnTabCloseListener implements OnTabCloseListener {

		@Override
		public void onTabClose(View v) {
			// TODO Auto-generated method stub
			removeTab((TabItem) v);
		}

	}

	private static final String LOG_TAG = "TabView";

	/** 限制最大的TAB数 */
	private static final int MAXTAB = 5;

	/** 滚动偏移 */
	private final static int OFFSET = 100;

	private Activity mActivity;

	private View mCustomView = null;

	private CustomViewCallback mCustomViewCallback = null;

	private FrameLayout mFullscreenContainer;

	private TabItem mHomeTabItem;

	private HomeView mHomeView;

	private LayoutInflater mLayoutInflater;

	private LimitVisitDialog mNoVisitDialog;

	// WIFI连接对话框
	private CustomDialog mWifiConnectDialog;

	private int mOriginalOrientation;

	private FrameLayout mTabContents;

	private HorizontalScrollView mTabHScrollView;

	private LinearLayout mTabLabels;

	/** 页面改变监听 */
	private OnPageChangeListener onPageChangeListener;

	BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
				long downloadId = intent.getLongExtra(
						DownloadManager.EXTRA_DOWNLOAD_ID, 0);
				if (downloadId == JDownload.DOWNID) {
					JDownload.DOWNID = 0;
				}

				Query query = new Query();
				query.setFilterById(downloadId);
				DownloadManager manager = (DownloadManager) mActivity
						.getSystemService(Context.DOWNLOAD_SERVICE);
				Cursor c = manager.query(query);

				if (c.moveToFirst()) {
					int status = c.getInt(c
							.getColumnIndex(DownloadManager.COLUMN_STATUS));
					if (DownloadManager.STATUS_SUCCESSFUL == status) {
						try {
							manager.openDownloadedFile(downloadId);
						} catch (FileNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						Toast.makeText(mActivity, "下载完成！", Toast.LENGTH_LONG)
								.show();

					}
				}
			}
		}
	};

	// WebView创建工厂
	WebViewFactory mWebViewFactory;

	/**
	 * 构造
	 * 
	 * @param context
	 */
	public TabView(Context context) {
		this(context, null);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 构造
	 * 
	 * @param context
	 * @param attrs
	 */
	public TabView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		mActivity = (Activity) context;
		mLayoutInflater = LayoutInflater.from(context);

		mLayoutInflater.inflate(R.layout.tabbar, this, true);
		mTabHScrollView = (HorizontalScrollView) findViewById(R.id.tabHScrollView);
		mTabLabels = (LinearLayout) findViewById(R.id.tabLabels);
		mTabContents = (FrameLayout) findViewById(R.id.tabContents);

		WebViewSettings.initialize(context);
		mWebViewFactory = new WebViewFactory(context);
		context.registerReceiver(receiver, new IntentFilter(
				DownloadManager.ACTION_DOWNLOAD_COMPLETE));

		// 初始化首页
		mHomeTabItem = new TabItem(context);
		mHomeTabItem.setTabImage(R.drawable.tab_homes);
		mHomeTabItem.setTabText(getResources().getString(R.string.home_page));
		mHomeTabItem.setOnClickListener(new TabItemOnClickListener());
		mTabLabels.addView(mHomeTabItem);
		mHomeView = new HomeView(context);
		mHomeView
				.setOnSiteNavigationListener(new HomeViewOnSiteNavigationListener());

		setCurrentTab(mHomeTabItem);
	}

	/**
	 * 添加TAB页
	 * 
	 * @param url
	 * @param inState
	 */
	private void addTab(String url, Bundle inState) {
		removeToomuchTab();

		TabItem mTabItem = new TabItem(mActivity);
		mTabItem.setOnClickListener(new TabItemOnClickListener());
		mTabItem.setOnTabCloseListener(new TabItemOnTabCloseListener());
		mTabLabels.addView(mTabItem);
		checkNetwork();

		WebView mWebView = mWebViewFactory.createWebView();
		mTabItem.setMainView(mWebView, inState);
		mWebView.setWebViewClient(new MyWebViewClient(url, 1));
		mWebView.setWebChromeClient(new MyWebChromeClient(mTabItem));
		mWebView.setDownloadListener(new MyDownloadListener());
		JLog.d(LOG_TAG, "正在加载URL : " + url);

		setCurrentTab(mTabItem);
	}

	/**
	 * 添加TAB页
	 * 
	 * @param baseObject
	 */
	private void addTab(BaseObject baseObject) {
		removeToomuchTab();

		TabItem mTabItem = new TabItem(mActivity);
		mTabItem.setTabText(baseObject.getName());
		mTabItem.setTag(baseObject.getUrl());
		mTabItem.setOnClickListener(new TabItemOnClickListener());
		mTabItem.setOnTabCloseListener(new TabItemOnTabCloseListener());
		mTabLabels.addView(mTabItem);
		checkNetwork();

		WebView mWebView = mWebViewFactory.createWebView();
		mTabItem.setMainView(mWebView);
		mWebView.setTag(baseObject);
		String url = baseObject.getUrl();
		if (!URLUtil.isNetworkUrl(url)) {
			url = URLUtil.guessUrl(url);
		}
		mWebView.setWebViewClient(new MyWebViewClient(url, baseObject
				.getPreset()));
		mWebView.setWebChromeClient(new MyWebChromeClient(mTabItem));
		mWebView.setDownloadListener(new MyDownloadListener());
		if (baseObject.isPermitadd()) {
			mWebView.setOnLongClickListener(new MyWebViewOnLongClickListener());
		}
		mWebView.loadUrl(url);

		JLog.d(LOG_TAG, "正在加载URL : " + url);

		setCurrentTab(mTabItem);
	}

	public void addTab(String url) {
		removeToomuchTab();

		BaseObject object = new BaseObject();
		object.setName(JUtil.stripUrl(url));
		object.setUrl(url);
		object.setPreset(1);
		object.setPermitadd(false);
		addTab(object);
	}

	/**
	 * 添加TAB页
	 * 
	 * @param mainView
	 * @return
	 */
	private WebView addTab(WebView mainView) {
		removeToomuchTab();

		BaseObject object = (BaseObject) mainView.getTag();
		String url = object.getUrl();
		TabItem mChildTab = new TabItem(mActivity);
		mChildTab.setTabText(object.getName());
		mChildTab.setTag(url);
		mChildTab.setOnClickListener(new TabItemOnClickListener());
		mChildTab.setOnTabCloseListener(new TabItemOnTabCloseListener());
		mTabHScrollView.bringChildToFront(mTabLabels);
		mTabLabels.addView(mChildTab);
		checkNetwork();

		WebView mChildView = mWebViewFactory.createSubWebView();
		mChildTab.setMainView(mChildView);
		mChildView.setTag(object);
		mChildView.setWebViewClient(new SubWebViewClient(url, object
				.getPreset()));
		mChildView.setWebChromeClient(new SubWebChromeClient(mChildTab));
		mChildView.setDownloadListener(new MyDownloadListener());
		if (object.isPermitadd()) {
			mChildView
					.setOnLongClickListener(new MyWebViewOnLongClickListener());
		}

		setCurrentTab(mChildTab);

		return mChildView;
	}

	/**
	 * 调整滚动位置
	 * 
	 * @param view
	 */
	private void adjustScroll(View view) {
		mTabHScrollView.requestChildFocus(mTabLabels, view);
		int[] location = new int[2];
		view.getLocationOnScreen(location);
		if (location[1] < OFFSET) {
			mTabHScrollView.scrollTo(view.getLeft(), 0);
		} else if (location[0] > mTabHScrollView.getWidth() - OFFSET) {
			mTabHScrollView.scrollTo(view.getRight(), 0);
		}
	}

	/**
	 * 检查网络
	 */
	private void checkNetwork() {
		if (!JUtil.isNetworkAvailable(mActivity)) {
			if (mWifiConnectDialog == null) {
				mWifiConnectDialog = new CustomDialog.Builder(mActivity)
						.setTitle(R.string.hintTitle)
						.setMessage(R.string.hintText)
						.setNegativeButton(R.string.btn_cancel,
								new CustomDialogOnClickListener())
						.setPositiveButton(R.string.btn_connect,
								new CustomDialogOnClickListener()).create();
			}

			if (!mWifiConnectDialog.isShowing()) {
				mWifiConnectDialog.show();
			}
		}
	}

	/**
	 * 清除tabs
	 */
	private synchronized void clearTabs() {
		for (int i = 0; i < mTabLabels.getChildCount(); i++) {
			TabItem tabItem = (TabItem) mTabLabels.getChildAt(i);
			if (tabItem != mHomeTabItem) {
				WebView mainView = tabItem.getMainView();
				mTabContents.removeViewInLayout(mainView);
				mainView.clearCache(true);
				mainView.clearHistory();
				mainView.destroy();
				mainView = null;
			}
		}

		mTabLabels.removeAllViews();
	}

	/**
	 * 释放资源
	 */
	public void distroy() {
		// TODO Auto-generated method stub
		clearTabs();
		mActivity.unregisterReceiver(receiver);
	}

	/**
	 * 释放内存
	 */
	public void freeMemory() {
		for (int i = 0; i < mTabLabels.getChildCount(); i++) {
			TabItem tabItem = (TabItem) mTabLabels.getChildAt(i);
			if (tabItem != mHomeTabItem) {
				if (!tabItem.isSelected()) {
					tabItem.freeMemory();
				}
			}
		}
	}

	/**
	 * 得到当前的TAB页
	 * 
	 * @return TabItem
	 */
	private synchronized TabItem getCurrentTab() {
		for (int i = 0; i < mTabLabels.getChildCount(); i++) {
			TabItem tabItem = (TabItem) mTabLabels.getChildAt(i);
			if (tabItem.isSelected()) {
				return tabItem;
			}
		}

		return null;
	}

	/**
	 * 得到当前Content
	 * 
	 * @return WebView
	 */
	public synchronized WebView getCurrentWebView() {
		TabItem tabItem = (TabItem) getCurrentTab();
		return tabItem.getMainView();
	}

	/**
	 * 后退
	 */
	public void goBack() {
		WebView webView = getCurrentWebView();
		if (webView != null && webView.canGoBack()) {
			webView.goBack();
		}
	}

	/**
	 * 前进
	 */
	public void goForward() {
		WebView webView = getCurrentWebView();
		if (webView != null && webView.canGoForward()) {
			webView.goForward();
		}
	}

	/**
	 * 跳转到HOME页
	 */
	public void goHome() {
		mTabHScrollView.smoothScrollTo(0, 0);

		WebView webView = getCurrentWebView();
		if (webView != null) {
			setCurrentTab(mHomeTabItem);
		}
	}

	/**
	 * 　写入历史信息
	 * 
	 * @param view
	 * @param url
	 */
	private void writeHistory(WebView view, final String url) {
		String title = view.getTitle();

		if (TextUtils.isEmpty(title)) {
			title = JUtil.stripUrl(url);
		}

		writeHistory(title, url);
	}

	/**
	 * 写入历史信息
	 * 
	 * @param title
	 * @param url
	 */
	public void writeHistory(final String title, final String url) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					ContentValues values = new ContentValues();
					values.put(HistorySites.TITLE, title);
					values.put(HistorySites.URL, url);
					mActivity.getContentResolver().insert(
							HistorySites.HISTORYSITES_URI, values);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
	}

	/**
	 * 返回键
	 * 
	 * @return
	 */
	public boolean onBackKey() {
		if (mCustomView != null) {
			hideCustomView();

			if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
				mActivity
						.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
				mActivity
						.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
			} else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
				mActivity
						.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
				mActivity
						.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
			}

			mActivity
					.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);

			return true;
		}

		return false;
	}

	/**
	 * 配置改变
	 */
	@Override
	protected void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
	}

	public void hideCustomView() {
		getCurrentWebView().setVisibility(View.VISIBLE);
		if (mCustomView == null)
			return;

		setFullscreen(false);
		FrameLayout decor = (FrameLayout) mActivity.getWindow().getDecorView();
		decor.removeView(mFullscreenContainer);
		mCustomView.setVisibility(View.GONE);
		mFullscreenContainer.removeView(mCustomView);
		mFullscreenContainer = null;
		mCustomView = null;
		mCustomViewCallback.onCustomViewHidden();
		// Show the content view.
		mActivity.setRequestedOrientation(mOriginalOrientation);
	}

	/**
	 * 暂停
	 */
	public void pause() {
		WebView mWebView = getCurrentWebView();
		if (mWebView != null) {
			if (mCustomView != null) {
				mWebView.setVisibility(View.VISIBLE);
				hideCustomView();
			}
			mWebView.onPause();
			mWebView.pauseTimers();
			WebView.disablePlatformNotifications();
		}

		mHomeView.pause();
	}

	/**
	 * 重新加载网页
	 */
	public void refresh() {
		WebView webView = getCurrentWebView();
		if (webView != null) {
			checkNetwork();
			webView.pauseTimers();
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			webView.resumeTimers();
			webView.reload();
		}
	}

	/**
	 * 移除过多的TAB页
	 */
	private void removeToomuchTab() {
		if (mTabLabels.getChildCount() >= MAXTAB) {
			TabItem tabItem = (TabItem) mTabLabels.getChildAt(1);
			removeTab(tabItem);
		}
	}

	/**
	 * 移除当前空的TAB页
	 */
	private void removeEmptyTab() {
		TabItem tabItem = getCurrentTab();
		WebView webView = tabItem.getMainView();
		if (webView.copyBackForwardList().getSize() == 0) {
			removeTab(tabItem);
		}
	}

	/**
	 * 移除当前TAB页
	 * 
	 * @param t
	 */
	private void removeTab(TabItem t) {
		int index = mTabLabels.indexOfChild(t);
		if (index > 0) {
			TabItem tabItem = (TabItem) mTabLabels.getChildAt(index - 1);
			setCurrentTab(tabItem);

			mTabLabels.removeViewInLayout(t);
			mTabLabels.requestLayout();
			WebView webView = t.getMainView();
			mTabContents.removeViewInLayout(webView);
			webView.clearCache(false);
			webView.destroy();
			webView = null;
			System.gc();
		}
	}

	/**
	 * 恢复状态
	 * 
	 * @param inState
	 */
	public void restoreState(Bundle inState) {
		// TODO Auto-generated method stub
		ArrayList<String> stateValue = inState.getStringArrayList(stateKey);
		if (stateValue == null || stateValue.isEmpty()) {
			return;
		}

		for (String url : stateValue) {
			if (TextUtils.isEmpty(url)) {
				continue;
			}
			Bundle state = inState.getBundle(url);
			if (state == null || state.isEmpty()) {
				continue;
			}
			addTab(url, state);
		}
	}

	/**
	 * 还原
	 */
	public void resume() {
		WebView mWebView = getCurrentWebView();
		if (mWebView != null) {
			mWebView.onResume();
			mWebView.resumeTimers();
			WebView.enablePlatformNotifications();
		}

		mHomeView.resume();
	}

	// 状态关键字
	static final String stateKey = "STATEKEY";

	/**
	 * 保存状态
	 * 
	 * @param outState
	 */
	public void saveState(Bundle outState) {
		// TODO Auto-generated method stub
		int childCount = mTabLabels.getChildCount();
		if (childCount == 1) {
			return;
		}

		ArrayList<String> stateValue = new ArrayList<String>();
		for (int i = 1; i < childCount; i++) {
			TabItem tabItem = (TabItem) mTabLabels.getChildAt(i);
			Bundle tabState = tabItem.saveState();
			if (tabState != null) {
				if (outState.containsKey(tabItem.getUrl())) {
					continue;
				}
				if (stateValue.contains(tabItem.getUrl())) {
					continue;
				}
				stateValue.add(tabItem.getUrl());
				outState.putBundle(tabItem.getUrl(), tabState);
			}
		}

		if (!outState.isEmpty()) {
			outState.putStringArrayList(stateKey, stateValue);
		}
	}

	/**
	 * 设置当前TAB页
	 * 
	 * @param view
	 */
	private void setCurrentTab(TabItem tabItem) {
		JUtil.closeKeyboard(mActivity);

		TabItem tmpTabItem;
		int childCount = mTabLabels.getChildCount();
		for (int i = 0; i < childCount; i++) {
			tmpTabItem = (TabItem) mTabLabels.getChildAt(i);
			if (tmpTabItem.isSelected()) {
				tmpTabItem.setSelected(false);
			}
		}
		tabItem.setSelected(true);

		mTabContents.removeAllViews();
		if (tabItem == mHomeTabItem) {
			mTabContents.addView(mHomeView);
			tabItem.setTabCloseVisiblity(View.GONE);
			if (onPageChangeListener != null) {
				onPageChangeListener.onPageChange(true);
			}
		} else {
			// 待优化
			if (tabItem.IsDestroyed()) {

			}

			mTabContents.addView(tabItem.getMainView());
			if (onPageChangeListener != null) {
				onPageChangeListener.onPageChange(false);
			}
		}

		adjustScroll(tabItem);
	}

	/**
	 * 设置全屏
	 * 
	 * @param enabled
	 */
	public void setFullscreen(boolean enabled) {
		Window win = mActivity.getWindow();
		WindowManager.LayoutParams winParams = win.getAttributes();
		final int bits = WindowManager.LayoutParams.FLAG_FULLSCREEN;
		if (enabled) {
			winParams.flags |= bits;
		} else {
			winParams.flags &= ~bits;
			if (mCustomView != null) {
				mCustomView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
			} else {
				mTabContents.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
			}
		}
		win.setAttributes(winParams);
	}

	/**
	 * 设置页面改变监听
	 * 
	 * @param onPageChangeListener
	 */
	public void setOnPageChangeListener(
			OnPageChangeListener onPageChangeListener) {
		this.onPageChangeListener = onPageChangeListener;
	}
}
