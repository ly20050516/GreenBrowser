package com.eebbk.greenbrowser.util;

import java.lang.Thread.UncaughtExceptionHandler;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class CrashHandler implements UncaughtExceptionHandler {

	public static final String TAG = "CrashHandler";

	// CrashHandler 实例
	private static CrashHandler INSTANCE = new CrashHandler();

	// 程序的 Context 对象
	private Activity mActivity;

	// 系统默认的 UncaughtException 处理类
	private Thread.UncaughtExceptionHandler mDefaultHandler;

	private CrashHandler() {
	}

	public static CrashHandler getInstance() {
		return INSTANCE;
	}

	/**
	 * 初始化
	 * 
	 * @param context
	 */
	public void init(Context context) {
		mActivity = (Activity) context;

		// 获取系统默认的 UncaughtException 处理器
		mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();

		// 设置该 CrashHandler 为程序的默认处理器
		Thread.setDefaultUncaughtExceptionHandler(this);
	}

	/**
	 * 当 UncaughtException 发生时会转入该函数来处理
	 */
	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		if (!handleException(ex) && mDefaultHandler != null) {
			// 如果用户没有处理则让系统默认的异常处理器来处理
			mDefaultHandler.uncaughtException(thread, ex);
		} else {
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				Log.e(TAG, "error : ", e);
			}

			// 退出程序
			android.os.Process.killProcess(android.os.Process.myPid());
			System.exit(1);
		}
	}

	/**
	 * 自定义错误处理
	 * 
	 * @param ex
	 * @return true：如果处理了该异常信息；否则返回 false
	 */
	private boolean handleException(Throwable ex) {
		if (ex == null) {
			return false;
		}

		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			Log.e(TAG, "error : ", e);
		}

		Intent i = mActivity
				.getBaseContext()
				.getPackageManager()
				.getLaunchIntentForPackage(
						mActivity.getBaseContext().getPackageName());
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		try {
			mActivity.startActivity(i);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		return true;
	}
}