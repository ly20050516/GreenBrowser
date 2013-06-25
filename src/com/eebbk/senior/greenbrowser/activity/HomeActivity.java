package com.eebbk.senior.greenbrowser.activity;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentCallbacks2;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.CookieSyncManager;
import android.webkit.WebIconDatabase;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.eebbk.greenbrowser.dialog.MenuPopupWindow;
import com.eebbk.greenbrowser.dialog.MenuPopupWindow.onMenuKeyListener;
import com.eebbk.greenbrowser.util.CrashHandler;
import com.eebbk.greenbrowser.util.JFileUtil;
import com.eebbk.greenbrowser.util.JLog;
import com.eebbk.greenbrowser.util.JUtil;
import com.eebbk.screenshots.ScreenShotListener;
import com.eebbk.screenshots.ScreenShots;
import com.eebbk.senior.greenbrowser.R;
import com.eebbk.senior.greenbrowser.view.TabView;
import com.eebbk.senior.greenbrowser.view.TabView.OnPageChangeListener;

/**
 * 
 * 说明：绿色上网主界面<br>
 * 公司名称 ：步步高教育电子<br>
 * 
 * @author 李修金
 * @version 1.0
 */
@SuppressLint("NewApi")
public class HomeActivity extends Activity implements OnClickListener {

	class MyPageChangeListener implements OnPageChangeListener {

		@Override
		public void onPageChange(boolean isHome) {
			// TODO Auto-generated method stub
			if (isHome) {
				mHomeButton.setEnabled(false);
				mBackButton.setEnabled(false);
				mForwardButton.setEnabled(false);
				mRefreshButton.setEnabled(false);
			} else {
				mHomeButton.setEnabled(true);

				WebView mWebView = mTabView.getCurrentWebView();
				if (mWebView != null && mWebView.canGoBack()) {
					mBackButton.setEnabled(true);
				} else {
					mBackButton.setEnabled(false);
				}

				if (mWebView != null && mWebView.canGoForward()) {
					mForwardButton.setEnabled(true);
				} else {
					mForwardButton.setEnabled(false);
				}

				mRefreshButton.setEnabled(true);
			}
		}
	}

	/**
	 * 截屏监听器类
	 */
	private class MyScreenShotListener implements ScreenShotListener {
		/*
		 * (non-Javadoc)
		 * 
		 * @see com.eebbk.screenshots.ScreenShotListener#cancel()
		 */
		@Override
		public void cancel() {
			disEnableScreenShots();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.eebbk.screenshots.ScreenShotListener#save(android.graphics.Bitmap
		 * , android.graphics.Rect)
		 */
		@Override
		public void save(Bitmap source, Rect selectRegion) {
			int x = selectRegion.left;
			int y = selectRegion.top;
			int width = selectRegion.right - selectRegion.left;
			int height = selectRegion.bottom - selectRegion.top;

			Bitmap bm = Bitmap.createBitmap(source, x, y, width, height);

			disEnableScreenShots();

			if (bm != null) {
				SharedPreferences sharedata = getSharedPreferences(
						OperatingtipsActivity.NAME_STRING, MODE_PRIVATE);
				int num = sharedata.getInt(KEY_IMG_NUM, 1); // 策划要求从1开始，晕。

				String title = mContext.getString(R.string.app_name) + "_"
						+ num;
				String fileString = "";
				String sepPahtString = "/多媒体/相册/课堂笔记";
				if (JFileUtil.existSDCard()
						&& JFileUtil.getSDFreeSize() > 5 * 1024 * 1024) {
					fileString = Environment.getExternalStorageDirectory()
							.getPath() + sepPahtString;
				} else {
					fileString = JFileUtil.FLASH + sepPahtString;
				}

				File file = new File(fileString);
				if (!file.exists()) {
					file.mkdir();
				}

				File imageFile = new File(file, title + ".jpg");
				try {
					imageFile.createNewFile();
					FileOutputStream fos = new FileOutputStream(imageFile);
					bm.compress(CompressFormat.JPEG, 50, fos);
					fos.flush();
					fos.close();

					bm.recycle();
					System.gc();

					SharedPreferences.Editor shareeditdata = getSharedPreferences(
							OperatingtipsActivity.NAME_STRING, MODE_PRIVATE)
							.edit();
					shareeditdata.putInt(KEY_IMG_NUM, num + 1);
					shareeditdata.commit();

					JUtil.notifyMediaCenterUpdate(mContext, imageFile.getPath());

					Toast.makeText(mContext,
							mContext.getString(R.string.saveImage),
							Toast.LENGTH_SHORT).show();

					return;

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				Toast.makeText(mContext,
						mContext.getString(R.string.cannot_save),
						Toast.LENGTH_SHORT).show();
			}
		}

		public boolean saveBitmap(String filePath, Bitmap bitmap,
				CompressFormat format) {
			long time = System.currentTimeMillis();

			boolean isSaveSuccess = false;
			File file = new File(filePath);

			if (null == bitmap) {
				file.delete();
				return true;
			}

			DataOutputStream cout = null;

			try {
				cout = new DataOutputStream(new BufferedOutputStream(
						new FileOutputStream(file)));
				isSaveSuccess = bitmap.compress(format, 100, cout);
				cout.flush();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (cout != null) {
					try {
						cout.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}

			time = System.currentTimeMillis() - time;
			System.out.println("save [" + filePath + "] cost:" + time + "ms.");

			return isSaveSuccess;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.eebbk.screenshots.ScreenShotListener#shot(android.graphics.Bitmap
		 * , android.graphics.Rect)
		 */
		@Override
		public void shot(Bitmap source, Rect selectRegion) {
			int x = selectRegion.left;
			int y = selectRegion.top;
			int width = selectRegion.right - selectRegion.left;
			int height = selectRegion.bottom - selectRegion.top;

			System.out.println("selectRegion " + selectRegion);
			System.out.println("x = " + x + " y = " + y + " width = " + width
					+ " height = " + height);
			System.out.println(" s width = " + source.getWidth()
					+ " s height = " + source.getHeight());

			Bitmap bm = Bitmap.createBitmap(source, x, y, width, height);

			disEnableScreenShots();

			String path = Environment.getExternalStorageDirectory()
					+ File.separator + "readcard.data";
			if (!saveBitmap(path, bm, CompressFormat.JPEG)) {
				return;
			}

			/**
			 * 跳转
			 */
			Intent intent = new Intent();
			Bundle bundle = new Bundle();

			bundle.putString("menuname", APPNAME);
			bundle.putString("imagepath", path);

			bm.recycle();
			System.gc();

			intent.putExtras(bundle);
			intent.setAction("android.intent.action.ACTION_EDIT_CARD_OUTER");
			startActivity(intent);
		}
	}

	private static final String LOG_TAG = HomeActivity.class.getSimpleName();

	private static final String APPNAME = "绿色上网";

	private static final String KEY_IMG_NUM = "imgnum";

	protected static final String KEYCODE_MENU = "isMenu";

	private boolean isExit = false;
	private Button mBackButton;
	private Button mCardButton;
	private Button mClipButton;
	private Context mContext = HomeActivity.this;

	private Button mForwardButton;
	Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			isExit = false;
		}

	};
	private Button mHomeButton;

	private LinearLayout mLinearLayout;

	private MenuPopupWindow mMenuPopupWindow;

	private Button mRefreshButton;

	private BroadcastReceiver mRemoveSDReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if (Intent.ACTION_MEDIA_EJECT.equals(intent.getAction())) {
				Toast.makeText(mContext, "TF卡已被拔出！", Toast.LENGTH_SHORT).show();
			}
		}
	};

	private ScreenShots mScreenShots;

	private TabView mTabView;

	private void disEnableScreenShots() {
		if (mScreenShots != null) {
			mScreenShots.recycle();
			mScreenShots.setVisibility(View.INVISIBLE);
		}
	}

	/**
	 * 开始截屏
	 */
	private void enableScreenShots() {
		if (mScreenShots != null) {
			mScreenShots.initShot();
			mScreenShots.setVisibility(View.VISIBLE);
		}
	}

	public void exit() {
		if (!isExit) {
			isExit = true;
			Toast.makeText(getApplicationContext(), R.string.exit,
					Toast.LENGTH_SHORT).show();
			mHandler.sendEmptyMessageDelayed(0, 2000);
		} else {
			this.finish();
			onDestroy();
		}
	}

	private void init() {
		mLinearLayout = (LinearLayout) findViewById(R.id.linearLayout);
		setDidScreenBackground();
		mBackButton = (Button) findViewById(R.id.backButton);
		mForwardButton = (Button) findViewById(R.id.forwardButton);
		mHomeButton = (Button) findViewById(R.id.homeButton);
		mRefreshButton = (Button) findViewById(R.id.refreshButton);
		mCardButton = (Button) findViewById(R.id.cardButton);
		mClipButton = (Button) findViewById(R.id.clipButton);
		mTabView = (TabView) findViewById(R.id.tabView);
		mBackButton.setOnClickListener(this);
		mForwardButton.setOnClickListener(this);
		mHomeButton.setOnClickListener(this);
		mRefreshButton.setOnClickListener(this);
		mClipButton.setOnClickListener(this);
		mCardButton.setOnClickListener(this);
		mTabView.setOnPageChangeListener(new MyPageChangeListener());

		mScreenShots = (ScreenShots) findViewById(R.id.screenshots);
		mScreenShots.setScreenShotListener(new MyScreenShotListener());
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		disEnableScreenShots();
		JUtil.closeKeyboard(mContext);

		switch (v.getId()) {
		case R.id.backButton:
			mTabView.goBack();
			break;
		case R.id.forwardButton:
			mTabView.goForward();
			break;
		case R.id.homeButton:
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			mTabView.goHome();
			break;
		case R.id.refreshButton:
			mTabView.refresh();
			break;
		case R.id.clipButton:
			enableScreenShots();
			break;
		case R.id.cardButton:
			Intent intent = new Intent();
			Bundle bundle = new Bundle();
			bundle.putString("menuname", APPNAME);
			intent.putExtras(bundle);
			intent.setAction("android.intent.action.ACTION_LOOK_CARD_OUTER");
			startActivity(intent);
			break;
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);

		setDidScreenBackground();

		JLog.i(LOG_TAG, "onConfigurationChanged");
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		init();
		CrashHandler.getInstance().init(mContext);
		openIconDatabase();
		CookieSyncManager.createInstance(this);

		Intent uriIntent = getIntent();
		if (uriIntent.getData() != null) {
			mTabView.addTab(uriIntent.getDataString());
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		JLog.i(LOG_TAG, "onDestroy");
		disEnableScreenShots();
		if (mTabView != null) {
			mTabView.distroy();
		}
		WebIconDatabase.getInstance().close();
		super.onDestroy();

		JLog.i(LOG_TAG, "killProcess");
		Process.killProcess(Process.myPid());
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		disEnableScreenShots();
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			boolean isFullScreen = mTabView.onBackKey();
			if (!isFullScreen) {
				exit();
			}

			return false;

		case KeyEvent.KEYCODE_SEARCH:
			break;
		case KeyEvent.KEYCODE_HOME:
			break;
		case KeyEvent.KEYCODE_MENU:
			break;
		default:
			break;
		}

		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_MENU:
			JUtil.closeKeyboard(mContext);
			Handler handler = new Handler();
			handler.postDelayed(new Runnable() {

				@Override
				public void run() {
					showPopupWindow();
				}
			}, 300);
			break;

		default:
			break;
		}
		return super.onKeyUp(keyCode, event);
	}

	@Override
	public void onLowMemory() {
		// TODO Auto-generated method stub
		super.onLowMemory();

		JLog.i(LOG_TAG, "onLowMemory");

		mTabView.freeMemory();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);

		if (intent.getData() != null) {
			mTabView.addTab(intent.getDataString());
		}
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		JLog.i(LOG_TAG, "onPause");

		unregisterReceiver(mRemoveSDReceiver);

		mTabView.pause();

		super.onPause();
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		JLog.i(LOG_TAG, "onRestart");
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onRestoreInstanceState(savedInstanceState);

		JLog.i(LOG_TAG, "onRestoreInstanceState");

		mTabView.restoreState(savedInstanceState);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		JLog.i(LOG_TAG, "onResume");

		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Intent.ACTION_MEDIA_REMOVED);
		intentFilter.addAction(Intent.ACTION_MEDIA_EJECT);
		intentFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
		intentFilter.addDataScheme("file");
		registerReceiver(mRemoveSDReceiver, intentFilter);

		mTabView.resume();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		JLog.i(LOG_TAG, "onSaveInstanceState");

		mTabView.saveState(outState);

		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		JLog.i(LOG_TAG, "onStart");
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		JLog.i(LOG_TAG, "onStop");
	}

	@Override
	public void onTrimMemory(int level) {
		// TODO Auto-generated method stub
		super.onTrimMemory(level);

		JLog.i(LOG_TAG, "onTrimMemory level: " + level);

		if (level >= ComponentCallbacks2.TRIM_MEMORY_BACKGROUND) {
			if (mTabView != null) {
				mTabView.refresh();
			}
		}
	}

	private void openIconDatabase() {
		final WebIconDatabase instance = WebIconDatabase.getInstance();
		instance.open(this.getDir("icons", MODE_PRIVATE).getPath());
	}

	/**
	 * 根据横竖屏设置不同的背景
	 */
	private void setDidScreenBackground() {
		if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			mLinearLayout
					.setBackgroundResource(R.drawable.landscape_bg_homepage);
		} else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			mLinearLayout.setBackgroundResource(R.drawable.bg_homepage);
		}
	}

	/**
	 * 显示弹出框
	 */
	private void showPopupWindow() {
		if (mMenuPopupWindow != null) {
			mMenuPopupWindow.dismiss();
		}
		mMenuPopupWindow = new MenuPopupWindow(findViewById(R.id.linearLayout),
				this, MenuPopupWindow.OPERATE_TYPE);
		mMenuPopupWindow.setonMenuKeyListener(new onMenuKeyListener() {
			@Override
			public void onClickMenu(int type) {
				if (MenuPopupWindow.OPERATE_GUIDE_BTN == type)// 操作提示
				{
					Intent intent = new Intent(HomeActivity.this,
							OperatingtipsActivity.class);
					intent.putExtra(KEYCODE_MENU, true);
					startActivity(intent);
				}
			}
		});
		mMenuPopupWindow.show();
	}
}
