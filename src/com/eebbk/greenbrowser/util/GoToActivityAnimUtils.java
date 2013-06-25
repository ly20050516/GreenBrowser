package com.eebbk.greenbrowser.util;

import android.app.Activity;

/**
 * 
 * Activity 切换动画小工具
 * 
 * @author humingming <humingming@oaserver.dw.gdbbk.com>
 * 
 */
public class GoToActivityAnimUtils {

	// TODO: 这里添加自己要跳转去的 activity 的 request code
	public final static int GO_TO_ACTIVITY_1_REQUEST_CODE = 10000;

	public final static int GO_TO_TIP_REQUEST_CODE = 10001;

	private static int mGoToEnterAnimResID = 0;
	private static int mGoToExitAnimResID = 0;

	private static int mReturnEnterAnimResID = 0;
	private static int mReturnExitAnimResID = 0;

	public final static void doGoToActivityAnim(Activity context) {
		doGoToActivityAnim(context, true);
	}

	public final static void doGoToActivityAnim(Activity context,
			boolean withAnim) {
		if (null == context) {
			return;
		}

		if (withAnim) {
			context.overridePendingTransition(mGoToEnterAnimResID,
					mGoToExitAnimResID);
		} else {
			context.overridePendingTransition(0, 0);
		}
	}

	public final static void doReturnActivityAnim(Activity context) {
		doReturnActivityAnim(context, true);
	}

	public final static void doReturnActivityAnim(Activity context,
			boolean withAnim) {
		if (null == context) {
			return;
		}

		if (withAnim) {
			context.overridePendingTransition(mReturnEnterAnimResID,
					mReturnExitAnimResID);
		} else {
			context.overridePendingTransition(0, 0);
		}
	}

	public final static void setTranstionAnim(int goToEnterAnimResID,
			int goToExitAnimResID, int returnEnterAnimResID,
			int returnExitAnimResID) {

		mGoToEnterAnimResID = goToEnterAnimResID;
		mGoToExitAnimResID = goToExitAnimResID;

		mReturnEnterAnimResID = returnEnterAnimResID;
		mReturnExitAnimResID = returnExitAnimResID;
	}

}
