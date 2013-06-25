package com.eebbk.greenbrowser.util;

import java.io.File;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.os.Looper;
import android.os.StatFs;
import android.webkit.CookieManager;
import android.webkit.URLUtil;
import android.widget.Toast;

import com.eebbk.senior.greenbrowser.R;

public class JDownload {

	public static long DOWNID = 0;

	public static final long RESERVED = 10 * 1024;

	private static String encodePath(String path) {
		char[] chars = path.toCharArray();

		boolean needed = false;
		for (char c : chars) {
			if (c == '[' || c == ']' || c == '|') {
				needed = true;
				break;
			}
		}
		if (needed == false) {
			return path;
		}

		StringBuilder sb = new StringBuilder("");
		for (char c : chars) {
			if (c == '[' || c == ']' || c == '|') {
				sb.append('%');
				sb.append(Integer.toHexString(c));
			} else {
				sb.append(c);
			}
		}

		return sb.toString();
	}

	private static String getFileName(String url) {
		String fileName = null;
		String urlStr;
		try {
			urlStr = JUtil.DecodeURL(url);
			String ss[] = urlStr.split("/");
			fileName = ss[ss.length - 1];
			return fileName;
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return null;
	}

	public static void onDownloadStart(final Activity activity,
			final String url, final String userAgent,
			final String contentDisposition, final String mimetype,
			final long contentLength) {

		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				onDownloadStartThread(activity, url, userAgent,
						contentDisposition, mimetype, contentLength);
			}
		}).start();
	}

	public static void onDownloadStartThread(final Activity activity,
			String url, String userAgent, String contentDisposition,
			String mimetype, long contentLength) {
		// TODO Auto-generated method stub
		String fileName = getFileName(url);

		if (fileName == null) {
			fileName = URLUtil.guessFileName(url, contentDisposition, mimetype);
		}

		String status = Environment.getExternalStorageState();
		if (!status.equals(Environment.MEDIA_MOUNTED)) {
			if (status.equals(Environment.MEDIA_SHARED)) {
				showToast(activity, R.string.download_sdcard_busy_dlg_msg);
			} else {
				showToast(activity, R.string.download_no_sdcard_dlg_msg);
			}
			return;
		} else {
			File path = Environment.getExternalStorageDirectory();
			File file = new File(path.getPath() + File.separator
					+ Environment.DIRECTORY_DOWNLOADS, fileName);
			if (file.exists() && contentLength == file.length()) {
				showToast(activity, R.string.fileexist);
				return;
			}

			StatFs sf = new StatFs(path.getPath());
			long blockSize = sf.getBlockSize();
			long freeBlocks = sf.getAvailableBlocks();
			if (freeBlocks * blockSize < contentLength + RESERVED) {
				showToast(activity, R.string.cannot_stored);
				return;
			}
		}

		String addressString = encodePath(url);
		Uri uri = Uri.parse(addressString);
		final DownloadManager.Request request;
		try {
			request = new DownloadManager.Request(uri);
		} catch (IllegalArgumentException e) {
			showToast(activity, R.string.cannot_download);
			return;
		}
		request.setMimeType(mimetype);

		request.setDestinationInExternalPublicDir(
				Environment.DIRECTORY_DOWNLOADS, fileName);

		request.allowScanningByMediaScanner();
		request.setDescription(uri.getHost());
		//
		String cookies = CookieManager.getInstance().getCookie(url);
		request.addRequestHeader("cookie", cookies);
		request.addRequestHeader("User-Agent", userAgent);
		request.addRequestHeader("Referer", null);
		request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
		if (mimetype == null) {
			showToast(activity, R.string.cannot_download);
			return;
		} else {
			final DownloadManager manager = (DownloadManager) activity
					.getSystemService(Context.DOWNLOAD_SERVICE);

			new Thread("GreenBrowser download") {
				@Override
				public void run() {
					DOWNID = manager.enqueue(request);

					// final DownloadManager manager = (DownloadManager)
					// activity
					// .getSystemService(Context.DOWNLOAD_SERVICE);
					// Query query = new Query();
					// query.setFilterById(DOWNID);
					// Cursor cursor = manager.query(query);
					// if (cursor == null) {
					// return;
					// }
					//
					// int totalSizeCol = cursor
					// .getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES);
					// int currentSizeCol = cursor
					// .getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR);
					// int stateCol = cursor
					// .getColumnIndex(DownloadManager.COLUMN_STATUS);
					// int reasonCol = cursor
					// .getColumnIndex(DownloadManager.COLUMN_REASON);
					// long totalSize = -1;
					// long currentSize = -1;
					// String reason;
					// int state;
					// if (cursor.moveToFirst()) {
					// totalSize = cursor.getLong(totalSizeCol);
					// currentSize = cursor.getLong(currentSizeCol);
					// reason = cursor.getString(reasonCol);
					// state = cursor.getInt(stateCol);
					// }
					// cursor.close();
					//
					// if (currentSize == totalSize) {
					// Toast.makeText(activity, "下载完成！", Toast.LENGTH_LONG)
					// .show();
					// }
				}
			}.start();
		}

		showToast(activity, R.string.download_pending);
	}

	/**
	 * 显示消息
	 * 
	 * @param activity
	 * @param msg
	 */
	public static void showToast(final Activity activity, final int resId) {
		new Thread() {
			@Override
			public void run() {
				Looper.prepare();
				Toast.makeText(activity, resId, Toast.LENGTH_LONG).show();
				Looper.loop();
			}
		}.start();
	}

	/**
	 * 显示消息
	 * 
	 * @param activity
	 * @param msg
	 */
	public static void showToast(final Activity activity, final String msg) {
		new Thread() {
			@Override
			public void run() {
				Looper.prepare();
				Toast.makeText(activity, msg, Toast.LENGTH_LONG).show();
				Looper.loop();
			}
		}.start();
	}
}
