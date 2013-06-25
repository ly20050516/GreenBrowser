package com.eebbk.senior.greenbrowser.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import com.eebbk.greenbrowser.util.BitmapUtils;
import com.eebbk.greenbrowser.util.GoToActivityAnimUtils;
import com.eebbk.senior.greenbrowser.R;

/**
 * 
 * 欢迎界面。
 * 
 * @author humingming <humingming@oaserver.dw.gdbbk.com>
 * 
 */
public class MainActivity extends Activity {

	private final static int MSG_UI_ENTER_MODULE = 100;
	private final static long SPLASH_SHOW_TIME = 1000;

	private Bitmap mBmpSplash;
	private ImageView mIvSplash;

	private Handler mUIHandler;

	private Bitmap decodeResource(Resources res, int resID) {
		if (null == res) {
			return null;
		}

		Bitmap bmp = null;
		try {
			bmp = BitmapFactory.decodeResource(res, resID);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return bmp;
	}

	private void enterModule() {
		// TODO: 自己实现判断是不是第一次，第一次运行跳去操作提示界面否则直接去主页
		boolean fakeFirstRun = false;
		if (fakeFirstRun) {
			goToTip();
		} else {
			goToHomePage();
		}
	}

	private void goToHomePage() {
		Intent intent = new Intent(this, OperatingtipsActivity.class);
		this.startActivity(intent);

		finish();

		GoToActivityAnimUtils.doGoToActivityAnim(this);
	}

	private void goToTip() {
		// TODO: 自己实现跳去欢迎界面
		// Intent intent = new Intent(this, TipActivity.class);
		// intent.putExtra(TipActivity.MODULE_NAME, mModuleName);
		// AppUtils.startActivityForResultSafely(this, intent,
		// GoToActivityAnimUtils.GO_TO_TIP_REQUEST_CODE);

		// GoToActivityAnimUtils.doGoToActivityAnim(this);
	}

	private void initConfig() {
		// 设置淡入淡出 activity 切换效果
		GoToActivityAnimUtils.setTranstionAnim(android.R.anim.fade_in,
				android.R.anim.fade_out, android.R.anim.fade_in,
				android.R.anim.fade_out);

		mBmpSplash = decodeResource(getResources(), R.drawable.splash);
	}

	private void initHandler() {

		mUIHandler = new Handler(new Handler.Callback() {

			@Override
			public boolean handleMessage(Message msg) {
				try {
					switch (msg.what) {
					case MSG_UI_ENTER_MODULE:
						enterModule();
						break;

					default:
						break;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

				return false;
			}

		});

		mUIHandler.sendEmptyMessageDelayed(MSG_UI_ENTER_MODULE,
				SPLASH_SHOW_TIME);
	}

	private void initView() {
		mIvSplash = (ImageView) findViewById(R.id.iv_splash);
		if (null != mIvSplash && null != mBmpSplash && !mBmpSplash.isRecycled()) {
			mIvSplash.setImageBitmap(mBmpSplash);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (GoToActivityAnimUtils.GO_TO_TIP_REQUEST_CODE == requestCode) {
			goToHomePage();
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);

		initConfig();
		initView();
		initHandler();

		Intent uriIntent = getIntent();
		if (uriIntent.getData() != null) {
			Intent homeIntent = new Intent(this, OperatingtipsActivity.class);
			homeIntent.setData(uriIntent.getData());
			this.startActivity(homeIntent);
			this.finish();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		if (null != mIvSplash) {
			mIvSplash.setVisibility(View.INVISIBLE);
		}

		if (null != mBmpSplash) {
			BitmapUtils.freeBitmap(mBmpSplash);
			mBmpSplash = null;
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);

		if (intent.getData() != null) {
			Intent homeIntent = new Intent(this, OperatingtipsActivity.class);
			homeIntent.setData(intent.getData());
			this.startActivity(homeIntent);
			this.finish();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	protected void onResume() {
		super.onResume();

		GoToActivityAnimUtils.doGoToActivityAnim(this);
	}

}
