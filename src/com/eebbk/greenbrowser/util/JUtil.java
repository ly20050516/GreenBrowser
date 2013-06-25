package com.eebbk.greenbrowser.util;

import java.io.File;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.view.inputmethod.InputMethodManager;
import android.webkit.URLUtil;

/**
 * 
 * 说明：工具类<br>
 * 公司名称 ：步步高教育电子<br>
 * 
 * @author 李修金
 * @version 1.0
 */
@SuppressLint("NewApi")
public class JUtil {

	// User agent strings.
	private static final String DESKTOP_USERAGENT = "Mozilla/5.0 (X11; "
			+ "Linux x86_64) AppleWebKit/534.24 (KHTML, like Gecko) "
			+ "Chrome/11.0.696.34 Safari/534.24";

	private static final String IPHONE_USERAGENT = "Mozilla/5.0 (iPhone; U; CPU iPhone OS 3_0 like Mac OS X; en-us)"
			+ " AppleWebKit/528.18 (KHTML, like Gecko) Version/4.0"
			+ " Mobile/7A341 Safari/528.16";

	private static final Pattern STRIP_URL_PATTERN = Pattern
			.compile("^http://(.*?)/?$");

	/**
	 * 是否加载
	 * 
	 * @param url
	 *            {@link10.0.0.0 - 10.255.255.255 172.16.0.0 - 172.31.255.255
	 *            192.168.0.0 - 192.168.255.255}
	 * @param enterUrl
	 * @return boolean
	 */
	public static boolean canVisited(String url, String enterUrl) {

		String host;
		String enterHost;

		try {
			host = new URL(url).getHost().toLowerCase();
			enterHost = new URL(enterUrl).getHost().toLowerCase();

			if (isIpAddress(host) || host.indexOf("eebbk") != -1
					|| host.indexOf("xue999") != -1
					|| host.indexOf("haohaoxue") != -1
					|| enterHost.indexOf(host) != -1
					|| host.indexOf(enterHost) != -1) {
				return true;
			}

			String[] enterArray = enterHost.split("\\.");
			if (enterArray != null) {
				if (enterArray.length == 2) {
					if (host.indexOf(enterArray[0]) != -1) {
						return true;
					}
				} else if (enterArray.length >= 3) {
					if (host.indexOf(enterArray[1]) != -1) {
						return true;
					}
				}
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return false;
	}

	/**
	 * 
	 * @param value
	 * @return
	 */
	private static boolean isIpAddress(String value) {
		int start = 0;
		int end = value.indexOf('.');
		int numBlocks = 0;
		while (start < value.length()) {
			if (end == -1) {
				end = value.length();
			}

			try {
				int block = Integer.parseInt(value.substring(start, end));
				if ((block > 255) || (block < 0)) {
					return false;
				}
			} catch (NumberFormatException e) {
				return false;
			}

			numBlocks++;

			start = end + 1;
			end = value.indexOf('.', start);
		}

		return numBlocks == 4;
	}

	/**
	 * 关闭软键盘
	 * 
	 * @param context
	 */
	public static void closeKeyboard(Context context) {
		if (((Activity) context).getCurrentFocus() != null) {
			InputMethodManager im = (InputMethodManager) context
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			im.hideSoftInputFromWindow(((Activity) context).getCurrentFocus()
					.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	/**
	 * 
	 * @param uriString
	 * @return
	 * @throws Exception
	 */
	public static String DecodeURL(String uriString) throws Exception {
		String str = URLDecoder.decode(uriString, "iso-8859-1");
		String rule = "^(?:[\\x00-\\x7f]|[\\xe0-\\xef][\\x80-\\xbf]{2})+$";
		if (str.matches(rule)) {
			return URLDecoder.decode(uriString, "UTF-8");
		} else {
			return URLDecoder.decode(uriString, "GB2312");
		}
	}

	/**
	 * 获取目录 如果不存在则创建它
	 * 
	 * @param path
	 * @return String
	 */
	public static String getDirs(String path) {
		File dir = new File(path);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		return path;
	}

	/**
	 * 获取系统日期
	 * 
	 * @return String
	 */
	@SuppressLint("SimpleDateFormat")
	public static String getSystemDate() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return format.format(new Date());
	}

	/**
	 * 判断网络连接是否有效
	 * 
	 * @param context
	 * @return boolean
	 */
	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager mgr = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo[] info = mgr.getAllNetworkInfo();
		if (info != null) {
			for (int i = 0; i < info.length; i++) {
				if (info[i].getState() == NetworkInfo.State.CONNECTED) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * 通知媒体库更新
	 * 
	 * @param context
	 *            Object of {@link Context}
	 * @param path
	 *            更新路径
	 */
	public final static void notifyMediaCenterUpdate(Context context,
			String path) {
		if (null == context || null == path) {
			return;
		}

		Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
				Uri.parse("file:///" + path));
		context.sendBroadcast(intent);
	}

	/**
	 * 解析URL到IP
	 * 
	 * @param url
	 * @return
	 */
	public static String parseUrl(String url) {
		String ip = null;
		InetAddress x;
		try {
			x = InetAddress.getByName(url);
			ip = x.getHostAddress(); // 得到字符串形式的ip地址
			System.out.println(ip);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ip;
	}

	/**
	 * 打开Wifi并跳转到Wifi界面
	 * 
	 * @param context
	 */
	public static void startSetWifiEnabled(Context context) {
		// 对WIFI网卡进行操作需要通过WifiManager对象来进行，获取该对象的方法如下：
		WifiManager wifiManager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		if (wifiManager.getWifiState() != WifiManager.WIFI_STATE_ENABLED) {
			// 打开WIFI网卡
			wifiManager.setWifiEnabled(true);
		}

		Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		try {
			context.startActivity(intent);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param url
	 * @return
	 */
	public static String getUserAgentString(String url) {

		try {
			if (!URLUtil.isNetworkUrl(url)) {
				url = URLUtil.guessUrl(url);
			}

			URL mUrl = new URL(url);
			String host = mUrl.getHost();

			if (host.endsWith("lekan.com") || host.endsWith("letv.com")
					|| host.endsWith("iqiyi.com") || host.endsWith("youku.com")
					|| host.endsWith("pptv.com") || host.endsWith("tudou.com")
					|| host.endsWith("ku6.com") || host.endsWith("sohu.com")
					|| host.endsWith("baidu.com")
					|| host.endsWith("sina.com.cn")
					|| host.endsWith("kankan.com") || host.endsWith("qq.com")
					|| host.endsWith("ifeng.com")) {
				return IPHONE_USERAGENT;
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return DESKTOP_USERAGENT;
	}

	/**
	 * 取名称
	 * 
	 * @param url
	 * @return
	 */
	public static String stripUrl(String url) {
		if (url == null)
			return null;
		Matcher m = STRIP_URL_PATTERN.matcher(url);
		if (m.matches()) {
			return m.group(1);
		} else {
			return url;
		}
	}
}
