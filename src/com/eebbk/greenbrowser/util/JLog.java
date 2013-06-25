package com.eebbk.greenbrowser.util;

import android.util.Log;

public class JLog {

	/** 默认开启 */
	private static boolean DEBUG = true;

	public static String build(String msg) {
		if (DEBUG)
			return msg;

		// 获取堆栈信息
		StackTraceElement caller = new Throwable().fillInStackTrace()
				.getStackTrace()[2];

		return new StringBuilder().append(caller.getClassName()).append(".")
				.append(caller.getMethodName()).append("(): ").append(msg)
				.toString();
	}

	public static void d(String tag, String msg) {
		if (DEBUG)
			Log.d(tag, msg);
	}

	public static void e(String tag, String msg) {
		if (DEBUG)
			Log.e(tag, msg);
	}

	public static void i(String tag, String msg) {
		if (DEBUG)
			Log.i(tag, msg);
	}

	/** 反转DEBUG设置 */
	public static void reverse() {
		DEBUG = !DEBUG;
	}

	public static void w(String tag, String msg) {
		if (DEBUG)
			Log.w(tag, msg);
	}
}